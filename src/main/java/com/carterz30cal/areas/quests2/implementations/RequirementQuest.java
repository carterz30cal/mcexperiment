package com.carterz30cal.areas.quests2.implementations;

import com.carterz30cal.areas.quests.requirements.QuestRequirement;
import com.carterz30cal.areas.quests2.QuestData;
import com.carterz30cal.areas.quests2.extensions.QuestWithDialog;
import com.carterz30cal.areas.quests2.requirements.ConfigurableQuestRequirement;
import com.carterz30cal.entities.player.GamePlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class RequirementQuest extends QuestWithDialog {
    protected final String display;
    protected QuestRequirement requirement;

    public RequirementQuest(QuestData data, GamePlayer owner, ConfigurationSection specifics) {
        super(data, owner, specifics);

        this.display = specifics.getString("message");
        this.requirement = ConfigurableQuestRequirement.create(Objects.requireNonNull(specifics.getConfigurationSection("quest-requirement")));
    }

    @Override
    public boolean finished() {
        return this.requirement.satisfied(owner.getOwner());
    }

    @Override
    protected String display() {
        return display;
    }


    @Override
    public void start() {
        if (finished()) {
            progress();
        }
        else {
            super.start();
        }
    }

    @Override
    public void progress() {
        super.progress();
    }

    @Override
    public void save(ConfigurationSection section) {

    }

    @Override
    public void load(ConfigurationSection section) {

    }
}
