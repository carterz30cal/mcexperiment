package com.carterz30cal.areas2.spawners;

import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.utils.EntityUtils;

import java.util.List;

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
        if (killCount >= killsToSpawn && GetCurrentlyValidToSpawn() && mobs.isEmpty()) {
            killCount = 0;
            GameEnemy enemy = GetValidSpawningOption().Spawn(spawnBox.GetRandomMobLocation());
            mobs.add(enemy.GetUUID());
        }
    }

    @Override
    protected boolean GetCurrentlyValidToSpawn() {
        List<GamePlayer> players = EntityUtils.getNearbyPlayers(spawnBox.GetMiddleAsLocation(), 20);
        return !players.isEmpty();
    }

    @Override
    public void onAreaKill(GameEnemy killed) {
        killCount++;
        super.onAreaKill(killed);
    }
}
