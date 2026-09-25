package com.carterz30cal.utils;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Location;

/**
 * @author carterz30cal
 * @version 4
 * @since 1.0.0
 */
public class RandomUtils
{
	private static final Random r = new Random();
	
	/**
	 * @param min inclusive minimum integer value
	 * @param max inclusive maximum integer value
	 * @return a value between <code>min</code> and <code>max</code>.
	 * @since 1.0.0 [1]
	 */
	public static int getRandom(int min, int max)
	{
		return min + r.nextInt((max-min)+1);
	}

	/**
	 * @implNote implements <code>Random.nextLong(min, max)</code>
	 * @param min inclusive minimum long integer value
	 * @param max inclusive maximum long integer value
	 * @return a long integer value between <code>min</code> and <code>max</code>.
	 * @since 1.0.0 [3]
	 */
	public static long getRandom(long min, long max) {
		return r.nextLong(min, max + 1);
	}

	public static int getRandomEx(int min, int max)
	{
		return min + r.nextInt(max-min);
	}
	public static double getDouble(double min, double max)
	{
		return min + (r.nextDouble() * (max-min));
	}
	
	public static <T> T getChoice(T[] array)
	{
		return array[getRandomEx(0,array.length)];
	}
	public static <T> T getChoice(List<T> list)
	{
		return list.get(getRandomEx(0,list.size()));
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getChoice(Set<T> list)
	{
		return (T) getChoice(list.toArray());
	}
	
	public static <T> T[] shuffle(T[] array) {
		T[] shuffled = Arrays.copyOf(array, array.length);

		List<T> shuffling = new ArrayList<T>(Arrays.asList(array));
		int i = 0;
		while (!shuffling.isEmpty()) {
			T ch = getChoice(shuffling);
			shuffled[i] = ch;
			i++;
			shuffling.remove(ch);
		}

		return shuffled;
	}

	public static Location getRandomInside(Location c1, Location c2)
	{
		boolean found = false;
		int attempts = 0;
		Location choice = null;
		int sx = Math.min(c1.getBlockX(), c2.getBlockX());
		int lx = Math.max(c1.getBlockX(), c2.getBlockX());
		int y = c1.getBlockY();
		int sz = Math.min(c1.getBlockZ(), c2.getBlockZ());
		int lz = Math.max(c1.getBlockZ(), c2.getBlockZ());
		while (!found && attempts < 100) {
			choice = new Location(c1.getWorld(), sx + getRandom(0, lx-sx), y, sz + getRandom(0, lz-sz));
			if (!choice.getBlock().isPassable()) {
				attempts++;
			}
			else found = true;
		}

		if (!found) return c1;
		else return choice;
	}
	
	public static Location getRandomAroundFlat(Location c, double r)
	{
		return c.clone().add(getDouble(-r,r),0,getDouble(-r,r));
	}
	
	public static Location getRandomInCircle(Location c, double rMin, double rMax)
	{
		double r = getDouble(rMin, rMax);
		double deg = getDouble(0, 2 * Math.PI);
		
		return c.clone().add(Math.sin(deg) * r, 0, Math.cos(deg) * r);
	}

	/**
	 * Get a random point between <code>[rMin, rMax]</code> on each axis. Point cloud would produce a cube.
	 * @param c centre of our random point
	 * @param rMin the minimum distance on each axis from the centre
	 * @param rMax the maximum distance on each axis from the centre
	 * @return a <code>Location</code> at a random point that meets these criteria
	 * @since 1.0.0 [4]
	 */
	public static Location getRandomAround(Location c, double rMin, double rMax)
	{
		double x = getDouble(rMin, rMax);
		double sx = (getRandom(0, 1) * 2) - 1;
		double y = getDouble(rMin, rMax);
		double sy = (getRandom(0, 1) * 2) - 1;
		double z = getDouble(rMin, rMax);
		double sz = (getRandom(0, 1) * 2) - 1;

		return c.clone().add(x * sx, y * sy, z * sz);
	}
}
