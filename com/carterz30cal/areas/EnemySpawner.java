package com.carterz30cal.areas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import com.carterz30cal.entities.EnemyManager;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.RandomUtils;

public class EnemySpawner extends BukkitRunnable
{
	public List<GameEnemy> enemies = new ArrayList<>();
	
	public Location corner1;
	public Location corner2;
	
	private int mode = 0;
	public Map<Integer, List<String>> types = new HashMap<>();
	
	//public List<String> types = new ArrayList<>();
	public int max;
	public AbstractArea area;
	
	
	public SpawnerModeLink link;
	
	public EnemySpawner(int x1, int z1, int x2, int z2, int y, AbstractArea area)
	{
		corner1 = new Location(Dungeons.w, x1, y, z1);
		corner2 = new Location(Dungeons.w, x2, y, z2);
		
		max = Math.abs((x2 - x1) * (z2 - z1) / 10);
		this.area = area;
		
		runTaskTimer(Dungeons.instance, 200, 200);
	}
	
	public static EnemySpawner create(int x1, int z1, int x2, int z2, int y, AbstractArea area, SpawnerModeLink link)
	{
		EnemySpawner spawner = new EnemySpawner(x1, z1, x2, z2, y, area);
		spawner.link = link;
		return spawner;
	}
	
	public void addType(String type)
	{
		addType(type, 0, 1);
	}
	
	public void addType(String type, int mode)
	{
		addType(type, mode, 1);
	}
	
	public void addType(String type, int mode, int weight)
	{
		types.putIfAbsent(mode, new ArrayList<>());
		for (int i = 0; i < weight; i++) types.get(mode).add(type);
	}
	
	public void addTypeWithWeight(String type, int weight) {
		addType(type, 0, weight);
	}
	
	public void setMode(int mode) {
		if (link != null) return;
		this.mode = mode;
	}
	
	
	
	@Override
	public void run() {
		if (types.size() == 0) return;
		
		boolean nearby = false;
		for (GamePlayer player : PlayerManager.players.values())
		{
			if (player.getLocation().distance(corner1) > 30) continue;
			
			nearby = true;
			break;
		}
		if (!nearby) return;
		
		enemies.removeIf((e) -> e.dead);
		while (enemies.size() < max)
		{
			GameEnemy spawned = EnemyManager.spawn(RandomUtils.getChoice(types.get(link == null ? mode : link.mode)), RandomUtils.getRandomInside(corner1, corner2));
			spawned.spawnedArea = area;
			enemies.add(spawned);
		}
	}
	
}
