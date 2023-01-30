package com.eclipsetrading.javatest.movingaverage.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MovingAverageStoreImpl implements MovingAverageStore {
    private final int size;
    private final Map<String, LinkedBlockingDeque<Double>> movingAverageMap;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public MovingAverageStoreImpl(int size) {
        this.size             = size;
        this.movingAverageMap = new ConcurrentHashMap<>(size);
    }

    @Override
    public void addSample(String producer, double value) {
        Objects.requireNonNull(producer);
        readWriteLock.writeLock().lock();
        if (movingAverageMap.containsKey(producer)) {
            LinkedBlockingDeque<Double> values = movingAverageMap.get(producer);
            if (values.size() < size) {
                values.add(value);
            } else {
                double firstValue = values.peekFirst();
                values.removeFirst();
                values.add(value);
            }
        } else {
            LinkedBlockingDeque<Double> queue = new LinkedBlockingDeque<>(size);
            queue.add(value);
            movingAverageMap.put(producer, queue);
        }
        readWriteLock.writeLock().unlock();
    }

    @Override
    public double getMovingAverage(String producer) {
        return movingAverageMap.get(producer).stream().mapToDouble(a -> a).average().stream().findFirst().orElse(0d);
    }

    @Override
    public Map<String, Double> getMovingAverages() {
        Map<String, Double> snapshotMovingAverages = new HashMap<>();
        movingAverageMap.forEach((k, v) -> snapshotMovingAverages.put(k, getMovingAverage(k)));
        return snapshotMovingAverages;
    }
}
