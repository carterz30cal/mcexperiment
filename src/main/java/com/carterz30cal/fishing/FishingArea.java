package com.carterz30cal.fishing;

import com.carterz30cal.entities.display.GameTextDisplay;
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

import java.util.*;

import static net.kyori.adventure.text.Component.text;

/**
 * @author carterz30cal
 * @version 3
 * @since 1.0.0
 */
public class FishingArea {
    private static final Map<String, FishingArea> fishingAreas = new HashMap<>();
    private static final Map<GamePlayer, FishingBobber> bobbers = new HashMap<>();
    private final List<FishingBracket> brackets;
    private int powerSubtraction;
    private int powerReduction;

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
                    area.brackets.get(rarity.ordinal()).bracketWeight = section.getInt(r);
                }
                area.brackets.removeIf((b) -> b.bracketWeight == 0);
                area.powerSubtraction = pa.getInt("power-subtraction", 0);
                area.powerReduction = pa.getInt("power-reduction", 2);
                fishingAreas.put(p, area);
            }
        }
    }


    public static FishingArea getFishingArea(String area) {
        if (!fishingAreas.containsKey(area)) {
            fishingAreas.put(area, new FishingArea());
        }
        return fishingAreas.get(area);
    }

    public void addToBracket(ItemRarity rarity, String mob) {
        brackets.get(rarity.ordinal()).bracketMobs.add(mob);
    }

    public FishingBobber getBobberUsingPower(Location location, GamePlayer owner) {
        int fishingPower;
        fishingPower = owner.stats.getStat(Stat.FISHING_POWER) - powerSubtraction;

        int adjustedPower = fishingPower;
        int startPower = 0;
        int i = 0;
        while (adjustedPower > 0 && i < brackets.size() - 1) {
            startPower += Math.min(adjustedPower, brackets.get(i).bracketWeight);
            adjustedPower -= brackets.get(i).bracketWeight;

            if (adjustedPower > 0) {
                i++;
                adjustedPower /= powerReduction;
            }
        }


        int choice = RandomUtils.getRandom(Math.max(0, startPower), getTotalWeight());
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

        Location bobberSpot = location.getBlock().getLocation().add(0, 1, 0);
        bobber.location = bobberSpot;
        bobber.uuid = UUID.randomUUID();
        bobber.title = GameTextDisplay.create(bobber.uuid + "_title", bobberSpot);
        bobber.subtitle = GameTextDisplay.create(bobber.uuid + "_subtitle", bobberSpot);

        bobber.physics = EntityUtils.spawnHologram(bobberSpot.clone().add(0, 0.4, 0), -1);
        bobber.physics.setGravity(true);
        bobber.physics.setMarker(false);
        bobber.physics.setVisible(false);
        bobber.physics.customName(text().build());
        bobber.physics.setCustomNameVisible(false);
        EntityUtils.applyKnockback(owner, bobber.physics, -100);
        bobber.physics.setVelocity(owner.getLocation().subtract(bobberSpot).toVector().normalize().setY(0.6));
        bobber.lifetime = (int)Math.round(20 * 45 * Math.log(bobber.rarity.ordinal() + 2));
        bobber.maxLifetime = bobber.lifetime;
        bobbers.put(owner, bobber);

        bobber.runTaskTimer(Dungeons.instance, 1, 1);
        return bobber;
    }

    private int getTotalWeight() {
        int power = 0;
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
     * @version 2
     * @since 1.0.0
     */
    public static class FishingBobber extends BukkitRunnable {
        public UUID uuid;
        public ItemRarity rarity;
        public List<String> bracketMobs;
        public GamePlayer owner;
        public Location location;
        public List<GameEnemy> enemies = new ArrayList<>();

        public GameTextDisplay title;
        public GameTextDisplay subtitle;

        public ArmorStand physics;

        public int lifetime = 45 * 20;
        public int maxLifetime;

        public void remove() {
            physics.remove();
            title.remove();
            subtitle.remove();
        }

        @Override
        public void cancel() {
            remove();
            super.cancel();
        }

        @Override
        public void run() {
            lifetime--;

            location = physics.getLocation();
            title.teleport(location.clone().add(0, 0.8, 0));
            subtitle.teleport(location.clone().add(0, 0.5, 0));
            Box attemptBox = new Box(location.clone().add(0, 0, 0)).expand(1, 0, 1);

            if (lifetime < 1 || bracketMobs.isEmpty()) {
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
                if (lifetime % 20 == 0) enemies.removeIf((e) -> e.dead);
                if (lifetime < maxLifetime - 40 && enemies.isEmpty()) {
                    if (attemptBox.getMiddleAsLocation().subtract(0, 1, 0).getBlock().getType() == Material.AIR) {
                        return;
                    }
                    Location attempt = attemptBox.getRandomMobLocation();
                    enemies.add(EnemyManager.spawn(RandomUtils.getChoice(bracketMobs), attempt.add(0, 0.25, 0)));
                    lifetime -= 20;
                }
                else if (lifetime % (20 * 4) == 1 && enemies.size() < 4) {
                    if (attemptBox.getMiddleAsLocation().subtract(0, 1, 0).getBlock().getType() == Material.AIR) {
                        return;
                    }
                    Location attempt = attemptBox.getRandomMobLocation();
                    enemies.add(EnemyManager.spawn(RandomUtils.getChoice(bracketMobs), attempt.add(0, 0.25, 0)));
                }
            }
        }
    }

    private static class FishingBracket {
        public int bracketWeight;
        public ItemRarity bracketRarity;
        public List<String> bracketMobs;

        private FishingBracket(ItemRarity rarity) {
            bracketMobs = new ArrayList<>();
            bracketRarity = rarity;
        }
    }
}
