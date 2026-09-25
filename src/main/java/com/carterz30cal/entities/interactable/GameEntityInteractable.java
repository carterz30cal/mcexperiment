package com.carterz30cal.entities.interactable;

import com.carterz30cal.areas.quests.requirements.QuestRequirement;
import com.carterz30cal.entities.enemies.representation.EnemyRepresentationBuilder;
import com.carterz30cal.entities.player.GamePlayer;
import org.bukkit.Location;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class GameEntityInteractable extends GameOwnable {
    protected QuestRequirement requirement = null;

    public GameEntityInteractable(EnemyRepresentationBuilder builder, Location location) {
        super(builder, location);
    }

    public void interact(GamePlayer interactingPlayer) {

    }

    @Override
    protected boolean isVisible(GamePlayer viewer) {
        return requirement == null || requirement.hasMetRequirements(viewer);
    }
}
