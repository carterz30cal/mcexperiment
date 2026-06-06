package com.carterz30cal.utils;

import com.carterz30cal.main.Dungeons;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class StringUtils
{
    @Deprecated
	public static String progressBar(double percent, int length)
	{
		return progressBar(percent, length, ChatColor.BLUE, ChatColor.RED);
	}

    @Deprecated
	public static String progressBar(double percent, int length, ChatColor yes, ChatColor no)
	{
		int pLength = (int)Math.round(percent * length);
		
		String bar = yes.toString();
		for (int i = 0; i <= pLength; i++) bar += "|";
		bar += no;
		for (int i = 0; i <= length - pLength; i++) bar += "|";
		return bar;
	}

    @Deprecated
	public static String progressBar(double percent, int length, String colour)
	{
		int pLength = (int)Math.round(percent * length);
		
		String bar = colour;
		for (int i = 0; i <= pLength; i++) bar += "|";
		bar += "DARK_GRAY";
		for (int i = 0; i <= length - pLength; i++) bar += "|";
		return colourString(bar);
	}

    /**
     * @param length         character length of the bar
     * @param percentFilled  this will be rounded, based on the length.
     * @param filledColour   what colour do we want in the filled portion of the bar?
     * @param unfilledColour what colour do we want in the unfilled portion of the bar?
     * @return a progress bar of the specified length, filled in to a rounded percent.
     * @since 1.0.0
     */
    public static TextComponent.Builder progressBar(int length, double percentFilled, NamedTextColor filledColour, NamedTextColor unfilledColour) {
        return progressBar(length, percentFilled, TextColor.color(filledColour), TextColor.color(unfilledColour));
    }


    /**
     * @param length         character length of the bar
     * @param percentFilled  this will be rounded, based on the length.
     * @param filledColour   what colour do we want in the filled portion of the bar?
     * @param unfilledColour what colour do we want in the unfilled portion of the bar?
     * @return a progress bar of the specified length, filled in to a rounded percent.
     * @since 1.0.0
     */
    public static TextComponent.Builder progressBar(int length, double percentFilled, TextColor filledColour, TextColor unfilledColour) {
        int filledLength = (int) Math.round(percentFilled * length);
        return text()
                .append(
                        text()
                                .content(new StringBuilder().repeat('|', filledLength).toString())
                                .color(filledColour))
                .append(
                        text()
                                .content(new StringBuilder().repeat('|', length - filledLength).toString())
                                .color(unfilledColour));
    }

    /**
     * @param length         character length of the bar
     * @param percentFilled  this will be rounded, based on the length.
     * @param filledColour   what colour do we want in the filled portion of the bar?
     * @param unfilledColour what colour do we want in the unfilled portion of the bar?
     * @return a progress bar of the specified length, filled in to a rounded percent.
     * @since 1.0.0
     */
    public static String stringProgressBar(int length, double percentFilled, TextColor filledColour, TextColor unfilledColour) {
        int filledLength = (int) Math.round(percentFilled * (double) length);
        return "<" + filledColour.asHexString() + ">" + new StringBuilder().repeat('|', filledLength) +
                "</" + filledColour.asHexString() + ">" + "<" + unfilledColour.asHexString() + ">" +
                new StringBuilder().repeat('|', length - filledLength)
                + "</" + unfilledColour.asHexString() + ">";
    }


	
	public static String asPercent(double percent)
	{
		return Math.round(percent * 100) + "%";
	}

    public static String asPercent2DP(double percent) {
        return truncatedDouble2(percent * 100) + "%";
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

    public static String addCommas(int amount) {
        return addCommas((long) amount);
    }

    public static String addCommas(long amount) {
        String am = "" + Math.abs(amount);
        StringBuilder adj = new StringBuilder();
        for (int i = 0; i < am.length(); i++) {
            int c = am.length() - (i + 1);
            if (i % 3 == 0 && i != 0) {
                adj.insert(0, am.charAt(c) + ",");
            }
            else {
                adj.insert(0, am.charAt(c));
            }
        }

        return adj.toString();
    }
	
	public static String getPrettyTime(int ticks)
	{
		if (ticks < 20) return " Soon!";
		
		int[] divs = {20*60*60, 20*60, 20};
		String[] suffix = {"h", "m", "s"};

        StringBuilder sentence = new StringBuilder();
		int remaining = ticks;
		for (int d = 0; d < divs.length; d++)
		{
			int rounded = remaining / divs[d];
			remaining = remaining % divs[d];
			
			if (rounded == 0) continue;
            sentence.append(" ").append(rounded).append(suffix[d]);
        }
        return sentence.toString();
	}

	public static String getPrettyTime(LocalDateTime finishes)
	{
		Duration duration = Duration.between(LocalDateTime.now(), finishes);

		long days = duration.toDays();
		long hours = duration.toHours() % 24;
		long minutes = duration.toMinutes() % 60;
		long seconds = duration.getSeconds() % 60;

        if (days == 0 && hours == 0 && minutes == 0 && seconds < 1) {
            return " Soon!";
        }

		StringBuilder str = new StringBuilder();
        if (days > 0) {
            str.append(days).append("d");
        }
        if (hours > 0) {
            str.append(hours).append("h");
        }
        if (minutes > 0) {
            str.append(minutes).append("m");
        }
        str.append(seconds).append("s");

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

    public static String truncatedDouble1(double val) {
        return ((int) val) + "." + ((int) (val * 10) % 10);
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

    public static long getLongFromStringConf(ConfigurationSection s, String p, int i) {
        return selectLongFromList(s.getString(p, "0"), i);
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

    public static long selectLongFromList(String list, int i) {
        String[] l = list.split(",");
        if (l.length == 1) {
            return Long.parseLong(l[0]);
        }
        else {
            return Long.parseLong(l[i].strip());
        }
    }

    @Deprecated
	public static String colourString(String string)
	{
		String coloured = string;
		coloured = coloured.replaceAll("DARK_GRAY", ChatColor.DARK_GRAY.toString());
		for (ChatColor c : ChatColor.values()) coloured = coloured.replaceAll(c.name(), c.toString());
		return coloured;
	}

    @Deprecated
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
