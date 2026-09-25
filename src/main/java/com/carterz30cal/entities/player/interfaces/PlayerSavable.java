package com.carterz30cal.entities.player.interfaces;

import org.bukkit.configuration.ConfigurationSection;
import com.carterz30cal.entities.player.GamePlayer;
/**
 * Interface for objects that should be saved with the <code>GamePlayer</code> object.
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 * @see GamePlayer
 */
public interface PlayerSavable {
    /**
     * Saves this object into the players' database.
     * Interfaces in YAML format, but may be saved differently.
     * @param section the configuration section we're using as the save location
     * @since 1.0.0
     */
    void save(ConfigurationSection section);
    /**
     * Loads this object given a blank object and a <code>ConfigurationSection</code> object.
     * Interfaces in YAML format, but may be saved differently.
     * @param section the configuration section we're using as the save location
     * @since 1.0.0
     */
    void load(ConfigurationSection section);
}
