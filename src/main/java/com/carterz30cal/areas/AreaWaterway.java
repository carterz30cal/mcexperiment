package com.carterz30cal.areas;

import com.carterz30cal.entities.EnemyManager;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.main.Dungeons;
import org.bukkit.Location;

public class AreaWaterway extends AbstractArea 
{
	public static AreaWaterway instance;
	public final int KILLS_FOR_SPAWN = 10;
	public final int RAIN_TIME_ON = 200 * 6 * 10;
	public final int RAIN_TIME_OFF = 200 * 6 * 30;
	
	public static boolean isRaining = false;
	
	public int killCount;
	
	private int rainTick = 0;
	private GameEnemy titan;
	private GameEnemy hydra;
	
	private SpawnerModeLink link;
	public AreaWaterway()
	{
		super();
		instance = this;
		
		link = new SpawnerModeLink();
		link.mode = 0;

//
//
//		EnemySpawner lunatic1_a = createSpawner(-35, -1, -52, 7, 66, "lunatic_1");
//        lunatic1_a.maxMult = 1.5;
//        lunatic1_a.absMax = 8;
//
//        EnemySpawner lunatic1_b = createSpawner(6, -28, 47, -45, 70, "lunatic_1");
//        lunatic1_b.maxMult = 0.5;
//
//        EnemySpawner lunatic2_a = createSpawner(-46, 48, -56, 40, 65, "lunatic_2");
//        EnemySpawner lunatic2_b = createSpawner(-9, 56, 22, 92, 66, "lunatic_2");
//        lunatic2_b.maxMult = 0.6;
//        EnemySpawner lunatic2_c = createSpawner(-37, 76, -28, 90, 65, "lunatic_2");
//
//        EnemySpawner lunaticF_a = createSpawner(-71, 26, -61, 17, 77, "lunatic_fisherman_1");
//
//        EnemySpawner lunatic3_a = createSpawner(-86, 61, -97, 73, 69, "lunatic_3");


		
		Dungeons.w.setStorm(false);
	}
	



	public static boolean attemptSpawnHydra(String type)
	{
		if (instance.hydra == null || instance.hydra.dead)
		{
			instance.hydra = EnemyManager.spawn(type, new Location(Dungeons.w, -168, 77, -22));
			return true;
		}
		else return false;
	}
	
	
	@Override
	public void onKill(GameEnemy enemy)
	{
		killCount++;
		if (killCount >= KILLS_FOR_SPAWN && (titan == null || titan.dead))
		{
			String type = isRaining ? "titan_1_rain" : "titan_1";
			titan = EnemyManager.spawn(type, new Location(Dungeons.w, -82, 72, -10));
			
			killCount = 0;
			enemy.lastDamager.sendMessage("REDThe titan has appeared somewhere in Waterway...", 20);
		}
	}
	
	public void toggleRain()
	{
		isRaining = !isRaining;
		rainTick = 0;
		
		Dungeons.w.setStorm(isRaining);
		if (!isRaining) link.mode = 0;
		else link.mode = 1;
	}
	
	
	public void onTick()
	{
		//rainTick++;
		
		if (rainTick == RAIN_TIME_ON && isRaining) toggleRain();
		else if (rainTick == RAIN_TIME_OFF && !isRaining) toggleRain();
	}
}
