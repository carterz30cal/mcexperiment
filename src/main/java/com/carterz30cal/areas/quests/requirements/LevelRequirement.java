package com.carterz30cal.areas.quests.requirements;

import com.carterz30cal.entities.player.GamePlayer;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public record LevelRequirement(long level) implements QuestRequirement {
    @Override
    public boolean hasMetRequirements(GamePlayer player) {
        return player.getLevel() >= level;
    }
}
