package com.carterz30cal.items.abilities2.waterway.sets;

import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.items.abilities2.implementation.GameAbility;
import com.carterz30cal.stats.Stat;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Waterway Armour Set.
 * Provides best-in-slot on-hit healing for Waterway and probably Necropolis too.
 *
 * @author carterz30cal
 * @version 1
 * @implSpec Healing scales with the log10 of the Vitality stat and some base value.
 * @since 1.0.0
 */
public class ZombieArmourSet extends GameAbility {
    public ZombieArmourSet() {

    }

    @Override
    public String name(AbilityContext context) {
        return "Undead Vitality";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull AbilityContext context) {
        var list = super.miniMessageDescription(context);
        list.add("<grey>Whenever an enemy hits you, heal <red>" +
                getHealing(context.owner.lastStats.getStat(Stat.VITALITY))
                + Stat.HEALTH.getIcon() + "</red>.");
        list.add("<dark_grey>Scales somewhat with Vitality.</dark_grey>");
        return list;
    }

    private long getHealing(long vitality) {
        return 2 + Math.round(Math.log10(vitality) * 2D);
    }

    @Override
    public int onDamaged(AbilityContext context, GameEnemy damager, int damage) {
        if (damager == null) return damage;

        long healing = getHealing(context.owner.stats.getStat(Stat.VITALITY));
        context.owner.gainHealth((int) healing);

        return super.onDamaged(context, damager, damage);
    }
}
