package com.carterz30cal.areas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.Particle.DustOptions;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.carterz30cal.entities.EnemyManager;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GameParticleProjectile;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.BlockUtils;
import com.carterz30cal.utils.BlockUtils.BlockStructure;
import com.carterz30cal.utils.RandomUtils;

public class BossHydra extends AbstractArea {
	
	private static final Particle.DustOptions DUST_SPIT = new Particle.DustOptions(Color.LIME, 1.3F);
	private static final Location ROOT = new Location(Dungeons.w, -167, 79.2, -23);
	
	private static BossHydra instance;
	public static int tierFighting;
	public static boolean active = false;
	
	public static int phase;
	public static int headsKilled;
	private static int headsTotal;
	public int currentTick = 0;
	
	public List<BlockStructure> structures = new ArrayList<>();
	public static List<GameEnemy> heads = new ArrayList<>();
	public static List<GameEnemy> minions = new ArrayList<>();
	
	public static Set<GamePlayer> participants = new HashSet<>();
	
	
	public BossHydra() {
		instance = this;
	}
	
	public void onTick()
	{
		if (!active) return;
		
		boolean closeEnough = false;
		for (GamePlayer p : participants) {
			if (p.getLocation().distance(ROOT) > 35) continue;
			closeEnough = true;
			break;
		}
		if (!closeEnough && participants.size() > 0) {
			onEnd();
		}
		
		currentTick++;
		if (phase == 0) {
			//-164, 79, -28
			//-159, 75, -20
			int y = -1;
			
			switch (currentTick) {
			case 1:
				y = 79;
				break;
			case 11:
				y = 78;
				break;
			case 21:
				y = 77;
				break;
			case 19:
				heads.add(EnemyManager.spawn("hydra", new Location(Dungeons.w, -167, 79.2, -23)));
				break;
			case 30:
				y = 76;
				break;
			case 23:
				heads.add(EnemyManager.spawn("hydra", new Location(Dungeons.w, -165, 79.2, -21)));
				break;
			case 37:
				y = 75;
				break;
			case 31:
				heads.add(EnemyManager.spawn("hydra", new Location(Dungeons.w, -164.5, 78, -17.5)));
				break;
			case 33:
				heads.add(EnemyManager.spawn("hydra", new Location(Dungeons.w, -166, 76.5, -25)));
				break;
			case 40:
				heads.add(EnemyManager.spawn("hydra", new Location(Dungeons.w, -168, 77, -21.5)));
				break;
			case 50:
				phase = 1;
				headsTotal = heads.size();
				currentTick = 0;
			default: break;
			}
			
			if (y != -1) {
				BlockStructure struct = BlockUtils.createStructure("hydra_" + y);
				for (int x = -165; x <= -159; x++) {
					for (int z = -29; z <= -20; z++) {
						struct.setIf(x, y, z, Material.AIR, Material.SAND);
					}
				}
				structures.add(struct);
			}
			
		}
		else if (phase == 1) phase1();
	}
	
	private void phase1() {
		if (heads.size() == 0) wonFight();
		
		if (participants.size() > 0 && currentTick % (int)(80D / ((headsTotal + 1) - heads.size())) == 2) {
			BlockStructure spit = BlockUtils.createStructure("spit" + currentTick);
			GameEnemy head = RandomUtils.getChoice(heads);
			GamePlayer choice = RandomUtils.getChoice(participants);
			
			if (choice != null && choice.getLocation().distance(head.getLocation()) < 30) {
				Location lo = choice.getLocation().add(0, 1, 0);
				Location s = head.getLocation().add(0, 0.5, 0);
				
				
				Vector dir = lo.clone().subtract(s.clone()).toVector().normalize();
				Vector bd = dir.clone().setY(0);
				Location bds = s.clone();
				while (bds.getBlock().getType() == Material.AIR) bds.setY(bds.getY() - 1);
				
				for (double i = 0; i <= lo.distance(s.clone()) + 7; i += 0.2) {
					Location b = bds.clone().add(bd.clone().multiply(i));
					spit.set(b.getBlockX(), b.getBlockY(), b.getBlockZ(), Material.LIME_CONCRETE);
				}
				
				new BukkitRunnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						GameParticleProjectile proj = GameParticleProjectile.spawnBasicProjectile(DUST_SPIT, s, dir);
						proj.speed = 12;
						proj.damagesPlayers = true;
						proj.piercesLeft = 4;
						proj.damage = 60;
						
						spit.setAll(Material.BLACK_CONCRETE);
					}
					
				}.runTaskLater(Dungeons.instance, 14);
				
				new BukkitRunnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						spit.wipe();
					}
					
				}.runTaskLater(Dungeons.instance, 20);
			}
			
			
		}
		
		
		if (participants.size() > 0 && RandomUtils.getRandom(1, 80) == 1) {
			BlockStructure slam = BlockUtils.createStructure("slam" + currentTick);
			GameEnemy head = RandomUtils.getChoice(heads);
			GamePlayer choice = RandomUtils.getChoice(participants);
			if (choice == null || (boolean)head.data.getOrDefault("onground", false)) return;
			Location lo = choice.getLocation();
			
			int fy = lo.getBlockY();
			while (Dungeons.w.getBlockAt(lo.getBlockX(), fy, lo.getBlockZ()).getType() == Material.AIR && fy > -50) fy--;
			
			
			final Location l = new Location(lo.getWorld(), lo.getBlockX(), fy, lo.getBlockZ());
			
			int slamRadius = 3;
			for (int x = -slamRadius; x <= slamRadius; x++) {
				for (int z = -slamRadius; z <= slamRadius; z++) {
					if (x*x + z*z > slamRadius*slamRadius) continue;
					int y = l.getBlockY() + 4;
					
					for (; y >= l.getBlockY(); y--) {
						int ax = l.getBlockX() + x;
						int az = l.getBlockZ() + z;
						slam.setIfNot(ax, y, az, Material.RED_CONCRETE, Material.AIR);
					}
				}
			}
			Location slamAbove = l.clone().add(1, 5, 0);
			Location original = (Location)head.data.get("original");
			Vector dir = slamAbove.clone().subtract(original.clone()).toVector();
			Vector dir2 = l.clone().subtract(slamAbove.clone()).toVector();
			new BukkitRunnable() {
				int tick = 0;
				@Override
				public void run() {
					// TODO Auto-generated method stub
					tick++;
					head.data.put("target", original.clone().add(dir.clone().multiply(tick/25D)));
					
					if (tick == 25) {
						for (int x = -slamRadius; x <= slamRadius; x++) {
							for (int z = -slamRadius; z <= slamRadius; z++) {
								if (x*x + z*z > slamRadius*slamRadius) continue;
								
								int y = l.getBlockY() + 4;
								
								for (; y >= l.getBlockY(); y--) {
									int ax = l.getBlockX() + x;
									int az = l.getBlockZ() + z;
									slam.setIfNot(ax, y, az, Material.BLACK_CONCRETE, Material.AIR);
								}
							}
						}
						cancel();
					}
				}
				
			}.runTaskTimer(Dungeons.instance, 0, 1);
			
			new BukkitRunnable() {
				int tick = 0;
				@Override
				public void run() {
					// TODO Auto-generated method stub
					tick++;
					head.data.put("target", slamAbove.clone().add(dir2.clone().multiply(tick/8D)));
					
					if (tick >= 8) {
						if (RandomUtils.getRandom(1, 4) == 1) {
							head.data.put("onground", true);
							head.data.put("target", l);
						}
						else head.data.put("target", original);
						slam.wipe();
						
						for (GamePlayer p : PlayerManager.getOnlinePlayers()) {
							if (p.getLocation().distance(l) < slamRadius) p.damage(200);
						}
						Dungeons.w.createExplosion(l, 2F, false, false);
						
						cancel();
					}
				}
			}.runTaskTimer(Dungeons.instance, 26, 1);
		}
	}
	
	private void wonFight() {
		phase = 2;
		
		int timeBetween = 6;
		for (int y = 75; y <= 79; y++) {
			int delay = (y - 74) * timeBetween;
			final int yo = y;
			new BukkitRunnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					BlockStructure s = BlockUtils.getStructure("hydra_" + yo);
					if (s != null) s.wipe();
				}
				
			}.runTaskLater(Dungeons.instance, delay);
		}
		
		new BukkitRunnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (GamePlayer part : participants) {
					if (part == null) continue;
					part.sendMessage("DARK_PURPLEBOLDHYDRA DOWN!", Sound.ENTITY_PLAYER_LEVELUP);
				}
				onEnd();
			}
			
		}.runTaskLater(Dungeons.instance, 6 * timeBetween);
	}
	
	public static void respawnHead(Location pos) {
		if (!active) return;
		new BukkitRunnable() {

			@Override
			public void run() {
				if (!active) return;
				heads.add(EnemyManager.spawn("hydra", pos));
			}
			
		}.runTaskLater(Dungeons.instance, 260 + 40 * headsKilled);
	}
	
	public void onEnd()
	{
		for (BlockStructure s : structures) s.wipe();
		for (GameEnemy h : heads) h.remove();
		for (GameEnemy m : minions) m.remove();
		active = false;
	}
	
	public static boolean startFight(int tier) {
		if (active) return false;
		
		tierFighting = tier;
		active = true;
		phase = 0;
		headsKilled = 0;
		instance.currentTick = 0;
		
		return true;
	}
}
