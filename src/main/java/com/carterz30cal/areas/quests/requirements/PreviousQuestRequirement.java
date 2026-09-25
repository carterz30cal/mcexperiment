package com.carterz30cal.areas.quests.requirements;

import com.carterz30cal.areas.quests.Questgivers;
import com.carterz30cal.entities.player.GamePlayer;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
@SuppressWarnings("ClassCanBeRecord")
public class PreviousQuestRequirement implements QuestRequirement {
    private final Questgivers questgiver;

    public PreviousQuestRequirement(Questgivers questgiver) {
        this.questgiver = questgiver;
    }

    @Override
    public boolean hasMetRequirements(GamePlayer player) {
        var parent = questgiver.getParent();
        if (parent == null) {
            return true;
        }
        else {
            return parent.complete(player, questgiver);
        }
    }
}
