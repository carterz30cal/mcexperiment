package com.carterz30cal.items.abilities2.generic;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.items.abilities2.implementation.*;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class DeadAbility extends GameAbility implements AbilityWithName, AggressiveAbility {

    @Override
    public String name(PlayerAbilityContext context) {
        return "<dark_red>Instant Death</dark_red>";
    }

    @Override
    public void damage(ContextWithAbility<? extends GameEntity> context, DamagePacket packet) {
        packet.defender.kill();
    }
}
