package com.carterz30cal.areas.quests.sections;

import com.carterz30cal.areas.quests.requirements.QuestRequirement;
import com.carterz30cal.areas.quests.rewards.QuestReward;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.utils.StringDescription;

import java.util.List;
import java.util.UUID;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class QuestSectionFinishOtherSection extends QuestSectionTalking {
    private final QuestRequirement requirement;

    public QuestSectionFinishOtherSection(QuestRequirement requirement, StringDescription startMessage, StringDescription description, StringDescription endMessage, QuestReward questReward) {
        super(startMessage, description, endMessage, questReward);
        this.requirement = requirement;
    }

    @Override
    public SectionSave CreateBlankSectionSave(GamePlayer player) {
        return new FinishOtherSectionSave(player);
    }

    @Override
    public List<String> GetDescription(SectionSave save) {
        var description = super.GetDescription(save);
        description.add("<white>You need to complete another quest!");
        return description;
    }

    /**
     * @author carterz30cal
     * @version 1
     * @since 1.0.0
     */
    private class FinishOtherSectionSave extends SectionSave {
        private FinishOtherSectionSave(GamePlayer player) {
            this.player = player;
            this.uuid = UUID.randomUUID();
        }

        @Override
        public boolean IsFinished() {
            return requirement.hasMetRequirements(player);
        }
    }
}
