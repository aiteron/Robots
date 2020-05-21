package NatSelection;

import java.awt.*;
import java.util.Observable;

public class Monster extends Observable {

    private final double SPEED = 0.1;
    private final double ROTATESPEED = 0.01;

    private double x, y, direction, targetDirection, visionDistance = 60;
    private Pair<Integer, Integer> targetCoords;
    private NSMap map;
    private int foodEaten = 0;
    private boolean isAlive = true, isAtHome = true, isGoingHome = false;
    private Color color;

    public Monster(NSMap nsMap, int x, int y)
    {
        this.x = x;
        this.y = y;
        direction = Math.random()*Math.PI*2;
        targetDirection = -1;
        map = nsMap;
        color = Color.BLUE;

        targetCoords = null;
    }

    private static double distance(double x1, double y1, double x2, double y2)
    {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    public void update(double dt)
    {
        double newX, newY;

        if(countObservers() != 0)
            updateDistanceToFood();

        if(isAtHome)
            return;

        if(foodEaten < 2 && !isGoingHome)
        {
            targetCoords = map.getTarget((int)x, (int)y, visionDistance);
        }

        if(targetCoords != null)
        {
            targetDirection = Math.atan2(targetCoords.getSecond() - y, targetCoords.getFirst() - x);

            if(distance(x, y, targetCoords.getFirst(), targetCoords.getSecond()) < 10)
                direction = targetDirection;
            else
                rotateToTargetDirection(dt);

            // TODO может вынести то что выше в нижнюю часть?

            if(distance(x, y, targetCoords.getFirst(), targetCoords.getSecond()) < SPEED*dt)
            {
                updateCoords(targetCoords.getFirst(), targetCoords.getSecond());

                targetCoords = null;
                targetDirection = -1;

                if(isGoingHome)
                {
                    isGoingHome = false;
                    isAtHome = true;

                    if(foodEaten == 0)
                        isAlive = false;
                    else if(foodEaten == 2)
                        multiply();

                    return;
                }
                if(!isGoingHome && map.removeFood(x, y))
                    foodEaten++;

                if(foodEaten == 2)
                    goHome();
            }
            else
            {
                newX = x + SPEED * Math.cos(direction) * dt;
                newY = y + SPEED * Math.sin(direction) * dt;

                if(newX < 0 || newX > map.getWidth() || newY < 0 || newY > map.getHeight())
                    direction = (direction + Math.PI)%(Math.PI*2);
                else
                {
                    updateCoords(newX, newY);
                }
            }
        }
        else
        {
            if(targetDirection == -1)
                targetDirection = (direction + (Math.random()*Math.PI - Math.PI/2))%(Math.PI*2);

            rotateToTargetDirection(dt);

            newX = x + SPEED * Math.cos(direction) * dt;
            newY = y + SPEED * Math.sin(direction) * dt;

            if(newX < 0 || newX > map.getWidth() || newY < 0 || newY > map.getHeight()) {
                direction = (direction + Math.PI)%(Math.PI*2);
                targetDirection = direction;
            }
            else
            {
                updateCoords(newX, newY);
            }
        }
    }

    private void rotateToTargetDirection(double dt)
    {
        if(Math.abs(direction - targetDirection) < ROTATESPEED*dt)
        {
            direction = targetDirection;
            targetDirection = -1;
        }
        else
        {
            if(targetDirection - direction > Math.PI)
                direction = Math.PI*2 + direction;
            else if(targetDirection - direction < -Math.PI)
                targetDirection = Math.PI*2 + targetDirection;


            if(targetDirection - direction > 0)
                direction = (direction + ROTATESPEED*dt)%(Math.PI*2);
            else
                direction = (direction - ROTATESPEED*dt)%(Math.PI*2);
        }
    }

    private void multiply() {
        map.createMonster((int)x, (int)y);
    }

    public Pair<Integer, Integer> getCoords()
    {
        return new Pair<Integer, Integer>((int)(x), (int)(y));
    }

    public double getVisionDistance() {
        return visionDistance;
    }

    public double getDirection() {
        return direction;
    }

    public boolean isAlive()
    {
        return isAlive;
    }

    public void goHome()
    {
        isGoingHome = true;

        double nX = 0, nY = 0, w = map.getWidth(), h = map.getHeight();
        double left = x, right = w-x, up = y, down = h-y;
        double min = Math.min(Math.min(left, right), Math.min(up, down));

        if(left == min)
            nY = y;
        else if(up == min)
            nX = x;
        else if(right == min)
        {
            nY = y;
            nX = w;
        }
        else
        {
            nX = x;
            nY = h;
        }

        targetCoords = new Pair<>((int)nX, (int)nY);
    }

    public boolean isAtHome() {
        return isAtHome;
    }

    public void activate() {
        isAtHome = false;
        foodEaten = 0;
    }

    private void updateCoords(double x, double y)
    {
        this.x = x;
        this.y = y;
        if(countObservers() != 0)
        {
            setChanged();
            notifyObservers(new Event(EventType.Coords, new Pair<Integer, Integer>((int)x, (int)y)));
        }
    }

    private void updateDistanceToFood()
    {
        var foodCoords = map.getTarget((int)x, (int)y, Double.MAX_VALUE);
        double distance = 0;
        if(foodCoords != null)
            distance = distance(x, y, foodCoords.getFirst(), foodCoords.getSecond());

        setChanged();
        notifyObservers(new Event(EventType.Distance, distance));
    }

    public void setColor(Color color)
    {
        this.color = color;
    }

    public Color getColor()
    {
        return color;
    }
}
