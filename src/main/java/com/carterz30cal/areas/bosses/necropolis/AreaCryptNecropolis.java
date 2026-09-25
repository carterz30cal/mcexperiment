package com.carterz30cal.areas.bosses.necropolis;

import com.carterz30cal.areas.PlayerTeleport;
import com.carterz30cal.areas.areas.GameAreaNecropolis;
import com.carterz30cal.areas.bosses.AbstractAreaBoss;
import com.carterz30cal.areas.bosses.WeightedDrop;
import com.carterz30cal.areas.spawners.AbstractEnemySpawner;
import com.carterz30cal.areas.spawners.FixedCountEnemySpawner;
import com.carterz30cal.entities.display.AnimatedGameItemDisplay;
import com.carterz30cal.entities.display.GameItemDisplay;
import com.carterz30cal.entities.display.GameTextDisplay;
import com.carterz30cal.entities.enemies.core.EnemyManager;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.*;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class AreaCryptNecropolis extends AbstractAreaBoss implements BlockMutator {
    public static final Box REGISTRATION_BOX = new Box(4, 75, 396, -12, 63, 384);
    private static final Box PASTE_BOX = new Box(-41, 60, 430, -158, 105, 316);
    private static final List<AreaCryptNecropolis> activeCrypts = new ArrayList<>();
    private final UUID uuid;
    private final GamePlayer owner;
    private final NecropolisCryptTier tier;
    private final List<Location> playerSpawnLocations = new ArrayList<>();
    private final Map<Integer, List<Box>> doorBlocks = new HashMap<>();
    private final Map<Integer, Box> runeDoors = new HashMap<>();
    private final Map<Integer, Location> runes = new HashMap<>();
    private final List<Location> minibosses = new ArrayList<>();
    private final List<AbstractEnemySpawner> enemies = new ArrayList<>();
    private final List<Box> triggerZones = new ArrayList<>();
    private final List<Location> lootChests = new ArrayList<>();
    private PastingBox paste;
    private World instancedWorld;
    private boolean hasNotifiedLowTPS = false;
    private Box doorBoss;
    private GameTextDisplay bossDoorDisplay;
    private Box bossTriggerZone;
    private GameEnemy boss;
    private boolean bossDoorOpened = false;
    private String rune;
    private GameItemDisplay floatingRune;
    private boolean runeCollected;
    private Location bossLocation;
    private GameEnemy keyMiniboss;
    private int totalPlayers;
    private int ticksOpen;

    public AreaCryptNecropolis(GamePlayer owner, NecropolisCryptTier tier) {
        uuid = UUID.randomUUID();
        this.tier = tier;
        GameAreaNecropolis.instance.register(this);
        register(owner);
        phase = -1;
        this.owner = owner;
        activeCrypts.add(this);
        start();
    }

    /**
     * Is this player currently in a crypt or generating one?
     *
     * @param player the player we're checking
     * @return <code>true</code> if this player is attempting a crypt or generating one, <code>false</code> otherwise.
     */
    public static boolean inCrypt(GamePlayer player) {
        for (var c : activeCrypts) {
            if (c.isRegistered(player)) {
                return true;
            }
        }
        return false;
    }

    private static Box getBox(Sign sign, String line3, Location loc) {
        var spl = line3.split(",");
        var width = Integer.parseInt(spl[0]);
        var height = Integer.parseInt(spl[1]);

        var box = new Box(loc);
        var rotation = getSignDir(sign);
        box = box.expand(rotation.multiply(width)).extend(height);
        return box;
    }

    private static Vector getSignDir(Sign sign) {
        var rotation = ((org.bukkit.block.data.type.Sign) sign.getBlockData()).getRotation();
        return new Vector(rotation.getDirection().getZ(), 0, rotation.getDirection().getX());
    }

    @Override
    public void start() {
        drops(tier.drops);
    }

    @Override
    public void tick() {
        super.tick();
        if (phase < 2) {
            for (var p : REGISTRATION_BOX.getPlayersWithin()) {
                if (!isRegistered(p)) {
                    register(p);
                    p.sendMessage("<grey>You've registered to plunder a <red>Crypt</red>, which will open shortly! Please be patient!");
                }
            }
            if (phase == -1) {
                createInstance();
                if (instancedWorld != null) {
                    phase = 0;
                    paste = new PastingBox(PASTE_BOX, Dungeons.w, instancedWorld);
                }
            }
            else if (phase == 0) {
                var tps = (float) Dungeons.instance.getServer().getTPS()[0];
                if (tps < 13 && !hasNotifiedLowTPS) {
                    owner.sendMessage("<red>This server has poor TPS, so this crypt will take longer to generate! Sorry for the wait!");
                    hasNotifiedLowTPS = true;
                }
                int pastes = Math.round(600 * tps);
                if (hasNotifiedLowTPS) {
                    pastes /= 2;
                }
                while (pastes > 0 && paste.next(this)) {
                    pastes--;
                }
                if (paste.finished()) {
                    phase = 1;
                    cooldown("WARP_PLAYERS_IN", 20 * 5);
                }
            }
            else if (phase == 1) {
                if (!isOnCooldown("WARP_PLAYERS_IN")) {
                    phase = 2;
                    totalPlayers = registered().size();
                    doorBoss.setTemporaryWithin(Material.NETHER_BRICKS);
                    for (var group : doorBlocks.values()) {
                        var selection = RandomUtils.getChoice(group);
                        selection.setTemporaryWithin(tier.wall);
                    }
                    var runeg = RandomUtils.getChoice(runeDoors.keySet());
                    rune = RandomUtils.getChoice(tier.runes);
                    floatingRune = new AnimatedGameItemDisplay(rune, runes.get(runeg)).setBobSpeed(45).setRotationSpeed(45);

                    for (var r : runeDoors.entrySet()) {
                        if (Objects.equals(r.getKey(), runeg)) {
                            continue;
                        }
                        r.getValue().setTemporaryWithin(tier.wall);
                    }
                    var key = RandomUtils.getChoice(minibosses);
                    for (var miniboss : minibosses) {
                        var spawning = EnemyManager.spawn(RandomUtils.getChoice(tier.minibosses), miniboss, true);
                        if (miniboss.equals(key)) {
                            keyMiniboss = spawning;
                        }
                    }
                    teleportPlayersIn();
                }
            }
        }
        else {
            ticksOpen++;
            if (ticksOpen == 20 * 60 * 15) {
                message("<grey><i>The crypt begins to shake, you should probably leave soon!");
            }
            else if (ticksOpen == 20 * 60 * 19) {
                message("<grey><i>The crypt violently shakes!");
                message("<grey><i>You probably have a minute left before this place collapses!", 1);
            }
            else if (ticksOpen == 20 * 60 * 20) {
                message("<grey><i>The crypt has collapsed around you, killing everyone and everything inside.");
                for (var p : registered()) {
                    p.kill("<red>You were killed by a falling rock.");
                }
                end();
            }
            for (var enemy : enemies) {
                enemy.tick();
            }

            for (var player : registered()) {
                if (!runeCollected && player.distance(floatingRune.getLocation()) < 2.6) {
                    runeCollected = true;
                    message("<light_purple><b>WOW!</b></light_purple> <grey>" + player.player.getName() + " has found a rune!");
                    for (var g : registered()) {
                        g.giveItem(ItemFactory.build(rune));
                    }
                    floatingRune.remove();
                    break;
                }
            }

            if (phase == 2) {
                if (keyMiniboss == null || !keyMiniboss.isAlive()) {
                    phase = 3;
                    message("<grey>You have defeated a key miniboss, and can now head into the boss room!", 10);
                }
            }
            else if (phase == 3) {
                if (!bossTriggerZone.getPlayersWithin().isEmpty()) {
                    phase = 4;
                    doorBoss.reset();
                    bossDoorOpened = true;
                    boss = EnemyManager.spawn(RandomUtils.getChoice(tier.bosses), bossLocation);
                }
            }
            else if (phase == 4) {
                if (!boss.isAlive()) {
                    message("<grey>You have defeated the ruler of this Crypt, and may now collect your loot and leave!", 20);
                    phase = 5;
                }
            }
        }

    }

    private void teleportPlayersIn() {
        var selectedSpawn = RandomUtils.getChoice(playerSpawnLocations);
        selectedSpawn.setWorld(instancedWorld);
        for (var r : registered()) {
            r.teleport(selectedSpawn);
        }
    }

    @Override
    public @Nullable List<String> scoreboard(@NotNull GamePlayer player) {
        if (!isRegistered(player)) {
            return null;
        }
        var lore = new ArrayList<String>();
        lore.add("<red><b>CRYPT");
        if (phase == -1) {
            lore.add("<grey>Loading instance...");
        }
        else if (phase == 0) {
            lore.add("<grey>Loading - <green>" + StringUtils.asPercent(paste.progress()));
        }
        else if (phase == 1) {
            lore.add("<grey>Waiting for more players!");
            lore.add("<grey>Crypt will open in<green>" + StringUtils.getPrettyTime(cooldown("WARP_PLAYERS_IN")));
        }
        else {
            lore.add("<grey>Players alive: <aqua>" + registered().size());
            if (ticksOpen >= 20 * 60 * 19) {
                lore.add("<grey>Time:<dark_red>" + StringUtils.getPrettyTime(ticksOpen));
            }
            else if (ticksOpen >= 20 * 60 * 15) {
                lore.add("<grey>Time:<red>" + StringUtils.getPrettyTime(ticksOpen));
            }
            else {
                lore.add("<grey>Time:<gold>" + StringUtils.getPrettyTime(ticksOpen));
            }
            if (phase == 2) {
                lore.add("<grey>Key: <red>No");
            }
            else if (phase == 3) {
                lore.add("<grey>Key: <green>Yes");
            }
            else if (phase == 4) {
                lore.add("");
                lore.add("<red><b>BOSS");
                lore.add(boss.name());
                lore.add("<red>" + boss.getHealth() + "♥");
            }
        }

        return lore;
    }

    @Override
    public void onLeftFight(@NotNull GamePlayer player, @NotNull LeftFightReason reason) {
        super.onLeftFight(player, reason);

        if (isRegistered(player)) {
            deregister(player);
            removeBossBar(player);
        }
    }

    private void createInstance() {
        var creator = new WorldCreator("necropolis-crypt-" + uuid.toString());
        creator.type(WorldType.FLAT);
        creator.generator(new ChunkGenerator() {

        });
        creator.biomeProvider(new BiomeProvider() {
            @Override
            public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
                return Biome.MUSHROOM_FIELDS;
            }

            @Override
            public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
                return List.of(Biome.MUSHROOM_FIELDS);
            }
        });
        try {
            instancedWorld = Dungeons.instance.getServer().createWorld(creator);
        } catch (IllegalStateException e) {
            Dungeons.instance.getLogger().warning("[Necropolis Crypt] Couldn't create instance at this moment, retrying!");
            instancedWorld = null;
        }
    }

    @Override
    public void register(GamePlayer player) {
        super.register(player);
        player.sendMessage("<grey>You've registered to plunder a <red>Crypt</red>, which will open shortly!");
    }

    @Override
    public void onRightClick(GamePlayer player, Location clicked) {
        super.onRightClick(player, clicked);
        if (lootChests.contains(clicked)) {
            if (player.player.getInventory().firstEmpty() == -1) {
                player.sendMessage("<red>Your inventory is full!");
                player.play(Key.key("block.chest.locked"), Sound.Source.BLOCK, 1.1, 0.9);
                return;
            }
            var data = (Chest) clicked.getBlock().getState();
            data.open();
            data.update(true, false);
            player.play(Key.key("block.chest.open"), Sound.Source.BLOCK, 0.9, 0.9);
            for (var r : registered()) {
                var drops = drops(r, RandomUtils.getRandom(3, 5));
                r.sendMessage("<gold><b>CHEST LOOT!");
                Map<String, Integer> loot = new HashMap<>();
                for (var drop : drops) {
                    var d = ItemFactory.getItem(drop);
                    loot.put(d.id, loot.getOrDefault(d.id, 0) + drop.getAmount());
                    r.giveItem(drop, true);
                }
                for (var entry : loot.entrySet()) {
                    var e = ItemFactory.getItem(entry.getKey());
                    r.sendMessage("<gold>- <" + e.rarity.textColor.asHexString() + ">" + e.name + "<dark_grey> x" + entry.getValue());
                }
            }
            lootChests.remove(clicked);
        }
    }

    @Override
    public void end() {
        for (var r : registered()) {
            r.teleport(PlayerTeleport.NECROPOLIS_CRYPTS);
        }
        clearRegistered();
        boolean unloaded = Dungeons.instance.getServer().unloadWorld(instancedWorld, false);
        if (!unloaded) {
            Dungeons.instance.getLogger().warning("[Necropolis Crypt] Failed to unload world " + instancedWorld.getName());
        }
        if (Dungeons.instance.isEnabled()) {
            Dungeons.instance.getServer().getScheduler().runTaskLater(Dungeons.instance, () -> {
                FileUtils.deleteDir(instancedWorld.getWorldFolder());
            }, 20L);
        }
        else {
            FileUtils.deleteDir(instancedWorld.getWorldFolder());
        }

        if (bossDoorDisplay != null) {
            bossDoorDisplay.remove();
        }
        if (floatingRune != null) {
            floatingRune.remove();
        }
        GameAreaNecropolis.instance.deregister(this);
        phase = -1;
    }

    @Override
    public void disable() {
        end();
    }

    private BlockState mutateSign(BlockState data) {
        var air = Material.AIR.createBlockData().createBlockState();
        if (data instanceof Sign sign) {
            var clazz = ((TextComponent) sign.getSide(Side.FRONT).line(0)).content();
            var line2 = ((TextComponent) sign.getSide(Side.FRONT).line(1)).content();
            var line3 = ((TextComponent) sign.getSide(Side.FRONT).line(2)).content();
            var line4 = ((TextComponent) sign.getSide(Side.FRONT).line(3)).content();
            int group;
            var loc = data.getLocation();
            loc.setWorld(instancedWorld);
            switch (clazz) {
                case "spawn":
                    playerSpawnLocations.add(data.getLocation());
                    break;
                case "rune":
                    group = Integer.parseInt(line2);
                    runes.put(group, loc.clone().add(0.5, 1, 0.5));
                    break;
                case "miniboss":
                    minibosses.add(loc);
                    break;
                case "boss":
                    bossLocation = loc;
                    break;
                case "loot":
                    lootChests.add(loc);
                    var chdata = (org.bukkit.block.data.type.Chest) Material.CHEST.createBlockData();
                    chdata.setFacing(((org.bukkit.block.data.type.Sign) data.getBlockData()).getRotation());
                    return chdata.createBlockState().copy(loc);
                case "mob":
                    List<String> options = switch (line2.strip()) {
                        case "regular" -> tier.regularEnemyOptions;
                        case "ranged" -> tier.rangedEnemyOptions;
                        default -> new ArrayList<>();
                    };
                    var spawner = new FixedCountEnemySpawner(loc, Integer.parseInt(line3), Integer.parseInt(line4), options.toArray(new String[0]));
                    enemies.add(spawner);
                    break;
                case "trigger":
                    var spl = line3.split(",");
                    var trigger = new Box(loc).expand(Integer.parseInt(spl[0]), Integer.parseInt(spl[1]), Integer.parseInt(spl[2]));
                    switch (line2.strip()) {
                        case "boss":
                            bossTriggerZone = trigger;
                            break;
                    }
                    break;
                case "door":
                    Box box;
                    try {
                        box = getBox(sign, line3, loc);
                    } catch (NumberFormatException e) {
                        return data;
                    }
                    switch (line2) {
                        case "boss":
                            doorBoss = box;
                            bossDoorDisplay = GameTextDisplay.create(uuid.toString() + "-crypt-boss-door-display", loc.clone().add(getSignDir(sign)).add(0, 1, 0));
                            bossDoorDisplay.name("<dark_red><b>BOSS DOOR");
                            break;
                        case "block":
                            group = Integer.parseInt(line4);
                            doorBlocks.putIfAbsent(group, new ArrayList<>());
                            doorBlocks.get(group).add(box);
                            break;
                        case "rune":
                            group = Integer.parseInt(line4);
                            runeDoors.put(group, box);
                            break;
                    }
            }
        }
        return air;
    }

    @Override
    public @NotNull BlockState mutate(@NotNull BlockState data) {
        var material = data.getBlockData().getMaterial();
        return switch (material) {
            case BROWN_WOOL ->
                    RandomUtils.getChoice(tier.floor).createBlockData().createBlockState().copy(data.getLocation());
            case LIGHT_GRAY_WOOL ->
                    RandomUtils.getChoice(tier.wall).createBlockData().createBlockState().copy(data.getLocation());
            case OAK_SIGN -> mutateSign(data);
            default -> data;
        };
    }

    /**
     * @author carterz30cal
     * @version 1
     * @since 1.0.0
     */
    public enum NecropolisCryptTier {
        LESSER(
                List.of(Material.DIRT, Material.COARSE_DIRT, Material.PODZOL),
                List.of(Material.STONE, Material.COBBLESTONE, Material.GRAVEL, Material.SUSPICIOUS_GRAVEL, Material.STONE_BRICKS),
                List.of("warrior_rune", "flow_rune", "standstill_rune", "separation_rune"),
                List.of("crypt_dweller_lesser", "crypt_tank_lesser"),
                List.of("crypt_ranger_lesser", "crypt_ranger_unmoving_lesser"),
                List.of("crypt_titan_lesser", "crypt_knight_lesser"),
                List.of("crypt_boss_golem"),
                new WeightedDrop("bone_dust£10", 1000),
                new WeightedDrop("weird_flesh£48", 800),
                new WeightedDrop("bone_dust£82", 400),
                new WeightedDrop("bone_dust£93", 250),
                new WeightedDrop("enchanted_book£1£enchants:ENCHANT_PEARLED-1", 25),
                new WeightedDrop("crypt_pebble£1", 25),
                new WeightedDrop("enchanted_book£1£enchants:ENCHANT_CRYPT_MIGHT-1", 20),
                new WeightedDrop("enchanted_book£1£enchants:ENCHANT_STEALTH-1", 15),
                new WeightedDrop("roasted_bone_dust£1", 15),
                new WeightedDrop("magic_cactus", 10),
                new WeightedDrop("crypt_knight_sword_epic", 1),
                new WeightedDrop("pet_crypt_dweller_common", 1)
        );
        public final List<Material> floor;
        public final List<Material> wall;
        public final List<String> regularEnemyOptions;
        public final List<String> rangedEnemyOptions;
        public final List<String> minibosses;
        public final List<String> bosses;
        public final List<String> runes;
        public final WeightedDrop[] drops;

        NecropolisCryptTier(List<Material> floor, List<Material> wall, List<String> runes,
                            List<String> regularEnemyOptions,
                            List<String> rangedEnemyOptions,
                            List<String> minibosses,
                            List<String> bosses,
                            WeightedDrop... drops) {
            this.floor = floor;
            this.wall = wall;
            this.regularEnemyOptions = regularEnemyOptions;
            this.rangedEnemyOptions = rangedEnemyOptions;
            this.minibosses = minibosses;
            this.bosses = bosses;
            this.runes = runes;
            this.drops = drops;
        }
    }
}
