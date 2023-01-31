package com.eclipsetrading.javatest.movingaverage.api;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class EvictingMovingAverageQueue<T> {
    private int size = 0;
    private final int capacity;
    private double currSum;
    private double currAvg;
    private final ArrayBlockingQueue<Double> queue;
    private final Lock lock = new ReentrantLock();
    public EvictingMovingAverageQueue(int capacity){
        this.capacity = capacity;
        this.queue    = new ArrayBlockingQueue<>(capacity);
    }

    /**
     * As the values are inserted at the tail of the arrayBlockingQueue always it is O(1)
     * When the size reaches the capacity, then the head is removed which is again O(1)
     * The currAvg is eagerly maintained so that the average can be quickly computed when requested.
     * The rationale behind doing this is based on the assumption that the ratio of offer and getCurrentMovingAverage is 1:1.
     * Under the above assumption, it is faster to compute the avg incrementally as opposed to on demand.
     * If however the reality is that the getCurrentMovingAverage is called half or less than half the times offer is called, it maybe fine to compute avg lazily on demand as well.
     * @param value
     */
    public void offer(double value){
        lock.lock();
        double valueRemoved = 0;
        queue.offer(value);
        size++;
        if(size > capacity){
            valueRemoved = queue.remove();
            size--;
        }
        currSum+=(value-valueRemoved);
        currAvg = currSum/size;
        lock.unlock();
    }

    public double getCurrMovingAverage(){
        return currAvg;
    }
}
