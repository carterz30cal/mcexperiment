package com.carterz30cal.areas2.quests.sections;

import com.carterz30cal.areas2.quests.QuestSection;
import com.carterz30cal.areas2.quests.rewards.QuestReward;
import com.carterz30cal.utils.StringDescription;
import org.jetbrains.annotations.Nullable;

public abstract class QuestSectionTalking extends QuestSection {
    private final StringDescription startMessage;
    private final StringDescription description;
    private final StringDescription endMessage;
    private final QuestReward questReward;

    public QuestSectionTalking(StringDescription startMessage, StringDescription description, StringDescription endMessage, QuestReward questReward) {
        this.startMessage = startMessage;
        this.description = description;
        this.endMessage = endMessage;
        this.questReward = questReward;
    }

    @Override
    public StringDescription GetStartDialogue() {
        return startMessage;
    }

    @Override
    public StringDescription GetEndDialogue() {
        return endMessage;
    }

    @Override
    public String GetChatter() {
        return description.GetRandomChoice();
    }

    @Override
    public @Nullable QuestReward GetQuestReward() {
        return questReward;
    }
}
