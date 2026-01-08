package com.carterz30cal.items.abilities2.generic;

import com.carterz30cal.items.abilities2.implementation.GameAbility;
import com.carterz30cal.stats.Stat;

import java.util.ArrayList;
import java.util.List;

public class HealingAbility extends GameAbility {
    public long manaCost;
    public long healing;


    public HealingAbility(long manaCost, long healing) {
        this.manaCost = manaCost;
        this.healing = healing;
    }

    @Override
    public String name(AbilityContext context) {
        return "LIGHT_PURPLEInstant Heal!";
    }

    @Override
    public List<String> description(AbilityContext context) {
        List<String> lore = new ArrayList<>();
        lore.add("GOLDLeft clickGRAY to consume " + display(Stat.MANA, manaCost) + "GRAY and");
        lore.add("GRAYthen heal you for " + display(Stat.HEALTH, healing));
        return lore;
    }

    @Override
    public void onLeftClick(AbilityContext context) {
        if (context.owner.useMana((int)manaCost)) {
            context.owner.gainHealth((int)healing);
        }
    }
}
