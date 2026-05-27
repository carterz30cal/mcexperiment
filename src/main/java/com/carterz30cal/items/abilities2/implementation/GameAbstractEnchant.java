package com.carterz30cal.items.abilities2.implementation;

import com.carterz30cal.items.ItemReq;
import com.carterz30cal.items.ItemType;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public abstract class GameAbstractEnchant extends GameAbility implements AbilityWithName {
    private static final TextColor GOLD = TextColor.color(NamedTextColor.GOLD);
    private static final TextColor BLUE = TextColor.color(NamedTextColor.BLUE);
    private static final TextColor DARK_PURPLE = TextColor.color(NamedTextColor.DARK_PURPLE);

    private final int maximumLevel;
    private final String name;
    private final Set<ItemType> validTypes;

    public GameAbstractEnchant(String name, int maximumLevel, ItemType... itemTypes) {
        this.maximumLevel = maximumLevel;
        this.name = name;
        this.validTypes = new HashSet<>(List.of(itemTypes));
    }

    @Override
    public String name(PlayerAbilityContext context) {
        return name;
    }

    @Override
    public TextColor colour(PlayerAbilityContext context) {
        if (context.level > getMaximumLevel()) {
            return GOLD;
        }
        else if (context.level == getMaximumLevel()) {
            return BLUE;
        }
        else {
            return DARK_PURPLE;
        }
    }

    /**
     * Method for determining what item types this ability can work with.
     * Currently only used for enchantments
     *
     * @return A set of all ItemTypes this enchant is usable on
     * @since 1.0.0
     */
    public final Set<ItemType> getValidTypes() {
        return validTypes;
    }

    public abstract List<ItemReq> getCatalystRequirements(PlayerAbilityContext context, long level);

    /**
     *
     * @param context ability context, determines level.
     * @return the amount of enchant power that the item needs to hold this enchantment.
     * @since 1.0.0
     */
    public abstract long getEnchantPower(PlayerAbilityContext context);

    public final int getMaximumLevel() {
        return maximumLevel;
    }
}
