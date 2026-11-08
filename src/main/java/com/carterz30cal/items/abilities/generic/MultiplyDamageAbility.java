package com.carterz30cal.items.abilities.generic;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.health.damage.DamageType;
import com.carterz30cal.items.abilities.implementation.*;
import com.carterz30cal.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class MultiplyDamageAbility extends GameAbility implements AbilityWithDescription, AggressiveAbility {
    public final DamageType type;
    public final double multiplier;
    private final String name;

    public MultiplyDamageAbility(String name, DamageType type, double multiplier) {
        this.type = type;
        this.multiplier = multiplier;
        this.name = name;
    }

    @Override
    public String name(PlayerAbilityContext context) {
        return name;
    }

    @Override
    public List<String> miniMessageDescription(@NotNull ContextWithAbility<? extends GameEntity> context) {
        var desc = "<grey>Multiply <" + type.getColour().asHexString() + ">" + type.getName() + "</" + type.getColour().asHexString() + "> damage by <red>" + multiplier + "x</red>.";

        return StringUtils.wrapText(desc, 30);
    }

    @Override
    public void damage(ContextWithAbility<? extends GameEntity> context, DamagePacket packet) {
        packet.multiply(type, multiplier);
    }
}
