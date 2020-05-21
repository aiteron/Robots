package NatSelection;


import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.*;

import javax.swing.JPanel;

public class NSMap extends JPanel
{
    private final int ITER_DURATION = 5000;

    private final Timer timer = new Timer("events generator", true);
    private ArrayList<Monster> monsters = new ArrayList<>();
    private ArrayList<Observer> MonsterListeners = new ArrayList<>();
    private final FoodGenerator foodGenerator;

    private NSWindow window;

    private int mobCount = 0, iterationCount = 0;

    private int iterationTimer, iterationCounter;
    private boolean isActive = false, isEndOfIteration = false, allMonstersAtHome = true;

    public NSMap(NSWindow window)
    {
        this.window = window;

        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                onRedrawEvent();
            }
        }, 0, 50);
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                onModelUpdateEvent();
            }
        }, 0, 10);

        foodGenerator = new FoodGenerator(this);

        setDoubleBuffered(true);
    }

    private void createMonsters(int num)
    {
        for(int i = 0; i < num; i++)
        {
            if(Math.random() < 0.5)
                createMonster((int)(Math.random()*this.getWidth()), 0);
            else
                createMonster((int)(Math.random()*this.getWidth()), this.getHeight());
        }
    }

    public void createMonster(int x, int y)
    {
        monsters.add(new Monster(this, x, y));
    }

    protected void onModelUpdateEvent()
    {
        if(!isActive)
            return;

        if(allMonstersAtHome)       // Start iteration
        {
            for(int i = 0; i < monsters.size(); i++)
            {
                monsters.get(i).activate();
            }
            allMonstersAtHome = false;
        }

        iterationTimer -= 10;
        if(iterationTimer < 0)
        {
            if(!isEndOfIteration)
            {
                for(int i = 0; i < monsters.size(); i++)
                {
                    if(!monsters.get(i).isAtHome())
                        monsters.get(i).goHome();
                }
                isEndOfIteration = true;
                foodGenerator.stop();
            }
            else
            {
                allMonstersAtHome = true;

                for(int i = 0; i < monsters.size(); i++)
                {
                    if(!monsters.get(i).isAtHome())
                    {
                        allMonstersAtHome = false;
                        break;
                    }
                }

                if(allMonstersAtHome)
                {
                    Iterator<Monster> i = monsters.iterator();
                    while (i.hasNext()) {
                        Monster s = i.next();
                        if(!s.isAlive()) {
                            s.deleteObservers();
                            i.remove();
                        }
                    }

                    iterationCounter++;
                    iterationTimer = ITER_DURATION;
                    isEndOfIteration = false;
                    foodGenerator.start();

                    if(iterationCounter >= iterationCount)
                    {
                        isActive = false;
                        foodGenerator.stop();
                        window.setResizable(true);
                        return;
                    }
                }
            }
        }

        for (int i = 0; i < monsters.size(); i++) {
            monsters.get(i).update(10);
        }
    }

    protected void onRedrawEvent()
    {
        EventQueue.invokeLater(this::repaint);
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;
        ArrayList<Pair<Integer, Integer>> foodCoords = foodGenerator.getFoodCoords();

        for(int i = 0; i < foodCoords.size(); i++)
            drawTarget(g2d, foodCoords.get(i).getFirst(), foodCoords.get(i).getSecond());

        for(int i = 0; i < monsters.size(); i++)
            drawMonster(g2d, monsters.get(i).getCoords().getFirst(), monsters.get(i).getCoords().getSecond(), monsters.get(i).getDirection());
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawTarget(Graphics2D g, int x, int y)
    {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }

    private void drawMonster(Graphics2D g, int x, int y, double direction)
    {
        AffineTransform t = AffineTransform.getRotateInstance(direction, x, y);
        g.setTransform(t);
        g.setColor(Color.BLUE);
        fillOval(g, x, y, 20, 10);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 20, 10);

        g.setColor(Color.RED);
        fillOval(g, x + 5, y, 5, 5);
    }

    public void setFoodGenerateCoeff(int num) {
        foodGenerator.setFoodCoeff(num);
    }

    public void setCountMobs(int i) {
        mobCount = i;
    }

    public void setCountIterations(int i) {
        iterationCount = i;
    }


    public Pair<Integer, Integer> getTarget(int x, int y, double distance)
    {
        return foodGenerator.getTarget(x, y, distance);
    }

    public boolean removeFood(double x, double y)
    {
        return foodGenerator.removeFood(x, y);
    }


    public void startSimulation() {
        window.setResizable(false);

        foodGenerator.restart();
        monsters.clear();

        createMonsters(mobCount);

        for (var listener :
                MonsterListeners) {
            monsters.get(0).addObserver(listener);
        }

        iterationCounter = 0;
        isActive = true;
        isEndOfIteration = false;
        allMonstersAtHome = true;
        iterationTimer = ITER_DURATION;
    }

    public void setMonsterCoordsListener(Observer listener) {
        MonsterListeners.add(listener);

        if(monsters.size() != 0)
            monsters.get(0).addObserver(listener);
    }
}
