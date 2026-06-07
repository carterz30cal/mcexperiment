package com.carterz30cal.entities.enemies.directors.behaviour;

import com.carterz30cal.areas.bosses.AbstractAreaBoss;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.utils.EntityUtils;
import com.carterz30cal.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/**
 * Targeting behaviour for mobs in a boss room that should always have a target
 *
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class BossRoomTargetingBehaviour implements TargetingBehaviour {
    /**
     * The boss that owns this targeting behaviour. Targeting will only look
     * at players on the register for this boss encounter.
     *
     * @since 1.0.0
     */
    private final AbstractAreaBoss boss;

    public BossRoomTargetingBehaviour(AbstractAreaBoss boss) {
        this.boss = boss;
    }

    @Override
    public LivingEntity findTarget(GameEnemy brain, Location location) {
        List<GameEnemy> enemies = EntityUtils.getNearbyEnemies(location, 14);
        enemies.removeIf((e) -> !e.isTargetable(brain));
        if (!enemies.isEmpty()) {
            return enemies.getFirst().getTargetableEntity();
        }
        else {
            if (boss.registered().isEmpty()) {
                return null;
            }
            else {
                return RandomUtils.getChoice(boss.registered()).player;
            }
        }
    }
}
