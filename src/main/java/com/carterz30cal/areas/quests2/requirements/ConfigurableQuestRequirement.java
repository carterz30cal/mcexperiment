package com.carterz30cal.areas.quests2.requirements;

import com.carterz30cal.areas.quests.requirements.QuestRequirement;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.InvocationTargetException;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public abstract class ConfigurableQuestRequirement implements QuestRequirement {
    public ConfigurableQuestRequirement(ConfigurationSection ignoredConfig) {

    }

    public static ConfigurableQuestRequirement create(ConfigurationSection config) {
        try {
            var clazz = Class.forName("com.carterz30cal.areas.quests2.requirements." + config.getString("class"));
            return (ConfigurableQuestRequirement) clazz.getConstructor(ConfigurationSection.class).newInstance(config);
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
