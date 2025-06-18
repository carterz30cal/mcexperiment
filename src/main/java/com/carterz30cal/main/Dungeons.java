package com.carterz30cal.main;

import com.carterz30cal.mining.Mineable;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.carterz30cal.areas.AbstractArea;
import com.carterz30cal.areas.AreaWaterway;
import com.carterz30cal.areas.BossHydra;
import com.carterz30cal.areas.BossWaterwayHydra;
import com.carterz30cal.areas.BossWaterwaySeraph;
import com.carterz30cal.areas.ForagingAreaWaterway;
import com.carterz30cal.commands.CommandForce;
import com.carterz30cal.commands.CommandItem;
import com.carterz30cal.commands.CommandKit;
import com.carterz30cal.commands.CommandMax;
import com.carterz30cal.commands.CommandSetLevel;
import com.carterz30cal.commands.CommandSpawn;
import com.carterz30cal.entities.EnemyManager;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.events.ListenerBlockEvents;
import com.carterz30cal.events.ListenerEntityDamage;
import com.carterz30cal.events.ListenerFishingEvents;
import com.carterz30cal.events.ListenerInventoryEvents;
import com.carterz30cal.events.ListenerLootDrop;
import com.carterz30cal.events.ListenerPlayerInteract;
import com.carterz30cal.events.ListenerPlayerJoinLeave;
import com.carterz30cal.items.DiscoveryManager;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.quests.QuestManager;
import com.carterz30cal.utils.BlockUtils;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerDigType;

public class Dungeons extends JavaPlugin
{
	public static Dungeons instance;
	public static World w;
	public static ProtocolManager proto;
	
	@Override
	public void onEnable()
	{
		instance = this;
		w = Bukkit.getWorld("world");
		GameEntity.allowDeregisters = true;
		proto = ProtocolLibrary.getProtocolManager();
		
		new QuestManager();
		new PlayerManager();
		new EnemyManager();
		new DiscoveryManager();
		new ItemFactory();
		
		new AreaWaterway();
		//new BossWaterwayHydra();
		//new BossHydra();
		//new BossWaterwaySeraph();
		new ForagingAreaWaterway();
		
		proto.addPacketListener(
			    new PacketAdapter(this, PacketType.Play.Client.BLOCK_DIG) {
			        @Override
			        public void onPacketReceiving(PacketEvent event) {
			            // Called when receiving a packet (client -> server)
			            Player targetPlayer = event.getPlayer();
			            PacketContainer packet = event.getPacket();
			            
			            GamePlayer player = PlayerManager.players.get(targetPlayer.getUniqueId());
			            
			            PlayerDigType digType = packet.getPlayerDigTypes().getValues().get(0);
			            if (digType == PlayerDigType.START_DESTROY_BLOCK) {
			            	new BukkitRunnable() {
			            		public void run() {
			            			player.mining = true;
			            		}
			            		
			            	}.runTaskLater(Dungeons.instance, 1);
			            	//System.out.println("ya");
			            }
			            else if (digType == PlayerDigType.ABORT_DESTROY_BLOCK || digType == PlayerDigType.STOP_DESTROY_BLOCK) {
			            	player.mining = false;
			            }
			        }
			});
		
		registerEvent(new ListenerPlayerJoinLeave());
		registerEvent(new ListenerEntityDamage());
		registerEvent(new ListenerLootDrop());
		registerEvent(new ListenerInventoryEvents());
		registerEvent(new ListenerPlayerInteract());
		registerEvent(new ListenerFishingEvents());
		registerEvent(new ListenerBlockEvents());

		for (Entity e : w.getEntities()) {
			if (e instanceof Player) continue;
			e.remove();
		}
		
		setCommand("item", new CommandItem());
		setCommand("spawn", new CommandSpawn());
		setCommand("setlevel", new CommandSetLevel());
		setCommand("force", new CommandForce());
		setCommand("kit", new CommandKit());
		setCommand("max", new CommandMax());
	}
	
	@Override
	public void onDisable()
	{
		//ChangeUtils.resetState();
		
		GameEntity.allowDeregisters = false;
		for (GameEntity enemy : GameEntity.entities.values())
		{
			enemy.remove();
		}
		for (Entity e : w.getEntities()) {
			if (e instanceof Player) continue;
			e.remove();
		}
		
		for (GamePlayer player : PlayerManager.players.values())
		{
			PlayerManager.savePlayer(player);
		}
		
		for (AbstractArea a : AbstractArea.areas.values()) a.onEnd();
		BlockUtils.removeStructures();
		Mineable.removeAll();
		
		PlayerManager.save();
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
