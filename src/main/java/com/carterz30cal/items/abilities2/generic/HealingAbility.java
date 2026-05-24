package com.carterz30cal.items.abilities2.generic;

import com.carterz30cal.items.abilities2.implementation.AbilityWithClick;
import com.carterz30cal.items.abilities2.implementation.AbilityWithDescription;
import com.carterz30cal.items.abilities2.implementation.GameAbility;
import com.carterz30cal.items.abilities2.implementation.PlayerAbilityContext;
import com.carterz30cal.stats.Stat;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class HealingAbility extends GameAbility implements AbilityWithDescription, AbilityWithClick {
    public long manaCost;
    public long healing;


    public HealingAbility(long manaCost, long healing) {
        this.manaCost = manaCost;
        this.healing = healing;
    }

    @Override
    public String name(PlayerAbilityContext context) {
        return "Instant Heal!";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var list = new ArrayList<String>();
        list.add("<grey><gold>Right click</gold> to consume " + formattedDisplay(Stat.MANA, manaCost) + " and then");
        list.add("<grey>heal you for at least " + formattedDisplay(Stat.HEALTH, healing));
        list.add("<dark_grey>This is affected by buffs to your healing.");
        return list;
    }

    @Override
    public void click(PlayerAbilityContext context, Situation situation) {
        if (situation == Situation.RIGHT_CLICK) {
            if (context.owner.useMana(manaCost)) {
                context.owner.heal(healing);
                context.owner.playSound(Sound.BLOCK_NOTE_BLOCK_CHIME, 0.3, 1.3);
            }
        }
    }
}
