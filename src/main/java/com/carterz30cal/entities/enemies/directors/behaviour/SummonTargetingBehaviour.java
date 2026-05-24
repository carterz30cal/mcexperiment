package com.carterz30cal.entities.enemies.directors.behaviour;

import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.player.GamePlayer;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class SummonTargetingBehaviour extends SimpleTargetingBehaviour {
    private final GamePlayer owner;

    public SummonTargetingBehaviour(GamePlayer owner) {
        super(true);
        this.owner = owner;
    }

    @Override
    public LivingEntity findTarget(GameEnemy brain, Location location) {
        if (owner.getLocation().distance(location) > 12) {
            return owner.getTargetableEntity();
        }
        else {
            return super.findTarget(brain, location);
        }
    }
}
