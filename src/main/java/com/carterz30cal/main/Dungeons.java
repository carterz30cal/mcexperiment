package com.carterz30cal.main;

import com.carterz30cal.commands.*;
import com.carterz30cal.entities.EnemyManager;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.events.*;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.discoveries.DiscoveryManager;
import com.carterz30cal.mining.MiningManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Dungeons extends JavaPlugin
{
	public static Dungeons instance;
	public static World w;
	public static ProtocolManager proto;
    public static ScoreboardLibrary scoreboardLibrary;
	
	@Override
	public void onEnable()
	{
		instance = this;
		w = Bukkit.getWorld("world");
		GameEntity.allowDeregisters = true;
		proto = ProtocolLibrary.getProtocolManager();

        try {
            scoreboardLibrary = ScoreboardLibrary.loadScoreboardLibrary(this);
        } catch (NoPacketAdapterAvailableException e) {
            // If server version is not yet supported, you can fall back to the no-op implementation:
            scoreboardLibrary = new NoopScoreboardLibrary();
            getLogger().warning("Server version unsupported, scoreboard functionality will not be visible!");
        }

        new DiscoveryManager();
        new ItemFactory();
        new PlayerManager();
        new EnemyManager();
		
		registerEvent(new ListenerPlayerJoinLeave());
		registerEvent(new ListenerEntityDamage());
		registerEvent(new ListenerLootDrop());
		registerEvent(new ListenerInventoryEvents());
		registerEvent(new ListenerPlayerInteract());
		registerEvent(new ListenerFishingEvents());
        registerEvent(new ListenerTarget());

		for (Entity e : w.getEntities()) {
			if (e instanceof Player) continue;
			e.remove();
		}
		
		setCommand("item", new CommandItem());
		setCommand("spawn", new CommandSpawn());
		setCommand("setlevel", new CommandSetLevel());
		setCommand("force", new CommandForce());
        setCommand("warp", new CommandWarp());
        getCommand("warp").setTabCompleter(new CommandWarp.WarpTabCompleter());
	}
	
	@Override
	public void onDisable()
	{
		//ChangeUtils.resetState();
        for (GamePlayer player : PlayerManager.players.values()) {
            PlayerManager.savePlayer(player);
        }
        PlayerManager.save();
		
		GameEntity.allowDeregisters = false;
		for (GameEntity enemy : GameEntity.entities.values())
		{
			enemy.remove();
		}
		for (Entity e : w.getEntities()) {
			if (e instanceof Player) continue;
			e.remove();
		}

        MiningManager.onDisable();
        scoreboardLibrary.close();
    }
	
	private void setCommand(String command, CommandExecutor executor)
	{
		getCommand(command).setExecutor(executor);
	}
	private void registerEvent(Listener listener)
	{
		getServer().getPluginManager().registerEvents(listener, this);
	}
}
