package com.carterz30cal.items.abilities.waterway.pets;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.TagHavingEntity;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.health.damage.DamageType;
import com.carterz30cal.entities.health.damage.operations.implementations.MultiplyDamageTypeOperation;
import com.carterz30cal.items.abilities.implementation.*;
import com.carterz30cal.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class PetDrenchedPassive extends GameAbility implements AggressiveAbility, AbilityWithDescription {
    @Override
    public String name(PlayerAbilityContext context) {
        return "Passive: Hunting Nemo";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var list = new ArrayList<String>();
        list.add("<grey>Deal <red>" + StringUtils.truncatedDouble2(1.1 + (0.1 * context.getLevel())) +
                "x</red> more damage to fishing mobs.");
        return list;
    }

    @Override
    public void damage(ContextWithAbility<? extends GameEntity> context, DamagePacket packet) {
        if (packet.defender instanceof TagHavingEntity taggable) {
            if (taggable.tag("FISHING")) {
                packet.addOperation(new MultiplyDamageTypeOperation(DamageType.PHYSICAL, 1.1 + (0.1 * context.getLevel())));
            }
        }
    }
}
