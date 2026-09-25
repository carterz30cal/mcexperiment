package com.carterz30cal.fishing;

import com.carterz30cal.entities.display.GameTextDisplay;
import com.carterz30cal.entities.enemies.core.EnemyBuilder;
import com.carterz30cal.entities.enemies.core.EnemyManager;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemRarity;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.utils.Box;
import com.carterz30cal.utils.EntityUtils;
import com.carterz30cal.utils.FileUtils;
import com.carterz30cal.utils.RandomUtils;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.kyori.adventure.text.Component.text;

/**
 * @author carterz30cal
 * @version 4
 * @since 1.0.0
 */
public class FishingArea {
    private static final Map<String, FishingArea> fishingAreas = new HashMap<>();
    private static final Map<GamePlayer, FishingBobber> bobbers = new HashMap<>();
    private final List<FishingBracket> brackets;
    static {
        String[] areaFiles = {
                "waterway/fishing_areas"
        };
        for (String file : areaFiles) {
            FileConfiguration c = FileUtils.getData(file);
            assert c != null;
            for (String p : c.getKeys(false)) {
                FishingArea area = new FishingArea();
                ConfigurationSection pa = c.getConfigurationSection(p);
                assert pa != null;
                ConfigurationSection section = pa.getConfigurationSection("brackets");
                assert section != null;
                for (String r : section.getKeys(false)) {
                    ItemRarity rarity = ItemRarity.valueOf(r);
                    area.brackets.get(rarity.ordinal()).bracketWeight = section.getLong(r + ".weight");
                    area.brackets.get(rarity.ordinal()).mobCount = section.getInt(r + ".mobs", 1);
                }
                area.brackets.removeIf((b) -> b.bracketWeight == 0);
                area.powerSubtraction = pa.getLong("power-subtraction", 0);
                area.powerReduction = pa.getLong("power-reduction", 2);
                area.powerMultiplier = pa.getLong("power-multiplier", 1);
                fishingAreas.put(p, area);
            }
        }
    }

    private long powerSubtraction;
    private long powerReduction;
    private long powerMultiplier;


    public static FishingArea getFishingArea(String area) {
        if (!fishingAreas.containsKey(area)) {
            fishingAreas.put(area, new FishingArea());
        }
        return fishingAreas.get(area);
    }

    public void addToBracket(ItemRarity rarity, String mob) {
        brackets.get(rarity.ordinal()).bracketMobs.add(mob);
    }

    /**
     * @param rarity what bracket should this go in?
     * @param mob    the rare mob to spawn
     * @since 1.0.0 [4]
     */
    public void addRare(ItemRarity rarity, String mob) {
        brackets.get(rarity.ordinal()).rareMobs.add(mob);
    }

    public FishingBobber getBobberUsingPower(Location location, GamePlayer owner) {
        long fishingPower;
        fishingPower = owner.stats.stat(Stat.FISHING_POWER) - powerSubtraction;

        long adjustedPower = fishingPower * powerMultiplier;
        if (adjustedPower < 0) return null;
        long startPower = 0;
        int i = 0;
        while (adjustedPower > 0 && i < brackets.size() - 1) {
            startPower += Math.min(adjustedPower, brackets.get(i).bracketWeight);
            adjustedPower -= brackets.get(i).bracketWeight;

            if (adjustedPower > 0) {
                i++;
                adjustedPower /= powerReduction;
            }
        }


        long choice = RandomUtils.getRandom(Math.max(0L, startPower), weight());
        i = 0;
        while (choice > 0 && i <= brackets.size() - 1) {
            choice -= brackets.get(i).bracketWeight;
            if (choice > 0) {
                i++;
            }
        }

        if (bobbers.containsKey(owner)) {
            var bobber = bobbers.get(owner);
            bobber.cancel();
        }

        FishingBracket bracket = brackets.get(i);
        FishingBobber bobber = new FishingBobber();
        bobber.owner = owner;
        bobber.rarity = bracket.bracketRarity;
        bobber.bracketMobs = bracket.bracketMobs;
        bobber.mobCount = bracket.mobCount + (int) owner.getStat(Stat.BONUS_FISH_MOBS);

        Location bobberSpot = location.getBlock().getLocation().add(0, 1, 0);
        bobber.location = bobberSpot;
        bobber.uuid = UUID.randomUUID();
        bobber.title = GameTextDisplay.create(bobber.uuid + "_title", bobberSpot);
        bobber.subtitle = GameTextDisplay.create(bobber.uuid + "_subtitle", bobberSpot);
        bobber.subsubtitle = GameTextDisplay.create(bobber.uuid + "_subtitle2", bobberSpot);

        bobber.physics = EntityUtils.spawnHologram(bobberSpot.clone().add(0, 0.4, 0), -1);
        bobber.physics.setGravity(true);
        bobber.physics.setMarker(false);
        bobber.physics.setVisible(false);
        bobber.physics.customName(text().build());
        bobber.physics.setCustomNameVisible(false);
        EntityUtils.applyKnockback(owner, bobber.physics, -100);
        bobber.physics.setVelocity(owner.getLocation().subtract(bobberSpot).toVector().normalize().setY(0.6));
        bobber.lifetime = (int) Math.round(20 * 55 * Math.log(bobber.rarity.ordinal() + 2));
        bobber.maxLifetime = bobber.lifetime;
        if (bracket.rareMobs.isEmpty()) {
            bobber.rare = null;
        }
        else {
            bobber.rare = RandomUtils.getChoice(bracket.rareMobs);
        }
        bobbers.put(owner, bobber);

        bobber.runTaskTimer(Dungeons.instance, 1, 1);
        return bobber;
    }

    private long weight() {
        long power = 0L;
        for (FishingBracket bracket : brackets) {
            power += bracket.bracketWeight;
        }
        return power;
    }

    public FishingArea() {
        brackets = new ArrayList<>();
        for (int i = 0; i < ItemRarity.values().length; i++) {
            brackets.add(new FishingBracket(ItemRarity.values()[i]));
        }
    }

    /**
     * @author carterz30cal
     * @version 3
     * @since 1.0.0
     */
    public static class FishingBobber extends BukkitRunnable {
        public UUID uuid;
        public ItemRarity rarity;
        public List<String> bracketMobs;
        public GamePlayer owner;
        public Location location;
        public List<GameEnemy> enemies = new ArrayList<>();
        public @Nullable String rare;

        public GameTextDisplay title;
        public GameTextDisplay subtitle;
        public GameTextDisplay subsubtitle;

        public ArmorStand physics;

        public int lifetime = 45 * 20;
        public int maxLifetime;
        public int mobCount;

        public void remove() {
            physics.remove();
            title.remove();
            subtitle.remove();
            subsubtitle.remove();
        }

        @Override
        public void cancel() {
            remove();
            owner.bobber = null;
            super.cancel();
        }

        @Override
        public void run() {
            lifetime--;

            location = physics.getLocation();
            title.teleport(location.clone().add(0, 0.9, 0));
            subtitle.teleport(location.clone().add(0, 0.6, 0));
            subsubtitle.teleport(location.clone().add(0, 0.3, 0));
            Box attemptBox = new Box(location.clone().add(0, 0, 0)).expand(1, 0, 1);

            if (lifetime < 1 || mobCount == 0 || bracketMobs.isEmpty()) {
                if (lifetime < 1) {
                    for (var mob : enemies) mob.remove();
                }
                if (rare != null) {
                    Location attempt = attemptBox.getRandomMobLocation();
                    enemies.add(EnemyManager.spawn(rare, attempt.add(0, 0.25, 0)));
                    owner.sendMessage("<blue><b>ELUSIVE!</b></blue><aqua> This bobber has lured in a " + EnemyBuilder.getBuilder(rare).getEnemyData().mmName);
                }
                cancel();
            }
            else {
                title.name(owner.player.displayName());
                subtitle.name(
                        text().append(
                                text(rarity.name.toUpperCase(), rarity.textColor).decorate(TextDecoration.BOLD),
                                text().color(NamedTextColor.DARK_GRAY).append(
                                        text(" ["),
                                        text(lifetime / 20),
                                        text("s]")
                                )
                        ).build()
                );
                subsubtitle.name("<gold>" + mobCount + " mobs left!</gold>");
                if (lifetime % 20 == 0) {
                    int check = enemies.size();
                    enemies.removeIf((e) -> e.dead);
                    mobCount = mobCount - (check - enemies.size());
                }

                if (mobCount - enemies.size() > 0) {
                    if (lifetime < maxLifetime - 100 && enemies.isEmpty()) {
                        if (attemptBox.getMiddleAsLocation().subtract(0, 1, 0).getBlock().getType() == Material.AIR) {
                            return;
                        }
                        Location attempt = attemptBox.getRandomMobLocation();
                        enemies.add(EnemyManager.spawn(RandomUtils.getChoice(bracketMobs), attempt.add(0, 0.25, 0)));
                        lifetime -= 20;
                    }
                    else if (lifetime < maxLifetime - 15 && lifetime % (20 * 2) == 1 && enemies.size() < 3) {
                        if (attemptBox.getMiddleAsLocation().subtract(0, 1, 0).getBlock().getType() == Material.AIR) {
                            return;
                        }
                        Location attempt = attemptBox.getRandomMobLocation();
                        enemies.add(EnemyManager.spawn(RandomUtils.getChoice(bracketMobs), attempt.add(0, 0.25, 0)));
                    }
                }
            }
        }
    }

    private static class FishingBracket {
        public long bracketWeight;
        public ItemRarity bracketRarity;
        public List<String> bracketMobs;
        public List<String> rareMobs;
        public int mobCount;

        private FishingBracket(ItemRarity rarity) {
            bracketMobs = new ArrayList<>();
            rareMobs = new ArrayList<>();
            bracketRarity = rarity;
        }
    }
}
