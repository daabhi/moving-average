package com.eclipsetrading.javatest.movingaverage.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The moving average store has a concurrent hash map to enable multiple producers to update its own evicting moving average queue concurrently
 * EvictingMovingAverage queue eagerly maintains the currSum so that the getMovingAverage as well as getMovingAverages can be computed very efficiently.
 * The underlying thought process is that the frequency of consumers trying to getMovingAverage and getMovingAverages
 * is almost proportional to the frequency of multiple producers maintaining the moving average. Given maintaining the currAvg is not very intensive operation it helps to speed up consumer.
 * If however there is a use case that the producer is exponentially faster and the consumer very occasionally calls getMovingAverage or getMovingAverages() then it makes sense for the consumer to lazily compute it in which
 * case the producer is relieved of that additional maintenance.
 */
public class MovingAverageStoreImpl implements MovingAverageStore {
    private final int size;
    private final Map<String, EvictingMovingAverageQueue<Double>> movingAverageMap;
    public MovingAverageStoreImpl(int size) {
        this.size             = size;
        this.movingAverageMap = new ConcurrentHashMap<>(size);
    }

    @Override
    public void addSample(String producer, double value) {
        Objects.requireNonNull(producer);
        movingAverageMap.computeIfAbsent(producer, k->new EvictingMovingAverageQueue<>(size)).offer(value);
    }

    @Override
    public double getMovingAverage(String producer) {
        if (producer == null) return Double.NaN;
        if (movingAverageMap.containsKey(producer)) {
            return movingAverageMap.get(producer).getCurrMovingAverage();
        }else{
            return Double.NaN;
        }
    }

    @Override
    public Map<String, Double> getMovingAverages() {
        Map<String, Double> snapshotMovingAverages = new HashMap<>();
        movingAverageMap.forEach((k,v)-> snapshotMovingAverages.put(k,v.getCurrMovingAverage()));
        return snapshotMovingAverages;
    }
}
