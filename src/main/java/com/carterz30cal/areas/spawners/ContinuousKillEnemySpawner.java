package com.carterz30cal.areas.spawners;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.utils.EntityUtils;

import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class ContinuousKillEnemySpawner extends KillEnemySpawner {
    protected int cap;

    public ContinuousKillEnemySpawner(String mob, int killCount, int capSpawns, int x, int y, int z) {
        super(mob, killCount, x, y, z);
        this.cap = capSpawns;
    }

    @Override
    protected boolean getCurrentlyValidToSpawn() {
        List<GamePlayer> players = EntityUtils.getNearbyPlayers(spawnBox.getMiddleAsLocation(), 20);
        return !players.isEmpty() && mobs.size() < cap;
    }
}
