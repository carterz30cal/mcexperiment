package com.carterz30cal.areas.quests;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.utils.StringDescription;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author carterz30cal
 * @version 3
 * @since 1.0.0
 */
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
            Questgivers.TUTORIAL_FISHING,
            Questgivers.FISHERMAN_FREDDY_2
    ),
    JIM(
            "Creepy Jim",
            new StringDescription(),
            Questgivers.CREEPY_JIM_SPOT1
    ),

    MORGK(
            "Morgk",
            new StringDescription(),
            Questgivers.MORGK
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
    ),
    WATERWAY_CHARLOTTE(
            "Charlotte",
            new StringDescription(),
            Questgivers.WATERWAY_CHARLOTTE_1
    ),

    WATERWAY_JAYLO_KATY(
            "The Hunt for Necropolis Knight Katy",
            new StringDescription("Help Jaylo find Katy, a fellow Necropolis Knight!", "This quest will take you throughout Waterway!"),
            Questgivers.WATERWAY_MAIN,
            Questgivers.WATERWAY_MAIN_2
    ),

    NECROPOLIS_YONNA(
            "Yonna",
            new StringDescription(),
            Questgivers.NECROPOLIS_YONNA
    )

    ;
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
        this.quests.forEach(q -> q.registerQuests(this));
    }

    public QuestSection getQuestSection(GamePlayer player) {
        QuestSave save = player.getQuestSave(this);
        return getQuestSection(save.currentSection);
    }

    public QuestSection getQuestSection(int cs) {
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

    public List<String> getDescription() {
        return description.GetList();
    }

    @SuppressWarnings("unused")
    public List<QuestSection> getCompletedSections(GamePlayer player) {
        QuestSave save = player.getQuestSave(this);
        return getCompletedSections(save.currentSection);
    }

    public List<QuestSection> getCompletedSections(int cs) {
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

    public int getTotalSectionCount() {
        int count = 0;
        for (Questgivers q : quests) {
            count += q.getQuestCount();
        }
        return count;
    }

    public boolean hasCompletedQuestgiver(GamePlayer player, Questgivers questgiver) {
        QuestSave save = player.getQuestSave(this);
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

    public QuestSave createSave(GamePlayer player) {
        QuestSave save = new QuestSave();
        save.currentSection = 0;
        var q = quests.getFirst().getQuest(0);
        if (q == null) {
            return null;
        }
        else {
            save.sectionSave = q.CreateBlankSectionSave(player);
            return save;
        }
    }

    public void fixSave(QuestSave save, GamePlayer player) {
        save.sectionSave = Objects.requireNonNull(getQuestSection(save.currentSection)).CreateBlankSectionSave(player);
        save.completedQuest = false;
    }

    public QuestSave loadSave(GamePlayer player, ConfigurationSection section) {
        QuestSave save = new QuestSave();
        save.currentSection = section.getInt("current-section", 0);
        save.completedQuest = section.getBoolean("is-finished", false);
        if (!save.completedQuest) {
            QuestSection qs = getQuestSection(save.currentSection);
            if (qs != null) {
                save.sectionSave = qs.CreateBlankSectionSave(player);
                save.sectionSave.Load(section);
            }
        }
        return save;
    }

    public void saveSave(GamePlayer player, ConfigurationSection section) {
        QuestSave save = player.getQuestSave(this);
        if (save == null) {
            return;
        }
        section.set("current-section", save.currentSection);
        section.set("is-finished", save.completedQuest);
        if (save.sectionSave != null) {
            save.sectionSave.Save(section);
        }
    }

    public void moveSave(GamePlayer player) {
        QuestSave save = player.getQuestSave(this);
        player.DeregisterEventHandler(save.sectionSave.GetUUID());
        save.currentSection++;
        QuestSection section = getQuestSection(player);
        if (section == null) {
            save.completedQuest = true;
        }
        else {
            save.sectionSave = section.CreateBlankSectionSave(player);
        }
    }

    public String getName() {
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
