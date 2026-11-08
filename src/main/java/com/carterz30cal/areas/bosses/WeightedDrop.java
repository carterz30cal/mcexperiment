package com.carterz30cal.areas.bosses;

/**
 * Framework for a pool of items with individual weights, used primarily for boss fight rewards.
 *
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class WeightedDrop {
    public int weight;
    public String data;

    public WeightedDrop(String data, int weight) {
        this.weight = weight;
        this.data = data;
    }

    /**
     * Constructor with a default weight of <code>1</code>.
     *
     * @param data item data
     * @since 1.0.0 [2]
     */
    public WeightedDrop(String data) {
        this.weight = 1;
        this.data = data;
    }
}
