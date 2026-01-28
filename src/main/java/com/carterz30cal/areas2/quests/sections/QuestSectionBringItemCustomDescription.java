package com.carterz30cal.areas2.quests.sections;

import com.carterz30cal.areas2.quests.rewards.QuestReward;
import com.carterz30cal.utils.StringDescription;

import java.util.List;

public class QuestSectionBringItemCustomDescription extends QuestSectionBringItem {
    protected StringDescription customScoreboard;

    public QuestSectionBringItemCustomDescription(
            StringDescription startMessage,
            StringDescription endMessage,
            StringDescription description,
            StringDescription customScoreboard,
            QuestReward questReward,
            String itemId, int amount) {
        super(startMessage, endMessage, description, questReward, itemId, amount);
        this.customScoreboard = customScoreboard;
    }

    @Override
    public List<String> GetDescription(SectionSave save) {
        return customScoreboard.GetList();
    }
}
