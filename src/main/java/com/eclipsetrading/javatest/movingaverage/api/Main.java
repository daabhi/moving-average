package com.eclipsetrading.javatest.movingaverage.api;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final ScheduledExecutorService producerService = Executors.newSingleThreadScheduledExecutor();
    private static final ScheduledExecutorService producerService2 = Executors.newSingleThreadScheduledExecutor();
    private static final ScheduledExecutorService consumerService = Executors.newSingleThreadScheduledExecutor();
    private static final ScheduledExecutorService consumerService2 = Executors.newSingleThreadScheduledExecutor();
    private static final ScheduledExecutorService consumerSnapshotService = Executors.newSingleThreadScheduledExecutor();
    private static final Random random = new Random();
    private static final MovingAverageStore movingAverageStore = new MovingAverageStoreImpl(100);
    public static void main(String[] args) {
        producerService.scheduleAtFixedRate(()-> movingAverageStore.addSample("P1",1.0+ random.nextInt(100)),0,1, TimeUnit.MILLISECONDS);
        producerService2.scheduleAtFixedRate(()-> movingAverageStore.addSample("P2",2.0+ random.nextInt(100)),0,1, TimeUnit.MILLISECONDS);
        consumerService.scheduleAtFixedRate(()-> movingAverageStore.getMovingAverage("P1"),0,1, TimeUnit.SECONDS);
        consumerService2.scheduleAtFixedRate(()-> movingAverageStore.getMovingAverage("P2"),0,1, TimeUnit.SECONDS);
        consumerSnapshotService.scheduleAtFixedRate(movingAverageStore::getMovingAverages,0,5, TimeUnit.SECONDS);
    }
}
