package com.carterz30cal.items.abilities.necropolis.items;

import com.carterz30cal.areas.Areas;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.enemies.core.EnemyManager;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.ItemLootTable;
import com.carterz30cal.items.abilities.implementation.*;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.utils.*;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class TreasureDetectorAbility extends GameAbility
        implements AbilityWithDescription, AbilityWithTick, AbilityWithClick {

    private static final Map<Areas, Map<GamePlayer, TreasureSpot>> spots = new HashMap<>();
    private static final Map<Areas, TreasureOptions> options = new HashMap<>();

    static {
        var option = new TreasureOptions();
        option.area = Areas.NECROPOLIS;
        option.items = new ItemLootTable();
        option.items.add("gold_nugget", 1, 1, 100, 1);
        option.items.add("purple_popcorn", 8, 14, 10, 1);
        option.boundingBox = new Box(42, -34, 325, 259, 62);
        option.enemyChance = 60;
        option.baseSearchRange = 4;
        option.enemies.add("treasure_mouldy_bull");
        option.enemies.add("treasure_randsplode");
        option.enemies.add("treasure_triblaze");
        options.put(Areas.NECROPOLIS, option);
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var desc = "<grey>Look around selected areas for buried treasure! You'll see particles when you're nearby.";
        if (context.getOwner() != null && !options.containsKey(context.getOwner().area)) {
            desc += " Your current zone contains <red>no treasure</red>.";
        }
        else if (context.getOwner() == null) {
            desc += " This zone may have treasure.";
        }
        else {
            desc += " Your current zone contains <gold>lots of treasure</gold>!";
        }
        return StringUtils.wrapText(desc, 36);
    }

    @Override
    public String name(PlayerAbilityContext context) {
        return "Treasure Hunt";
    }

    @Override
    public void click(PlayerAbilityContext context, Situation situation, Location location) {
        if (situation != Situation.LEFT_CLICK_ARM_SWING && situation != Situation.RIGHT_CLICK) {
            return;
        }
        if (context == null || location == null || !(context.getOwner() instanceof GamePlayer player)) {
            return;
        }
        var spot = get(player);
        if (spot == null) {
            return;
        }
        var blocked = new Location(location.getWorld(), location.getBlockX(), location.getBlockY() - 1, location.getBlockZ());
        if (blocked.equals(spot.location)) {
            var option = options.get(player.area);
            if (RandomUtils.getRandomEx(0, 100) < option.enemyChance && !option.enemies.isEmpty()) {
                player.sendMessage("<red><b>AHH!</b> You dug up an angry creature!");
                player.playSound(Sound.ENTITY_CREEPER_HURT, 1, 0.9);
                EnemyManager.spawn(RandomUtils.getChoice(option.enemies), blocked.clone().add(0, 1, 0));
            }
            else {
                var item = option.items.generateContextOne(player);
                var i = ItemFactory.getItem(item.getItemStack());
                var name = i.name;
                player.playSound(Sound.BLOCK_GRASS_BREAK, 0.8, 0.9);
                var a = player.getStat(Stat.DETECTION) / 100L;
                if (RandomUtils.getRandomEx(0, 100) < player.getStat(Stat.DETECTION) - (a * 100)) {
                    a++;
                }
                a++;
                var stack = item.getItemStack();
                stack.setAmount((int) a);

                player.sendMessage("<green><b>YAY!</b> You dug up <" + i.rarity.textColor.asHexString() + ">" + a + "x " + name + "!");
                player.giveItem(stack);
                player.playSound(Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1.3);
            }
            spots.get(player.area).remove(player);
        }
        else {
            if (!spot.guesses.contains(blocked)) {
                spot.guesses.add(blocked);
            }
        }
    }

    @Override
    public void tick(ContextWithAbility<? extends GameEntity> context, int tick) {
        if (context == null || !(context.getOwner() instanceof GamePlayer player)) return;
        var spot = get(player);
        if (spot == null) return;
        if (tick % 12 == 1) {
            for (int x = -spot.radius - 1; x <= spot.radius + 1; x++) {
                for (int z = -spot.radius - 1; z <= spot.radius + 1; z++) {
                    if (Math.abs(x) == spot.radius + 1 || Math.abs(z) == spot.radius + 1) {
                        ParticleUtils.spawn(spot.centre.clone().add(x, 1.05, z), Particle.SMALL_FLAME, 0.5, 2);
                    }
                }
            }
        }
        for (var location : spot.guesses) {
            var dist = MathsUtils.chebyshev(spot.location, location);
            ParticleUtils.spawn(location.clone().add(0.5, 1.2, 0.5), new Particle.DustOptions(dist < 2 ? Color.BLUE : (dist < 5 ? Color.YELLOW : Color.RED), 1.3F), 0.1);
        }
    }

    /**
     * @param owner the owner of the spot
     * @return a <code>TreasureSpot</code>, if one exists or has been generated this tick.
     * @since 1.0.0 [1]
     */
    private @Nullable TreasureSpot get(@Nullable GamePlayer owner) {
        if (owner == null) return null;
        if (!options.containsKey(owner.area)) return null;
        spots.putIfAbsent(owner.area, new HashMap<>());

        var spot = spots.get(owner.area).get(owner);
        if (spot == null) {
            var option = options.get(owner.area);
            spot = new TreasureSpot();
            spot.centre = option.boundingBox.getRandomMobLocation().subtract(0, 1, 0);
            var detection = owner.getStat(Stat.DETECTION);
            if (RandomUtils.getRandomEx(0, 100) < detection - (detection / 100L)) detection += 100;
            spot.radius = Math.max(1, option.baseSearchRange - Math.toIntExact(detection / 100));
            int attempts = 0;
            do {
                int rx = RandomUtils.getRandom(-spot.radius, spot.radius);
                int rz = RandomUtils.getRandom(-spot.radius, spot.radius);
                spot.location = spot.centre.clone().add(rx, 0, rz);
                attempts++;
            }
            while (!spot.location.getBlock().isSolid() && spot.location.clone().add(0, 1, 0).getBlock().isSolid() && attempts < 40);
            if (!spot.location.getBlock().isSolid() || spot.location.clone().add(0, 1, 0).getBlock().isSolid()) {
                return null;
            }
            spots.get(owner.area).put(owner, spot);
        }
        return spot;
    }

    /**
     * @author carterz30cal
     * @version 1
     * @since 1.0.0
     */
    private static class TreasureSpot {
        public Location location;
        /**
         * Where do we spawn the particles from?
         * <br>This is so we can do a little minigame when the player gets near to the
         * treasure spot.
         */
        public Location centre;
        /**
         * How far out do we spawn particles?
         */
        public int radius;
        /**
         * Where has the player guessed so far?
         */
        public List<Location> guesses = new ArrayList<>();
    }

    /**
     * @author carterz30cal
     * @version 1
     * @since 1.0.0
     */
    private static class TreasureOptions {
        public Areas area;
        public ItemLootTable items;
        public final List<String> enemies = new ArrayList<>();
        public Box boundingBox;
        public int enemyChance;
        public int baseSearchRange;
    }

}
