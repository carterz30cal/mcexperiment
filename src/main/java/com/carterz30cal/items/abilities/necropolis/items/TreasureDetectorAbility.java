package com.carterz30cal.items.abilities.necropolis.items;

import com.carterz30cal.areas.Areas;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemLootTable;
import com.carterz30cal.items.abilities.implementation.*;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.utils.Box;
import com.carterz30cal.utils.ParticleUtils;
import com.carterz30cal.utils.RandomUtils;
import com.carterz30cal.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        option.boundingBox = new Box(13, 368, 6, 380, 69);
        options.put(Areas.NECROPOLIS, option);
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var desc = "<grey>Look around selected areas for buried treasure! You'll see particles when you're nearby.";
        return StringUtils.wrapText(desc, 36);
    }

    @Override
    public String name(PlayerAbilityContext context) {
        return "Treasure Hunt";
    }

    @Override
    public void click(PlayerAbilityContext context, Situation situation) {

    }

    @Override
    public void tick(ContextWithAbility<? extends GameEntity> context, int tick) {
        if (context == null || !(context.getOwner() instanceof GamePlayer player)) return;
        var spot = get(player);
        if (spot == null) return;
        if (tick % 15 == 1) {
            for (int x = -spot.radius; x <= spot.radius; x++) {
                for (int z = -spot.radius; z <= spot.radius; z++) {
                    ParticleUtils.spawn(spot.centre.clone().add(x, 0.1, z), Particle.FALLING_HONEY, 0.4, 2);
                }
            }
        }

    }

    /**
     * @param owner the owner of the spot
     * @return a <code>TreasureSpot</code>, if one exists or has been generated this tick.
     * @since 1.0.0 [1]
     * @implNote currently uses a recursive call to fix issues - should fix!
     */
    private @Nullable TreasureSpot get(@Nullable GamePlayer owner) {
        if (owner == null) return null;
        if (!options.containsKey(owner.area)) return null;
        spots.putIfAbsent(owner.area, new HashMap<>());
        var spot = spots.get(owner.area).get(owner);
        if (spot != null) {
            return spot;
        }
        else {
            var option = options.get(owner.area);
            spot = new TreasureSpot();
            spot.centre = option.boundingBox.getRandomMobLocation();
            var detection = owner.getStat(Stat.DETECTION);
            if (RandomUtils.getRandomEx(0, 100) < detection - (detection / 100L)) detection += 100;
            spot.radius = Math.max(1, 7 - Math.toIntExact(detection / 100));
            int rx = RandomUtils.getRandom(-spot.radius, spot.radius);
            int rz = RandomUtils.getRandom(-spot.radius, spot.radius);
            spot.location = spot.centre.clone().add(rx, 0, rz);
            if (!spot.location.getBlock().isSolid()) return get(owner);

            Dungeons.instance.getLogger().fine(spot.location.toString());
            spots.get(owner.area).put(owner, spot);
            return spot;
        }
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
    }

    /**
     * @author carterz30cal
     * @version 1
     * @since 1.0.0
     */
    private static class TreasureOptions {
        public Areas area;
        public ItemLootTable items;
        public List<String> enemies;
        public Box boundingBox;
    }

}
