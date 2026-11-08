package com.carterz30cal.areas.bosses.waterway;

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
public class AreaMinibossWaterMatriarch extends AbstractAreaBoss {
    public final static Location ALTAR_LOCATION = new Location(Dungeons.w, -83, 103, 186);
    public final static AreaMinibossWaterMatriarch instance = new AreaMinibossWaterMatriarch();
    private final static Box BOSS_ARENA_ENTRANCE = new Box(-93, 108, 172, -93, 102, 179);
    private final static Box BOSS_ARENA = new Box(-63, 119, 204, -97, 102, 164);
    private final static Location[] ITEM_REWARD_LOCATION = new Location[]{
            ALTAR_LOCATION.clone().add(-2.5, 0, 0),
            ALTAR_LOCATION.clone().add(0, 0, -2.5),
            ALTAR_LOCATION.clone().add(0, 0, 2.5),
    };
    private final static Location EYE_REWARD_LOCATION = ALTAR_LOCATION.clone().add(2.5, 0, 0);
    private final List<GameFloatingItem> floatingItems = new ArrayList<>();
    private MatriarchBossTier tier;
    private GameEnemy boss;

    public AreaMinibossWaterMatriarch() {
        GameTextDisplay.create("WATERWAY_MATRIARCH_SUMMON_TITLE", ALTAR_LOCATION.clone().add(0.5, 1.75, 0.5));
        waiting();
    }

    /**
     * Try and start the matriarch boss fight
     *
     * @param tier  what tier do we want to start?
     * @param owner who is starting the fight?
     * @return <code>true</code> if the fight has started, <code>false</code> otherwise.
     * @since 1.0.0 [1]
     */
    public static boolean start(MatriarchBossTier tier, GamePlayer owner) {
        return instance.tryStart(tier, owner);
    }

    private boolean tryStart(MatriarchBossTier tier, GamePlayer owner) {
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
        GameTextDisplay.create("WATERWAY_MATRIARCH_SUMMON_TITLE", ALTAR_LOCATION.clone().add(0.5, 1.75, 0.5));
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
        var title = GameTextDisplay.get("WATERWAY_MATRIARCH_SUMMON_TITLE");
        if (title != null) {
            title.name("<green>Matriarch Altar</green>");
        }
    }

    private void preparing() {
        var title = GameTextDisplay.get("WATERWAY_MATRIARCH_SUMMON_TITLE");
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
            BOSS_ARENA_ENTRANCE.setTemporaryWithin(Material.WHITE_STAINED_GLASS, Material.AIR);
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
                title.name("<red>Kill the matriarch!</red>");
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
    public enum MatriarchBossTier {
        LESSER("water_spider_matriarch", "<blue>Water Matriarch</blue>", "water_spider_matriarch_lootbox",
                new WeightedDrop("spider_silk£50", 200),
                new WeightedDrop("pet_water_spider_common", 5),
                new WeightedDrop("waterway_sac£1", 150),
                new WeightedDrop("combination_catalyst_shard£1", 100),
                new WeightedDrop("waterway_sac£2", 20),
                new WeightedDrop("water_matriarch_key$1", 5),
                new WeightedDrop("enchanted_book£1£enchants:ENCHANT_BLADE-1", 1),
                new WeightedDrop("enchanted_book£1£enchants:ENCHANT_HEALTHY-2", 5),
                new WeightedDrop("enchanted_book£1£enchants:ENCHANT_TITANIC-3", 5)),
        ;
        public final String boss;
        public final String name;
        public final String lootbox;
        public final WeightedDrop[] drops;

        MatriarchBossTier(String boss, String name, String lootbox, WeightedDrop... drops) {
            this.boss = boss;
            this.name = name;
            this.lootbox = lootbox;
            this.drops = drops;
        }
    }
}
