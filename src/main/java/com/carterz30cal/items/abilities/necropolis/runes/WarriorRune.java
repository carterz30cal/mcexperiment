package com.carterz30cal.items.abilities.necropolis.runes;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.health.damage.DamageType;
import com.carterz30cal.items.abilities.implementation.AggressiveAbility;
import com.carterz30cal.items.abilities.implementation.ContextWithAbility;
import com.carterz30cal.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class WarriorRune extends GameRuneAbility implements AggressiveAbility {
    public WarriorRune() {
        super("Warrior");
    }

    @Override
    public void damage(ContextWithAbility<? extends GameEntity> context, DamagePacket packet) {
        switch (packet.attack) {
            case MELEE -> packet.addDamage(DamageType.PHYSICAL, 200);
            case ARROW -> packet.addDamage(DamageType.PROJECTILE, 200);
            default -> {
            }
        }
    }

    @Override
    public List<String> miniMessageDescription(@NotNull ContextWithAbility<? extends GameEntity> context) {
        var description = "<grey>Whenever you attack an enemy, deal 200 more physical damage. Bows gain projectile damage instead.";
        return StringUtils.wrapText(description, 30);
    }
}
