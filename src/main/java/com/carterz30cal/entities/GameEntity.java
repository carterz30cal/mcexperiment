package com.carterz30cal.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

public abstract class GameEntity
{
	public static Map<UUID, GameEntity> entities = new HashMap<>();
	public static boolean allowDeregisters = true;
	
	public double health = 1;
	public boolean dead;
	
	public abstract int getHealth();
	
	public abstract void remove();
	
	public abstract void damage(DamageInfo info);
	
	public void damage(int damage) {
		DamageInfo info = new DamageInfo();
		info.damage = damage;
		info.type = DamageType.PHYSICAL;
		
		damage(info);
	}
	
	public double getHeight() {
		return 2;
	}
	
	public Color getBloodColour() {
		return Color.RED;
	}
	
	public abstract Location getLocation();
	
	public double getDistance(Location l) {
		return l.distance(getLocation());
	}
	
	protected void register(UUID uuid)
	{
		entities.put(uuid, this);
	}
	protected final void deregister(UUID uuid)
	{
		if (!allowDeregisters) return;
		entities.remove(uuid);
	}
	
	public static GameEntity get(Entity e)
	{
		if (e == null || !e.getPersistentDataContainer().has(GameEnemy.keyEnemy, PersistentDataType.STRING)) return null;
		
		UUID uuid = UUID.fromString(e.getPersistentDataContainer().get(GameEnemy.keyEnemy, PersistentDataType.STRING));
		return entities.get(uuid);
	}
}
