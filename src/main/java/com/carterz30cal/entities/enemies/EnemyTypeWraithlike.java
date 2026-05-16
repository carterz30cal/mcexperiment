package com.carterz30cal.entities.enemies;

import com.carterz30cal.entities.enemies.implementation.GameEnemy;
import com.carterz30cal.utils.EntityUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

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
