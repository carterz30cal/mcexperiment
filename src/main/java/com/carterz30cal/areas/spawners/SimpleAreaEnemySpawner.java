package com.carterz30cal.areas.spawners;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.utils.Box;
import com.carterz30cal.utils.EntityUtils;
import com.carterz30cal.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class SimpleAreaEnemySpawner extends AbstractEnemySpawner {
    public static final int DEFAULT_SPAWN_TIMER = 20 * 3;

    protected List<SpawningOption> options = new ArrayList<>();
    protected Box spawnBox;
    protected int spawnTimer;
    protected List<UUID> mobs = new ArrayList<>();
    protected double spawnMultiplier = 1;

    private int spawnTick = 0;

    public SimpleAreaEnemySpawner(int x1, int y1, int z1, int x2, int y2, int z2) {
        spawnBox = new Box(x1, y1, z1, x2, y2, z2);
        spawnTimer = DEFAULT_SPAWN_TIMER;
    }

    public SimpleAreaEnemySpawner(int x1, int x2, int z1, int z2, int y) {
        spawnBox = new Box(x1, x2, z1, z2, y);
        spawnTimer = DEFAULT_SPAWN_TIMER;
    }

    public SimpleAreaEnemySpawner(int x1, int z1, int x2, int z2, int y, String... options) {
        spawnBox = new Box(x1, x2, z1, z2, y);
        spawnTimer = DEFAULT_SPAWN_TIMER;

        for (var o : options) {
            this.options.add(new SpawningOption(o, "NORMAL"));
        }
    }

    public SimpleAreaEnemySpawner(int x1, int z1, int x2, int z2, int y, double spawnMultiplier, String... options) {
        spawnBox = new Box(x1, x2, z1, z2, y);
        spawnTimer = DEFAULT_SPAWN_TIMER;
        this.spawnMultiplier = spawnMultiplier;

        options("NORMAL", options);
    }

    /**
     * Adds a set of options to a specific mode. Can be chained.
     * @param mode what mode are we targeting?
     * @param options what options are we adding for this mode?
     * @since 1.0.0
     */
    public SimpleAreaEnemySpawner options(String mode, String... options) {
        for (var o : options) {
            this.options.add(new SpawningOption(o, mode));
        }
        return this;
    }

    protected SpawningOption getValidSpawningOption() {
        List<SpawningOption> validOptions = new ArrayList<>();
        int totalWeight = 0;
        for (var option : options) {
            if (option.valid()) {
                validOptions.add(option);
                totalWeight += option.weight;
            }
        }
        if (validOptions.isEmpty() || totalWeight <= 0) {
            return null;
        }
        int randomWeight = RandomUtils.getRandom(1, totalWeight);
        for (var validOption : validOptions) {
            if (randomWeight <= validOption.weight) {
                return validOption;
            }
            else {
                randomWeight -= validOption.weight;
            }
        }
        throw new IllegalStateException("Somehow didn't pick a valid weight for spawning option?");
    }

    @Override
    public void tick() {
        spawnTick++;
        if (spawnTick >= spawnTimer && getCurrentlyValidToSpawn()) {
            int max = getPlayersWithinValidArea().size() + 2;
            while (mobs.size() < getMaxMobCount() && max > 0) {
                GameEnemy enemy = getValidSpawningOption().spawn(spawnBox.getRandomMobLocation());
                mobs.add(enemy.getUUID());
                max--;
            }
            spawnTick = 0;
        }
        mobs.removeIf((e) -> !GameEntity.entities.containsKey(e) || GameEntity.entities.get(e).dead);
        super.tick();
    }

    @Override
    public void onAreaKill(GameEnemy killed) {
        mobs.remove(killed.getUUID());
        super.onAreaKill(killed);
    }

    protected int getMaxMobCount() {
        int crossArea = spawnBox.getHorizontalCrossSectionalArea();
        return (int) Math.round((crossArea / 81D) * spawnMultiplier);
    }

    protected List<GamePlayer> getPlayersWithinValidArea() {
        return EntityUtils.getNearbyPlayers(spawnBox.getMiddleAsLocation(), spawnBox.getHorizontalLongestSide() + 4);
    }

    protected boolean getCurrentlyValidToSpawn() {
        List<GamePlayer> players = getPlayersWithinValidArea();
        return !players.isEmpty();
    }
}
