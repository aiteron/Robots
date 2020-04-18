package NatSelection;

public class Monster {

    private final double SPEED = 0.1;
    private double x, y, direction;
    private Pair<Integer, Integer> targetCoords;

    public Monster()
    {
        x = 0;
        y = 0;
        direction = Math.random()*Math.PI*2;

        targetCoords = null;
    }

    public void update(double dt)
    {
        /*
        Чекает еду в зоне видимости
        Если есть - идет к ней
        Если нет - идет рандомно. (Точнее в +- том направлении что двигался. И если врезался - то в обратном)
         */

        //targetCoords = getTarget();

        if(targetCoords != null)
        {

        }
        else
        {
            direction += (Math.random()*Math.PI) - Math.PI/2;
        }

        x += SPEED * Math.cos(direction) * dt;
        y += SPEED * Math.sin(direction) * dt;
    }

    public Pair<Integer, Integer> getCoords()
    {
        return new Pair<Integer, Integer>((int)(x), (int)(y));
    }
}
