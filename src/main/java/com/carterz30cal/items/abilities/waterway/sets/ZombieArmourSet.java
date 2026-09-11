package com.carterz30cal.items.abilities.waterway.sets;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.items.abilities.implementation.*;
import com.carterz30cal.stats.Stat;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Waterway Armour Set.
 * Provides best-in-slot on-hit healing for Waterway and probably Necropolis too.
 *
 * @author carterz30cal
 * @version 2
 * @implSpec Healing scales with the log10 of the Vitality stat and some base value.
 * @since 1.0.0
 */
public class ZombieArmourSet extends GameAbility implements AbilityWithDescription, AbilityWithDefend {
    @Override
    public String name(PlayerAbilityContext context) {
        return "Undead Vitality";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var list = new ArrayList<String>();
        if (context.owner == null || context.owner.lastStats == null) {
            return list;
        }
        list.add("<grey>Whenever an enemy hits you, heal <red>" +
                getHealing(context.owner.lastStats.stat(Stat.VITALITY))
                + Stat.HEALTH.getIcon() + "</red>.");
        list.add("<dark_grey>Scales somewhat with Vitality.</dark_grey>");
        return list;
    }

    private long getHealing(long vitality) {
        return 2 + Math.round(Math.log10(vitality) * 2D);
    }

    @Override
    public void defend(@NotNull ContextWithAbility<? extends GameEntity> context, @NotNull DamagePacket packet) {
        long healing = getHealing(packet.defender.getStat(Stat.VITALITY));
        packet.defender.heal(healing);
    }
}
