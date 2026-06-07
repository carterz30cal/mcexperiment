package com.carterz30cal.areas.areas;

import com.carterz30cal.areas.AbstractGameArea;
import com.carterz30cal.areas.PlayerTeleport;
import com.carterz30cal.areas.spawners.SimpleAreaEnemySpawner;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.Box;
import org.bukkit.Location;

public class GameAreaNecropolis extends AbstractGameArea {

    public GameAreaNecropolis() {
        this.areaName = "Necropolis";
        this.boundingBox = new Box(
                new Location(Dungeons.w, 95, 255, 271),
                new Location(Dungeons.w, -100, -60, 443)
        );
        this.context = new SpawnerContext();

        RegisterSpawner(new SimpleAreaEnemySpawner(37, 392, 27, 372, 54, 1.5, "dusted_1"));
    }

    @Override
    public PlayerTeleport GetRespawnPoint(GamePlayer died) {
        return PlayerTeleport.NECROPOLIS_SPAWN;
    }
}
