package com.carterz30cal.items.abilities2.generic;

import com.carterz30cal.items.abilities2.implementation.GameAbility;
import com.carterz30cal.stats.Stat;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class HealingAbility extends GameAbility {
    public long manaCost;
    public long healing;


    public HealingAbility(long manaCost, long healing) {
        this.manaCost = manaCost;
        this.healing = healing;
    }

    @Override
    public String name(AbilityContext context) {
        return "Instant Heal!";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull AbilityContext context) {
        var list = super.miniMessageDescription(context);
        list.add("<grey><gold>Left click</gold> to consume " + formattedDisplay(Stat.MANA, manaCost) + " and then");
        list.add("<grey>heal you for at least " + formattedDisplay(Stat.HEALTH, healing));
        list.add("<dark_grey>This is affected by buffs to your healing.");
        return list;
    }

    @Override
    public void onLeftClick(AbilityContext context) {
        if (context.owner.useMana((int)manaCost)) {
            context.owner.gainHealth((int)healing);
        }
    }
}
