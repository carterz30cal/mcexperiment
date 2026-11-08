package com.carterz30cal.areas.quests2.requirements;

import com.carterz30cal.entities.player.GamePlayer;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class CompleteQuestRequirement extends ConfigurableQuestRequirement {
    protected String qid;

    public CompleteQuestRequirement(ConfigurationSection config) {
        super(config);

        qid = config.getString("quest");
    }

    @Override
    public boolean satisfied(GamePlayer player) {
        return player.questing.finished(qid);
    }
}
