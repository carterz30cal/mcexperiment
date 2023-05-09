package com.carterz30cal.entities.enemies;

import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;

import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.utils.ParticleUtils;

public class EnemyTypeSeraphMageHealth extends EnemyTypeSimple
{
	public EnemyTypeSeraphMageHealth(ConfigurationSection m) 
	{
		super(m);
	}

	public void onTick(GameEnemy enemy)
	{
		enemy.setHealth(enemy.getHealth() + 1);
		ParticleUtils.spawn(enemy.getLocation().add(0, 1.7, 0), Particle.HEART, 0.8, 2);
	}

}
