package NatSelection;


import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;

public class NSMap extends JPanel
{
    private final int ITER_DURATION = 5000;

    private final Timer timer = new Timer("events generator", true);
    private List<Monster> monsters = new CopyOnWriteArrayList<Monster>();
    private Observer monsterCoordListener;
    private Observer monsterDistanceListener;
    private Monster observableMonster;
    private final FoodGenerator foodGenerator;
    public double maxDist = 0;

    private NSWindow window;

    private int mobCount = 0, iterationCount = 0;

    private int iterationTimer, iterationCounter;
    private boolean isActive = false, isEndOfIteration = false, allMonstersAtHome = true;

    public NSMap(NSWindow window)
    {
        this.window = window;

        window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                maxDist = getMaxDist();
            }
        });

        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                onRedrawEvent();
            }
        }, 0, 17);
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                onModelUpdateEvent();
            }
        }, 0, 11);

        foodGenerator = new FoodGenerator(this);

        setDoubleBuffered(true);
    }

    private double getMaxDist() {
        var size = window.getSize();
        var h = size.height;
        var w = size.width;
        return Math.sqrt(h * h + w * w);
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
            for (Monster monster : monsters) {
                monster.activate();
            }
            allMonstersAtHome = false;
        }

        iterationTimer -= 10;
        if(iterationTimer < 0)
        {
            if(!isEndOfIteration)
            {
                for (Monster monster : monsters) {
                    if (!monster.isAtHome())
                        monster.goHome();
                }
                isEndOfIteration = true;
                foodGenerator.stop();
            }
            else
            {
                allMonstersAtHome = true;

                for (Monster monster : monsters) {
                    if (!monster.isAtHome()) {
                        allMonstersAtHome = false;
                        break;
                    }
                }

                if(allMonstersAtHome)
                {
                    if(observableMonster != null && !observableMonster.isAlive())
                    {
                        observableMonster.deleteObservers();
                        observableMonster = null;
                    }

                    monsters.removeIf(mon -> !mon.isAlive());
                    if(observableMonster == null && monsters.size() != 0) {
                        observableMonster = monsters.get(0);
                        observableMonster.addObserver(monsterCoordListener);
                        observableMonster.addObserver(monsterDistanceListener);
                        observableMonster.setColor(Color.MAGENTA);
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
        RTree<Nothing, Point> foodCoords = foodGenerator.getFoodCoords();

        foodCoords.entries().map(Entry::geometry).forEach(e ->
                drawTarget(g2d, (int)e.x(), (int)e.y()));

        for (int i = 0; i < monsters.size(); i++) {
            Monster monster = monsters.get(i);
            if (monster != observableMonster)
                drawMonster(g2d, monster.getCoords().getFirst(), monster.getCoords().getSecond(), monster.getDirection(), monster.getColor());
        }

        if(observableMonster != null)
            drawMonster(g2d, observableMonster.getCoords().getFirst(), observableMonster.getCoords().getSecond(), observableMonster.getDirection(), observableMonster.getColor());
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

    private void drawMonster(Graphics2D g, int x, int y, double direction, Color color)
    {
        AffineTransform t = AffineTransform.getRotateInstance(direction, x, y);
        g.setTransform(t);
        g.setColor(color);
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


    public Point getTarget(int x, int y, double distance)
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

        if(monsters.size() != 0)
        {
            observableMonster = monsters.get(0);
            observableMonster.addObserver(monsterCoordListener);
            observableMonster.addObserver(monsterDistanceListener);
            observableMonster.setColor(Color.MAGENTA);
        }

        iterationCounter = 0;
        isActive = true;
        isEndOfIteration = false;
        allMonstersAtHome = true;
        iterationTimer = ITER_DURATION;
    }

    public void setMonsterCoordsListener(Observer listener) {
        monsterCoordListener = listener;
    }

    public void setMonsterDistanceListener(Observer listener)
    {
        monsterDistanceListener = listener;
    }
}
