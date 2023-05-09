package com.carterz30cal.entities.enemies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;

import com.carterz30cal.entities.EnemyManager;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.utils.EntityUtils;
import com.carterz30cal.utils.ParticleUtils;

public class EnemyTypeSeraphMageMana extends EnemyTypeSimple
{
	public int lifeDrain;
	public int lifeDrainTimer;
	public int lifeDrainRadius;
	
	public int summonTimer;
	public String summon;
	public int summonHeal;
	
	private Map<GameEnemy, List<GameEnemy>> summons = new HashMap<>();
	
	private Particle.DustOptions dust = new Particle.DustOptions(Color.RED, 1F);
	private Particle.DustOptions healDust = new Particle.DustOptions(Color.GREEN, 0.8F);
	
	public EnemyTypeSeraphMageMana(ConfigurationSection m) 
	{
		super(m);
		
		lifeDrain = m.getInt("life-drain", 0);
		lifeDrainTimer = m.getInt("life-drain-timer", 20);
		lifeDrainRadius = m.getInt("life-drain-radius", 0);
		
		summonTimer = m.getInt("summon-timer", 40);
		summon = m.getString("summon", "dripper_1");
		summonHeal = m.getInt("summon-heal", 100);
	}

	public void onTick(GameEnemy enemy)
	{
		int tick = (int)enemy.data.getOrDefault("life_drain_timer", 0);
		if (tick >= lifeDrainTimer)
		{
			for (GamePlayer player : EntityUtils.getNearbyPlayers(enemy.getLocation(), lifeDrainRadius))
			{
				player.damage(lifeDrain);
				ParticleUtils.spawnLine(player.getLocation().add(0, 1, 0), enemy.getLocation().add(0, 1.7, 0), dust, 20);
			}
			
			enemy.data.put("life_drain_timer", 0);
		}
		else enemy.data.put("life_drain_timer", tick + 1);
		
		tick = (int)enemy.data.getOrDefault("summon_timer", 0);
		if (tick >= summonTimer)
		{
			for (GameEnemy summoned : summons.getOrDefault(enemy, new ArrayList<>())) 
			{
				if (summoned.dead) continue;
				ParticleUtils.spawnLine(summoned.getLocation().add(0, 1, 0), enemy.getLocation().add(0, 1.7, 0), healDust, 20);
				enemy.setHealth(enemy.getHealth() + summonHeal);
				summoned.setHealth(summoned.getHealth() - summonHeal / 8);
			}
			
			GameEnemy summoned = EnemyManager.spawn(summon, enemy.getLocation());
			summons.putIfAbsent(enemy, new ArrayList<>());
			
			summons.get(enemy).add(summoned);
			enemy.data.put("summon_timer", 0);
		}
		else enemy.data.put("summon_timer", tick + 1);
	}

	public void onKilled(GameEnemy enemy)
	{
		for (GameEnemy summoned : summons.getOrDefault(enemy, new ArrayList<>())) summoned.remove();
		summons.remove(enemy);
	}
	
}
