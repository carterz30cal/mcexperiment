package com.carterz30cal.areas;

import com.carterz30cal.areas.bosses.AbstractAreaBoss;
import com.carterz30cal.areas.spawners.AbstractEnemySpawner;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.utils.Box;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 3
 * @since 1.0.0
 */
public abstract class AbstractGameArea {
    public Areas parent;
    protected Box boundingBox;
    protected String areaName;
    protected SpawnerContext context;
    protected List<AbstractEnemySpawner> registeredSpawners = new ArrayList<>();
    protected List<AbstractAreaBoss> registeredBosses = new ArrayList<>();
    private final List<AbstractEnemySpawner> toRegister = new ArrayList<>();
    private final List<AbstractEnemySpawner> toRemove = new ArrayList<>();


    public void tick() {
        for (var spawner : registeredSpawners) {
            spawner.tick();
        }
        registeredSpawners.addAll(toRegister);
        registeredSpawners.removeAll(toRemove);
        toRegister.clear();
        toRemove.clear();
    }

    public void onKill(GameEnemy enemy) {
        for (var spawner : registeredSpawners) {
            spawner.onAreaKill(enemy);
        }
    }

    public void onPlayerDeath(GamePlayer player) {
        for (var boss : registeredBosses) {
            boss.onLeftFight(player, AbstractAreaBoss.LeftFightReason.DEATH);
        }
    }

    public void register(AbstractEnemySpawner spawner) {
        if (spawner instanceof AbstractAreaBoss boss) {
            registeredBosses.add(boss);
        }
        toRegister.add(spawner);
        spawner.register(this);
    }

    /**
     *
     * @param spawner
     * @since 1.0.0 [3]
     */
    public void deregister(AbstractEnemySpawner spawner) {
        if (spawner instanceof AbstractAreaBoss boss) {
            registeredBosses.remove(boss);
        }
        toRemove.add(spawner);
    }

    /**
     *
     * @param player   Guaranteed to be from within the area.
     * @param location Not guaranteed to be within bounds.
     * @since 1.0.0
     */
    public void onRightClick(GamePlayer player, Location location) {
        for (var boss : registeredBosses) {
            boss.onRightClick(player, location);
        }
    }

    public void onTeleport(GamePlayer player, PlayerTeleport teleport) {
        for (var boss : registeredBosses) {
            boss.onLeftFight(player, AbstractAreaBoss.LeftFightReason.TELEPORTED);
        }
    }

    public List<String> scoreboard(GamePlayer player) {
        List<String> list = new ArrayList<>();
        for (var boss : registeredBosses) {
            var s = boss.scoreboard(player);
            if (s != null) {
                list.addAll(s);
            }
        }
        return list;
    }

    /**
     * Called when the server shuts down
     *
     * @since 1.0.0 [3]
     */
    public void disable() {
        for (var boss : registeredBosses) {
            boss.disable();
        }
    }


    public String getSubAreaName(GamePlayer player) {
        return areaName;
    }

    public abstract PlayerTeleport getRespawnPoint(GamePlayer died);

    public boolean isInBounds(GameEntity entity) {
        return boundingBox.isWithin(entity.getLocation());
    }

    public boolean isInBounds(GamePlayer player) {
        return isInBounds((GameEntity) player);
    }

    public SpawnerContext context() {
        return context;
    }

    public static class SpawnerContext {
        public String spawningMode = "NORMAL";
    }
}
