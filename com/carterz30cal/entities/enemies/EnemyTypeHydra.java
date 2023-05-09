package com.carterz30cal.entities.enemies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.EquipmentSlot;

import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.EnemyManager;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.utils.EntityUtils;

public class EnemyTypeHydra extends EnemyTypeDamageCapped
{
	public int lifeDrain;
	public int lifeDrainTimer;
	public int lifeDrainRadius;
	
	public String minion;
	public int minionCount;
	public int minionRegenTimer;
	
	public int regen;
	
	public EnemyTypeHydra(ConfigurationSection m) {
		super(m);
		// TODO Auto-generated constructor stub
		
		lifeDrain = m.getInt("life-drain", 0);
		lifeDrainTimer = m.getInt("life-drain-timer", 20);
		lifeDrainRadius = m.getInt("life-drain-radius", 0);
		
		minion = m.getString("minion");
		minionCount = m.getInt("minion-count", 10);
		minionRegenTimer = m.getInt("minion-timer", 100);
		
		regen = m.getInt("regeneration", 0);
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
		Guardian g = (Guardian)EntityUtils.spawnPart(EntityType.GUARDIAN, location);
		g.setLaser(false);
		mob.main.addPassenger(g);
		mob.parts.add(g);
		
		List<GameEnemy> minions = new ArrayList<>();
		for (int c = 0; c < minionCount; c++)
		{
			GameEnemy m = EnemyManager.spawn(minion, location);
			minions.add(m);
		}
		mob.data.put("minions", minions);
		
		
		mob.doTick();
		mob.register();
		return mob;
	}
	
	@SuppressWarnings("unchecked")
	public void onTick(GameEnemy enemy)
	{
		int tick = (int)enemy.data.getOrDefault("life_drain_timer", 0);
		if (tick >= lifeDrainTimer)
		{
			for (GamePlayer player : EntityUtils.getNearbyPlayers(enemy.getLocation(), lifeDrainRadius))
			{
				player.damage(lifeDrain);
			}
			
			enemy.data.put("life_drain_timer", 0);
		}
		else enemy.data.put("life_drain_timer", tick + 1);
		
		int mTick = (int)enemy.data.getOrDefault("minion_check", 0);
		if (mTick >= minionRegenTimer)
		{
			List<GameEnemy> minions = (List<GameEnemy>) enemy.data.get("minions");
			minions.removeIf((e) -> e.dead);
			while (minions.size() < minionCount)
			{
				GameEnemy m = EnemyManager.spawn(minion, enemy.getLocation());
				minions.add(m);
			}
			
			enemy.data.put("minion_check", 0);
		}
		else enemy.data.put("minion_check", mTick + 1);
		
		
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
		
		List<GameEnemy> minions = (List<GameEnemy>) enemy.data.get("minions");
		for (GameEnemy minion : minions) minion.remove();
		
		for (GamePlayer damager : damagers)
		{
			enemy.dropItems(damager);
		}
		
		super.onKilled(enemy);
	}

}
