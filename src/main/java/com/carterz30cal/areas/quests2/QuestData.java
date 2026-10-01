package com.carterz30cal.areas.quests2;

import com.carterz30cal.areas.quests.requirements.QuestRequirement;
import com.carterz30cal.areas.quests2.requirements.AlwaysTrueQuestRequirement;
import com.carterz30cal.areas.quests2.requirements.ConfigurableQuestRequirement;
import com.carterz30cal.areas.quests2.rewards.QuestReward;
import com.carterz30cal.entities.interactable.GameQuestEntity;
import com.carterz30cal.utils.FileUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public final class QuestData {
    private static final Map<String, QuestData> data = new HashMap<>();
    private static final String[] files = {
            "waterway/quests/tutorial_tam", "waterway/quests/fisherman", "waterway/quests/raintown", "waterway/quests/joes_tavern"
    };
    public String name;
    public String id;
    public boolean repeatable;

    public Class<? extends Quest> questClass;
    public ConfigurationSection specifics;
    public QuestRequirement requirement;
    public QuestReward reward;

    public QuestData(ConfigurationSection section) {
        this.name = section.getString("name");
        this.id = section.getName();
        this.repeatable = section.getBoolean("repeatable", false);
        try {
            //noinspection unchecked
            questClass = (Class<? extends Quest>) Class.forName("com.carterz30cal.areas.quests2.implementations." + section.getString("class"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.specifics = section;
        if (section.contains("requirement")) {
            this.requirement = ConfigurableQuestRequirement.create(Objects.requireNonNull(section.getConfigurationSection("requirement")));
        }
        else {
            this.requirement = AlwaysTrueQuestRequirement.instance;
        }

        if (section.contains("npc")) {
            GameQuestEntity.create(this, Objects.requireNonNull(section.getConfigurationSection("npc")));
        }
        if (section.contains("reward")) {
            this.reward = QuestReward.create(Objects.requireNonNull(section.getConfigurationSection("reward")));
        }
        else {
            this.reward = QuestReward.blank;
        }
    }

    public static void init() {
        for (String file : files) {
            FileConfiguration c = FileUtils.getData(file);
            if (c == null) {
                continue;
            }

            for (var key : c.getKeys(false)) {
                data.put(key, new QuestData(Objects.requireNonNull(c.getConfigurationSection(key))));
            }
        }
    }

    /**
     *
     * @param id the id of the quest data we're trying to get
     * @return a <code>QuestData</code>, if one exists.
     * @since 1.0.0 [1]
     */
    public static @Nullable QuestData get(@NotNull String id) {
        return data.getOrDefault(id, null);
    }
}
