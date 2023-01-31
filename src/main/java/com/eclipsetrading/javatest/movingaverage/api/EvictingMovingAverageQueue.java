package com.eclipsetrading.javatest.movingaverage.api;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class EvictingMovingAverageQueue<T> {
    private int size = 0;
    private final int capacity;
    private double currSum;
    private final ArrayBlockingQueue<Double> queue;
    private final Lock lock = new ReentrantLock();
    public EvictingMovingAverageQueue(int capacity){
        this.capacity = capacity;
        this.queue    = new ArrayBlockingQueue<>(capacity);
    }

    /**
     * As the values are inserted at the tail of the arrayBlockingQueue always it is O(1)
     * When the size reaches the capacity, then the head is removed which is again O(1)
     * The currSum is eagerly maintained so that the average can be quickly computed when requested
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
        lock.unlock();
    }

    public double getCurrMovingAverage(){
        return currSum/size;
    }
}
