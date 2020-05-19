package NatSelection;

import java.util.ArrayList;

public class FoodGenerator implements Runnable {
    private volatile boolean isActive = false;
    private double food_expRatePoisson = 0;
    private final ArrayList<Pair<Integer, Integer>> foodCoords = new ArrayList<>();
    NSMap map;

    private Thread foodThread;

    public FoodGenerator(NSMap map) {
        this.map = map;

        foodThread  = new Thread(this);
        foodThread.start();
    }

    private int PoissonKnuth() {
        int k = 0;
        double prod = 1;
        while (prod > food_expRatePoisson) {
            prod *= Math.random();
            ++k;
        }
        return k;
    }

    private void createFood()
    {
        int num = PoissonKnuth();

        synchronized (foodCoords) {
            for (int i = 0; i < num; i++) {
                foodCoords.add(new Pair<Integer, Integer>((int) (Math.random() * (map.getWidth())), (int) (Math.random() * (map.getHeight()))));
            }
        }
    }

    private static double distance(double x1, double y1, double x2, double y2)
    {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    public Pair<Integer, Integer> getTarget(int x, int y, double distance) {        // TODO оптимизация - поиск по секторам
        double minDist = distance, dist;
        int foodNum = -1;

        synchronized (foodCoords)
        {
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
    }

    public boolean removeFood(double x, double y) {
        synchronized (foodCoords) {
            for (int i = 0; i < foodCoords.size(); i++) {
                if (foodCoords.get(i).getFirst() == x && foodCoords.get(i).getSecond() == y) {
                    foodCoords.remove(i);
                    return true;
                }
            }
            return false;
        }
    }

    public ArrayList<Pair<Integer, Integer>> getFoodCoords()
    {
        synchronized (foodCoords) {
            ArrayList<Pair<Integer, Integer>> clone = new ArrayList<Pair<Integer, Integer>>(foodCoords.size());
            for (Pair<Integer, Integer> item : foodCoords) clone.add(new Pair<>(item.getFirst(), item.getSecond()));
            return clone;
        }
    }

    public void setFoodCoeff(double foodCountInSecond)
    {
        food_expRatePoisson = Math.exp(-foodCountInSecond);
    }

    public void restart()
    {
        foodCoords.clear();
        isActive = true;
    }

    public void start()
    {
        isActive = true;
    }

    public void stop()
    {
        isActive = false;
    }

    @Override
    public void run() {
        while(true)
        {
            if(isActive)
            {
                createFood();
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {}
            }
        }
    }
}
