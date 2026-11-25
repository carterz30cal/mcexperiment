package com.carterz30cal.items.abilities.necropolis.runes;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.health.damage.AttackType;
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
public class StandstillRune extends GameRuneAbility implements AggressiveAbility {
    public StandstillRune() {
        super("Standstill");
    }

    @Override
    public List<String> miniMessageDescription(@NotNull ContextWithAbility<? extends GameEntity> context) {
        var description = "<grey>Your attacks deal <red>50%</red> less damage, but all of your damage is converted into <dark_blue>frost</dark_blue> damage.";
        return StringUtils.wrapText(description, 30);
    }

    @Override
    public void damage(ContextWithAbility<? extends GameEntity> context, DamagePacket packet) {
        if (packet.attack == AttackType.ARROW || packet.attack == AttackType.MELEE) {
            packet.multiply(0.5);
            var total = packet.getTotalDamage();
            packet.damages.clear();
            packet.addDamage(DamageType.FROST, total);
        }
    }
}
