import com.eclipsetrading.javatest.movingaverage.api.MovingAverageStore;
import com.eclipsetrading.javatest.movingaverage.api.MovingAverageStoreImpl;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class MovingAverageStoreImplTest {
    @BeforeAll
    public static void setup(){
        List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
        loggers.add(LogManager.getRootLogger());
        for (Logger logger : loggers) {
            logger.setLevel(Level.OFF);
        }
    }
    @Test
    public void testBasicReturnsMovingAverageWithWhateverIsThereIfLessThanConfiguredSize(){
        MovingAverageStore movingAverageStore = new MovingAverageStoreImpl(100);
        movingAverageStore.addSample("P1",1.0d);
        movingAverageStore.addSample("P1",2.0d);
        movingAverageStore.addSample("P1",3.0d);
        movingAverageStore.addSample("P2",3.0d);
        movingAverageStore.addSample("P2",3.0d);

        Assertions.assertEquals(2, movingAverageStore.getMovingAverage("P1"));
        Assertions.assertEquals(3, movingAverageStore.getMovingAverage("P2"));
        Assertions.assertEquals("{P1=2.0, P2=3.0}", movingAverageStore.getMovingAverages().toString());
    }

    @Test
    public void testPerformance(){
        MovingAverageStore movingAverageStore = new MovingAverageStoreImpl(100);
        long startTime = System.currentTimeMillis();
        IntStream.range(0,1000000).forEach(a-> movingAverageStore.addSample("P1",a));
        long duration = System.currentTimeMillis()-startTime;
        Assertions.assertEquals(400, duration, 400, "Inserted 1mill records for moving average in ~400 ms");

        long consumerStartTime = System.currentTimeMillis();
        IntStream.range(0,1000000).forEach(a->movingAverageStore.getMovingAverage("P1"));
        long consumerDuration = System.currentTimeMillis() - consumerStartTime;
        Assertions.assertEquals(100, consumerDuration, 100, "Queried 1mill times for moving average in ~200 ms");

        long consumerSnapStartTime = System.currentTimeMillis();
        IntStream.range(0,1000000).forEach(a->movingAverageStore.getMovingAverages());
        long consumerSnapDuration = System.currentTimeMillis() - consumerSnapStartTime;
        Assertions.assertEquals(500, consumerSnapDuration, 500, "Queried 1mill times for moving average snapshot in ~500 ms");
    }

    @Test
    public void testProducerNotFoundWhenSearchingMovingAverage(){
        MovingAverageStore movingAverageStore = new MovingAverageStoreImpl(100);
        movingAverageStore.addSample("P1",1.0d);
        Assertions.assertEquals(Double.NaN, movingAverageStore.getMovingAverage("P2"));
        Assertions.assertEquals(Double.NaN, movingAverageStore.getMovingAverage(null));
    }
    @Test
    public void testEmptyWhenSearchingMovingAverage(){
        MovingAverageStore movingAverageStore = new MovingAverageStoreImpl(100);
        Assertions.assertEquals(Double.NaN, movingAverageStore.getMovingAverage("P2"));
        Assertions.assertEquals(Double.NaN, movingAverageStore.getMovingAverage(null));
        Assertions.assertEquals("{}", movingAverageStore.getMovingAverages().toString());
    }
    @Test
    public void testEvictionAfterSizeBreached(){
        MovingAverageStore movingAverageStore = new MovingAverageStoreImpl(100);
        IntStream.range(0, 100).forEach(i -> movingAverageStore.addSample("P1", 1));
        movingAverageStore.addSample("P1",10);

        Assertions.assertEquals(1.09, movingAverageStore.getMovingAverage("P1"));
        Assertions.assertEquals("{P1=1.09}", movingAverageStore.getMovingAverages().toString());

        movingAverageStore.addSample("P1",100);
        Assertions.assertEquals("{P1=2.08}", movingAverageStore.getMovingAverages().toString());
    }

    @Test
    public void testMultipleProducerConsumer() throws InterruptedException {
        MovingAverageStore movingAverageStore = new MovingAverageStoreImpl(100);

        Thread p1 = new Thread(() -> movingAverageStore.addSample("P1", 1.0));
        Thread p2 = new Thread(() -> movingAverageStore.addSample("P2", 2.0));
        p1.start();
        p2.start();
        p1.join();
        p2.join();
        Thread c1 = new Thread(() -> Assertions.assertEquals("{P1=1.0, P2=2.0}",movingAverageStore.getMovingAverages().toString()));
        Thread c2 = new Thread(() -> Assertions.assertEquals(1,movingAverageStore.getMovingAverage("P1")));
        Thread c3 = new Thread(() -> Assertions.assertEquals(2,movingAverageStore.getMovingAverage("P2")));
        Thread c4 = new Thread(() -> Assertions.assertEquals("{P1=1.0, P2=2.0}",movingAverageStore.getMovingAverages().toString()));

        c1.start();
        c2.start();
        c3.start();
        c4.start();

        c1.join();
        c2.join();
        c3.join();
        c4.join();
    }
}
