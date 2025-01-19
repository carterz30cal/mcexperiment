package com.carterz30cal.entities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.carterz30cal.items.ForgingItem;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.quests.Quest;

public class PlayerManager
{
	public static PlayerManager instance;
	public static Map<UUID, GamePlayer> players = new HashMap<>();
	
	private static FileConfiguration f;
	
	public PlayerManager()
	{
		instance = this;
		File file = new File(Dungeons.instance.getDataFolder(), "players.yml");
		if (!file.exists())
		{
			file.getParentFile().mkdirs();
			Dungeons.instance.saveResource("players.yml", false);
		}
		
		f = new YamlConfiguration();
		
		try
		{
			f.load(file);
		}
		catch (IOException | InvalidConfigurationException e)
		{
			e.printStackTrace();
		}
		
		for (Player player : Bukkit.getOnlinePlayers()) registerPlayer(player);
		
		new BukkitRunnable()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (GamePlayer player : players.values()) player.tick();
			}
			
		}.runTaskTimer(Dungeons.instance, 1, 1);
		
		
	}
	
	public static List<GamePlayer> getOnlinePlayers() {
		return new ArrayList<>(players.values());
	}
	public static List<GameEntity> getOnlinePlayersAsEntity() {
		return new ArrayList<>(players.values());
	}
	
	public static void save()
	{
		File file = new File(Dungeons.instance.getDataFolder(), "players.yml");
		try {
			f.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void registerPlayer(Player p)
	{
		GamePlayer player = new GamePlayer();
		player.player = p;
		player.health = 1;
		player.completedQuests.add("player_joined");
		
		loadPlayer(player);
		
		player.register(p.getUniqueId());
		players.put(p.getUniqueId(), player);
	}
	
	public static void loadPlayer(GamePlayer p)
	{
		if (!f.contains(p.player.getUniqueId().toString())) {
			System.out.println("Could not load player: " + p.player.getUniqueId().toString());
			return;
		}
		ConfigurationSection c = f.getConfigurationSection(p.player.getUniqueId().toString());
		
		p.level = c.getInt("level", 0);
		p.xp = c.getInt("xp", 0);
		p.coins = c.getInt("coins", 0);
		
		ConfigurationSection forging = c.getConfigurationSection("forging");
		if (forging != null)
		{
			for (String path : forging.getKeys(false))
			{
				ForgingItem item = new ForgingItem(forging.getConfigurationSection(path));
				p.forge.add(item);
			}
		}
		
		
		ConfigurationSection quiver = c.getConfigurationSection("quiver");
		if (quiver != null)
		{
			for (String path : quiver.getKeys(false))
			{
				p.quiver.put(path, quiver.getInt(path, 0));
			}
		}
		
		ConfigurationSection discoveries = c.getConfigurationSection("discoveries");
		if (discoveries != null)
		{
			for (String path : discoveries.getKeys(false))
			{
				p.discoveries.put(path, discoveries.getInt(path, 0));
			}
		}
		
		ConfigurationSection sack = c.getConfigurationSection("sack");
		if (sack != null)
		{
			for (String path : sack.getKeys(false))
			{
				p.sack.put(path, sack.getInt(path, 0));
			}
		}
		
		
		p.talismans = c.getStringList("talismans");
		if (p.talismans == null) p.talismans = new ArrayList<>();
		
		p.completedQuests = c.getStringList("quests.completed");
		if (p.completedQuests == null) {
			p.completedQuests = new ArrayList<>();
			p.completedQuests.add("player_joined");
		}
		
		ConfigurationSection questing = c.getConfigurationSection("quests.in-progress");
		if (questing != null)
		{
			for (String path : questing.getKeys(false))
			{
				Quest q = Quest.ids.get(path);
				p.quests.put(q.id, q.questType.generate(p, q, questing.getString(path)));
			}
		}
		
	}
	
	public static void savePlayer(GamePlayer p)
	{
		if (!f.contains(p.player.getUniqueId().toString())) f.createSection(p.player.getUniqueId().toString());
		ConfigurationSection c = f.getConfigurationSection(p.player.getUniqueId().toString());
		
		
		c.set("level", p.level);
		c.set("xp", p.xp);
		c.set("coins", p.coins);
		c.set("talismans", p.talismans);
		
		c.set("forging", null);
		c.createSection("forging");
		for (ForgingItem item : p.forge) 
		{
			item.save(c.getConfigurationSection("forging"));
		}
		
		c.set("quiver", null);
		c.createSection("quiver");
		for (String arrow : p.quiver.keySet())
		{
			c.set("quiver." + arrow, p.quiver.get(arrow));
		}
		
		c.set("discoveries", null);
		c.createSection("discoveries");
		for (String arrow : p.discoveries.keySet())
		{
			c.set("discoveries." + arrow, p.discoveries.get(arrow));
		}
		
		c.set("sack", null);
		c.createSection("sack");
		for (String arrow : p.sack.keySet())
		{
			c.set("sack." + arrow, p.sack.get(arrow));
		}
		
		c.set("quests", null);
		c.createSection("quests");
		c.createSection("quests.in-progress");
		c.set("quests.completed", p.completedQuests);
		for (String inp : p.quests.keySet())
		{
			c.set("quests.in-progress." + inp, p.quests.get(inp).saveProgress());
		}
		
	}
	
}
