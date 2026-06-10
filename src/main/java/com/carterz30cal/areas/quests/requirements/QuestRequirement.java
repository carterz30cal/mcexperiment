package com.carterz30cal.areas.quests.requirements;

import com.carterz30cal.entities.player.GamePlayer;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public interface QuestRequirement {
    boolean hasMetRequirements(GamePlayer player);
}
