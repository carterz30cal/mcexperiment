package com.carterz30cal.events;

import com.carterz30cal.areas2.Areas;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GamePlayer;
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
