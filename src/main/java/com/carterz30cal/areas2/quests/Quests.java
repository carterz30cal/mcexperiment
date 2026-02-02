package com.carterz30cal.areas2.quests;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.utils.StringDescription;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Quests {
    WATERWAY_TUTORIAL(
            "Tam's Teachings",
            new StringDescription("Tam wants to show you the ropes!"),
            Questgivers.TUTORIAL_TAM_SPOT1,
            Questgivers.TUTORIAL_TAM_SPOT2
    ),
    WATERWAY_FISHING_TUTORIAL(
            "Fisherman Freddy",
            new StringDescription(),
            Questgivers.TUTORIAL_FISHING
    ),
    JIM(
            "Creepy Jim",
            new StringDescription(),
            Questgivers.CREEPY_JIM_SPOT1
    ),

    WATERWAY_SAM(
            "Sam",
            new StringDescription(),
            Questgivers.SCARED_SAM_SPOT1
    ),
    WATERWAY_ANDY_APPLE(
            "Andy Apple",
            new StringDescription(),
            Questgivers.ANDY_APPLE
    );
    private final List<Questgivers> quests;
    private final String name;
    private final StringDescription description;
    private final Quests self = this;

    Quests(
            String name, StringDescription description,
            Questgivers... quests
    ) {
        this.name = name;
        this.description = description;
        this.quests = Arrays.asList(quests);
        this.quests.forEach(q -> q.RegisterQuests(this));
    }

    public QuestSection GetQuestSection(GamePlayer player) {
        QuestSave save = player.GetQuestSave(this);
        return GetQuestSection(save.currentSection);
    }

    public QuestSection GetQuestSection(int cs) {
        int r = cs;
        int i = 0;
        while (i < quests.size() && r >= quests.get(i).getQuestCount()) {
            r -= quests.get(i).getQuestCount();
            i++;
        }
        if (i == quests.size()) {
            return null;
        }
        return quests.get(i).getQuest(r);
    }

    public List<String> GetDescription() {
        return description.GetList();
    }

    public List<QuestSection> GetCompletedSections(GamePlayer player) {
        QuestSave save = player.GetQuestSave(this);
        return GetCompletedSections(save.currentSection);
    }

    public List<QuestSection> GetCompletedSections(int cs) {
        List<QuestSection> sections = new ArrayList<>();
        int i = 0;
        int j = 0;
        while (j < cs && i < quests.size()) {
            sections.add(quests.get(i).getQuest(j));
            j++;
            if (j >= quests.get(i).getQuestCount()) {
                i++;
                j = 0;
            }
        }
        return sections;
    }

    public int GetTotalSectionCount() {
        int count = 0;
        for (Questgivers q : quests) {
            count += q.getQuestCount();
        }
        return count;
    }

    public boolean HasCompletedQuestgiver(GamePlayer player, Questgivers questgiver) {
        QuestSave save = player.GetQuestSave(this);
        if (save == null) {
            return false;
        }
        int r = save.currentSection;
        int i = 0;
        while (i < quests.size() && r >= quests.get(i).getQuestCount()) {
            r -= quests.get(i).getQuestCount();
            if (quests.get(i) == questgiver) {
                return r >= 0;
            }
            i++;
        }
        return false;
    }

    public QuestSave CreateSave(GamePlayer player) {
        QuestSave save = new QuestSave();
        save.currentSection = 0;
        save.sectionSave = quests.get(0).getQuest(0).CreateBlankSectionSave(player);
        return save;
    }

    public void FixSave(QuestSave save, GamePlayer player) {
        save.sectionSave = GetQuestSection(save.currentSection).CreateBlankSectionSave(player);
        save.completedQuest = false;
    }

    public QuestSave LoadSave(GamePlayer player, ConfigurationSection section) {
        QuestSave save = new QuestSave();
        save.currentSection = section.getInt("current-section", 0);
        save.completedQuest = section.getBoolean("is-finished", false);
        if (!save.completedQuest) {
            QuestSection qs = GetQuestSection(save.currentSection);
            assert qs != null;
            save.sectionSave = qs.CreateBlankSectionSave(player);
            save.sectionSave.Load(section);
        }
        return save;
    }

    public void SaveSave(GamePlayer player, ConfigurationSection section) {
        QuestSave save = player.GetQuestSave(this);
        if (save == null) {
            return;
        }
        section.set("current-section", save.currentSection);
        section.set("is-finished", save.completedQuest);
        if (save.sectionSave != null) {
            save.sectionSave.Save(section);
        }
    }

    public void MoveSave(GamePlayer player) {
        QuestSave save = player.GetQuestSave(this);
        player.DeregisterEventHandler(save.sectionSave.GetUUID());
        save.currentSection++;
        QuestSection section = GetQuestSection(player);
        if (section == null) {
            save.completedQuest = true;
        }
        else {
            save.sectionSave = section.CreateBlankSectionSave(player);
        }
    }

    public String GetName() {
        return name;
    }

    public class QuestSave {
        public int currentSection;
        public QuestSection.SectionSave sectionSave;
        public boolean completedQuest = false;

        public Quests GetQuest() {
            return self;
        }
    }
}
