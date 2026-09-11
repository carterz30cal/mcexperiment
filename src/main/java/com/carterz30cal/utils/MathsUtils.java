package com.carterz30cal.utils;

import org.bukkit.Location;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class MathsUtils
{
	public static double getCircleX(double degrees)
	{
		return Math.sin(Math.toRadians(degrees));
	}
	public static double getCircleZ(double degrees)
	{
		return Math.cos(Math.toRadians(degrees));
	}

    /**
     * @param l1 location 1
     * @param l2 location 2
     * @return the taxicab/manhattan distance between two points.
     * @since 1.0.0 [2]
     */
    public static double manhattan(Location l1, Location l2) {
        var x = Math.abs(l1.getX() - l2.getX());
        var y = Math.abs(l1.getY() - l2.getY());
        var z = Math.abs(l1.getZ() - l2.getZ());
        return x + y + z;
    }

    /**
     * @param l1 location 1
     * @param l2 location 2
     * @return the largest distance on any single axis
     * @since 1.0.0 [2]
     */
    public static double chebyshev(Location l1, Location l2) {
        var x = Math.abs(l1.getX() - l2.getX());
        var y = Math.abs(l1.getY() - l2.getY());
        var z = Math.abs(l1.getZ() - l2.getZ());
        return Math.max(x, Math.max(y, z));
    }
}
