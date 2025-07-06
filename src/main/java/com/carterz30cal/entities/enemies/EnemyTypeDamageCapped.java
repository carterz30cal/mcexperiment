package com.carterz30cal.entities.enemies;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GamePlayer;

public class EnemyTypeDamageCapped extends EnemyTypeSimple 
{
	public static Map<GameEnemy, Map<GamePlayer, Integer>> overkills = new HashMap<>();
	
	public double capPercent = 1;
	public int overkillReward = 0;
	
	public EnemyTypeDamageCapped(ConfigurationSection m) {
		super(m);
		
		capPercent = m.getInt("damage-cap-percentage", 1) / 100D;
		overkillReward = m.getInt("overkill-reward", 0);
	}
	
	public void onDamaged(GameEnemy enemy, DamageInfo info)
	{
		int damageCap = (int)(health * capPercent);
		if (info.damage > damageCap)
		{
			info.damage = damageCap;
			
			overkills.putIfAbsent(enemy, new HashMap<>());
			overkills.get(enemy).put(info.attacker, overkills.get(enemy).getOrDefault(info.attacker, 0) + 1);
		}
	}
	
	public void onKilled(GameEnemy enemy)
	{
		for (GamePlayer overkiller : overkills.getOrDefault(enemy, new HashMap<>()).keySet())
		{
			int oks = overkills.get(enemy).get(overkiller);
			int rew = overkillReward * oks;

			if (rew > 0) {
				overkiller.coins += rew;
				overkiller.sendMessage("REDBOLDOverkill! GOLD+" + rew + " coins!", 10);
			}

		}
		
		overkills.remove(enemy);
	}

}
