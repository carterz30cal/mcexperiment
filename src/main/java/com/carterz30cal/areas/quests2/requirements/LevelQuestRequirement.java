package com.carterz30cal.areas.quests2.requirements;

import com.carterz30cal.entities.player.GamePlayer;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class LevelQuestRequirement extends ConfigurableQuestRequirement {
    protected final long level;

    public LevelQuestRequirement(ConfigurationSection config) {
        super(config);

        level = config.getLong("level", 0L);
    }

    @Override
    public boolean satisfied(GamePlayer player) {
        return player.getLevel() >= level;
    }
}
