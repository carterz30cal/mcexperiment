package com.carterz30cal.areas.quests.requirements;

import com.carterz30cal.areas.quests.Questgivers;
import com.carterz30cal.entities.player.GamePlayer;

public class PreviousQuestRequirement implements QuestRequirement {
    private final Questgivers questgiver;

    public PreviousQuestRequirement(Questgivers questgiver) {
        this.questgiver = questgiver;
    }

    @Override
    public boolean HasMetRequirements(GamePlayer player) {
        return questgiver.GetParent().hasCompletedQuestgiver(player, questgiver);
    }
}
