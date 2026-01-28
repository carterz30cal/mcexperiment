package com.carterz30cal.areas2.quests;

import com.carterz30cal.areas2.quests.rewards.QuestReward;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.events.GameEventHandler;
import com.carterz30cal.utils.StringDescription;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class QuestSection {
    private final QuestSection self = this;
    public Quests parent;
    public Questgivers questgiver;

    public StringDescription GetStartDialogue() {
        return new StringDescription("start a quest yo");
    }

    public String GetChatter() {
        return "random chatter";
    }

    public StringDescription GetEndDialogue() {
        return new StringDescription("Well done!!!");
    }

    public List<String> GetDescription(SectionSave save) {
        return new ArrayList<>();
    }

    /**
     *
     * @return A QuestReward, or child, if one is attributed to the QuestSection
     * @author carterz30cal
     * @since 1.0.0
     */
    @Nullable
    public QuestReward GetQuestReward() {
        return null;
    }

    public abstract SectionSave CreateBlankSectionSave(GamePlayer player);

    public class SectionSave implements GameEventHandler {
        protected GamePlayer player;
        protected UUID uuid;
        protected QuestSection section = self;
        protected boolean initialInteraction = false;

        public void Load(ConfigurationSection section) {
            initialInteraction = section.getBoolean("has-talked-to");
        }

        public void Save(ConfigurationSection section) {
            section.set("has-talked-to", initialInteraction);
        }

        public List<String> GetDescription() {
            return section.GetDescription(this);
        }

        public StringDescription Talk() {
            if (!initialInteraction) {
                initialInteraction = true;
                player.SetSelectedQuest(parent);
                return GetStartDialogue();
            }
            else {
                return new StringDescription(GetChatter());
            }
        }

        public void Interact() {
        }

        public boolean IsFinished() {
            return true;
        }

        public boolean HasTalkedTo() {
            return initialInteraction;
        }

        public UUID GetUUID() {
            return uuid;
        }

        public QuestSection GetSection() {
            return section;
        }
    }
}
