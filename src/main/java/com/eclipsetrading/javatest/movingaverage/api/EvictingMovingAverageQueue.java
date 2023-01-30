package com.eclipsetrading.javatest.movingaverage.api;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class EvictingMovingAverageQueue<T> {
    private int size = 0;
    private int capacity;
    private double currSum;
    private LinkedBlockingDeque<Double> queue = new LinkedBlockingDeque<>();
    private Lock lock = new ReentrantLock();
    public EvictingMovingAverageQueue(int capacity){
        this.capacity =capacity;
    }

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
