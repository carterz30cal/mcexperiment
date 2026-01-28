package com.carterz30cal.entities;

import com.carterz30cal.areas2.quests.Quests;
import com.carterz30cal.items.ForgingItem;
import com.carterz30cal.main.Dungeons;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

        ConfigurationSection quests = c.getConfigurationSection("quests");
        if (quests != null) {
            for (Quests quest : Quests.values()) {
                ConfigurationSection qs = quests.getConfigurationSection(quest.name());
                if (qs != null) {
                    p.LoadQuestSave(quest.LoadSave(p, qs));
                }
            }
        }
        String selected = c.getString("selected-quest");
        if (selected != null) {
            p.SetSelectedQuest(Quests.valueOf(selected));
        }

		
		
		ConfigurationSection quiver = c.getConfigurationSection("quiver");
		if (quiver != null)
		{
			for (String path : quiver.getKeys(false))
			{
				p.quiver.put(path, quiver.getInt(path, 0));
			}
		}

        ConfigurationSection kills = c.getConfigurationSection("kills");
        if (kills != null) {
            for (var path : kills.getKeys(false)) {
                p.kills.put(path, kills.getLong(path, 0));
            }
        }

		
		ConfigurationSection discoveries = c.getConfigurationSection("discoveries");
		if (discoveries != null)
		{
			for (String path : discoveries.getKeys(false))
			{
				p.discoveries.put(path, discoveries.getLong(path, 0));
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

		ConfigurationSection backpack = c.getConfigurationSection("backpack");
		if (backpack != null)
		{
			for (String path : backpack.getKeys(false))
			{
				p.backpack.put(Integer.parseInt(path), backpack.getString(path));
			}
		}

		p.activePet = c.getString("active-pet");
		p.pets = c.getStringList("pets");
		
		
		p.talismans = c.getStringList("talismans");
	}
	
	public static void savePlayer(GamePlayer p)
	{
		if (!f.contains(p.player.getUniqueId().toString())) f.createSection(p.player.getUniqueId().toString());
		ConfigurationSection c = f.getConfigurationSection(p.player.getUniqueId().toString());


        assert c != null;
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

        c.set("quests", null);
        c.createSection("quests");
        for (Quests quest : Quests.values()) {
            ConfigurationSection qs = Objects.requireNonNull(c.getConfigurationSection("quests")).createSection(quest.name());
            quest.SaveSave(p, qs);
        }
        Quests selected = p.GetSelectedQuest();
        if (selected != null) {
            c.set("selected-quest", selected.name());
        }
        else {
            c.set("selected-quest", null);
        }


        c.set("kills", null);
        c.createSection("kills");
        for (var kill : p.kills.keySet()) {
            c.set("kills." + kill, p.kills.get(kill));
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

		c.set("backpack", null);
		c.createSection("backpack");
		for (Integer bp : p.backpack.keySet()) {
			c.set("backpack." + bp, p.backpack.get(bp));
		}

		c.set("active-pet", p.activePet);
		c.set("pets", p.pets);
	}
	
}
