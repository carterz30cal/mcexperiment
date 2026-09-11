package com.carterz30cal.entities.enemies.directors.behaviour;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.TargetableEntity;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.entities.player.summons.GameSummon;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

/**
 * @author carterz30cal
 * @version 2
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
            var basic = GameEntity.get(super.findTarget(brain, location));
            if (basic instanceof GameSummon || basic instanceof GamePlayer) {
                return null;
            }
            else {
                return ((TargetableEntity) basic).getTargetableEntity();
            }
        }
    }

}
