package NatSelection;


import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.*;

import javax.swing.JPanel;

public class NSMap extends JPanel
{
    private final int ITER_DURATION = 5000;

    private final Timer m_timer = initTimer();
    private final ArrayList<Pair<Integer, Integer>> foodCoords = new ArrayList<>();
    private final ArrayList<Monster> monsters = new ArrayList<>();

    private int foodCount = 0, mobCount = 0, iterationCount = 0;


    private int iterationTimer, iterationCounter;
    private boolean isActive = false, isEndOfIteration = false, allMonstersAtHome = true;

    private static Timer initTimer()
    {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    public NSMap()
    {
        for(int i = 0; i < 10; i++)
            createMonster((int) (Math.random()*350), (int)(Math.random()*350));

        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                onRedrawEvent();
            }
        }, 0, 50);
        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                onModelUpdateEvent();
            }
        }, 0, 10);
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

    public void createMonster(int x, int y) {
        monsters.add(new Monster(this, x, y));
    }

    private void createFood(int num) {
        for (int i = 0; i < num; i++)
        {
            foodCoords.add(new Pair<Integer, Integer>((int) (Math.random()*(this.getWidth())), (int) (Math.random()*(this.getHeight()))));
        }
    }

    protected void onModelUpdateEvent()
    {
        if(!isActive)
            return;

        if(allMonstersAtHome)
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
                        if(!s.isAlive())
                            i.remove();
                    }

                    foodCoords.clear();
                    createFood(foodCount);

                    iterationCounter++;
                    iterationTimer = ITER_DURATION;
                    isEndOfIteration = false;

                    if(iterationCounter >= iterationCount)
                    {
                        isActive = false;
                        return;
                    }
                }
            }
        }

        for (int i = 0; i < monsters.size(); i++) {
            monsters.get(i).update(10);
        }
    }

    private static double distance(double x1, double y1, double x2, double y2)
    {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    public Pair<Integer, Integer> getTarget(int x, int y, double distance) {
        double minDist = distance, dist;
        int foodNum = -1;

        for(int i = 0; i < foodCoords.size(); i++)
        {
            dist = distance(x, y, foodCoords.get(i).getFirst(), foodCoords.get(i).getSecond());

            if(dist < minDist)
            {
                minDist = dist;
                foodNum = i;
            }
        }

        if(foodNum != -1)
            return new Pair<>(foodCoords.get(foodNum).getFirst(), foodCoords.get(foodNum).getSecond());
        else
            return null;
    }

    public boolean removeFood(double x, double y) {
        for(int i = 0; i < foodCoords.size(); i++)
        {
            if(foodCoords.get(i).getFirst() == x && foodCoords.get(i).getSecond() == y)
            {
                foodCoords.remove(i);
                return true;
            }
        }
        return false;
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


    public void setCountFood(int i) {
        foodCount = i;
    }

    public void setCountMobs(int i) {
        mobCount = i;
    }

    public void setCountIterations(int i) {
        iterationCount = i;
    }

    public void startSimulation() {
        foodCoords.clear();
        monsters.clear();

        createFood(foodCount);
        createMonsters(mobCount);

        iterationCounter = 0;
        isActive = true;
        isEndOfIteration = false;
        allMonstersAtHome = true;
        iterationTimer = ITER_DURATION;


    }
}
