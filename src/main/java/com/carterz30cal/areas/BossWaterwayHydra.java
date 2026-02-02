package com.carterz30cal.areas;

import com.carterz30cal.entities.EnemyManager;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GameFloatingItem;
import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.entities.enemies.EnemyTypeWave;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.ParticleUtils;
import com.carterz30cal.utils.RandomUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class BossWaterwayHydra extends AbstractArea
{
	public static boolean active = false;
	public static boolean corrupted = false;
	//public static int phase = 0;
	
	//public static Location altar = new Location(Dungeons.w, -169.5, 83, 34.5);
	//private static ArmorStand spawnText;
	//private static final Particle.DustOptions PITCHFORK_COLOUR = new Particle.DustOptions(Color.BLUE, 0.4F);
	
	protected static Location[] headPos = {
			new Location(Dungeons.w, -170, 83, -31),
			new Location(Dungeons.w, -164, 82, -27),
			new Location(Dungeons.w, -162, 79, -25),
			new Location(Dungeons.w, -163.5, 82, -19),
			};
	private static Location[] corruptedHeadPos = {
			new Location(Dungeons.w, -167, 83, -29),
			new Location(Dungeons.w, -162, 83, -20),
	};
	private static Location spawnCorner1 = new Location(Dungeons.w, -174, 76, -29);
	private static Location spawnCorner2 = new Location(Dungeons.w, -165, 76, -19);
	
	
	private static final Particle.DustOptions BEAM_SPAWNING = new Particle.DustOptions(Color.GREEN, 0.8F);
	private static final Particle.DustOptions BEAM_SPAWNING_SMALL = new Particle.DustOptions(Color.GREEN, 0.5F);
	
	private static Location arenaMiddle = new Location(Dungeons.w, -170, 75, -22);
	
	
	//protected static Location tntSpawn = new Location(Dungeons.w, -158, 82, 21);
	//protected static Location bossArenaMid = new Location(Dungeons.w, -149, 75, 21);
	
	public static int wave = 0;
	private static List<GamePlayer> participated = new ArrayList<>();
	private static List<GamePlayer> participating = new ArrayList<>();
	private static List<GameEnemy> enemies = new ArrayList<>();
	private static List<GameEnemy> heads = new ArrayList<>();
	
	private static List<String> choices = new ArrayList<>();
	private static int waveSpawnPoints = 0;
	private static int spawnPoints = 0;
	private int tick = 0;
	private int waveCooldown = 0;
	
	//private static GameEnemy boss;
	
	public BossWaterwayHydra()
	{
		super();
		
	}
	
	
	public static boolean hasParticipated(GamePlayer p) {
		return participated.contains(p);
	}
	
	public void onTick()
	{
		if (!active) return;
		
		participating.removeIf((p) -> p.player.getLocation().distance(arenaMiddle) > 25);
		enemies.removeIf((e) -> e.dead);
		
		tick++;
		waveCooldown--;
		
		
		if (participating.size() == 0) {
			endFight();
		}
		else if (enemies.size() == 0 && spawnPoints == 0 && waveCooldown <= 0) {
			wave++;
			addWaveSpice();
			
			int players = participating.size();
			int enemyCount = 10 + players * 2 + wave * 3;
			enemyCount = Math.min(enemyCount, 75);
			
			/*
			String choice = RandomUtils.getChoice(choices);
			while (enemyCount > 0) 
			{
				spawnQueue.add(choice);
				enemyCount--;
			}
			*/
			spawnPoints = 10 + ((3 + players) * (wave - 1)) + (8 * wave / 5);
			waveSpawnPoints = spawnPoints;
			
			waveCooldown = 150;
		}
		else if (spawnPoints > 0) {
			final int spawnTime = Math.max(8, 16 - wave / 2);
			if (tick % spawnTime == 0) {
				new BukkitRunnable() {
					Location l1 = RandomUtils.getChoice(heads).getLocation();
					Location l2 = RandomUtils.getRandomInside(spawnCorner1, spawnCorner2).add(0.5, 0, 0.5);
					
					Location c1 = l2.clone().subtract(0.5, 0,  0.5);
					Location c2 = l2.clone().subtract(0.5, 0, -0.5);
					Location c3 = l2.clone().add(0.5, 0,  0.5);
					Location c4 = l2.clone().add(0.5, 0, -0.5);
					
					int stick = 0;
					@Override
					public void run() {
						if (!active) cancel();
						else if (stick >= spawnTime + 5) {
							for (int w = choices.size() - 1; w >= 0; w--)
							{
								EnemyTypeWave choice = (EnemyTypeWave)EnemyManager.getType(choices.get(w));
								if (choice.wavePoints > spawnPoints) continue;
								
								double chance = 1 - Math.pow(choice.wavePoints / (double)waveSpawnPoints, 0.5);
								if (RandomUtils.getDouble(0, 1) > chance) continue;
								
								choice.wave = wave;
								spawnPoints -= choice.wavePoints;
								
								enemies.add(EnemyManager.spawn(choices.get(w), l2));
								cancel();
								break;
							}
						}
						else {
							ParticleUtils.spawnLine(l1, l2, BEAM_SPAWNING, 30);
							ParticleUtils.spawnLine(c1, c2, BEAM_SPAWNING_SMALL, 6);
							ParticleUtils.spawnLine(c1, c3, BEAM_SPAWNING_SMALL, 6);
							ParticleUtils.spawnLine(c1, c4, BEAM_SPAWNING_SMALL, 6);
							ParticleUtils.spawnLine(c2, c3, BEAM_SPAWNING_SMALL, 6);
							ParticleUtils.spawnLine(c2, c4, BEAM_SPAWNING_SMALL, 6);
							ParticleUtils.spawnLine(c3, c4, BEAM_SPAWNING_SMALL, 6);
						}
						stick++;
					}
					
				}.runTaskTimer(Dungeons.instance, 0, 1);
			}
		}
	}
	
	/*
	 * make waves more *SPICY*
	 * - your local meth dealer
	 */
	private void addWaveSpice() {
		if (wave == 5) choices.add("hydra_regular_flamer");
		if (wave == 8) choices.add("hydra_regular_mite");
		if (wave == 10) choices.add("hydra_regular_tanker");
		if (wave == 15) choices.add("hydra_regular_dominator");
		if (wave == 25) choices.add("hydra_regular_obliterator");
		if (wave == 35) choices.add("hydra_regular_deathbringer");
	}
	
	public static void doRewards() {
		/*
		 * REWARDS
		 * - every player gains 1 hydra flesh every 3 waves that are completed - and one as a minimum.
		 * - every player has a 20% chance of dropping a Sharpness book of Levels 1-3
		 * - at wave 10 or above, every player will have a 1 in 3 chance of dropping a Hydra Head.
		 * - - Hydra heads can be used to craft the exclusive Hydra Gear.
		 * - at wave 15 or above, every player will drop a Warped Hydra Flesh.
		 */
		
		for (GamePlayer p : participated) {
			int flesh = 1 + wave / 3;
			GameFloatingItem.spawn(arenaMiddle, ItemFactory.build("hydra_flesh", flesh), p);
			if (RandomUtils.getRandom(1, 5) == 1) 
			{
				int enchLevel = RandomUtils.getRandom(1, 3);
				GameFloatingItem.spawn(arenaMiddle.clone().subtract(0, 0, -2), ItemFactory.buildBook("ENCHANT_SHARPNESS-" + enchLevel), p);
			}
			if (wave >= 10 && RandomUtils.getRandom(1, 3) == 1) GameFloatingItem.spawn(arenaMiddle.clone().subtract(0, 0, 2), ItemFactory.build("hydra_head", 1), p);
			if (wave >= 15) GameFloatingItem.spawn(arenaMiddle.clone().subtract(-2, 0, 0), ItemFactory.build("warped_hydra_flesh", 1), p);
		}
	}
	
	public static void endFight()
	{
		active = false;
		
		doRewards();
		
		wave = 0;
		for (GameEnemy e : enemies) e.remove();
		for (GameEnemy e : heads) e.remove();
		spawnPoints = 0;
		choices.clear();
		
		participating.clear();
		participated.clear();
	}
	
	
	public static boolean attemptStartFight()
	{
		if (active) return false;
		for (GamePlayer p : PlayerManager.players.values()) {
			if (p.player.getLocation().distance(arenaMiddle) < 25) {
				participating.add(p);
				participated.add(p);
			}
		}
		if (participating.size() == 0) return false;
		
		active = true;
		corrupted = false;
		
		choices.add("hydra_regular_swarmer");
		
		
		
		for (Location l : headPos)
		{
			GameEnemy head = EnemyManager.spawn("hydra_head", l);
			head.getMain().setGravity(false);
			((LivingEntity)head.getMain()).setAI(false);
			heads.add(head);
		}
		
		return true;
	}
	
	//-178, 83, 32
	
	
	
	@Override
	public void onEnd()
	{
		if (active) endFight();
		//if (spawnText != null) spawnText.remove();
	}
}
