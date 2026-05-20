package com.carterz30cal.entities.enemies.directors.behaviour;

import com.carterz30cal.entities.GameSummon;
import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.utils.EntityUtils;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.List;

public class SimpleTargetingBehaviour implements TargetingBehaviour {
    private boolean ignoresTargetLimit;

    public SimpleTargetingBehaviour(boolean ignoresTargetLimit) {
        this.ignoresTargetLimit = ignoresTargetLimit;
    }

    @Override
    public LivingEntity findTarget(Location location) {
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
                player.targeted.add(this);
                return player;
            }
            return null;
        }
        return null;
    }
}
