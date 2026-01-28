package com.carterz30cal.areas2.quests.sections;

import com.carterz30cal.areas2.Areas;
import com.carterz30cal.areas2.quests.rewards.QuestReward;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.utils.StringDescription;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.UUID;

public class QuestSectionKill extends QuestSectionTalking {
    private final String killTag;
    private final String prettyName;
    private final int killsRequired;

    public QuestSectionKill(
            StringDescription startMessage, StringDescription endMessage,
            StringDescription description, QuestReward questReward,
            String killTag, String prettyName, int killsRequired
    ) {
        super(startMessage, description, endMessage, questReward);
        this.killTag = killTag;
        this.killsRequired = killsRequired;
        this.prettyName = prettyName;
    }

    @Override
    public List<String> GetDescription(SectionSave save) {
        List<String> list = super.GetDescription(save);
        KillSectionSave killSectionSave = castSave(save);
        if (!save.IsFinished()) {
            list.add("WHITEKill RED" + killSectionSave.kills + "WHITE/GREEN" + killsRequired + " WHITE" + this.prettyName + "WHITE.");
        }
        else {
            list.add("WHITEReturn to " + questgiver.toString() + "WHITE!");
        }
        return list;
    }

    private KillSectionSave castSave(SectionSave save) {
        if (save instanceof KillSectionSave) {
            return (KillSectionSave) save;
        }
        else {
            throw new IllegalStateException("Section is of the wrong type!");
        }
    }

    @Override
    public SectionSave CreateBlankSectionSave(GamePlayer player) {
        KillSectionSave killSectionSave = new KillSectionSave(player);
        player.RegisterEventHandler(killSectionSave.GetUUID(), killSectionSave);
        return killSectionSave;
    }

    private class KillSectionSave extends SectionSave {
        private int kills;

        private KillSectionSave(GamePlayer player) {
            this.player = player;
            this.kills = 0;
            this.uuid = UUID.randomUUID();
        }

        @Override
        public void Load(ConfigurationSection section) {
            super.Load(section);
            kills = section.getInt("kills", 0);

            if (!IsFinished()) {
                player.RegisterEventHandler(uuid, this);
            }
        }

        @Override
        public void Save(ConfigurationSection section) {
            super.Save(section);
            section.set("kills", kills);
        }

        @Override
        public boolean IsFinished() {
            return this.kills >= killsRequired;
        }

        @Override
        public void OnKill(GamePlayer killer, GameEnemy killed, Areas area) {
            if (killed.hasTag(killTag)) {
                kills++;
            }
        }
    }
}
