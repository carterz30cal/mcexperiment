package com.carterz30cal.items.abilities;

import java.util.ArrayList;
import java.util.List;

import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.stats.Stat;

public class AbilityWandVitalityGreater extends ItemAbility 
{

	public AbilityWandVitalityGreater(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "GREENHealing Strike";
	}
	
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYAttacks consume LIGHT_PURPLE3" + Stat.MANA.getIcon() + "GRAY.");
		l.add("GRAYYou heal GREEN11" + Stat.HEALTH.getIcon() + "GRAY per hit.");
		return l;
	}
	
	public void onPreAttack(DamageInfo info)
	{
		if (info.defender instanceof GameEnemy)
		{
			if (owner.useMana(3)) 
			{
				owner.gainHealth(11);
			}
			else info.damage = 0;
		}
		
	}

}
