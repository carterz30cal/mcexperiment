package com.carterz30cal.events;

import com.carterz30cal.areas.Areas;
import com.carterz30cal.entities.enemies.implementation.GameEnemy;
import com.carterz30cal.entities.player.GamePlayer;
import org.bukkit.Location;

public interface GameEventHandler {
    default void OnKill(GamePlayer killer, GameEnemy killed, Areas area) {
    }

    default void OnPlayerDeath(GamePlayer victim) {
    }

    default void OnRightClickLocation(GamePlayer player, Location location) {
    }

    default void OnLeftClickLocation(GamePlayer player, Location location) {
    }
}
