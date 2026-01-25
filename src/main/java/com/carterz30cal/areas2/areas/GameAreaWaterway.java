package com.carterz30cal.areas2.areas;

import com.carterz30cal.areas2.AbstractGameArea;
import com.carterz30cal.areas2.spawners.KillEnemySpawner;
import com.carterz30cal.areas2.spawners.SimpleAreaEnemySpawner;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.Box;
import org.bukkit.Location;

public class GameAreaWaterway extends AbstractGameArea {
    public GameAreaWaterway() {
        this.areaName = "Waterway";
        this.boundingBox = new Box(
                new Location(Dungeons.w, 108, -64, 198),
                new Location(Dungeons.w, -256, 255, -156)
        );
        this.context = new SpawnerContext();

        RegisterSpawner(new SimpleAreaEnemySpawner(-35, -1, -52, 7, 66, "lunatic_1"));
        RegisterSpawner(new SimpleAreaEnemySpawner(6, -28, 47, -45, 70, "lunatic_1"));

        RegisterSpawner(new SimpleAreaEnemySpawner(-46, 48, -56, 40, 65, "lunatic_2"));
        RegisterSpawner(new SimpleAreaEnemySpawner(-9, 56, 22, 92, 66, "lunatic_2"));
        RegisterSpawner(new SimpleAreaEnemySpawner(-37, 76, -28, 90, 65, "lunatic_2"));

        RegisterSpawner(new KillEnemySpawner("titan_1", 50, -85, 65, -2));

        RegisterSpawner(new SimpleAreaEnemySpawner(-86, 61, -97, 73, 69, "lunatic_3"));

        // LAVA AREA BELOW MAIN AREA
        RegisterSpawner(new SimpleAreaEnemySpawner(-20, -51, -41, -27, 38, 0.5, "lunatic_5_lava"));
    }
}
