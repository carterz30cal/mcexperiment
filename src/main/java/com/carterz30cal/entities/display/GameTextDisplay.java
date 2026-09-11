package com.carterz30cal.entities.display;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.LocatableEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Provides a method of displaying text without the faff
 *
 * @author carterz30cal
 * @version 2
 * @see TextDisplay
 * @since 1.0.0
 */
public class GameTextDisplay extends GameEntity implements LocatableEntity {
    private static final Map<String, GameTextDisplay> entities = new HashMap<>();
    private final String id;
    private final TextDisplay display;

    protected GameTextDisplay(String id, Location location) {
        this.id = id;
        this.display = location.getWorld().spawn(location, TextDisplay.class);
        this.display.setBillboard(Display.Billboard.CENTER);
        this.display.setTeleportDuration(1);
        this.display.setAlignment(TextDisplay.TextAlignment.CENTER);
        this.uuid = UUID.randomUUID();
        register(this.uuid);
    }

    /**
     * Creates a <code>GameTextDisplay</code> if id doesn't exist, otherwise it will
     * return the existing copy.
     *
     * @param id       identifier for the display
     * @param location where do we want the display?
     * @return an existing or new <code>GameTextDisplay</code> with the correct id
     * @throws IllegalStateException if something very weird has happened with the ids, safe to ignore.
     * @since 1.0.0
     */
    public static GameTextDisplay create(String id, Location location) {
        if (entities.containsKey(id)) {
            var entity = entities.get(id);
            if (!entity.id.equals(id)) {
                throw new IllegalStateException("Somehow the GameTextDisplay entity has a mismatched id with the mapped id?");
            }
            if (entity.dead || !entity.display.isValid()) {
                entities.remove(id);
                return create(id, location);
            }
            else {
                return entity;
            }
        }
        else {
            var entity = new GameTextDisplay(id, location);
            entities.put(id, entity);
            return entity;
        }
    }

    /**
     * Attempts to get a <code>GameTextDisplay</code> object, returning <code>null</code> if not present.
     *
     * @param id display identifier
     * @return the display, if it exists, otherwise <code>null</code>
     * @since 1.0.0
     */
    public static @Nullable GameTextDisplay get(@NotNull String id) {
        return entities.get(id);
    }

    /**
     * Set the display text of the <code>TextDisplay</code> entity within.
     *
     * @param name the text you want to display, in <code>MiniMessage</code> format
     * @see MiniMessage
     * @since 1.0.0
     */
    public void name(@NotNull String name) {
        var component = MiniMessage.miniMessage().deserialize(name);
        this.display.text(component);
    }

    /**
     * Set the display text of the <code>TextDisplay</code> entity within.
     *
     * @param name the text you want to display, in <code>Component</code> format.
     * @see Component
     * @since 1.0.0
     */
    public void name(@NotNull Component name) {
        this.display.text(name);
    }

    @Override
    public void remove() {
        this.display.remove();
        entities.remove(this.id);
        deregister(this.uuid);
    }

    @Override
    public Location getLocation() {
        return this.display.getLocation();
    }

    @Override
    public void teleport(@NotNull Location location) {
        display.teleportAsync(location);
    }
}
