package com.carterz30cal.areas.bosses.necropolis;

import com.carterz30cal.areas.bosses.AbstractAreaBoss;
import com.carterz30cal.areas.bosses.WeightedDrop;
import com.carterz30cal.entities.GameFloatingItem;
import com.carterz30cal.entities.display.GameTextDisplay;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.Box;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class AreaMinibossNecropolisHydra extends AbstractAreaBoss {
    public final static Location ALTAR_LOCATION = new Location(Dungeons.w, 28, 92, 272);
    public final static AreaMinibossNecropolisHydra instance = new AreaMinibossNecropolisHydra();
    private final static String BOSS_MESSAGE_PREFIX_LESSER = "<dark_grey>[<#7335db>Lesser Hydra</#7335db>]: </dark_grey>";
    private final static Box BOSS_ARENA_ENTRANCE = new Box(46, 86, 298, 55, 96, 297);
    private final static Box BOSS_ARENA = new Box(48, 87, 295, 0, 116, 245);
    private final static Location[] ITEM_REWARD_LOCATION = new Location[]{
            ALTAR_LOCATION.clone().add(-2.5, 0, 0),
            ALTAR_LOCATION.clone().add(0, 0, -2.5),
            ALTAR_LOCATION.clone().add(0, 0, 2.5),
    };
    private final static Location EYE_REWARD_LOCATION = ALTAR_LOCATION.clone().add(2.5, 0, 0);
    private final List<GameFloatingItem> floatingItems = new ArrayList<>();
    private HydraBossTier tier;
    private GameEnemy boss;

    public AreaMinibossNecropolisHydra() {

    }

    /**
     * Try and start the hydra boss fight
     *
     * @param tier  what tier do we want to start?
     * @param owner who is starting the fight?
     * @return <code>true</code> if the fight has started, <code>false</code> otherwise.
     * @since 1.0.0 [1]
     */
    public static boolean start(HydraBossTier tier, GamePlayer owner) {
        return instance.tryStart(tier, owner);
    }

    private boolean tryStart(HydraBossTier tier, GamePlayer owner) {
        if (phase != -1) {
            return false;
        }
        this.tier = tier;
        register(owner);
        start();
        return true;
    }

    @Override
    public void start() {
        cooldown("SPAWN_HYDRA", 20 * 15);
        drops(tier.drops);
        GameTextDisplay.create("NECROPOLIS_HYDRA_SUMMON_TITLE", ALTAR_LOCATION.clone().add(0.5, 1.75, 0.5));
        phase = 0;
    }

    @Override
    public void end() {
        BOSS_ARENA_ENTRANCE.reset();
        phase = -1;
        removeEnemies();
        removeBossBar();
        floatingItems.clear();
        for (var r : registered()) {
            try {
                deregister(r);
            } catch (Exception _) {

            }
        }
    }

    @Override
    public void disable() {

    }

    @Override
    public void tick() {
        super.tick();

        switch (phase) {
            case -1 -> waiting();
            case 0 -> preparing();
            case 1 -> phase1();
            case 2 -> phase2();
        }
    }

    private void waiting() {
        var title = GameTextDisplay.get("NECROPOLIS_HYDRA_SUMMON_TITLE");
        if (title != null) {
            title.name("<green>Hydra Altar</green>");
        }
    }

    private void preparing() {
        var title = GameTextDisplay.get("NECROPOLIS_HYDRA_SUMMON_TITLE");
        if (title != null) {
            title.name("<red>Spawning... </red> <dark_red>[" + cooldown("SPAWN_HYDRA") / 20 + "s]");
        }

        if (BOSS_ARENA.getPlayersWithin().isEmpty()) {
            end();
        }

        if (!isOnCooldown("SPAWN_HYDRA")) {
            for (var o : BOSS_ARENA.getPlayersWithin()) {
                try {
                    register(o);
                } catch (IllegalStateException _) {

                }
            }
            phase = 1;
            BOSS_ARENA_ENTRANCE.setTemporaryWithin(Material.LIME_STAINED_GLASS);
            boss = spawn(tier.boss, ALTAR_LOCATION.clone().subtract(1, 0, 0));
            createBar(tier.name, BossBar.Color.GREEN, BossBar.Overlay.PROGRESS);
        }
    }

    private void phase1() {
        if (!boss.isAlive()) {
            phase = 2;
            for (var r : registered()) {
                r.sendMessage("<dark_grey>You have defeated the " + tier.name + "! Please collect your rewards and leave the boss arena!");
                var drops = drops(r, ITEM_REWARD_LOCATION.length);
                for (var i = 0; i < ITEM_REWARD_LOCATION.length; i++) {
                    floatingItems.add(GameFloatingItem.spawn(ITEM_REWARD_LOCATION[i], drops.get(i), r));
                }
                floatingItems.add(GameFloatingItem.spawn(EYE_REWARD_LOCATION, ItemFactory.build(tier.lootbox), r));
            }
        }
        else {
            bossBar(boss.getHealthPercentage());
            var title = GameTextDisplay.get("NECROPOLIS_HYDRA_SUMMON_TITLE");
            if (title != null) {
                title.name("<red>Kill the hydra!</red>");
            }
        }
    }

    private void phase2() {
        boolean collected = true;
        for (var l : floatingItems) {
            if (!l.collected) {
                collected = false;
                break;
            }
        }
        if (collected) {
            end();
        }
    }

    @Override
    public @Nullable List<String> scoreboard(@NotNull GamePlayer player) {
        var s = super.scoreboard(player);
        var enemy = boss;
        if (s == null) {
            return null;
        }
        s.add(tier.name);
        if (phase == 0) {
            s.add("<red>Spawning!");
            s.add("<red>Fight will start in " + cooldown("SPAWN_HYDRA") / 20 + "s");
        }
        else if (phase == 2) {
            s.add("<grey>Collect your loot!");
        }
        else if (enemy != null) {
            s.add("<red>" + enemy.getHealth() + "♥");
        }

        return s;
    }

    @Override
    public void onLeftFight(@NotNull GamePlayer player, @NotNull LeftFightReason reason) {
        super.onLeftFight(player, reason);

        if (isRegistered(player)) {
            deregister(player);
            removeBossBar(player);
        }
    }

    /**
     * @author carterz30cal
     * @version 1
     * @since 1.0.0 [1]
     */
    public enum HydraBossTier {
        LESSER("necropolis_hydra_1", "<blue>Lesser Hydra</blue>", "hydra_lootbox_lesser",
                new WeightedDrop("decaying_hydra_head£1", 60),
                new WeightedDrop("pet_baby_hydra_common", 5),
                new WeightedDrop("combination_catalyst_shard£3", 100),
                new WeightedDrop("hydra_flesh£40", 125),
                new WeightedDrop("hydra_heart£1", 25),
                new WeightedDrop("green_chunk", 2),
                new WeightedDrop("hydra_lootbox_lesser£2", 5),
                new WeightedDrop("blue_slime", 5),
                new WeightedDrop("potion_health£2", 4),
                new WeightedDrop("weird_sword_rare", 1),
                new WeightedDrop("living_metal", 5),
                new WeightedDrop("enchanted_book£1£enchants:ENCHANT_BLADE-1", 10),
                new WeightedDrop("enchanted_book£1£enchants:ENCHANT_BLADE-2", 2),
                new WeightedDrop("enchanted_book£1£enchants:ENCHANT_WETSUIT-1", 5),
                new WeightedDrop("enchanted_book£1£enchants:ENCHANT_HEALTHY-3", 10),
                new WeightedDrop("enchanted_book£1£enchants:ENCHANT_TITANIC-3", 10)),
        ;
        public final String boss;
        public final String name;
        public final String lootbox;
        public final WeightedDrop[] drops;

        HydraBossTier(String boss, String name, String lootbox, WeightedDrop... drops) {
            this.boss = boss;
            this.name = name;
            this.lootbox = lootbox;
            this.drops = drops;
        }
    }
}
