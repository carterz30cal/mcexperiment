package com.carterz30cal.items.abilities.generic;

import com.carterz30cal.items.abilities.implementation.AbilityWithClick;
import com.carterz30cal.items.abilities.implementation.AbilityWithDescription;
import com.carterz30cal.items.abilities.implementation.GameAbility;
import com.carterz30cal.items.abilities.implementation.PlayerAbilityContext;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.stats.Stat;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
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
    public int ticks;


    public HealingAbility(long manaCost, long healing, int ticks) {
        this.manaCost = manaCost;
        this.healing = healing;
        this.ticks = ticks;
    }

    @Override
    public String name(PlayerAbilityContext context) {
        return "Instant Heal!";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var list = new ArrayList<String>();
        list.add("<grey><gold>Right click</gold> to consume " + formattedDisplay(Stat.MANA, manaCost) + " and then");
        list.add("<grey>heal you for at least " + formattedDisplay(Stat.HEALTH, healing) + "<dark_grey>x" + ticks);
        list.add("<dark_grey>This is affected by buffs to your healing.");
        return list;
    }

    @Override
    public void click(PlayerAbilityContext context, Situation situation, Location location) {
        if (situation == Situation.RIGHT_CLICK) {
            if (context.owner.useMana(manaCost)) {
                new BukkitRunnable() {
                    int i = ticks;

                    @Override
                    public void run() {
                        if (i == 0) {
                            cancel();
                        }
                        else {
                            context.owner.heal(healing);
                            context.owner.playSound(Sound.BLOCK_NOTE_BLOCK_CHIME, 0.3, 1.3);
                            i--;
                        }
                    }
                }.runTaskTimer(Dungeons.instance, 0, 5);
            }
        }
    }
}
