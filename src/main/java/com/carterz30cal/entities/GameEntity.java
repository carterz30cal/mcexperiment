package com.carterz30cal.entities;

import com.carterz30cal.entities.enemies.core.GameEnemy;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public abstract class GameEntity
{
	public static Map<UUID, GameEntity> entities = new HashMap<>();
	public static boolean allowDeregisters = true;

    @Deprecated
	public double health = 1;
	public boolean dead;
    protected UUID uuid;

	public abstract void remove();
	
	public abstract Location getLocation();
	
	public double getDistance(Location l) {
		return l.distance(getLocation());
	}

	public static GameEntity get(Entity e)
	{
		if (e == null || !e.getPersistentDataContainer().has(GameEnemy.keyEnemy, PersistentDataType.STRING)) return null;

        UUID uuid = UUID.fromString(Objects.requireNonNull(e.getPersistentDataContainer().get(GameEnemy.keyEnemy, PersistentDataType.STRING)));
		return entities.get(uuid);
	}

    public static GameEntity get(@NotNull UUID uuid) {
        return entities.get(uuid);
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
	
    public UUID getUUID() {
        return uuid;
    }
}
