package NatSelection;

public class Monster {

    private final double SPEED = 0.05;
    private double x, y, direction;
    private Pair<Integer, Integer> targetCoords;
    private NSMap map;

    public Monster(NSMap nsMap)
    {
        x = 100;
        y = 100;
        direction = Math.random()*Math.PI*2;
        map = nsMap;

        //targetCoords = null;
        targetCoords = new Pair<>(200, 200);
    }

    private static double distance(double x1, double y1, double x2, double y2)
    {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    public void update(double dt)
    {
        /*
        Чекает еду в зоне видимости
        Если есть - идет к ней
        Если нет - идет рандомно. (Точнее в +- том направлении что двигался. И если врезался - то в обратном)
         */

        if(targetCoords == null)
        {
            targetCoords = map.getTarget((int)x, (int)y);
            if(targetCoords != null)
                System.out.println("yep");
        }


        if(targetCoords != null)
        {
            System.out.println(targetCoords.getFirst() + " " + targetCoords.getSecond());

            direction = Math.atan2(targetCoords.getSecond() - y, targetCoords.getFirst() - x);

            if(distance(x, y, targetCoords.getFirst(), targetCoords.getSecond()) < SPEED*dt)
            {
                x = targetCoords.getFirst();
                y = targetCoords.getSecond();

                map.removeFood(x, y);

                targetCoords = null;
            }
            else
            {
                // TODO Добавить проверку на выход за границы экрана
                x += SPEED * Math.cos(direction) * dt;
                y += SPEED * Math.sin(direction) * dt;
            }
        }
        else
        {
            direction += (Math.random()*Math.PI) - Math.PI/2;

            // TODO Добавить проверку на выход за границы экрана
            x += SPEED * Math.cos(direction) * dt;
            y += SPEED * Math.sin(direction) * dt;
        }

    }

    public Pair<Integer, Integer> getCoords()
    {
        return new Pair<Integer, Integer>((int)(x), (int)(y));
    }

    public double getVisionDistance() {
        return 60;
    }
}
