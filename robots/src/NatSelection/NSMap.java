package NatSelection;


import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;


/*
Содержит поле

На поле есть еда.
 */

public class NSMap extends JPanel
{
    private final Timer m_timer = initTimer();
    private final ArrayList<Pair<Integer, Integer>> foodCoords = new ArrayList<>();
    private Monster monster;

    private static Timer initTimer()
    {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    public NSMap()
    {
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

        monster = new Monster();
    }



    // MODEL

    Pair<Integer, Integer> getRandomCoords(int firstStart, int firstEnd, int secondStart, int secondEnd)
    {
        return new Pair<Integer, Integer>((int) (firstStart + Math.random()*(firstEnd - firstStart)), (int) (secondStart + Math.random()*(secondEnd - secondStart)));
    }

    private void createFood(int num) {
        for (int i = 0; i < num; i++)
        {
            foodCoords.add(getRandomCoords(0, this.getWidth(), 0, this.getHeight()));
        }
    }

    protected void onModelUpdateEvent()
    {
        if(foodCoords.size() == 0 && this.getWidth() != 0)
            createFood(10);

        monster.update(10);
    }




    // VIEW

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
        drawMonster(g2d, monster.getCoords().getFirst(), monster.getCoords().getSecond());
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

    private void drawMonster(Graphics2D g, int x, int y)
    {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(Color.BLUE);
        fillOval(g, x, y, 10, 10);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 10, 10);
    }

}
