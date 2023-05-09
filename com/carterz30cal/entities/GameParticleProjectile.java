package com.carterz30cal.entities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.EntityUtils;
import com.carterz30cal.utils.ParticleUtils;

public class GameParticleProjectile extends GameEntity 
{
	public Particle.DustOptions display;
	public Location location;
	public Vector direction;
	public double speed;
	public int damage;
	public boolean damagesPlayers;
	public int piercesLeft;
	
	private int heartbeat;
	private List<GameEntity> hit = new ArrayList<>();
	
	protected BukkitRunnable ticker;
	protected GamePlayer playerOwner;
	
	@Override
	public int getHealth() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

	@Override
	public void damage(DamageInfo info) {
		// TODO Auto-generated method stub

	}

	@Override
	public Location getLocation() {
		// TODO Auto-generated method stub
		return location;
	}
	
	protected void tick() {
		heartbeat--;
		Location next = location.clone().add(direction.clone().multiply(speed / 20D));
		
		List<GameEntity> checking;
		if (damagesPlayers) checking = PlayerManager.getOnlinePlayersAsEntity();
		else checking = EntityUtils.getNearbyEnemiesAsEntity(next, 2);
		checking.removeAll(hit);
		for (GameEntity e : checking) {
			Location check = e.getLocation();
			check.setY(next.getY());
			
			double dy = next.getY() - e.getLocation().getY();
			
			if (check.distance(next) <= 0.6 && dy <= 1.8 && dy > 0)
			{
				hit.add(e);
				
				DamageInfo info = new DamageInfo();
				info.damage = damage;
				info.attacker = playerOwner;
				info.defender = e;
				info.type = DamageType.MAGICAL;
				info.main = true;
				
				e.damage(info);
				piercesLeft--;
			}
			
			if (piercesLeft <= 0) {
				ticker.cancel();
				return;
			}
		}
		
		ParticleUtils.spawnLine(location, next, display, 3);
		location = next;
		if (heartbeat <= 0) ticker.cancel();
	}

	public static GameParticleProjectile spawnBasicProjectile(Particle.DustOptions dust, Location location, Vector vector) {
		GameParticleProjectile projectile = new GameParticleProjectile();
		projectile.location = location;
		projectile.direction = vector;
		projectile.display = dust;
		projectile.heartbeat = 200;
		
		projectile.ticker = new BukkitRunnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				projectile.tick();
			}
			
		};
		projectile.ticker.runTaskTimer(Dungeons.instance, 0, 1);
		
		return projectile;
	}
	
	
	public static GameParticleProjectile spawnPlayerProjectile(GamePlayer player, double speed, Color colour, double size, int damage, int pierces) {
		GameParticleProjectile projectile = new GameParticleProjectile();
		projectile.location = player.getLocation().add(0, 1.3, 0);
		projectile.direction = player.getDirection();
		projectile.damage = damage;
		projectile.piercesLeft = pierces + 1;
		projectile.damagesPlayers = false;
		projectile.display = new Particle.DustOptions(colour, (float)size);
		projectile.speed = speed;
		projectile.heartbeat = (int)(600 / speed);
		projectile.playerOwner = player;
		
		projectile.ticker = new BukkitRunnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				projectile.tick();
			}
			
		};
		projectile.ticker.runTaskTimer(Dungeons.instance, 0, 1);
		
		return projectile;
	}
}
