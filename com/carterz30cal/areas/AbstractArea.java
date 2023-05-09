package com.carterz30cal.areas;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.main.Dungeons;

public abstract class AbstractArea extends BukkitRunnable
{
	public static Map<String, AbstractArea> areas = new HashMap<>();
	
	public AbstractArea()
	{
		areas.put(getClass().getName(), this);
		runTaskTimer(Dungeons.instance, 1, 1);
	}
	
	public void run()
	{
		onTick();
	}
	
	public void onTick()
	{
		
	}
	
	public void onKill(GameEnemy enemy)
	{
		
	}
	
	public void onEnd()
	{
		
	}
	
	public void onMine(GamePlayer miner, Location loc)
	{
		
	}
}
