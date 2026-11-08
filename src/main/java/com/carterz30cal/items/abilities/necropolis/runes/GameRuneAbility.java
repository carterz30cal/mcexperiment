package com.carterz30cal.items.abilities.necropolis.runes;

import com.carterz30cal.items.abilities.implementation.AbilityWithDescription;
import com.carterz30cal.items.abilities.implementation.GameAbility;
import com.carterz30cal.items.abilities.implementation.PlayerAbilityContext;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

/**
 * Basically just changes ability name colour and includes description by default
 *
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class GameRuneAbility extends GameAbility implements AbilityWithDescription {
    private final String name;

    public GameRuneAbility(String name) {
        this.name = name;
    }

    @Override
    public String name(PlayerAbilityContext context) {
        return "Rune: " + name;
    }

    @Override
    public TextColor colour(PlayerAbilityContext context) {
        return NamedTextColor.RED;
    }
}
