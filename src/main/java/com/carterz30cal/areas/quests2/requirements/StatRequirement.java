package com.carterz30cal.areas.quests2.requirements;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.stats.Stat;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class StatRequirement extends ConfigurableQuestRequirement {
    protected final Stat stat;
    protected final long amount;

    public StatRequirement(ConfigurationSection config) {
        super(config);

        stat = Stat.valueOf(config.getString("stat"));
        amount = config.getLong("amount");
    }

    @Override
    public boolean satisfied(GamePlayer player) {
        return player.getStat(stat) >= amount;
    }
}
