package com.carterz30cal.areas.areas;

import com.carterz30cal.areas.AbstractGameArea;
import com.carterz30cal.areas.PlayerTeleport;
import com.carterz30cal.areas.bosses.waterway.AreaBossWaterwaySeraph;
import com.carterz30cal.areas.events.AbstractEvent;
import com.carterz30cal.areas.events.EventManager;
import com.carterz30cal.areas.events.EventProvider;
import com.carterz30cal.areas.events.waterway.WaterwayRainEvent;
import com.carterz30cal.areas.spawners.KillEnemySpawner;
import com.carterz30cal.areas.spawners.SimpleAreaEnemySpawner;
import com.carterz30cal.entities.enemies.representation.EnemyRepresentationBuilder;
import com.carterz30cal.entities.enemies.representation.EnemyRepresentationData;
import com.carterz30cal.entities.interactable.GameFactoryOwnerEntity;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.Box;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class GameAreaWaterway extends AbstractGameArea implements EventProvider {
    public static @Nullable AbstractEvent downpour;

    public GameAreaWaterway() {
        this.areaName = "Waterway";
        this.boundingBox = new Box(
                new Location(Dungeons.w, 308, -64, 249),
                new Location(Dungeons.w, -256, 256, -156)
        );
        this.context = new SpawnerContext();
        EventManager.register(this);

        var factoryEntityBuilder = new EnemyRepresentationBuilder();
        var rep = new EnemyRepresentationData();
        rep.offset = new Vector();
        rep.type = EntityType.VILLAGER;
        rep.scale = 1;
        rep.invisible = false;
        rep.equipment = new HashMap<>();
        factoryEntityBuilder.add(rep);
        new GameFactoryOwnerEntity(factoryEntityBuilder, new Location(Dungeons.w, -94.5, 65, 6.5, -120, 0));

        // BOSSES
        register(AreaBossWaterwaySeraph.instance);

        var corridorLunatic1 = new SimpleAreaEnemySpawner(-35, -1, -52, 7, 66, "lunatic_1");
        corridorLunatic1.options("RAIN", "shocker_1");
        register(corridorLunatic1);
        var openLunatic1 = new SimpleAreaEnemySpawner(6, -28, 47, -45, 70, "lunatic_1");
        openLunatic1.options("RAIN", "shocker_1");
        register(openLunatic1);

        register(new SimpleAreaEnemySpawner(-46, 48, -56, 40, 65, "lunatic_2"));
        register(new SimpleAreaEnemySpawner(-9, 56, 22, 92, 66, "lunatic_2"));
        register(new SimpleAreaEnemySpawner(-37, 76, -28, 90, 65, "lunatic_2"));

        var titanSpot1 = new KillEnemySpawner("titan_1", 10, -67, 65, -34);
        var titanSpot2 = new KillEnemySpawner("titan_1", 15, -25, 94, 105);
        titanSpot1.options("RAIN", "titan_2");
        titanSpot2.options("RAIN", "titan_2");
        register(titanSpot1);
        register(titanSpot2);

        register(
                new SimpleAreaEnemySpawner(-94, -77, -66, -88, 63,
                        0.7, "lunatic_2", "lunatic_3")
                        .options("RAIN", "shocker_2", "shocker_3")
        );

        var stepsLunatic3 = new SimpleAreaEnemySpawner(-86, 61, -97, 73, 69, 2.5, "lunatic_3");
        stepsLunatic3.options("RAIN", "shocker_3");
        register(stepsLunatic3);
        register(new SimpleAreaEnemySpawner(-107, 42, -121, 57, 64, 1.5, "lunatic_4"));

        register(new SimpleAreaEnemySpawner(-74, 161, -82, 151, 102, 2.8, "spider_1"));

        // LAVA AREA BELOW MAIN AREA
        register(new SimpleAreaEnemySpawner(-20, -51, -41, -27, 38, 0.5, "lunatic_5_lava"));

        // SKY AREA
        var skyLunatics = new SimpleAreaEnemySpawner(-27, 75, -4, 106, 125, 0.75, "lunatic_5_sky");
        skyLunatics.options("RAIN", "shocker_5_sky");
        register(skyLunatics);


        var fanaticTemple = new SimpleAreaEnemySpawner(59, 142, 45, 132, 96, 2, "lunatic_6");
        fanaticTemple.options("RAIN", "shocker_6");
        register(fanaticTemple);
    }

    @Override
    public PlayerTeleport getRespawnPoint(GamePlayer died) {
        return PlayerTeleport.WATERWAY_SPAWN;
    }

    @Override
    public @Nullable AbstractEvent next() {
        if (downpour == null || (downpour.active() && downpour.duration() < 1)) {
            downpour = new WaterwayRainEvent(EventManager.getGlobalTime() + (20 * 60 * 40), 20 * 60 * 20);
            return downpour;
        }
        else return null;
    }

    @Override
    public int pollDuration() {
        return 20 * 60;
    }
}
