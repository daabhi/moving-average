import com.eclipsetrading.javatest.movingaverage.api.MovingAverageStore;
import com.eclipsetrading.javatest.movingaverage.api.MovingAverageStoreImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MovingAverageStoreImplTest {
    private MovingAverageStore movingAverageStore = new MovingAverageStoreImpl(100);
    @Test
    public void testBasic(){
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
    public void testSize(){
        for(int i=0;i<=100;i++) {
            movingAverageStore.addSample("P1", 1);
        }
        movingAverageStore.addSample("P1",10);

        Assertions.assertEquals(1.09, movingAverageStore.getMovingAverage("P1"));
        Assertions.assertEquals("{P1=1.09}", movingAverageStore.getMovingAverages().toString());

        movingAverageStore.addSample("P1",100);
        Assertions.assertEquals("{P1=2.08}", movingAverageStore.getMovingAverages().toString());
    }
}
