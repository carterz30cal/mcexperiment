package com.carterz30cal.entities.enemies;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.EquipmentSlot;

import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.utils.EntityUtils;

public class EnemyTypeSeraph extends EnemyTypeDamageCapped
{
	//public int lifeDrain;
	//public int lifeDrainTimer;
	//public int lifeDrainRadius;
	
	public EnemyTypeSeraph(ConfigurationSection m) {
		super(m);
		// TODO Auto-generated constructor stub
		
		//lifeDrain = m.getInt("life-drain", 0);
		//lifeDrainTimer = m.getInt("life-drain-timer", 20);
		//lifeDrainRadius = m.getInt("life-drain-radius", 0);
	}

	@Override
	public GameEnemy generate(Location location) {
		GameEnemy mob = new GameEnemy(location, this);
		
		mob.main = EntityUtils.spawnPart(mainType, location);
		if (mob.main instanceof Mob)
		{
			Mob main = (Mob)mob.main;
			for (EquipmentSlot slot : equipment.keySet())
			{
				EntityUtils.setArmourPiece(main, slot, equipment.get(slot));
			}
		}
		
		mob.doTick();
		mob.register();
		return mob;
	}
	
	public void onTick(GameEnemy enemy)
	{
//		int tick = (int)enemy.data.getOrDefault("life_drain_timer", 0);
//		if (tick >= lifeDrainTimer)
//		{
//			for (GamePlayer player : EntityUtils.getNearbyPlayers(enemy.getLocation(), lifeDrainRadius))
//			{
//				player.damage(lifeDrain);
//			}
//			
//			enemy.data.put("life_drain_timer", 0);
//		}
//		else enemy.data.put("life_drain_timer", tick + 1);
	}
	
	@SuppressWarnings("unchecked")
	public void onDamaged(GameEnemy enemy, DamageInfo info)
	{
		//if (info.type == DamageType.PROJECTILE) info.damage = 0;
		
		enemy.data.putIfAbsent("damagers", new HashSet<GamePlayer>());
		((HashSet<GamePlayer>)enemy.data.get("damagers")).add(info.attacker);
		
		super.onDamaged(enemy, info);
	}
	
	@SuppressWarnings("unchecked")
	public void onKilled(GameEnemy enemy)
	{
		Set<GamePlayer> damagers = ((HashSet<GamePlayer>)enemy.data.getOrDefault("damagers", new HashSet<GamePlayer>()));
		damagers.remove(enemy.lastDamager);
		
		for (GamePlayer damager : damagers)
		{
			enemy.dropItems(damager);
		}
		
		super.onKilled(enemy);
	}

}
