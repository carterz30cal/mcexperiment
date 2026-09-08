package com.carterz30cal.areas.areas;

import com.carterz30cal.areas.AbstractGameArea;
import com.carterz30cal.areas.PlayerTeleport;
import com.carterz30cal.areas.spawners.ContinuousKillEnemySpawner;
import com.carterz30cal.areas.spawners.SimpleAreaEnemySpawner;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.Box;
import org.bukkit.Location;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class GameAreaNecropolis extends AbstractGameArea {

    public GameAreaNecropolis() {
        this.areaName = "Necropolis";
        this.boundingBox = new Box(
                new Location(Dungeons.w, 95, 255, 250),
                new Location(Dungeons.w, -100, -60, 443)
        );
        this.context = new SpawnerContext();

        register(new SimpleAreaEnemySpawner(37, 392, 27, 372, 54, 1.5, "dusted_1"));

        var spiritKillSpawner = new ContinuousKillEnemySpawner("sword_spirit_1", 6, 5, 85, 95, 328);
        register(spiritKillSpawner);
        var spiritKillSpawner2 = new ContinuousKillEnemySpawner("sword_spirit_1", 6, 5, 98, 95, 329);
        register(spiritKillSpawner2);
        var spiritKillSpawner3 = new ContinuousKillEnemySpawner("sword_spirit_1", 6, 5, 85, 95, 350);
        register(spiritKillSpawner3);
    }

    @Override
    public PlayerTeleport getRespawnPoint(GamePlayer died) {
        return PlayerTeleport.NECROPOLIS_SPAWN;
    }
}
