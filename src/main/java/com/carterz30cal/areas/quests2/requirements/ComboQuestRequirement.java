package com.carterz30cal.areas.quests2.requirements;

import com.carterz30cal.areas.quests.requirements.QuestRequirement;
import com.carterz30cal.entities.player.GamePlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Allows for multiple requirements to be combined into one as either
 * an OR or an AND operation.
 *
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class ComboQuestRequirement extends ConfigurableQuestRequirement {
    protected final boolean requireAll;
    protected final List<QuestRequirement> requirements = new ArrayList<>();

    public ComboQuestRequirement(ConfigurationSection config) {
        super(config);

        requireAll = config.getBoolean("require-all", true);
        for (var key : config.getConfigurationSection("sub-requirements").getKeys(false)) {
            var req = ConfigurableQuestRequirement.create(Objects.requireNonNull(config.getConfigurationSection("sub-requirements." + key)));
            requirements.add(req);
        }
    }

    @Override
    public boolean satisfied(GamePlayer player) {
        if (requireAll) {
            for (var req : requirements) {
                if (!req.satisfied(player)) {
                    return false;
                }
            }
            return true;
        }
        else {
            for (var req : requirements) {
                if (req.satisfied(player)) {
                    return true;
                }
            }
            return false;
        }
    }
}
