package com.carterz30cal.utils;

import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.mining.MiningManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author carterz30cal
 * @version 3
 * @since 1.0.0
 */
public class Box {
    protected final int x1;
    protected final int y1;
    protected final int z1;
    protected final int x2;
    protected final int y2;
    protected final int z2;
    private final List<BukkitRunnable> temporary = new ArrayList<>();
    protected World world;

    public Box(Location l1, Location l2) {
        if (!l1.getWorld().equals(l2.getWorld())) {
            throw new IllegalArgumentException("The worlds these locations belong to do not match!");
        }
        x1 = Math.min(l1.getBlockX(), l2.getBlockX());
        x2 = Math.max(l1.getBlockX(), l2.getBlockX());
        y1 = Math.min(l1.getBlockY(), l2.getBlockY());
        y2 = Math.max(l1.getBlockY(), l2.getBlockY());
        z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
        z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());
        world = l1.getWorld();
    }
    public Box(int x1, int y1, int z1, int x2, int y2, int z2) {
        this(x1, y1, z1, x2, y2, z2, Dungeons.w);
    }

    public Box(int x1, int y1, int z1, int x2, int y2, int z2, World world) {
        this.x1 = Math.min(x1, x2);
        this.x2 = Math.max(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.y2 = Math.max(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.z2 = Math.max(z1, z2);
        this.world = world;
    }

    public Box(int x1, int x2, int z1, int z2, int y) {
        this.x1 = Math.min(x1, x2);
        this.x2 = Math.max(x1, x2);
        this.z1 = Math.min(z1, z2);
        this.z2 = Math.max(z1, z2);
        this.y1 = y;
        this.y2 = y;
        this.world = Dungeons.w;
    }

    public Box(int x, int y, int z) {
        this.x1 = x;
        this.y1 = y;
        this.z1 = z;
        this.x2 = x;
        this.y2 = y;
        this.z2 = z;
        this.world = Dungeons.w;
    }

    /**
     * Copy constructor
     *
     * @param original the original to clone
     * @since 1.0.0 [3]
     */
    public Box(Box original) {
        this.x1 = original.x1;
        this.y1 = original.y1;
        this.z1 = original.z1;
        this.x2 = original.x2;
        this.y2 = original.y2;
        this.z2 = original.z2;
        this.world = original.world;
    }

    public Box(Location location) {
        this(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        this.world = location.getWorld();
    }

    public Box expand(int by) {
        return new Box(x1 - by, y1 - by, z1 - by, x2 + by, y2 + by, z2 + by, world);
    }

    public Box expand(int byX, int byY, int byZ) {
        return new Box(x1 - byX, y1 - byY, z1 - byZ, x2 + byX, y2 + byY, z2 + byZ, world);
    }

    public Box expand(Vector v) {
        return expand(v.getBlockX(), v.getBlockY(), v.getBlockZ());
    }

    /**
     * Extends this box upwards.
     *
     * @param byY the y to extend upwards by
     * @return a new box that has been extended upwards
     * @since 1.0.0 [3]
     */
    public Box extend(int byY) {
        return new Box(x1, y1, z1, x2, y2 + byY, z2, world);
    }

    public boolean isWithin(Location l1) {
        int x = l1.getBlockX();
        int y = l1.getBlockY();
        int z = l1.getBlockZ();
        return x >= x1 && x <= x2 && y >= y1 && y <= y2 && z >= z1 && z <= z2;
    }

    public Location getLowerCornerAsLocation() {
        return new Location(world, x1, y1, z1);
    }

    public Location getUpperCornerAsLocation() {
        return new Location(world, x2, y2, z2);
    }

    public Location getMiddleAsLocation() {
        return new Location(world, (double) (x1 + x2) / 2, (double) (y1 + y2) / 2, (double) (z1 + z2) / 2);
    }

    public Stream<Location> getWithin() {
        List<Location> locations = new ArrayList<>();
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    locations.add(new Location(world, x, y, z));
                }
            }
        }
        return locations.stream();
    }

    /**
     * Set blocks within on a temporary basis, calling <code>reset()</code> will undo changes.
     *
     * @param data block data we want to set it as
     * @since 1.0.0 [3]
     */
    public void setTemporaryWithin(BlockData data) {
        getWithin().forEach(location -> {
            temporary.add(MiningManager.set(location, data));
        });
    }

    /**
     * Set blocks within on a temporary basis, calling <code>reset()</code> will undo changes.
     *
     * @param data block data we want to set it as
     * @since 1.0.0 [3]
     */
    public void setTemporaryWithin(Material data) {
        getWithin().forEach(location -> temporary.add(MiningManager.set(location, data)));
    }

    /**
     * Set blocks within on a temporary basis, calling <code>reset()</code> will undo changes.
     *
     * @param choices a selection of <code>Material</code>s to choose from
     * @since 1.0.0 [3]
     */
    public void setTemporaryWithin(List<Material> choices) {
        getWithin().forEach(location -> temporary.add(MiningManager.set(location, RandomUtils.getChoice(choices))));
    }

    /**
     * Resets temporary blocks to their original state
     *
     * @since 1.0.0 [3]
     */
    public void reset() {
        for (var t : temporary) t.run();
    }

    /**
     * Get the volume of the box including the border blocks
     *
     * @return the volume of the box
     * @since 1.0.0 [3]
     */
    public int volume() {
        return ((x2 + 1) - x1) * ((y2 + 1) - y1) * ((z2 + 1) - z1);
    }


    public Set<GamePlayer> getPlayersWithin() {
        Set<GamePlayer> players = new HashSet<>();
        for (var player : PlayerManager.getOnlinePlayers()) {
            if (isWithin(player.getLocation())) {
                players.add(player);
            }
        }
        return players;
    }

    public Location getRandomMobLocation() {
        int rx = RandomUtils.getRandom(x1, x2);
        int ry = Math.min(y1, y2);
        int rz = RandomUtils.getRandom(z1, z2);

        Location location = new Location(world, rx, ry, rz);
        while (location.getBlock().getType() != Material.AIR && ry <= y2) {
            ry++;
            location = new Location(world, rx, ry, rz);
        }
        if (location.getBlock().getType() != Material.AIR) {
            return getRandomMobLocation();
        }
        else {
            int attempts = 0;
            while (location.clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR && attempts < 15) {
                ry--;
                attempts++;
                location = new Location(world, rx, ry, rz);
            }
            if (location.clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR) {
                return getRandomMobLocation();
            }
        }

        return location;
    }

    public int getHorizontalCrossSectionalArea() {
        return (x2 - x1) * (z2 - z1);
    }

    public double getHorizontalLongestSide() {
        return Math.max(x2 - x1, z2 - z1);
    }
}
