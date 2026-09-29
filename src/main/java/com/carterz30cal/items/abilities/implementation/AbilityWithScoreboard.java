package com.carterz30cal.items.abilities.implementation;

import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public interface AbilityWithScoreboard extends Ability {
    List<String> scoreboard(PlayerAbilityContext context);
}
