package com.carterz30cal.utils;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Location;

public class RandomUtils
{
	private static final Random r = new Random();
	
	/*
	 * INCLUSIVE MIN + MAX
	 */
	public static int getRandom(int min, int max)
	{
		return min + r.nextInt((max-min)+1);
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
		while (shuffling.size() > 0) {
			T ch = getChoice(shuffling);
			shuffled[i] = ch;
			i++;
			shuffling.remove(ch);
		}
		
		return shuffled;
	}
	
	public static Location getRandomInside(Location c1, Location c2)
	{
		int sx = Math.min(c1.getBlockX(), c2.getBlockX());
		int lx = Math.max(c1.getBlockX(), c2.getBlockX());
		int y = c1.getBlockY();
		int sz = Math.min(c1.getBlockZ(), c2.getBlockZ());
		int lz = Math.max(c1.getBlockZ(), c2.getBlockZ());
		
		return new Location(c1.getWorld(), sx + getRandom(0, lx-sx), y, sz + getRandom(0, lz-sz));
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
}
