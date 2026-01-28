package com.carterz30cal.fishing;

import com.carterz30cal.entities.EnemyManager;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemRarity;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.utils.EntityUtils;
import com.carterz30cal.utils.FileUtils;
import com.carterz30cal.utils.RandomUtils;
import com.carterz30cal.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FishingArea {
    private static final Map<String, FishingArea> fishingAreas = new HashMap<>();
    private final List<FishingBracket> brackets;
    private int powerSubtraction;
    private int powerReduction;

    static {
        String[] areaFiles = {
                "waterway2/fishing_areas"
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

        FishingBracket bracket = brackets.get(i);
        FishingBobber bobber = new FishingBobber();
        bobber.owner = owner;
        bobber.rarity = bracket.bracketRarity;
        bobber.bracketMobs = bracket.bracketMobs;

        Location bobberSpot = location.getBlock().getLocation().add(0, 1, 0);
        bobber.location = bobberSpot;
        bobber.displayTop = EntityUtils.spawnHologram(bobberSpot.clone().add(0, 0.4, 0), -1);
        bobber.displayBottom = EntityUtils.spawnHologram(bobberSpot.clone().add(0, 0.4, 0), -1);
        bobber.displayBottom.setGravity(true);
        bobber.displayBottom.setMarker(false);
        EntityUtils.applyKnockback(owner, bobber.displayBottom, -100);
        bobber.displayBottom.setVelocity(owner.getLocation().subtract(bobberSpot).toVector().normalize().setY(0.6));
        bobber.lifetime = (int)Math.round(20 * 45 * Math.log(bobber.rarity.ordinal() + 2));

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

    public static class FishingBobber extends BukkitRunnable {
        public ItemRarity rarity;
        public List<String> bracketMobs;
        public GamePlayer owner;
        public Location location;
        public List<GameEnemy> enemies = new ArrayList<>();

        public ArmorStand displayTop;
        public ArmorStand displayBottom;

        public int lifetime = 45 * 20;

        public void remove() {
            cancel();
            displayTop.remove();
            displayBottom.remove();
        }

        @Override
        public void run() {
            lifetime--;

            location = displayBottom.getLocation();
            displayTop.teleport(location.clone().add(0, 0.5, 0));

            if (lifetime < 1 || bracketMobs.isEmpty()) {
                displayTop.remove();
                displayBottom.remove();
                cancel();
            }
            else {
                displayTop.setCustomName(
                        StringUtils.colourString(owner.player.getDisplayName())
                );
                displayBottom.setCustomName(
                        StringUtils.colourString(
                                rarity.colour + "BOLD" + rarity.name.toUpperCase()
                                + " DARK_GRAY[" + (lifetime / 20) + "s]"
                        )
                );
                if (lifetime % 20 == 0) enemies.removeIf((e) -> e.dead);
                if (lifetime % (20 * 4) == 1 && enemies.size() < 4) {
                    Location spawnLocation = location.clone().add(0,1.25,0);
                    enemies.add(EnemyManager.spawn(RandomUtils.getChoice(bracketMobs), spawnLocation));
                }
            }
        }
    }

    private static class FishingBracket {
        public int bracketWeight;
        public ItemRarity bracketRarity;
        public List<String> bracketMobs;

        private FishingBracket(FishingBracket bracket) {
            bracketWeight = bracket.bracketWeight;
            bracketRarity = bracket.bracketRarity;
            bracketMobs = bracket.bracketMobs;
        }

        private FishingBracket(ItemRarity rarity) {
            bracketMobs = new ArrayList<>();
            bracketRarity = rarity;
        }
    }
}
