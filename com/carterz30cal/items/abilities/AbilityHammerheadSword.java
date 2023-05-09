package com.carterz30cal.items.abilities;

import java.util.ArrayList;
import java.util.List;

import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatOperationType;

public class AbilityHammerheadSword extends ItemAbility
{
	public AbilityHammerheadSword(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "REDHammerhead Force";
	}
	
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYGain BLUE+2" + Stat.MIGHT.getIcon() + " GRAYper attack on the same mob.");
		return l;
	}
	
	public void onPreAttack(DamageInfo info)
	{
		if (info.defender instanceof GameEnemy)
		{
			GameEnemy enemy = (GameEnemy)info.defender;
			if (enemy.lastDamager == info.attacker)
			{
				info.attacker.stats.scheduleOperation(Stat.MIGHT, StatOperationType.ADD, enemy.timesHit * 2);
			}
		}
		
	}

}
