package com.carterz30cal.areas.quests2.requirements;

import com.carterz30cal.areas.quests.requirements.QuestRequirement;
import com.carterz30cal.entities.player.GamePlayer;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class AlwaysTrueQuestRequirement implements QuestRequirement {
    public static AlwaysTrueQuestRequirement instance = new AlwaysTrueQuestRequirement();

    @Override
    public boolean satisfied(GamePlayer player) {
        return true;
    }
}
