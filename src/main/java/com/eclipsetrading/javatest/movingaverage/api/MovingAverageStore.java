package com.eclipsetrading.javatest.movingaverage.api;

import java.util.Map;

/**
 * This data class stores samples for different statistics producers and summarizes with moving average with the last 100 samples.
 * <p/>
 * Implementation of the store <strong>MUST</strong> be thread-safe to allow concurrent updates from multiple producers and snapshots from statistics consumers.
 */
public interface MovingAverageStore {

    /**
     * Add a sample from the specific producer
     * 
     * @param producer,
     *            not null
     * @param value
     */
    void addSample(String producer, double value);

    /**
     * Obtain a snapshot of moving average of the specific producer.
     * <p>
     * If number of collected samples is less than 100, the average should be calculated from the collected samples.
     * </p>
     * 
     * @param producer
     * @return moving average, {@link Double.NaN} if not available
     */
    double getMovingAverage(String producer);

    /**
     * Obtain a snapshot of moving averages of all producers.
     * <p>
     * If number of collected samples is less than 100, the average should be calculated from the collected samples.
     * </p>
     */
    Map<String, Double> getMovingAverages();
}
