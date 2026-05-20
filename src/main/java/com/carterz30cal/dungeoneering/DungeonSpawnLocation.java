package com.carterz30cal.dungeoneering;

import com.carterz30cal.entities.enemies.core.EnemyManager;
import com.carterz30cal.utils.RandomUtils;
import org.bukkit.Location;

public class DungeonSpawnLocation {
	public Location location;
	public FloorMaterials floor;
	
	public boolean spawned;
	
	public DungeonSpawnLocation (Location l, FloorMaterials m) 
	{
		location = l.add(0.5, 0, 0.5);
		floor = m;
	}
	
	public void spawn() {
		if (spawned) return;
		
		EnemyManager.spawn(RandomUtils.getChoice(floor.enemies), location);
		spawned = true;
	}
}
