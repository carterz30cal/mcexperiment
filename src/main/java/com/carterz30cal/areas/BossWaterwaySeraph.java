package com.carterz30cal.areas;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.carterz30cal.entities.EnemyManager;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GameFloatingItem;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.BlockUtils;
import com.carterz30cal.utils.EntityUtils;
import com.carterz30cal.utils.ParticleUtils;
import com.carterz30cal.utils.RandomUtils;

public class BossWaterwaySeraph extends AbstractArea
{
	public static boolean active = false;
	public static boolean corrupted = false;
	public static int phase = 0;
	
	public static Location altar = new Location(Dungeons.w, -169.5, 83, 34.5);
	private static ArmorStand spawnText;
	private static final Particle.DustOptions PITCHFORK_COLOUR = new Particle.DustOptions(Color.BLUE, 0.4F);
	
	protected static BlockUtils.BlockStructure entrance = BlockUtils.createStructure("SERAPH_ENTRANCE");
	protected static BlockUtils.BlockStructure mageExit = BlockUtils.createStructure("SERAPH_MAGE_EXIT");
	protected static BlockUtils.BlockStructure poolTop = BlockUtils.createStructure("SERAPH_POOL_TOP");
	protected static BlockUtils.BlockStructure poolMid = BlockUtils.createStructure("SERAPH_POOL_MID");
	protected static BlockUtils.BlockStructure poolBottom = BlockUtils.createStructure("SERAPH_POOL_BOTTOM");
	protected static BlockUtils.BlockStructure hole = BlockUtils.createStructure("SERAPH_HOLE");
	
	protected static Location[] spawnPos = {
			new Location(Dungeons.w, -154.5, 75, 26.5),
			new Location(Dungeons.w, -143.5, 75, 26.5),
			new Location(Dungeons.w, -143.5, 75, 15.5),
			new Location(Dungeons.w, -154.5, 75, 15.5),
			};
	protected static Location tntSpawn = new Location(Dungeons.w, -158, 82, 21);
	protected static Location bossArenaMid = new Location(Dungeons.w, -149, 75, 21);
	
	public static List<GamePlayer> participating = new ArrayList<>();
	private static List<GameEnemy> mages = new ArrayList<>();
	private static List<GameFloatingItem> rewards = new ArrayList<>();
	
	private static GameEnemy boss;
	
	
	private int flipflop;
	private static int summoned;
	private static int repeats;
	private int spawnCap;
	private int bossHpTracker;
	
	public BossWaterwaySeraph()
	{
		super();
		
		//-166, 83, 33
		for (int y = 83; y <= 86; y++) for (int z = 33; z <= 35; z++) entrance.set(-166, y, z, Material.RED_STAINED_GLASS, Material.AIR);
		
		//-155, 64, 33
		for (int y = 64; y <= 67; y++) for (int z = 33; z <= 35; z++) mageExit.set(-155, y, z, Material.PURPLE_STAINED_GLASS, Material.AIR);
		
		for (int x = -159; x <= -140; x++)
		{
			for (int z = 11; z <= 30; z++)
			{
				Material m1 = poolBottom.fill(x, 72, z, Material.WATER);
				Material m2 = poolMid.fill(x, 73, z, Material.WATER);
				Material m3 = poolTop.fill(x, 74, z, Material.WATER);
				if (m1 == Material.PURPLE_CONCRETE || m1 == Material.CRACKED_STONE_BRICKS) hole.set(x, 72, z, Material.CRACKED_STONE_BRICKS, Material.PURPLE_CONCRETE);
				else if (m1 == Material.WATER) poolBottom.set(x, 72, z, Material.WATER, Material.AIR);
				if (m2 == Material.PURPLE_CONCRETE || m2 == Material.CRACKED_STONE_BRICKS) hole.set(x, 73, z, Material.CRACKED_STONE_BRICKS, Material.PURPLE_CONCRETE);
				else if (m2 == Material.WATER) poolBottom.set(x, 73, z, Material.WATER, Material.AIR);
				if (m3 == Material.PURPLE_CONCRETE || m3 == Material.CRACKED_STONE_BRICKS) hole.set(x, 74, z, Material.CRACKED_STONE_BRICKS, Material.PURPLE_CONCRETE);
				else if (m3 == Material.WATER) poolBottom.set(x, 74, z, Material.WATER, Material.AIR);
			}
		}
		
		
		for (int y = 72; y <= 79; y++) 
		{
			for (int z = 16; z <= 25; z++) 
			{
				Material c = hole.fill(-160, y, z, Material.CRACKED_STONE_BRICKS);
				if (c == Material.CRACKED_STONE_BRICKS) hole.set(-160, y, z, Material.CRACKED_STONE_BRICKS, Material.AIR);
			}
		}
	}
	
	
	public void onTick()
	{
		if (!active)
		{
			Location pitchfork = altar.clone().add(0, 1.5, 0);
			Vector up = new Vector(0, 1D / 10, 0);
			Vector north = new Vector(0, 0, -1D/ 10);
			
			
			
			Location mid = ParticleUtils.spawnLine(pitchfork, up, 8, PITCHFORK_COLOUR);
			Location left = ParticleUtils.spawnLine(mid, north, 3, PITCHFORK_COLOUR);
			Location right = ParticleUtils.spawnLine(mid, north.multiply(-1), 3, PITCHFORK_COLOUR);
			
			ParticleUtils.spawnLine(mid, up, 7, PITCHFORK_COLOUR);
			ParticleUtils.spawnLine(left, up, 6, PITCHFORK_COLOUR);
			ParticleUtils.spawnLine(right, up, 6, PITCHFORK_COLOUR);
			
			if (spawnText == null || !spawnText.isValid())
			{
				spawnText = EntityUtils.spawnHologram(pitchfork.subtract(0, 0.5, 0), -1);
				EntityUtils.setEntityName(spawnText, "REDBOLDBeware! REDThe DARK_PURPLESeraph'sRED lab lies ahead!");
			}
		}
		else
		{
			mages.removeIf((e) -> e.dead);
			
			if (phase == 0) // pre-mages
			{
				int inRoom = 0;
				for (GamePlayer player : participating)
				{
					if (player.getLocation().getBlockY() < 70) inRoom++;
				}
				
				if (inRoom == participating.size())
				{
					phase = 1;
					
					EntityUtils.sayTo(participating, "DARK_RED<< REDWaterway Seraph DARK_RED>>: WHITEYou dare enter my lab?", 10);
					EntityUtils.sayTo(participating, "DARK_RED<< REDWaterway Seraph DARK_RED>>: WHITEMy mages will sort you out.", 20);
					mages.add(EnemyManager.spawn("seraph_mage_health",  new Location(Dungeons.w, -151.5, 64, 39.5)));
					mages.add(EnemyManager.spawn("seraph_mage_defence", new Location(Dungeons.w, -151.5, 64, 29.5)));
					mages.add(EnemyManager.spawn("seraph_mage_mana",    new Location(Dungeons.w, -140.5, 64, 34.5)));
					if (corrupted)
					{
						mages.add(EnemyManager.spawn("seraph_mage_health",  new Location(Dungeons.w, -151.5, 64, 39.5)));
						mages.add(EnemyManager.spawn("seraph_mage_defence", new Location(Dungeons.w, -151.5, 64, 29.5)));
						mages.add(EnemyManager.spawn("seraph_mage_mana",    new Location(Dungeons.w, -140.5, 64, 34.5)));
					}
					//-151.5, 64, 29.5
				}
			}
			else if (phase == 1 && mages.size() == 0)
			{
				phase = 2;
				mageExit.revert();
				
				EntityUtils.sayTo(participating, "DARK_RED<< REDWaterway Seraph DARK_RED>>: WHITEThey failed?!", 10);
				EntityUtils.sayTo(participating, "DARK_RED<< REDWaterway Seraph DARK_RED>>: WHITEI thought I taught them better.", 20);
				EntityUtils.sayTo(participating, "DARK_RED<< REDWaterway Seraph DARK_RED>>: WHITENevermind...", 60);
				
				boss = EnemyManager.spawn(corrupted ? "corrupt_seraph" : "seraph", new Location(Dungeons.w, -141.5, 64, 22.5, 90, 0));
				boss.setImmune(true);
			}
			else if (phase == 2)
			{
				int inRoom = 0;
				for (GamePlayer player : participating)
				{
					if (player.getLocation().getBlockZ() < 25) inRoom++;
				}
				
				if (inRoom >= 1)
				{
					phase = 3;
					
					EntityUtils.sayTo(participating, "DARK_RED<< REDWaterway Seraph DARK_RED>>: WHITECULTISTS!", 10);
					EntityUtils.sayTo(participating, "DARK_RED<< REDWaterway Seraph DARK_RED>>: WHITEKill these intruders!", 20);
					
					spawnCap = corrupted ? participating.size() * 17 : participating.size() * 12;
					if (spawnCap > 100) spawnCap = 100;
				}
			}
			else if (phase == 3)
			{
				flipflop++;
				if (flipflop % 6 == 4 && summoned < spawnCap)
				{
					mages.add(EnemyManager.spawn(corrupted ? "corrupt_seraph_cultist" : "seraph_cultist",  
							summoned % 2 == 0 ? new Location(Dungeons.w, -143.5, 64, 21.5) : new Location(Dungeons.w, -143.5, 64, 23.5)));
					summoned++;
					
					if (summoned == spawnCap) EntityUtils.sayTo(participating, "DARK_RED<< REDWaterway Seraph DARK_RED>>: WHITEThat should be enough.", 10);
				}
				
				if (summoned == spawnCap && mages.size() == 0)
				{
					if (participating.size() > 5 && repeats == 0)
					{
						repeats++;
						mages.clear();
						summoned = 0;
						
						EntityUtils.sayTo(participating, "DARK_RED<< REDWaterway Seraph DARK_RED>>: WHITEYou thought I was done?", 50);
					}
					else
					{
						phase = 4;
						
						EntityUtils.sayTo(participating, "DARK_RED<< REDWaterway Seraph DARK_RED>>: WHITEUgh.", 10);
						EntityUtils.sayTo(participating, "DARK_RED<< REDWaterway Seraph DARK_RED>>: WHITEI guess I'll just kill you myself.", 20);
						
						boss.setLocation(bossArenaMid);
						
						new BukkitRunnable()
						{
							int tick = 0;
							@Override
							public void run() {
								if (tick == 40)
								{
									EntityUtils.sayTo(participating, "DARK_RED<< REDWaterway Seraph DARK_RED>>: WHITEWelcome to my lab, where you shall die.");
									for (GamePlayer p : participating) p.player.teleport(RandomUtils.getChoice(spawnPos));
								}
								else if (tick == 60)
								{
									cancel();
									phase = 5;
									
									boss.setImmune(false);
								}
								tick++;
							}
							
						}.runTaskTimer(Dungeons.instance, 0, 1);
					}
				}
			}
			else if (phase == 5)
			{
				flipflop++;
				if (corrupted)
				{
					if (flipflop % 20 == 19 && mages.size() < 24) mages.add(EnemyManager.spawn("seraph_lab_servant", RandomUtils.getChoice(spawnPos)));
				}
				else
				{
					if (flipflop % 60 == 45 && mages.size() < 16) mages.add(EnemyManager.spawn("seraph_lab_servant", RandomUtils.getChoice(spawnPos)));
				}
				
				
				if (boss.getHealth() / 3500 != bossHpTracker / 3500)
				{
					boss.setLocation(bossArenaMid);
				}
				
				if (boss.getHealth() < 3000 && !corrupted)
				{
					boss.setImmune(true);
					boss.setLocation(bossArenaMid);
					boss.setHealth(3000);
					phase = 6;
					EntityUtils.sayTo(participating, "DARK_RED<< REDWaterway Seraph DARK_RED>>: WHITEENOUGH!");
					ParticleUtils.spawn(bossArenaMid.clone().add(0, 3, 0), Particle.EXPLOSION, 4);
					
					for (GameEnemy servant : mages)
					{
						boss.setHealth(boss.getHealth() + 250);
						servant.remove();
						ParticleUtils.spawn(servant.getLocation(), Particle.HEART, 1, 8);
					}
					
					new BukkitRunnable()
					{
						int tick = 0;
						int golem = 0;

						@Override
						public void run() {
							tick++;
							if (tick == 120) 
							{
								phase = 7;
								cancel();
								boss.setImmune(false);
							}
							else if (tick % 10 == 9 && golem < 4)
							{
								mages.add(EnemyManager.spawn("seraph_golem", spawnPos[golem % 4]));
								golem++;
							}
							else if (tick % 8 == 4)
							{
								for (GamePlayer p : participating) 
								{
									ParticleUtils.spawnLine(boss.getLocation().add(0, 2.2, 0), p.getLocation().add(0, 1, 0),
											new Particle.DustOptions(Color.RED, 1.1F), 50);
									p.damage(10);
								}
								boss.setHealth(boss.getHealth() + 500);
							}
							
							if (tick == 9) poolBottom.setAll(Material.BLUE_ICE);
							else if (tick == 22) poolMid.setAll(Material.BLUE_ICE);
							else if (tick == 34) poolTop.setAll(Material.BLUE_ICE);
						}
					}.runTaskTimer(Dungeons.instance, 1, 1);
				}
				else if (boss.getHealth() < 3000 && corrupted)
				{
					boss.setImmune(true);
					boss.setLocation(bossArenaMid.clone().add(0, 3, 0));
					boss.setHealth(3000);
					
					phase = 6;

					ParticleUtils.spawn(bossArenaMid.clone().add(0, 3, 0), Particle.EXPLOSION, 4);
					ParticleUtils.spawn(bossArenaMid.clone().add(2, 3, 0), Particle.EXPLOSION, 4);
					ParticleUtils.spawn(bossArenaMid.clone().add(0, 3, 3), Particle.EXPLOSION, 4);
					ParticleUtils.spawn(bossArenaMid.clone().add(1, 3, 2), Particle.EXPLOSION, 4);
					EntityUtils.sayTo(participating, "DARK_RED<< REDWaterway Seraph DARK_RED>>: WHITEENOUGH!");
					EntityUtils.sayTo(participating, "DARK_RED<< REDWaterway Seraph DARK_RED>>: WHITEYou leave me no choice.", 20);
					EntityUtils.sayTo(participating, "DARK_RED<< REDWaterway Seraph DARK_RED>>: WHITEI must test my latest invention.", 40);
					
					for (GameEnemy servant : mages)
					{
						boss.setHealth(boss.getHealth() + 500);
						servant.remove();
						ParticleUtils.spawn(servant.getLocation(), Particle.HEART, 1, 8);
					}
					
					new BukkitRunnable()
					{
						int tick = 0;

						@Override
						public void run() {
							tick++;
							if (tick == 240) 
							{
								phase = 7;
								cancel();
								boss.setImmune(false);
							}
							else if (tick == 120)
							{
								boss.deregisterEnemy();
								boss.main.remove();
								boss.main = Dungeons.w.spawnEntity(bossArenaMid.clone().add(0, 3, 0), EntityType.IRON_GOLEM);
								boss.register();
								boss.setImmune(true);
								ParticleUtils.spawn(bossArenaMid.clone().add(0, 3, 0), Particle.EXPLOSION, 4);
								ParticleUtils.spawn(bossArenaMid.clone().add(2, 3, 0), Particle.EXPLOSION, 4);
								ParticleUtils.spawn(bossArenaMid.clone().add(0, 3, 3), Particle.EXPLOSION, 4);
								ParticleUtils.spawn(bossArenaMid.clone().add(1, 3, 2), Particle.EXPLOSION, 4);
							}
							else if (tick < 120 && tick % 2 == 0)
							{
								for (Location l : spawnPos) ParticleUtils.spawnLine(l.clone().add(0, 1, 0), boss.getLocation().add(0, 1.2, 0),
										new Particle.DustOptions(Color.PURPLE, 1.4F), 80);
							}
							
							
							if (tick % 5 == 4)
							{
								for (GamePlayer p : participating) 
								{
									ParticleUtils.spawnLine(boss.getLocation().add(0, 2.2, 0), p.getLocation().add(0, 1, 0),
											new Particle.DustOptions(Color.RED, 1.1F), 50);
									p.damage(8);
								}
								boss.setHealth(boss.getHealth() + 1250);
							}
							
							if (tick == 9) poolBottom.setAll(Material.OBSIDIAN);
							else if (tick == 22) poolMid.setAll(Material.OBSIDIAN);
							else if (tick == 34) poolTop.setAll(Material.OBSIDIAN);
						}
					}.runTaskTimer(Dungeons.instance, 1, 1);
				}
				
				bossHpTracker = boss.getHealth();
			}
			else if (phase == 7 && boss.dead)
			{
				for (GameEnemy e : mages) e.kill();
				
				EntityUtils.sayTo(participating, "DARK_RED<< REDWaterway Seraph DARK_RED>>: WHITEHow could this happen?");
				EntityUtils.sayTo(participating, "DARK_RED<< REDWaterway Seraph DARK_RED>>: WHITEAll my work.. for nothing..", 15);
				
				phase = 8;
				new BukkitRunnable()
				{
					int tick = 0;
					@Override
					public void run() {
						// TODO Auto-generated method stub
						tick++;
						if (tick == 10) poolTop.revert();
						if (tick == 20) poolMid.revert();
						if (tick == 30) poolBottom.revert();
						if (tick == 40)
						{
							int i_max = corrupted ? 20 : 7;
							for (int i = 0; i < i_max; i++)
							{
								TNTPrimed tnt = (TNTPrimed)Dungeons.w.spawnEntity(tntSpawn, EntityType.TNT);
								tnt.setFuseTicks(60 - (i_max - i));
								
								tnt.setVelocity(new Vector(RandomUtils.getDouble(0, 1.1), 0, RandomUtils.getDouble(-0.75, 0.75)));
							}
						}
						if (tick == 100) 
						{
							hole.setAll(Material.AIR);
							phase = 9;
							cancel();
						}
					}
					
				}.runTaskTimer(Dungeons.instance, 1, 1);
			}
			else if (phase == 9)
			{
				for (GamePlayer p : participating)
				{
					// REWARD THE FIRST
					// 2% chance for 1x Super-Flaming Sword
					// 5% chance for a Storm Catalyst
					// 98% chance for 1x Waterway Gold
					int weight = RandomUtils.getRandom(1, 100);
					Location rew1 = bossArenaMid.clone().subtract(0, 0, 3);
					if (weight <= 2) rewards.add(GameFloatingItem.spawn(rew1, ItemFactory.build("super_flaming_sword", 1), p));
					else if (weight <= 7) rewards.add(GameFloatingItem.spawn(rew1, ItemFactory.build("rain_summon", 1), p));
					else rewards.add(GameFloatingItem.spawn(rew1, ItemFactory.build("waterway_gold", 1), p));
					
					
					// REWARD THE SECOND
					// 20% chance for 1x Ender Stew
					// 50% chance for 1x Seraph's Arrow Bundle
					// 20% chance for 2x Seraph's Arrow Bundles
					// 10% chance for 1x Fish Barrel
					weight = RandomUtils.getRandom(1, 100);
					Location rew2 = bossArenaMid.clone();
					if (weight <= 20) rewards.add(GameFloatingItem.spawn(rew2, ItemFactory.build("ender_stew", 1), p));
					else if (weight <= 70) rewards.add(GameFloatingItem.spawn(rew2, ItemFactory.build("arrow_bundle_seraph", 1), p));
					else if (weight <= 90) rewards.add(GameFloatingItem.spawn(rew2, ItemFactory.build("arrow_bundle_seraph", 2), p));
					else rewards.add(GameFloatingItem.spawn(rew2, ItemFactory.build("fish_barrel", 1), p));
					
					// REWARD THE THIRD
					// 6% chance for the Seraph's Talisman
					// 14% chance for 3 Seraph's Arrow Bundles
					// 10% chance for enchantment [Blade 3]
					// 20% chance for enchantment [Blade 2]
					// 50% chance for enchantment [Blade 1]
					weight = RandomUtils.getRandom(1, 100);
					Location rew3 = bossArenaMid.clone().add(0, 0, 3);
					if (weight <= 6) rewards.add(GameFloatingItem.spawn(rew3, ItemFactory.build("seraph_talisman", 1), p));
					else if (weight <= 20) rewards.add(GameFloatingItem.spawn(rew3, ItemFactory.build("arrow_bundle_seraph", 3), p));
					else if (weight <= 30) rewards.add(GameFloatingItem.spawn(rew3, ItemFactory.buildBook("ENCHANT_BLADE-3"), p));
					else if (weight <= 50) rewards.add(GameFloatingItem.spawn(rew3, ItemFactory.buildBook("ENCHANT_BLADE-2"), p));
					else rewards.add(GameFloatingItem.spawn(rew3, ItemFactory.buildBook("ENCHANT_BLADE-1"), p));
					
					if (corrupted)
					{
						// REWARD THE FOURTH
						// 50% chance for 2x Waterway Gold
						// 25% chance for enchantment [Corrupt Might 1]
						// 25% chance for enchantment [Fire Aspect 1]
						weight = RandomUtils.getRandom(1, 100);
						Location rew4 = bossArenaMid.clone().add(3, 0, 0);
						if (weight <= 50) rewards.add(GameFloatingItem.spawn(rew4, ItemFactory.build("waterway_gold", 2), p));
						else if (weight <= 75) rewards.add(GameFloatingItem.spawn(rew4, ItemFactory.buildBook("ENCHANT_CORRUPTMIGHT-1"), p));
						else rewards.add(GameFloatingItem.spawn(rew4, ItemFactory.buildBook("ENCHANT_FIREASPECT-1"), p));
					}
				}
				phase = 10;
			}
			else if (phase == 10)
			{
				rewards.removeIf((i) -> i.collected);
				if (rewards.size() == 0 && EntityUtils.getNearbyPlayers(bossArenaMid, 16).size() == 0) endFight();
			}
		}
	}
	
	public static void endFight()
	{
		active = false;
		phase = 0;
		
		BlockUtils.BlockStructure wall = BlockUtils.getStructure("SERAPH_BLOCKING_WALL");
		wall.wipe();
		entrance.setAll(Material.RED_STAINED_GLASS);
		
		participating.clear();
		summoned = 0;
		repeats = 0;
		hole.setAll(Material.CRACKED_STONE_BRICKS);
		mageExit.setAll(Material.PURPLE_STAINED_GLASS);
		poolTop.setAll(Material.WATER);
		poolMid.setAll(Material.WATER);
		poolBottom.setAll(Material.WATER);
	}
	
	public static boolean attemptStartCorruptFight()
	{
		if (active) return false;
		corrupted = true;
		
		active = true;
		phase = -1;
		spawnText.remove();
		
		new BukkitRunnable() {
			int tick = 0;
			BlockUtils.BlockStructure wall = BlockUtils.createStructure("SERAPH_BLOCKING_WALL");
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				tick++;
				if (tick == 40)
				{
					cancel();
					
					entrance.revert();
					phase = 0;
					
					
					for (GamePlayer player : EntityUtils.getNearbyPlayers(altar, 9))
					{
						if (player.getLocation().getBlockX() > -177) participating.add(player);
					}
				}
				else if (tick == 5)
				{
					for (int z = 32; z <= 36; z++) wall.fill(-178, 87, z, Material.BEDROCK);
					for (int z = 32; z <= 36; z++) wall.fill(-178, 86, z, Material.BEDROCK);
				}
				else if (tick == 15)
				{
					for (int z = 32; z <= 36; z++) wall.fill(-178, 85, z, Material.BEDROCK);
				}
				else if (tick == 25)
				{
					for (int z = 32; z <= 36; z++) wall.fill(-178, 84, z, Material.BEDROCK);
				}
				else if (tick == 35)
				{
					for (int z = 32; z <= 36; z++) wall.fill(-178, 83, z, Material.BEDROCK);
				}
			}
			
		}.runTaskTimer(Dungeons.instance, 1, 1);
		
		return true;
	}
	
	public static boolean attemptStartFight()
	{
		if (active) return false;
		
		active = true;
		phase = -1;
		spawnText.remove();
		
		new BukkitRunnable() {
			int tick = 0;
			BlockUtils.BlockStructure wall = BlockUtils.createStructure("SERAPH_BLOCKING_WALL");
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				tick++;
				if (tick == 40)
				{
					cancel();
					
					entrance.revert();
					phase = 0;
					
					
					for (GamePlayer player : EntityUtils.getNearbyPlayers(altar, 9))
					{
						if (player.getLocation().getBlockX() > -177) participating.add(player);
					}
				}
				else if (tick == 5)
				{
					for (int z = 32; z <= 36; z++) wall.fill(-178, 87, z, Material.BEDROCK);
					for (int z = 32; z <= 36; z++) wall.fill(-178, 86, z, Material.BEDROCK);
				}
				else if (tick == 15)
				{
					for (int z = 32; z <= 36; z++) wall.fill(-178, 85, z, Material.BEDROCK);
				}
				else if (tick == 25)
				{
					for (int z = 32; z <= 36; z++) wall.fill(-178, 84, z, Material.BEDROCK);
				}
				else if (tick == 35)
				{
					for (int z = 32; z <= 36; z++) wall.fill(-178, 83, z, Material.BEDROCK);
				}
			}
			
		}.runTaskTimer(Dungeons.instance, 1, 1);
		
		return true;
	}
	
	//-178, 83, 32
	
	
	
	@Override
	public void onEnd()
	{
		if (active) endFight();
		if (spawnText != null) spawnText.remove();
	}
}
