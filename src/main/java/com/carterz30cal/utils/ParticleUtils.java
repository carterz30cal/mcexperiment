package com.carterz30cal.utils;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.entities.PlayerManager;

public class ParticleUtils {
	public static void spawnAround(Entity p, Particle.DustOptions t, int a)
	{
		p.getWorld().spawnParticle(Particle.REDSTONE, p.getLocation().add(0, 1, 0), a, 0.5D, 0.25D, 0.5D, t);
	}
	public static void spawnAround(Entity p, Particle t, int a)
	{
		p.getWorld().spawnParticle(t, p.getLocation().add(0, 1, 0), a, 0.5D, 0.25D, 0.5D);
	}
	public static void spawn(Location l, Particle p, double radius)
	{
		l.getWorld().spawnParticle(p, l, 1, radius / 2, radius / 2, radius / 2, 0);
	}
	public static void spawn(Location l, Particle p, double radius, int quantity)
	{
		l.getWorld().spawnParticle(p, l, quantity, radius / 2, radius / 2, radius / 2, 0);
	}
	public static void spawn(Location l, Particle.DustOptions d, double radius)
	{
		l.getWorld().spawnParticle(Particle.REDSTONE, l, 1, radius / 2, radius / 2, radius / 2, 0, d);
	}
	
	
	
	
	public static void spawnLine(Location l1, Location l2,  Particle.DustOptions particle, int density)
	{
		Vector direction = l2.clone().subtract(l1.clone()).toVector().normalize().multiply(l1.distance(l2)/density);
		for (int i = 0; i <= density; i++) spawn(l1.clone().add(direction.clone().multiply(i)), particle, 0);
	}
	
	public static void spawnDamagingLine(Location l1, Location l2,  Particle.DustOptions particle, int density, int damage)
	{
		Vector direction = l2.clone().subtract(l1.clone()).toVector().normalize().multiply(l1.distance(l2)/density);
		for (int i = 0; i <= density; i++) 
		{
			Location pos = l1.clone().add(direction.clone().multiply(i));
			spawn(pos, particle, 0);
			
			Set<GamePlayer> hit = new HashSet<>();
			for (GamePlayer o : PlayerManager.getOnlinePlayers()) {
				if (hit.contains(o)) continue;
				double dx = Math.abs(o.getLocation().getX() - pos.getX());
				double dz = Math.abs(o.getLocation().getZ() - pos.getZ());
				double dy = pos.getY() - o.getLocation().getY();
				
				double dist = Math.sqrt(dx*dx + dz*dz);
				if (dist < 0.6 && dy < 2) {
					o.damage(damage);
					hit.add(o);
				}
			}
		}
	}
	
	public static void spawnLine(Location l1, Location l2,  Particle.DustOptions particle, int density, double progress)
	{
		Vector direction = l2.clone().subtract(l1.clone()).toVector().normalize().multiply(l1.distance(l2)/density);
		for (int i = 0; i <= (int)(density * progress); i++) spawn(l1.clone().add(direction.clone().multiply(i)), particle, 0);
	}
	
	public static Location spawnLine(Location start, Vector dir, int times, Particle.DustOptions particle)
	{
		Location st = start.clone();
		for (int i = 0; i < times; i++) 
		{	
			st = st.add(dir);
			spawn(st, particle, 0);
		}
		
		return st;
	}
	
}
