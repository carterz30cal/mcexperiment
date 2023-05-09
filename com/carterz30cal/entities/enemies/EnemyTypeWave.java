package com.carterz30cal.entities.enemies;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffectType;

import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.EntityUtils;

public class EnemyTypeWave extends EnemyTypeSimple {

	
	public int wave = 1;
	public int wavePoints = 1;
	
	public EnemyTypeWave(ConfigurationSection m) {
		super(m);
		
		wavePoints = m.getInt("wave-points", 1);
	}
	
	@Override
	public GameEnemy generate(Location location)
	{
		GameEnemy e = super.generate(location);
		
		if (mainType == EntityType.SILVERFISH || mainType == EntityType.ENDERMITE)
		{
			e.deregisterEnemy();
			Zombie rails = (Zombie)Dungeons.w.spawnEntity(location, EntityType.ZOMBIE);
			rails.getEquipment().clear();
			//rails.getEquipment().setHelmet(ItemFactory.build("STONE_BUTTON"));
			rails.setAdult();
			//rails.setInvulnerable(true);
			EntityUtils.applyPotionEffect(rails, PotionEffectType.FIRE_RESISTANCE, 10000000, 1, false);
			EntityUtils.applyPotionEffect(rails, PotionEffectType.INVISIBILITY, 10000000, 1, false);
			
			e.parts.add(rails);
			e.data.put("rails", rails);
			e.register();
		}
		
		
		
		return e;
	}
	
	@Override
	public void onTick(GameEnemy enemy)
	{
		Zombie dest = (Zombie)enemy.data.get("rails");
		if (dest == null) return;
		
		dest.setHealth(20);
		dest.teleport(enemy.getMain());
	}

	
	@Override
	public int getMaxHealth() {
		int tens = wave / 10;
		return (int)Math.round(health * (0.9 + wave * 0.1 + tens * 0.2));
	}
	
}
