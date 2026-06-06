package com.carterz30cal.areas.areas;

import com.carterz30cal.areas.AbstractGameArea;
import com.carterz30cal.areas.PlayerTeleport;
import com.carterz30cal.areas.bosses.waterway.AreaBossWaterwaySeraph;
import com.carterz30cal.areas.spawners.KillEnemySpawner;
import com.carterz30cal.areas.spawners.SimpleAreaEnemySpawner;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.Box;
import org.bukkit.Location;

public class GameAreaWaterway extends AbstractGameArea {
    public GameAreaWaterway() {
        this.areaName = "Waterway";
        this.boundingBox = new Box(
                new Location(Dungeons.w, 308, -64, 300),
                new Location(Dungeons.w, -256, 256, -156)
        );
        this.context = new SpawnerContext();

        // BOSSES
        RegisterBoss(AreaBossWaterwaySeraph.instance);

        RegisterSpawner(new SimpleAreaEnemySpawner(-35, -1, -52, 7, 66, "lunatic_1"));
        RegisterSpawner(new SimpleAreaEnemySpawner(6, -28, 47, -45, 70, "lunatic_1"));

        RegisterSpawner(new SimpleAreaEnemySpawner(-46, 48, -56, 40, 65, "lunatic_2"));
        RegisterSpawner(new SimpleAreaEnemySpawner(-9, 56, 22, 92, 66, "lunatic_2"));
        RegisterSpawner(new SimpleAreaEnemySpawner(-37, 76, -28, 90, 65, "lunatic_2"));

        RegisterSpawner(new KillEnemySpawner("titan_1", 10, -67, 65, -34));

        RegisterSpawner(new SimpleAreaEnemySpawner(-94, -77, -66, -88, 63, 0.7, "lunatic_2", "lunatic_3"));

        RegisterSpawner(new SimpleAreaEnemySpawner(-86, 61, -97, 73, 69, 2.5, "lunatic_3"));
        RegisterSpawner(new SimpleAreaEnemySpawner(-107, 42, -121, 57, 64, 1.5, "lunatic_4"));

        RegisterSpawner(new SimpleAreaEnemySpawner(-74, 161, -82, 151, 102, 2.8, "spider_1"));

        // LAVA AREA BELOW MAIN AREA
        RegisterSpawner(new SimpleAreaEnemySpawner(-20, -51, -41, -27, 38, 0.5, "lunatic_5_lava"));

        // SKY AREA
        RegisterSpawner(new SimpleAreaEnemySpawner(-27, 75, -4, 106, 125, 0.75, "lunatic_5_sky"));

        RegisterSpawner(new SimpleAreaEnemySpawner(59, 142, 45, 132, 96, 2, "lunatic_6"));
    }

    @Override
    public PlayerTeleport GetRespawnPoint(GamePlayer died) {
        return PlayerTeleport.WATERWAY_SPAWN;
    }
}
