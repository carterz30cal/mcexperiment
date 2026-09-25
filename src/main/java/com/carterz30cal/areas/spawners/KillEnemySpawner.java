package com.carterz30cal.areas.spawners;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.utils.EntityUtils;

import java.util.List;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class KillEnemySpawner extends SimpleAreaEnemySpawner {
    protected int killsToSpawn;
    private int killCount;

    public KillEnemySpawner(String mob, int killCount, int x, int y, int z) {
        super(x - 1, x + 1, z - 1, z + 1, y);
        options.add(new SpawningOption(mob, "NORMAL"));
        killsToSpawn = killCount;
        this.killCount = 0;
    }

    @Override
    public void tick() {
        mobs.removeIf((e) -> !GameEntity.entities.containsKey(e) || GameEntity.entities.get(e).dead);
        if (killCount >= killsToSpawn && getCurrentlyValidToSpawn()) {
            killCount -= killsToSpawn;
            GameEnemy enemy = getValidSpawningOption().spawn(spawnBox.getRandomMobLocation());
            mobs.add(enemy.getUUID());
        }
    }

    @Override
    protected boolean getCurrentlyValidToSpawn() {
        List<GamePlayer> players = EntityUtils.getNearbyPlayers(spawnBox.getMiddleAsLocation(), 20);
        return !players.isEmpty() && mobs.isEmpty();
    }

    @Override
    public void onAreaKill(GameEnemy killed) {
        if (containsType(killed.getTypeId())) {
            return;
        }
        killCount++;
        super.onAreaKill(killed);
    }
}
