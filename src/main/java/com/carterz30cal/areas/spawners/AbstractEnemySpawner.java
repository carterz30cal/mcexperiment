package com.carterz30cal.areas.spawners;

import com.carterz30cal.areas.AbstractGameArea;
import com.carterz30cal.entities.enemies.core.EnemyManager;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public abstract class AbstractEnemySpawner {
    protected AbstractGameArea parent;

    public void tick() {

    }

    public void onAreaKill(GameEnemy killed) {

    }

    public void register(AbstractGameArea parent) {
        this.parent = parent;
    }

    /**
     * @author carterz30cal
     * @version 2
     * @since 1.0.0
     */
    protected class SpawningOption {
        public String mob;
        public int weight;
        public List<String> modes;

        public SpawningOption(String mob, String mode) {
            this.mob = mob;
            this.modes = new ArrayList<>();
            this.weight = 1;
            this.modes.add(mode);
        }

        public SpawningOption(String mob, int weight) {
            this.mob = mob;
            this.weight = weight;
        }

        public SpawningOption(String mob, int weight, String mode) {
            this.mob = mob;
            this.weight = weight;
            this.modes = new ArrayList<>();
        }

        public SpawningOption(String mob, int weight, String... modes) {
            this.mob = mob;
            this.weight = weight;
            this.modes = new ArrayList<>();
            this.modes.addAll(Arrays.asList(modes));
        }

        public SpawningOption(String mob, int weight, List<String> modes) {
            this.mob = mob;
            this.weight = weight;
            this.modes = modes;
        }

        public GameEnemy spawn(Location location) {
            GameEnemy enemy = EnemyManager.spawn(this.mob, location);
            enemy.spawnedArea = parent;
            return enemy;
        }

        public boolean valid() {
            return modes.contains(parent.context().spawningMode);
        }
    }
}
