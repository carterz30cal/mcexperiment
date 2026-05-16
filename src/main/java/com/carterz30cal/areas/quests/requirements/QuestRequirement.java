package com.carterz30cal.areas.quests.requirements;

import com.carterz30cal.entities.player.GamePlayer;

public interface QuestRequirement {
    boolean HasMetRequirements(GamePlayer player);
}
