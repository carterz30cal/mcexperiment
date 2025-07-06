package com.carterz30cal.entities.enemies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GameParticleProjectile;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.BlockUtils;
import com.carterz30cal.utils.BlockUtils.BlockStructure;
import com.carterz30cal.utils.EntityUtils;
import com.carterz30cal.utils.MathsUtils;
import com.carterz30cal.utils.ParticleUtils;
import com.carterz30cal.utils.RandomUtils;

public class EnemyTypeTitanDrench extends EnemyTypeDamageCapped
{
	public final Particle.DustOptions DUST_WARNING = new Particle.DustOptions(Color.RED, 0.6F);
	public final Particle.DustOptions DUST_PHASE2_WARNING = new Particle.DustOptions(Color.PURPLE, 0.6F);
	public final Particle.DustOptions DUST_ATTACK = new Particle.DustOptions(Color.WHITE, 0.7F);
	public final Particle.DustOptions DUST_SAFE = new Particle.DustOptions(Color.GREEN, 0.4F);
	
	
	public int explosionRadius;
	public int explosionDamage;
	public int explosionCooldown;
	
	public int slashDamage;
	public int slashCooldown;
	public int slashTime;
	
	public int beamDamage;
	public int beamMinRange;
	public int beamCooldown;
	
	public int rainDamage;
	public int rainMinRange;
	public int rainMaxRange;
	
	public EnemyTypeTitanDrench(ConfigurationSection m) {
		super(m);
		// TODO Auto-generated constructor stub
		
		explosionRadius = m.getInt("titan-fight.explosion-radius", 5);
		explosionDamage = m.getInt("titan-fight.explosion-damage", 500);
		explosionCooldown = m.getInt("titan-fight.explosion-cooldown", 300);
		
		slashDamage = m.getInt("titan-fight.slash-damage", 40);
		slashCooldown = m.getInt("titan-fight.slash-cooldown", 40);
		slashTime = m.getInt("titan-fight.slash-time", 17);
		
		beamDamage = m.getInt("titan-fight.beam-damage", 100);
		beamMinRange = m.getInt("titan-fight.beam-min-range", 8);
		beamCooldown = m.getInt("titan-fight.beam-cooldown", 30);
		
		rainDamage = m.getInt("titan-fight.rain-damage", 20);
		rainMinRange = m.getInt("titan-fight.rain-min-range", 5);
		rainMaxRange = m.getInt("titan-fight.rain-max-range", 10);
	}

	@Override
	public GameEnemy generate(Location location) {
		GameEnemy mob = new GameEnemy(location, this);
		
		mob.main = EntityUtils.spawnPart(mainType, location);
		if (mob.main instanceof Mob)
		{
			Mob main = (Mob)mob.main;
			for (EquipmentSlot slot : equipment.keySet())
			{
				EntityUtils.setArmourPiece(main, slot, equipment.get(slot));
			}
		}
		
		mob.doTick();
		mob.register();
		return mob;
	}
	
	public String onName(GameEnemy enemy) {
		boolean exploding = (boolean)enemy.data.getOrDefault("exploding", false);
		if (exploding) {
			return "DARK_REDBOLDEXPLODING!";
		}
		else if (enemy.health < 0.4) {
			return "DARK_RED\u2620 REDTitan DARK_RED\u2620";
		}
		else return name;
	}
	
	@SuppressWarnings("unchecked")
	public void onTick(GameEnemy enemy)
	{
		int explosion = (int)enemy.data.getOrDefault("explosion", 0);
		boolean exploding = (boolean)enemy.data.getOrDefault("exploding", false);
		
		if (enemy.health == 1) {
			
		}
		else if (exploding) {
			ParticleUtils.spawnAround(enemy.getMain(), new DustOptions(Color.GRAY, 1.3f), 6);
		}
		else if (explosion >= explosionCooldown) {
			BlockStructure warning = BlockUtils.createStructure("explosion-warning-" + enemy.getMain().getUniqueId());
			new BukkitRunnable() {

				@Override
				public void run() {
					
					for (GamePlayer p : PlayerManager.getOnlinePlayers()) {
						double dist = p.getLocation().distance(enemy.getLocation());
						if (dist <= explosionRadius)
						{
							p.damage(explosionDamage);
						}
						else if (dist <= explosionRadius + 1) {
							ArmorStand holo = EntityUtils.spawnHologram(p.getLocation().add(0, 1.1, 0), 40);
							EntityUtils.setEntityName(holo, "REDBOLDClose!");
						}
					}
					
					Dungeons.w.createExplosion(enemy.getLocation(), 4F, false, false, enemy.getMain());
					enemy.data.put("exploding", false);
					warning.wipe();
				}
				
			}.runTaskLater(Dungeons.instance, 70);
			
			EntityUtils.applyPotionEffect((LivingEntity)enemy.getMain(), PotionEffectType.SLOWNESS, 80, 20, true);
			for (int x = -explosionRadius; x <= explosionRadius; x++) {
				for (int z = -explosionRadius; z <= explosionRadius; z++) {
					int distance = (int)Math.sqrt(x*x + z*z);
					if (distance > explosionRadius) continue;
					
					int bx = enemy.getLocation().getBlockX() + x;
					int bz = enemy.getLocation().getBlockZ() + z;
					int y = enemy.getLocation().getBlockY() + 3;
					
					
					while (Dungeons.w.getBlockAt(bx, y, bz).getType() == Material.AIR && y > -50) y--;
					
					warning.set(bx, y, bz, Material.BLACK_CONCRETE);
				}
			}
			
			
			enemy.data.put("exploding", true);
			enemy.data.put("explosion", 0);
		}
		else enemy.data.put("explosion", explosion + 1);
		
		int slash = (int)enemy.data.getOrDefault("slash", 0);
		
		final double distance = enemy.target == null ? 99999999 : enemy.target.getLocation().distance(enemy.getLocation());
		
		if (slash >= slashCooldown && distance < 3 && !exploding) {
			Location pos = enemy.getLocation();
			final double degrees = (Math.toDegrees(Math.atan2(enemy.target.getLocation().getX() - enemy.getLocation().getX(), enemy.target.getLocation().getZ() - enemy.getLocation().getZ())));
			new BukkitRunnable() {
				int time = slashTime;
				
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					time--;
					Set<GamePlayer> hit = new HashSet<>();
					for (double d = degrees - 60; d <= degrees + 60; d += 5) {
						double x = pos.getX() + MathsUtils.getCircleX(d) * distance;
						double z = pos.getZ() + MathsUtils.getCircleZ(d) * distance;
						double y = pos.getY() + 1.3 + (0.2 * ((d - degrees) / 60));
						
						Location location = new Location(pos.getWorld(), x, y, z);
						if (time == 0) {
							for (GamePlayer o : PlayerManager.getOnlinePlayers()) {
								if (hit.contains(o)) continue;
								double dx = Math.abs(o.getLocation().getX() - x);
								double dz = Math.abs(o.getLocation().getZ() - z);
								double dy = y - o.getLocation().getY();
								
								double dist = Math.sqrt(dx*dx + dz*dz);
								if (dist < 0.6 && dy < 2) {
									o.damage(slashDamage);
									hit.add(o);
								}
							}
							ParticleUtils.spawn(location, DUST_ATTACK, 0);
						}
						else {
							if (time % 4 == 0) ParticleUtils.spawn(location, DUST_WARNING, 0);
						}
					}
					if (time == 0) cancel();
				}
				
			}.runTaskTimer(Dungeons.instance, 1, 1);
			
			if (enemy.health < 0.4) {
				new BukkitRunnable() {
					int time = slashTime + 8;
					
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						time--;
						Set<GamePlayer> hit = new HashSet<>();
						for (double d = degrees - 60; d <= degrees + 60; d += 5) {
							double x = pos.getX() + MathsUtils.getCircleX(d) * (distance + 0.3);
							double z = pos.getZ() + MathsUtils.getCircleZ(d) * (distance + 0.2);
							double y = pos.getY() + 1.3 + (-0.2 * ((d - degrees) / 60));
							
							Location location = new Location(pos.getWorld(), x, y, z);
							if (time == 0) {
								for (GamePlayer o : PlayerManager.getOnlinePlayers()) {
									if (hit.contains(o)) continue;
									double dx = Math.abs(o.getLocation().getX() - x);
									double dz = Math.abs(o.getLocation().getZ() - z);
									double dy = y - o.getLocation().getY();
									
									double dist = Math.sqrt(dx*dx + dz*dz);
									if (dist < 0.6 && dy < 2) {
										o.damage(slashDamage);
										hit.add(o);
									}
								}
								ParticleUtils.spawn(location, DUST_ATTACK, 0);
							}
							else {
								if (time % 4 == 0) ParticleUtils.spawn(location, DUST_PHASE2_WARNING, 0);
							}
						}
						if (time == 0) cancel();
					}
					
				}.runTaskTimer(Dungeons.instance, 1, 1);
			}
			
			
			
			EntityUtils.applyPotionEffect((LivingEntity)enemy.getMain(), PotionEffectType.SLOWNESS, 30, 20, true);
			enemy.data.put("slash", 0);
		}
		else enemy.data.put("slash", slash + 1);
		
		if (enemy.health > 0.4) {
			int beam = (int)enemy.data.getOrDefault("beam", 0);
			if (beam >= beamCooldown) {
				// beam
				
				GamePlayer beamTarget = null;
				Set<GamePlayer> damagers = ((HashSet<GamePlayer>)enemy.data.getOrDefault("damagers", new HashSet<GamePlayer>()));
				List<GamePlayer> options = new ArrayList<>();
				for (GamePlayer damager : damagers) {
					if (damager == null) continue;
					
					double dist = damager.getLocation().distance(enemy.getLocation());
					if (dist >= beamMinRange && dist < 13) {
						options.add(damager);
					}
				}
				
				if (options.size() != 0) {
					beamTarget = RandomUtils.getChoice(options);
					
					
					Location l1 = enemy.getLocation().add(0, 1.7, 0);
					Vector line = beamTarget.getLocation().add(0, 1.8, 0).subtract(l1).toVector();
					
					Location l2 = l1.clone().add(line.clone().multiply(0.7));
					Location l3 = l1.clone().add(line.clone().multiply(1.1));
					new BukkitRunnable() {
						int time = 25;

						@Override
						public void run() {
							// TODO Auto-generated method stub
							time--;
							
							ParticleUtils.spawnLine(l1, l2, DUST_SAFE, 20);
							if (time > 0) {
								ParticleUtils.spawnLine(l2, l3, DUST_WARNING, 20);
							}
							else {
								ParticleUtils.spawnDamagingLine(l2, l3, DUST_ATTACK, 20, beamDamage);
								cancel();
							}
						}
					}.runTaskTimer(Dungeons.instance, 1, 1);
					
					enemy.data.put("beam", 0);
				}
			} 
			else {
				enemy.data.put("beam", beam + 1);
			}
		}
		else if (slash % 2 == 1){
			
			
			double degrees = RandomUtils.getDouble(0, 360);
			double circle = RandomUtils.getDouble(rainMinRange, rainMaxRange);
			double x = enemy.getLocation().getX() + MathsUtils.getCircleX(degrees) * circle;
			double z = enemy.getLocation().getZ() + MathsUtils.getCircleZ(degrees) * circle;
			double y = enemy.getLocation().getY() + 7;
			
			GameParticleProjectile rain = GameParticleProjectile.spawnBasicProjectile(DUST_ATTACK, new Location(enemy.getLocation().getWorld(), x, y, z), new Vector(0, -1, 0));
			rain.damage = rainDamage;
			rain.damagesPlayers = true;
			rain.piercesLeft = 5;
			rain.speed = 2.6;
		}
		
		
		
		/*
		int tick = (int)enemy.data.getOrDefault("life_drain_timer", 0);
		if (tick >= lifeDrainTimer)
		{
			for (GamePlayer player : EntityUtils.getNearbyPlayers(enemy.getLocation(), lifeDrainRadius))
			{
				player.damage(lifeDrain);
			}
			
			enemy.data.put("life_drain_timer", 0);
		}
		else enemy.data.put("life_drain_timer", tick + 1);
		*/
	}
	
	@SuppressWarnings("unchecked")
	public void onDamaged(GameEnemy enemy, DamageInfo info)
	{
		
		enemy.data.putIfAbsent("damagers", new HashSet<GamePlayer>());
		((HashSet<GamePlayer>)enemy.data.get("damagers")).add(info.attacker);
		
		super.onDamaged(enemy, info);
	}
	
	@SuppressWarnings("unchecked")
	public void onKilled(GameEnemy enemy)
	{
		Set<GamePlayer> damagers = ((HashSet<GamePlayer>)enemy.data.getOrDefault("damagers", new HashSet<GamePlayer>()));
		damagers.remove(enemy.lastDamager);
		
		for (GamePlayer damager : damagers)
		{
			enemy.dropItems(damager);
		}
		
		super.onKilled(enemy);
	}

}
