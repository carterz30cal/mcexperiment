package com.carterz30cal.areas.bosses;

/**
 * Framework for a pool of items with individual weights, used primarily for boss fight rewards.
 *
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class WeightedDrop {
    public int weight;
    public String data;

    public WeightedDrop(String data, int weight) {
        this.weight = weight;
        this.data = data;
    }
}
