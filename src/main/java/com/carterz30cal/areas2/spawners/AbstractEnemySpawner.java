package com.carterz30cal.areas2.spawners;

import com.carterz30cal.areas2.AbstractGameArea;
import com.carterz30cal.entities.EnemyManager;
import com.carterz30cal.entities.GameEnemy;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractEnemySpawner {
    protected AbstractGameArea parent;

    public void tick() {

    }

    public void onAreaKill(GameEnemy killed) {

    }

    public void RegisterParent(AbstractGameArea parent) {
        this.parent = parent;
    }


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

        public GameEnemy Spawn(Location location) {
            GameEnemy enemy = EnemyManager.spawn(this.mob, location);
            enemy.spawnedArea = parent;
            return enemy;
        }

        public boolean IsValid() {
            return modes.contains(parent.GetContext().spawningMode);
        }
    }
}
