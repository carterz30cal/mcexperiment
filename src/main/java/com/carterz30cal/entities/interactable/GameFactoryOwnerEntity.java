package com.carterz30cal.entities.interactable;

import com.carterz30cal.entities.enemies.representation.EnemyRepresentationBuilder;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.gui.ItemProducerGUI;
import org.bukkit.Location;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class GameFactoryOwnerEntity extends GameEntityInteractable {
    public GameFactoryOwnerEntity(EnemyRepresentationBuilder builder, Location location) {
        super(builder, location);
        title("<gold>Factory</gold>");
        subtitle("<gold><b>Click!</b></gold>");
    }

    @Override
    public void interact(GamePlayer interactingPlayer) {
        interactingPlayer.openGui(new ItemProducerGUI(interactingPlayer, interactingPlayer.factory));
        super.interact(interactingPlayer);
    }
}
