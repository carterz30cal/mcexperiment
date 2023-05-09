package com.carterz30cal.entities.enemies;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.utils.EntityUtils;

public class EnemyTypeWraithlike extends EnemyTypeSimple
{
	public EnemyTypeWraithlike(ConfigurationSection m) 
	{
		super(m);
	}
	
	public void onTick(GameEnemy enemy)
	{
		EntityUtils.applyPotionEffect((LivingEntity)enemy.main, PotionEffectType.INVISIBILITY, 21, 1, true);
	}
}
