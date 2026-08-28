package com.carterz30cal.items.abilities.waterway.pets;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.health.damage.DamageType;
import com.carterz30cal.entities.health.damage.operations.implementations.ConvertDamageTypeOperation;
import com.carterz30cal.items.abilities.implementation.*;
import com.carterz30cal.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The Lightning Bug is a pet crafted using empty storm pearls, which are
 * found during the waterway downpour event. Upgrades will be crafted using
 * future area event ingredients. It focuses on dealing lightning damage.
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class LightningBugPetActive extends GameAbility implements AggressiveAbility, AbilityWithDescription {

    @Override
    public void damage(ContextWithAbility<? extends GameEntity> context, DamagePacket packet) {
        packet.addOperation(new ConvertDamageTypeOperation(DamageType.PHYSICAL, DamageType.LIGHTNING, 0.05, false));
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var desc = "<grey>Your attacks gain <blue>5%</blue> of your <white>physical</white> damage as <yellow>lightning</yellow> damage.";
        return StringUtils.wrapText(desc, 42);
    }

    @Override
    public String name(PlayerAbilityContext context) {
        return "Active: Crackler";
    }
}
