package com.carterz30cal.areas;

import com.carterz30cal.entities.EnemyManager;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.Box;
import com.carterz30cal.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnemySpawner extends BukkitRunnable
{
	public List<GameEnemy> enemies = new ArrayList<>();
	
	public Location corner1;
	public Location corner2;
	
	private int mode = 0;
	public Map<Integer, List<String>> types = new HashMap<>();
	
	//public List<String> types = new ArrayList<>();
	public int max;
	public int absMax = -1;
	public AbstractArea area;
	public double maxMult = 1;
	
	
	public SpawnerModeLink link;

    private final Box bounds;
    private final Box triggerBounds;

    private int emptyTicks;
	
	public EnemySpawner(int x1, int z1, int x2, int z2, int y, AbstractArea area)
    {
		this.area = area;

        bounds = new Box(x1, y, z1, x2, y, z2);
        triggerBounds = bounds.Expand(2, 4, 2);

        corner1 = bounds.GetLowerCornerAsLocation();
        corner2 = bounds.GetUpperCornerAsLocation();

        max = (int) Math.abs(bounds.GetHorizontalCrossSectionalArea() / 95D);
		
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
		if (types.isEmpty()) return;
		
		boolean nearby = false;
		int numPlayers = 0;
		for (GamePlayer player : PlayerManager.players.values())
		{
            if (!triggerBounds.IsWithin(player.getLocation())) continue;

			numPlayers++;
			nearby = true;
			break;
		}
		if (nearby) {
            enemies.removeIf((e) -> e.dead);

            double logScaler = 2.5 * Math.log1p(numPlayers);

            int tempMax = (int) Math.round(max * logScaler * maxMult);
            if (absMax != -1) tempMax = Math.min(Math.max(1, tempMax), absMax);
            while (enemies.size() < tempMax)
            {
                GameEnemy spawned = EnemyManager.spawn(RandomUtils.getChoice(types.get(link == null ? mode : link.mode)), RandomUtils.getRandomInside(corner1, corner2));
                spawned.spawnedArea = area;
                enemies.add(spawned);
            }
        }
        else if (enemies.isEmpty()) return;
        else if (emptyTicks >= 20 * 60) {
            for (GameEnemy enemy : enemies) {
                enemy.remove();
            }
            enemies.clear();
            emptyTicks = 0;
        }
        else {
            emptyTicks += 200;
        }
	}
	
}
