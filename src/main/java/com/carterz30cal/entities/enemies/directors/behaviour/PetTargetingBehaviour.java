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
public class PetTargetingBehaviour implements TargetingBehaviour {
    private final GamePlayer owner;

    public PetTargetingBehaviour(GamePlayer owner) {
        this.owner = owner;
    }

    @Override
    public LivingEntity findTarget(GameEnemy brain, Location location) {
        if (owner.getLocation().distance(location) > 6) {
            return owner.getTargetableEntity();
        }
        else {
            return null;
        }
    }
}
