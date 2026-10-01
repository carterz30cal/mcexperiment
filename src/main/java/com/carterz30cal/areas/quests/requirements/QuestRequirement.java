package com.carterz30cal.areas.quests.requirements;

import com.carterz30cal.entities.player.GamePlayer;

/**
 * @author carterz30cal
 * @version 3
 * @since 1.0.0
 */
public interface QuestRequirement {
    boolean satisfied(GamePlayer player);
}
