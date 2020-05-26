package NatSelection;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.internal.PointDouble;
import com.github.davidmoten.rtree.internal.EntryDefault;

import java.util.ArrayList;

public class FoodGenerator implements Runnable {
    private volatile boolean isActive = false;
    private double food_expRatePoisson = 0;
    private final String foodLock = "";
    private RTree<Nothing, Point> foodCoords = RTree.create();
    NSMap map;

    private Thread foodThread;

    public FoodGenerator(NSMap map) {
        this.map = map;
        foodThread = new Thread(this);
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
        ArrayList<Entry<Nothing, Point>> entries = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            int x = (int) (Math.random() * (map.getWidth()));
            int y = (int) (Math.random() * (map.getHeight()));
            var entry = new EntryDefault<Nothing, Point>(Nothing.getInstance(), PointDouble.create(x, y));
            entries.add(entry);
        }

        foodCoords = foodCoords.add(entries);
    }

    public Point getTarget(int x, int y, double distance) {
        Entry<Nothing, Point> target;
        target = foodCoords.nearest(PointDouble.create(x, y), map.maxDist, 1)
                .toBlocking()
                .singleOrDefault(null);
        if (target != null)
            return target.geometry();
        else
            return null;
    }

    public boolean removeFood(double x, double y) {
        var point = PointDouble.create(x, y);
        var found = foodCoords.search(point).toBlocking().singleOrDefault(null);
        if (found == null)
            return false;
        foodCoords = foodCoords.delete(found);
        return true;
    }

    public RTree<Nothing, Point> getFoodCoords()
    {
        return foodCoords;
    }

    public void setFoodCoeff(double foodCountInSecond)
    {
        food_expRatePoisson = Math.exp(-foodCountInSecond);
    }

    public void restart()
    {
        foodCoords = RTree.create();
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
