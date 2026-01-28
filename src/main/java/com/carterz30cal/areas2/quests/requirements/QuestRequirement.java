package com.carterz30cal.areas2.quests.requirements;

import com.carterz30cal.entities.GamePlayer;

public interface QuestRequirement {
    boolean HasMetRequirements(GamePlayer player);
}
