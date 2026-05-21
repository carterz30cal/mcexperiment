package com.carterz30cal.entities.enemies.directors.behaviour;

import com.carterz30cal.entities.GameSummon;
import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.utils.EntityUtils;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class SimpleTargetingBehaviour implements TargetingBehaviour {
    private final boolean ignoresTargetLimit;
    private final GameEnemy owner;

    public SimpleTargetingBehaviour(GameEnemy owner, boolean ignoresTargetLimit) {
        this.ignoresTargetLimit = ignoresTargetLimit;
        this.owner = owner;
    }

    @Override
    public LivingEntity findTarget(GameEnemy owner, Location location) {
        List<GameEnemy> enemies = EntityUtils.getNearbyEnemies(location, 14);
        enemies.removeIf((e) -> !(e instanceof GameSummon));
        if (!enemies.isEmpty()) {
            return enemies.getFirst().getTargetableEntity();
        }
        else {
            for (var player : PlayerManager.getOnlinePlayers()) {
                if (player.getLocation().distance(location) > player.stats.getStat(Stat.VISIBILITY)) {
                    continue;
                }
                if (player.targeted.size() >= player.getMaxTargets() && !ignoresTargetLimit) {
                    continue;
                }
                player.targeted.add(owner);
                return player.player;
            }
            return null;
        }
    }
}
