package com.carterz30cal.entities.interactable;

import com.carterz30cal.areas.quests.requirements.QuestRequirement;
import com.carterz30cal.entities.enemies.representation.EnemyRepresentationBuilder;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.utils.EntityUtils;
import org.bukkit.Location;

/**
 * @author carterz30cal
 * @version 3
 * @since 1.0.0
 */
public class GameEntityInteractable extends GameOwnable {
    protected QuestRequirement requirement = null;
    protected Location original;
    protected double focusRadius;

    public GameEntityInteractable(EnemyRepresentationBuilder builder, Location location) {
        super(builder, location);

        this.original = location.clone();
        this.focusRadius = 6;
    }

    @Override
    protected void tick() {
        if (representation.valid() && focusRadius > 0) {
            var nearby = EntityUtils.getNearbyPlayers(getLocation(), this.focusRadius);
            if (nearby.isEmpty()) {
                representation.teleport(original);
            }
            else {
                nearby.sort((a, b) -> (int) (b.distance(getLocation()) - a.distance(getLocation())));
                representation.look(nearby.getFirst().getLocation().add(0, 1.5, 0));
            }
        }

        super.tick();
    }

    public void interact(GamePlayer interactingPlayer) {

    }

    @Override
    protected boolean isVisible(GamePlayer viewer) {
        return requirement == null || requirement.satisfied(viewer);
    }
}
