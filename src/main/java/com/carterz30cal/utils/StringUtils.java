package com.carterz30cal.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import com.carterz30cal.main.Dungeons;

public class StringUtils
{
	public static String progressBar(double percent, int length)
	{
		return progressBar(percent, length, ChatColor.BLUE, ChatColor.RED);
	}
	
	public static String progressBar(double percent, int length, ChatColor yes, ChatColor no)
	{
		int pLength = (int)Math.round(percent * length);
		
		String bar = yes.toString();
		for (int i = 0; i <= pLength; i++) bar += "|";
		bar += no;
		for (int i = 0; i <= length - pLength; i++) bar += "|";
		return bar;
	}
	
	public static String progressBar(double percent, int length, String colour)
	{
		int pLength = (int)Math.round(percent * length);
		
		String bar = colour;
		for (int i = 0; i <= pLength; i++) bar += "|";
		bar += "DARK_GRAY";
		for (int i = 0; i <= length - pLength; i++) bar += "|";
		return colourString(bar);
	}
	
	public static String asPercent(double percent)
	{
		return Math.round(percent * 100) + "%";
	}
	
	public static int[] convertStringToFraction(String string)
	{
		String[] split = string.split("/");
		int numerator = Integer.parseInt(split[0]);
		int denominator = Integer.parseInt(split[1]);
		
		return new int[] {numerator, denominator};
	}
	
	public static int[] convertStringToIntArray(String string)
	{
		String[] split = string.split(",");
		int[] fin = new int[split.length];
		
		for (int i = 0; i < split.length; i++)
		{
			fin[i] = Integer.parseInt(split[i]);
		}
		
		return fin;
	}
	
	public static String getPrettyCoins(int amount)
	{
		String adj = commaify(amount);
		return amount < 0 ? "GOLD-" + adj + " Coins" : "GOLD" + adj + " Coins";
	}
	
	public static String commaify(int amount) {
		String am = "" + Math.abs(amount);
		String adj = "";
		for (int i = 0; i < am.length(); i++)
		{
			int c = am.length() - (i + 1);
			if (i % 3 == 0 && i != 0) adj = am.charAt(c) + "," + adj;
			else adj = am.charAt(c) + adj;
		}
		
		return adj;
	}
	
	public static String getPrettyTime(int ticks)
	{
		if (ticks < 20) return " Soon!";
		
		int[] divs = {20*60*60, 20*60, 20};
		String[] suffix = {"h", "m", "s"};
		
		String sentence = "";
		int remaining = ticks;
		for (int d = 0; d < divs.length; d++)
		{
			int rounded = remaining / divs[d];
			remaining = remaining % divs[d];
			
			if (rounded == 0) continue;
			sentence += " " + rounded + suffix[d];
		}
		return sentence;
	}

	public static String getPrettyTime(LocalDateTime finishes)
	{
		Duration duration = Duration.between(LocalDateTime.now(), finishes);

		long days = duration.toDays();
		long hours = duration.toHours() % 24;
		long minutes = duration.toMinutes() % 60;
		long seconds = duration.getSeconds() % 60;

		if (seconds < 1) return " Soon!";

		StringBuilder str = new StringBuilder();
		if (days > 0) str.append(days + "d");
		if (hours > 0) str.append(hours + "h");
		if (minutes > 0) str.append(minutes + "m");
		str.append(seconds + "s");

		return str.toString();
	}

	
	public static int convertPrettyTime(String time)
	{
		int[] divs = {20*60*60, 20*60, 20};
		String[] suffix = {"h", "m", "s"};
		
		int ticks = 0;
		String t = time;
		for (int d = 0; d < divs.length; d++)
		{
			String[] spl = t.split(suffix[d], -1);
			if (spl.length == 1) continue;
			else 
			{
				ticks += Integer.parseInt(spl[0]) * divs[d];
				t = spl[1];
			}
		}
		
		return ticks;
	}
	
	public static String truncatedDouble2(double val) {
		return ((int)val) + "." + ((int)(val * 100) % 100);
	}


	public static Location getLocationFromString(String s) {
		String[] sp = s.split(",");
		double x = Double.parseDouble(sp[0]);
		double y = Double.parseDouble(sp[1]);
		double z = Double.parseDouble(sp[2]);
		double yaw = 0;
		double pitch = 0;
		if (sp.length >= 4) yaw = Double.parseDouble(sp[3]);
		if (sp.length >= 5) pitch = Double.parseDouble(sp[4]);
		return new Location(Dungeons.w, x, y, z, (float)yaw, (float)pitch);
	}
	
	public static int getIntFromStringConf(ConfigurationSection s, String p, int i)
	{
		return selectFromList(s.getString(p, "0"), i);
	}
	public static String getStrFromStringConf(ConfigurationSection s, String p, int i)
	{
		return s.getString(p).split(",")[i];
	}
	
	public static int selectFromList(String list, int i)
	{
		String[] l = list.split(",");
		if (l.length == 1) return Integer.parseInt(l[0]);
		else return Integer.parseInt(l[i].strip());
	}
	
	public static String colourString(String string)
	{
		String coloured = string;
		coloured = coloured.replaceAll("DARK_GRAY", ChatColor.DARK_GRAY.toString());
		for (ChatColor c : ChatColor.values()) coloured = coloured.replaceAll(c.name(), c.toString());
		return coloured;
	}
	
	public static List<String> colourList(List<String> uncoloured)
	{
		List<String> coloured = new ArrayList<>();
		for (String i : uncoloured)
		{
			coloured.add(colourString(i));
		}
		return coloured;
	}
}
