package com.carterz30cal.areas.bosses.waterway;

import com.carterz30cal.areas.bosses.AbstractAreaBoss;
import com.carterz30cal.areas.bosses.WeightedDrop;
import com.carterz30cal.entities.GameFloatingItem;
import com.carterz30cal.entities.display.GameTextDisplay;
import com.carterz30cal.entities.enemies.abilities.waterway.seraph.EnemyAbilityTeleportAfterDamage;
import com.carterz30cal.entities.enemies.core.EnemyBuilder;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.health.damage.DamageType;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.Box;
import com.carterz30cal.utils.ParticleUtils;
import com.carterz30cal.utils.RandomUtils;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Singleton boss class for the final fight of the Waterway area.
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
@SuppressWarnings("UnnecessaryUnicodeEscape")
public final class AreaBossWaterwaySeraph extends AbstractAreaBoss {
    private final static String BOSS_MESSAGE_PREFIX = "<dark_grey>[<#7335db>Water Seraph</#7335db>]: </dark_grey>";
    private final static Box BOSS_ARENA = new Box(118, 66, 143, 170, 110, 191);
    private final static double DAMAGING_WATER_HEIGHT = 66.8D;
    private final static long DAMAGING_WATER_DAMAGE = 40;
    private final static Vector INVERT_Y_VECTOR = new Vector(1, 0, 1);
    private final static Vector BOUNCE_VECTOR = new Vector(0, 2.65, 0);
    private final static Location SERAPH_SPAWN_LOCATION = new Location(Dungeons.w, 146.5, 89, 167.5, 90, 0);
    private final static Location[] SPAWN_AD_LOCATIONS = new Location[]{
            new Location(Dungeons.w, 169.5, 88, 167.5, 90, 0),
            new Location(Dungeons.w, 146.5, 88, 190.5, -180, 0),
            new Location(Dungeons.w, 146.5, 88, 144.5, 0, 0)};
    private final static Location ENTRANCE_LOCATION = new Location(Dungeons.w, 114.5, 88, 167.5, -90, 0);
    private final static String[] PHASE3_AD_OPTIONS = new String[]{
            "water_seraph_ad_phase3_lunatic", "water_seraph_ad_phase3_magic_archer",
            "water_seraph_ad_phase3_creeper"
    };
    private final static Location[] ITEM_REWARD_LOCATION = new Location[]{
            new Location(Dungeons.w, 143.5, 88, 166.5),
            new Location(Dungeons.w, 143.5, 88, 164.5),
            new Location(Dungeons.w, 143.5, 88, 168.5),
            new Location(Dungeons.w, 143.5, 88, 170.5)
    };
    private final static Location EYE_REWARD_LOCATION = new Location(Dungeons.w, 141.5, 88, 165.5);
    private final static String PHASE4_GOLEM = "water_seraph_ad_phase4_golem";
    private final static List<GameEnemy> phase4Golems = new ArrayList<>();
    private final static BlockData PILLAR_WARNING_BLOCK = Dungeons.instance.getServer().createBlockData(Material.ORANGE_STAINED_GLASS);
    private final static BlockData PILLAR_DANGEROUS_BLOCK = Dungeons.instance.getServer().createBlockData(Material.RED_STAINED_GLASS);
    private final static long PILLAR_DAMAGE = 35;
    private final static Vector PILLAR_BOUNCE_VECTOR = new Vector(0, 0.9, 0);
    public static AreaBossWaterwaySeraph instance = new AreaBossWaterwaySeraph();

    private AreaBossWaterwaySeraph() {
        drops(new WeightedDrop("enchanted_book£1£enchants:ENCHANT_LUCK-1~", 40),
                new WeightedDrop("seraph_sword£1", 8),
                new WeightedDrop("enchanted_book£1£enchants:ENCHANT_CONCENTRATION-1~", 25),
                new WeightedDrop("enchanted_book£1£enchants:ENCHANT_SHARPNESS-3~", 10),
                new WeightedDrop("enchanted_book£1£enchants:ENCHANT_BLADE-1~", 30),
                new WeightedDrop("waterway_seraph_key£5", 5),
                new WeightedDrop("seraphs_eye£3", 2),
                new WeightedDrop("clear_glass_helmet£1", 5),
                new WeightedDrop("seraphs_pyjamas£1", 5),
                new WeightedDrop("seraph_ooze", 5),
                new WeightedDrop("seraphs_guide_to_summoning_spirits", 5));
    }

    @Override
    public void register(GamePlayer player) {
        if (phase > 0) {
            throw new IllegalStateException("Fight is already in progress, cannot register!");
        }
        super.register(player);
        player.sendMessage("<grey>You have registered for the <#7335db>Water Seraph</#7335db> fight! <em>It will start shortly</em>.", 5);
        if (!isOnCooldown("PREPARED")) {
            phase = 0;
            var title = GameTextDisplay.create("SERAPH_TITLE", new Location(Dungeons.w, 129.5, 89.7, 167.5, 90, 0));
            title.name("<#7335db><b>Water Seraph</b></#7335db>");
            GameTextDisplay.create("SERAPH_SUBTITLE", new Location(Dungeons.w, 129.5, 89.3, 167.5, 90, 0));
            createBar("<#7335db>Water Seraph</#7335db>", BossBar.Color.PURPLE, BossBar.Overlay.NOTCHED_20);
            bossBar(0);
        }
        player.teleport(ENTRANCE_LOCATION);
        cooldown("PREPARED", 200);
    }

    @Override
    public void start() {
        phase = 1;
        Objects.requireNonNull(GameTextDisplay.get("SERAPH_TITLE")).remove();
        Objects.requireNonNull(GameTextDisplay.get("SERAPH_SUBTITLE")).remove();

        var bossBuilder = adjustBuilderTargetingBehaviour(EnemyBuilder.getBuilder("water_seraph_boss"));
        bossBuilder.addAbility(new EnemyAbilityTeleportAfterDamage(1250, SERAPH_SPAWN_LOCATION));
        var boss = spawn("SERAPH", bossBuilder, SERAPH_SPAWN_LOCATION);
        boss.getEnemyDirector().setSpeed(0);
    }

    @Override
    public void end() {
        phase = -1;
        phase4Golems.clear();
        removeEnemies();
        removeBossBar();
    }

    @Override
    public void tick() {
        super.tick();
        switch (phase) {
            case 0 -> preparingPhase();
            case 1 -> phase1();
            case 2 -> phase2();
            case 3 -> phase3();
            case 4 -> phase4();
            case 5 -> phase5();
            case 6 -> phase6();
            case 7 -> phase7();
        }

        if (phase == 7) {
            bossBar("<gold>Collect your loot and leave!", 0);
        }
        else if (phase == 6) {
            bossBar("<#7335db>Water Seraph</#7335db>", 0);
        }
        if (phase >= 1) {
            var boss = get("SERAPH");
            if (boss != null) {
                var health = boss.getHealthSystem().getHealth();
                var percent = boss.getHealthPercentage();
                bossBar("<#7335db>Water Seraph</#7335db> <red>" + health + "\u2665</red>", percent);
            }
        }

        // BOUNCY WATER W/ DAMAGE
        for (var player : registered()) {
            var cooldownId = player.getUUID().toString() + "_WATER_BOUNCE";
            if (player.getLocation().getY() <= DAMAGING_WATER_HEIGHT && !isOnCooldown(cooldownId)) {
                var velocity = player.player.getVelocity().multiply(INVERT_Y_VECTOR).add(BOUNCE_VECTOR);
                player.player.setVelocity(velocity);
                cooldown(cooldownId, 10);
                if (phase > 0) {
                    var packet = new DamagePacket(player, null, DAMAGING_WATER_DAMAGE, DamageType.FROST);
                    player.damage(packet);
                }
            }
        }

        for (var enemy : getEnemies()) {
            if (enemy.dead) {
                continue;
            }
            var cooldownId = enemy.getUUID().toString() + "_WATER_DAMAGE";
            if (enemy.getLocation().getY() <= DAMAGING_WATER_HEIGHT && !isOnCooldown(cooldownId)) {
                cooldown(cooldownId, 4);
                if (phase > 0) {
                    var multiplier = phase + 2;
                    var packet = new DamagePacket(enemy, null, DAMAGING_WATER_DAMAGE * multiplier, DamageType.FROST);
                    enemy.damage(packet);
                }
            }
        }
    }

    @Override
    public void onLeftFight(@NotNull GamePlayer player, @NotNull LeftFightReason reason) {
        super.onLeftFight(player, reason);

        if (isRegistered(player)) {
            deregister(player);
        }
    }

    /**
     * This is phase <code>0</code>, whilst we're waiting for all players who want to register, to do so.<br>
     * We'll display a <code>TextDisplay</code> entity, with the time until the boss fight starts.
     *
     * @since 1.0.0
     */
    private void preparingPhase() {
        if (!isOnCooldown("PREPARED")) {
            start();
        }
        else {
            var subtitle = GameTextDisplay.get("SERAPH_SUBTITLE");
            assert subtitle != null;
            subtitle.name("<light_purple>" + Math.round(cooldown("PREPARED") / 20D) + "s till start</light_purple>");
        }
    }

    /**
     * Initial messages, adds cooldown for Seraph to spawn
     */
    private void phase1() {
        message(BOSS_MESSAGE_PREFIX + "Welcome...");
        message(BOSS_MESSAGE_PREFIX + "You have come far.. but you will not pass me.", 20);
        cooldown("PHASE_2_TRANSITION", 100);
        phase = 2;
    }

    private void phase2() {
        if (!isOnCooldown("PHASE_2_TRANSITION")) {
            phase = 3;
            var seraph = get("SERAPH");
            if (seraph == null) {
                Dungeons.instance.getLogger().severe("Had to abandon Waterway Seraph fight as Seraph was null. Please investigate!");
                end();
                return;
            }
            seraph.getEnemyDirector().resetSpeed();
            cooldown("SPAWN_AD_PHASE3", 60);
        }
    }

    private void phase3() {
        var seraph = get("SERAPH");
        if (seraph == null) {
            Dungeons.instance.getLogger().severe("Had to abandon Waterway Seraph fight as Seraph was null. Please investigate!");
            end();
            return;
        }
        if (seraph.getHealthPercentage() <= 0.5) {
            message(BOSS_MESSAGE_PREFIX + "Gah!");
            message(BOSS_MESSAGE_PREFIX + "I think I need to up the ante.", 30);
            phase = 4;
            seraph.teleport(SERAPH_SPAWN_LOCATION);
            seraph.getEnemyDirector().setSpeed(0);
            seraph.getHealthSystem().setImmune(true);
            for (var spawn : SPAWN_AD_LOCATIONS) {
                var builder = adjustBuilderTargetingBehaviour(EnemyBuilder.getBuilder(PHASE4_GOLEM));
                phase4Golems.add(spawn(builder, spawn));
            }
        }
        if (!isOnCooldown("SPAWN_AD_PHASE3")) {
            var count = RandomUtils.getRandomEx(1, 4);
            while (count-- > 0 && getAliveEnemyCount() < 26) {
                var choice = EnemyBuilder.getBuilder(RandomUtils.getChoice(PHASE3_AD_OPTIONS));
                spawn(adjustBuilderTargetingBehaviour(choice), RandomUtils.getChoice(SPAWN_AD_LOCATIONS));
            }
            cooldown("SPAWN_AD_PHASE3", 120);
        }

        if (!isOnCooldown("SPAWN_FIRE_PILLAR")) {
            var count = RandomUtils.getRandomEx(4, 9);
            while (count-- > 0) {
                spawnFirePillar();
            }
            cooldown("SPAWN_FIRE_PILLAR", 140);
        }
    }

    private void phase4() {
        boolean golemsAlive = false;
        for (var golem : phase4Golems) {
            if (!golem.dead) {
                golemsAlive = true;
                break;
            }
        }
        if (!golemsAlive) {
            message(BOSS_MESSAGE_PREFIX + "I thought my golems were stronger than that!");
            message(BOSS_MESSAGE_PREFIX + "No matter, I'll just have to eliminate you myself.", 20);
            var seraph = get("SERAPH");
            if (seraph == null) {
                Dungeons.instance.getLogger().severe("Had to abandon Waterway Seraph fight as Seraph was null. Please investigate!");
                end();
                return;
            }
            seraph.getEnemyDirector().resetSpeed();
            seraph.getHealthSystem().setImmune(false);
            phase = 5;
        }
        if (!isOnCooldown("SPAWN_FIRE_PILLAR")) {
            var count = RandomUtils.getRandomEx(4, 11);
            while (count-- > 0) {
                spawnFirePillar();
            }
            cooldown("SPAWN_FIRE_PILLAR", 120);
        }
    }

    private void phase5() {

        var seraph = get("SERAPH");
        if (seraph == null) {
            Dungeons.instance.getLogger().severe("Had to abandon Waterway Seraph fight as Seraph was null. Please investigate!");
            end();
            return;
        }
        if (seraph.dead) {
            message(BOSS_MESSAGE_PREFIX + "No.....");
            message(BOSS_MESSAGE_PREFIX + "It can't end like this...", 30);
            for (var enemy : getEnemies()) enemy.remove();
            spawn("SERAPH_FINAL", Objects.requireNonNull(EnemyBuilder.getBuilder("water_seraph_boss_final")), seraph.getLocation());
            phase = 6;
        }

        if (!isOnCooldown("SPAWN_AD_PHASE3")) {
            var count = RandomUtils.getRandomEx(3, 7);
            while (count-- > 0 && getAliveEnemyCount() < 30) {
                var choice = EnemyBuilder.getBuilder(RandomUtils.getChoice(PHASE3_AD_OPTIONS));
                spawn(adjustBuilderTargetingBehaviour(choice), RandomUtils.getChoice(SPAWN_AD_LOCATIONS));
            }
            cooldown("SPAWN_AD_PHASE3", 75);
        }

        if (!isOnCooldown("SPAWN_FIRE_PILLAR")) {
            var count = RandomUtils.getRandomEx(7, 16);
            while (count-- > 0) {
                spawnFirePillar();
            }
            cooldown("SPAWN_FIRE_PILLAR", 100);
        }
    }

    private void phase6() {
        var seraph = get("SERAPH_FINAL");
        if (seraph == null || seraph.dead) {
            message("<grey>You have defeated the <#7335db>Water Seraph</#7335db>! Congratulations! <em>Please collect your loot.</em>", 10);
            phase = 7;
            for (var registered : registered()) {
                var drops = drops(registered, ITEM_REWARD_LOCATION.length);
                for (var i = 0; i < ITEM_REWARD_LOCATION.length; i++) {
                    GameFloatingItem.spawn(ITEM_REWARD_LOCATION[i], drops.get(i), registered);
                }
                GameFloatingItem.spawn(EYE_REWARD_LOCATION, ItemFactory.build("seraphs_eye"), registered);
            }
        }
    }

    private void phase7() {
        boolean left = true;
        for (var registered : registered()) {
            if (BOSS_ARENA.isWithin(registered.getLocation())) {
                left = false;
                break;
            }
        }
        if (left) {
            end();
        }
    }

    private void spawnFirePillar() {
        int rx = RandomUtils.getRandomEx(BOSS_ARENA.getLowerCornerAsLocation().getBlockX(), BOSS_ARENA.getUpperCornerAsLocation().getBlockX() + 1);
        int rz = RandomUtils.getRandomEx(BOSS_ARENA.getLowerCornerAsLocation().getBlockZ(), BOSS_ARENA.getUpperCornerAsLocation().getBlockZ() + 1);
        final Location pillar = new Location(Dungeons.w, rx + 0.5, 87, rz + 0.5);
        if (pillar.getBlock().getType() == Material.AIR) {
            return;
        }
        final var data = pillar.getBlock().getBlockData();
        for (var player : registered()) player.player.sendBlockChange(pillar, PILLAR_WARNING_BLOCK);
        new BukkitRunnable() {
            int time = 100;

            @Override
            public void run() {
                time--;
                if (time <= 0 || phase < 1) {
                    cancel();
                    for (var player : registered()) player.player.sendBlockChange(pillar, data);
                }
                else if (time < 40) {
                    for (int y = pillar.getBlockY(); y <= pillar.getBlockY() + 13; y++) {
                        var fire = pillar.clone().set(pillar.getX(), y, pillar.getZ());
                        ParticleUtils.spawn(fire, Particle.SMALL_FLAME, 0.5, 4);
                        for (var player : registered()) {
                            var cooldownId = player.getUUID().toString() + "_FIRE_PILLAR";
                            if (player.distance(fire) < 1 && !isOnCooldown(cooldownId)) {
                                var velocity = player.player.getVelocity().multiply(INVERT_Y_VECTOR).add(PILLAR_BOUNCE_VECTOR);
                                player.player.setVelocity(velocity);
                                cooldown(cooldownId, 10);
                                if (phase > 0) {
                                    var packet = new DamagePacket(player, null, PILLAR_DAMAGE, DamageType.FIRE);
                                    player.damage(packet);
                                }
                            }
                        }
                        for (var enemy : getEnemies()) {
                            if (enemy.dead) {
                                continue;
                            }
                            if (enemy.distance(fire) < 1.3) {
                                var packet = new DamagePacket(enemy, null, PILLAR_DAMAGE * 4, DamageType.FIRE);
                                enemy.damage(packet);
                            }
                        }
                    }
                    if (time % 6 == 0) {
                        Dungeons.w.playSound(pillar, Sound.BLOCK_NOTE_BLOCK_CHIME, 0.8F, 1.4F);
                    }
                }
                else if (time == 40) {
                    for (var player : registered()) player.player.sendBlockChange(pillar, PILLAR_DANGEROUS_BLOCK);
                }
                else {
                    for (int y = pillar.getBlockY(); y <= pillar.getBlockY() + 1; y++) {
                        ParticleUtils.spawn(pillar.clone().set(pillar.getX(), y, pillar.getZ()), Particle.SMALL_FLAME, 0.3, 1);
                    }
                    if (time < 70 && time % 8 == 0) {
                        Dungeons.w.playSound(pillar, Sound.BLOCK_NOTE_BLOCK_CHIME, 0.6F, 1.2F);
                    }
                }
            }
        }.runTaskTimer(Dungeons.instance, 1, 1);
    }

    /*
    static {
        drops.add(new WeightedDrop("enchanted_book£1£enchants:ENCHANT_LUCK-1~", 60));
        drops.add(new WeightedDrop("waterway_sac£6", 40));
        drops.add(new WeightedDrop("gold_leaf£96", 40));
        drops.add(new WeightedDrop("seraph_sword£1", 5));
        drops.add(new WeightedDrop("enchanted_book£1£enchants:ENCHANT_CONCENTRATION-1~", 25));
        drops.add(new WeightedDrop("enchanted_book£1£enchants:ENCHANT_SHARPNESS-3~", 20));
        drops.add(new WeightedDrop("waterway_seraph_key£5", 5));
        drops.add(new WeightedDrop("seraphs_eye£3", 5));
        drops.add(new WeightedDrop("eye_of_spider£2", 10));
        drops.add(new WeightedDrop("clear_glass_helmet£1", 5));
        drops.add(new WeightedDrop("seraphs_pyjamas£1", 5));
        drops.add(new WeightedDrop("seraph_ooze", 2));
        drops.add(new WeightedDrop("seraphs_guide_to_summoning_spirits", 2));
    }
*/
}
