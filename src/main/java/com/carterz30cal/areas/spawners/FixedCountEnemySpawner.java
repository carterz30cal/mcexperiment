package com.carterz30cal.areas.spawners;

import org.bukkit.Location;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class FixedCountEnemySpawner extends SimpleAreaEnemySpawner {
    protected int count;

    public FixedCountEnemySpawner(Location location, int count, int expandRadius, String... options) {
        super(location, options);
        this.spawnBox = this.spawnBox.expand(expandRadius, 0, expandRadius);
        this.count = count;
        this.validRadius = 10;
        this.cullDead = false;
        this.spawnTimer = 4;
    }

    @Override
    protected boolean getCurrentlyValidToSpawn() {
        return true;
    }

    @Override
    protected int getMaxMobCount() {
        return count;
    }
}
