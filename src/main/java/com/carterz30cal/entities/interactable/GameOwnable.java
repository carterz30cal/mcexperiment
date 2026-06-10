package com.carterz30cal.entities.interactable;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.entities.enemies.representation.EnemyInformationDisplay;
import com.carterz30cal.entities.enemies.representation.EnemyRepresentation;
import com.carterz30cal.entities.enemies.representation.EnemyRepresentationBuilder;
import com.carterz30cal.entities.enemies.representation.RepresentedEntity;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.main.Dungeons;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static net.kyori.adventure.text.Component.text;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class GameOwnable extends GameEntity implements RepresentedEntity {
    private final BukkitRunnable ticker;
    protected Location location;

    protected final EnemyInformationDisplay display;
    private final EnemyRepresentationBuilder repBuilder;
    protected EnemyRepresentation representation;
    private Component title;
    private Component subtitle;

    public GameOwnable(EnemyRepresentationBuilder representation, Location location) {
        this.repBuilder = representation;
        this.representation = representation.build(location);
        this.location = location;
        this.uuid = UUID.randomUUID();
        this.display = new EnemyInformationDisplay(this);
        this.ticker = new BukkitRunnable() {

            @Override
            public void run() {
                tick();
            }
        };
        this.ticker.runTaskTimer(Dungeons.instance, 0, 20);
        title = text("");
        subtitle = text("");
        register(uuid);
    }


    @Override
    protected void register(UUID uuid) {
        this.representation.register(this);
        super.register(uuid);
    }

    @Override
	public void remove() {
        this.representation.remove();
        this.ticker.cancel();
        deregister(this.uuid);
	}

    @Override
	public Location getLocation() {
        return location;
	}

    @Override
    public void teleport(@NotNull Location location) {
        representation.tick(location);
    }

    /**
     * Sets the title of this entity, using <code>MiniMessage</code> format.
     *
     * @param to the <code>String</code> representation of the title, in <code>MiniMessage</code> format.
     * @see MiniMessage
     * @since 1.0.0
     */
    protected void title(String to) {
        title = MiniMessage.miniMessage().deserialize(to);
    }

    /**
     * Sets the subtitle of this entity, using <code>MiniMessage</code> format.
     *
     * @param to the <code>String</code> representation of the subtitle, in <code>MiniMessage</code> format.
     * @see MiniMessage
     * @since 1.0.0
     */
    protected void subtitle(String to) {
        subtitle = MiniMessage.miniMessage().deserialize(to);
    }

    /**
     * Determines whether a specific <code>GamePlayer</code> should
     * be able to view this ownable entity.
     *
     * @param viewer the specific <code>GamePlayer</code>
     * @return <code>true</code> if the <code>GamePlayer</code> should be able to see this entity, <code>false</code> otherwise.
     * @implNote this should be overwritten by any deriving class, as the default implementation is always <code>true</code>
     * @since 1.0.0
     */
    protected boolean isVisible(
            @SuppressWarnings("unused") GamePlayer viewer) {
        return true;
    }

    protected void tick() {
        if (!representation.valid()) {
            representation.remove();
            if (!getLocation().isChunkLoaded()) {
                return;
            }
            representation = repBuilder.build(location);
            representation.register(this);
            display.reset();
        }
        for (var player : PlayerManager.players.values()) {
            representation.display(player, isVisible(player));
            display.display(player, isVisible(player));
        }
        display.setLine(0, title);
        display.setLine(1, subtitle);
        display.tick();
    }

    @Override
    public EnemyRepresentation getRepresentation() {
        return representation;
    }
}
