package com.carterz30cal.utils;

import com.carterz30cal.main.Dungeons;
import org.bukkit.Location;

public class Box {
    private final int x1;
    private final int y1;
    private final int z1;
    private final int x2;
    private final int y2;
    private final int z2;

    public Box(Location l1, Location l2) {
        x1 = Math.min(l1.getBlockX(), l2.getBlockX());
        x2 = Math.max(l1.getBlockX(), l2.getBlockX());
        y1 = Math.min(l1.getBlockY(), l2.getBlockY());
        y2 = Math.max(l1.getBlockY(), l2.getBlockY());
        z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
        z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());
    }
    public Box(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.x1 = Math.min(x1, x2);
        this.x2 = Math.max(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.y2 = Math.max(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.z2 = Math.max(z1, z2);
    }

    public Box Expand(int by) {
        return new Box(x1 - by, y1 - by, z1 - by, x2 + by, y2 + by, z2 + by);
    }
    public Box Expand(int byX, int byY, int byZ) {
        return new Box(x1 - byX, y1 - byY, z1 - byZ, x2 + byX, y2 + byY, z2 + byZ);
    }

    public boolean IsWithin(Location l1) {
        int x = l1.getBlockX();
        int y = l1.getBlockY();
        int z = l1.getBlockZ();
        return x >= x1 && x <= x2 && y >= y1 && y <= y2 && z >= z1 && z <= z2;
    }

    public Location GetLowerCornerAsLocation() {
        return new Location(Dungeons.w, x1, y1, z1);
    }
    public Location GetUpperCornerAsLocation() {
        return new Location(Dungeons.w, x2, y2, z2);
    }

    public int GetHorizontalCrossSectionalArea() {
        return (x2 - x1) * (z2 - z1);
    }
}
