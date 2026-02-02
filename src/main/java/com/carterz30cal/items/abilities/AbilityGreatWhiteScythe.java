package com.carterz30cal.items.abilities;

import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class AbilityGreatWhiteScythe extends ItemAbility
{
	public AbilityGreatWhiteScythe(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "AQUABOLDKing of the Ocean";
	}
	
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYGain BLUE+3" + Stat.MIGHT.getIcon() + " GRAYper attack on the same mob.");
		l.add("GRAYAlso gain BLUE+4" + Stat.STRENGTH.getIcon() + "GRAY for every kill");
		l.add("GRAYin the last 30 seconds.");
		return l;
	}
	
	public void onKill(GameEnemy killed)
	{
		owner.counters.put("great_white", owner.counters.getOrDefault("great_white", 0) + 1);
		
		new BukkitRunnable()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				owner.counters.put("great_white", owner.counters.getOrDefault("great_white", 0) - 1);
			}
			
		}.runTaskLater(Dungeons.instance, 20 * 30);
	}
	
	public void onItemStats(StatContainer item)
	{
		if (owner == null) return;
		int kills = owner.counters.getOrDefault("great_white", 0);
		item.scheduleOperation(Stat.STRENGTH, StatOperationType.ADD, kills * 4);
	}
	
	public void onPreAttack(DamageInfo info)
	{
		if (info.defender instanceof GameEnemy)
		{
			GameEnemy enemy = (GameEnemy)info.defender;
			if (enemy.lastDamager == info.attacker)
			{
				info.attacker.stats.scheduleOperation(Stat.MIGHT, StatOperationType.ADD, enemy.timesHit * 3);
			}
		}
		
	}

}
