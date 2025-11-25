package com.carterz30cal.areas;

import org.bukkit.Location;

import com.carterz30cal.entities.EnemyManager;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.main.Dungeons;

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
	private EnemySpawner dripper1;
	private EnemySpawner dripper2;
	private EnemySpawner dripper3;
	private EnemySpawner dripper4;
	private EnemySpawner drenched1;
	private EnemySpawner drenched2;
	private EnemySpawner drenched3;
	private EnemySpawner drenched4;
	private EnemySpawner drenched4_2;
	public AreaWaterway()
	{
		super();
		instance = this;
		
		link = new SpawnerModeLink();
		link.mode = 0;



		EnemySpawner lunatic1_a = createSpawner(-35, -1, -52, 7, 66, "lunatic_1");
		lunatic1_a.absMax = 6;

        EnemySpawner lunatic1_b = createSpawner(6, -28, 47, -45, 70, "lunatic_1");
        lunatic1_b.maxMult = 0.8;

//		EnemySpawner lunatic2_a = EnemySpawner.create(-37, 21, -42, 24, 66, this, link);
//		lunatic2_a.addType("lunatic_2", 0);
//
//		EnemySpawner lunatic3_a = EnemySpawner.create(-78, 11, -87, 0, 66, this, link);
//		lunatic3_a.addType("lunatic_3", 0);
//		lunatic3_a.maxMult = 0.25;
//
//		EnemySpawner spider1_a = EnemySpawner.create(-104, 38, -99, 31, 77, this, link);
//		spider1_a.addType("spider_1", 0);
//		spider1_a.addType("spider_1", 1);
//		spider1_a.absMax = 4;
//
//		EnemySpawner lunatic4_a = EnemySpawner.create(-97, 41, -105, 33, 86, this, link);
//		lunatic4_a.addType("lunatic_4", 0);
//		lunatic4_a.maxMult = 0.3;
//
//		EnemySpawner lunatic5_a = EnemySpawner.create(-119, 6, -146, -16, 78, this, link);
//		lunatic5_a.addType("lunatic_5", 0);
//		lunatic5_a.maxMult = 0.08;
		/*
		dripper1 = EnemySpawner.create(-211, 13, -216, -2, 67, this, link);
		dripper1.addType("dripper_1", 0);
		dripper1.addType("shocker_dripper_1", 1);
		
		dripper2 = EnemySpawner.create(-201, 6, -192, -11, 67, this, link);
		dripper2.addType("dripper_1", 0, 69);
		dripper2.addType("dripper_2", 0, 30);
		dripper2.addType("dripper_golden", 0, 1);
		dripper2.addType("shocker_dripper_1", 1, 69);
		dripper2.addType("shocker_dripper_2", 1, 30);
		dripper2.addType("dripper_golden", 1, 1);
		
		dripper3 = EnemySpawner.create(-224, -19, -229, -27, 68, this, link);
		dripper3.addType("dripper_2", 0, 79);
		dripper3.addType("dripper_3", 0, 20);
		dripper3.addType("dripper_golden", 0, 1);
		dripper3.addType("shocker_dripper_2", 1, 75);
		dripper3.addType("shocker_dripper_3", 1, 24);
		dripper3.addType("dripper_golden", 1, 1);
		dripper3.max = 7;
		
		dripper4 = EnemySpawner.create(-168, -58, -184, -49, 68, this, link);
		dripper4.addType("dripper_3", 0, 99);
		dripper4.addType("dripper_golden", 0, 1);
		dripper4.addType("shocker_dripper_3", 1, 100);
		
		//-182, 66, -16
		//-170, 66, -11
		drenched1 = EnemySpawner.create(-182, -16, -170, -11, 66, this, link);
		drenched1.addType("drenched_1", 0);
		drenched1.addType("shocker_drenched_1", 1);
		drenched1.max = 8;
		
		//-176, 73, -43
		//-165, 73, -48
		drenched2 = EnemySpawner.create(-176, -48, -165, -43, 73, this, link);
		drenched2.addType("drenched_2", 0, 49);
		drenched2.addType("drenched_golden", 1);
		drenched2.addType("shocker_drenched_2", 1);
		
		/*
		dripper2_cave = EnemySpawner.create(-201, 6, -192, -11, 67, this);
		drenched1 = EnemySpawner.create(-224, -19, -229, -27, 68, this);
		drenched_miner = EnemySpawner.create(-191, -34, -186, -32, 66, this);
		drenched_miner.max = 0;
		drenched3 = EnemySpawner.create(-194, 7, -201, -10, 80, this);
		drenched3.max /= 2;
		drenched4 = EnemySpawner.create(-158, -33, -141, -27, 82, this);
		drenched4.max = 11;
		
		drenched4_2 = EnemySpawner.create(-164, -49, -174, -45, 73, this);
		drenched4_2.max = 3;
		
		//-174, 72, -42
		//-164, 72, -49
		
		
		setNormalSpawners();
		*/
		
		Dungeons.w.setStorm(false);
	}
	
	private EnemySpawner createSpawner(int x1, int z1, int x2, int z2, int y, String modeZeroEnemy) {
        EnemySpawner es = EnemySpawner.create(x1, z1, x2, z2, y, this, link);
        es.addType(modeZeroEnemy, 0, 100);
        return es;
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
