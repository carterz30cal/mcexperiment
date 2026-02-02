package com.carterz30cal.areas2;

import com.carterz30cal.areas2.bosses.AbstractAreaBoss;
import com.carterz30cal.areas2.spawners.AbstractEnemySpawner;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.utils.Box;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGameArea {
    public Areas parent;
    protected Box boundingBox;
    protected String areaName;
    protected SpawnerContext context;
    protected List<AbstractEnemySpawner> registeredSpawners = new ArrayList<>();
    protected List<AbstractAreaBoss> registeredBosses = new ArrayList<>();


    public void Tick() {
        for (var spawner : registeredSpawners) {
            spawner.tick();
        }
    }

    public void OnKill(GameEnemy enemy) {
        for (var spawner : registeredSpawners) {
            spawner.onAreaKill(enemy);
        }
    }

    public void OnPlayerDeath(GamePlayer player) {
        for (var boss : registeredBosses) {
            boss.OnPlayerDeath(player);
        }
    }

    public void RegisterSpawner(AbstractEnemySpawner spawner) {
        registeredSpawners.add(spawner);
        spawner.RegisterParent(this);
    }

    public void RegisterBoss(AbstractAreaBoss boss) {
        registeredBosses.add(boss);
        RegisterSpawner(boss);
    }

    /**
     *
     * @param player   Guaranteed to be from within the area.
     * @param location Not guaranteed to be within bounds.
     * @since 1.0.0
     */
    public void OnRightClick(GamePlayer player, Location location) {
        for (var boss : registeredBosses) {
            boss.OnRightClick(player, location);
        }
    }

    public void OnTeleport(GamePlayer player, PlayerTeleport teleport) {
        for (var boss : registeredBosses) {
            boss.OnTeleport(player, teleport);
        }
    }

    public List<String> GetScoreboard(GamePlayer player) {
        List<String> list = new ArrayList<>();
        for (var boss : registeredBosses) {
            if (!boss.IsPlayerInvolved(player)) {
                continue;
            }
            list.addAll(boss.GetScoreboard(player));
        }
        return list;
    }


    public String GetSubAreaName(GamePlayer player) {
        return areaName;
    }

    public abstract PlayerTeleport GetRespawnPoint(GamePlayer died);

    public boolean IsInBounds(GameEntity entity) {
        return boundingBox.IsWithin(entity.getLocation());
    }

    public boolean IsInBounds(GamePlayer player) {
        return IsInBounds((GameEntity) player);
    }

    public SpawnerContext GetContext() {
        return context;
    }

    public static class SpawnerContext {
        public String spawningMode = "NORMAL";
    }
}
