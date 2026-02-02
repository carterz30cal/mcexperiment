package com.carterz30cal.items.abilities;

import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.stats.Stat;

import java.util.ArrayList;
import java.util.List;

public class AbilityWandVitalityGreaterGreater extends ItemAbility 
{

	public AbilityWandVitalityGreaterGreater(GamePlayer owner) {
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
		l.add("GRAYAttacks consume LIGHT_PURPLE4" + Stat.MANA.getIcon() + "GRAY.");
		l.add("GRAYYou heal GREEN14" + Stat.HEALTH.getIcon() + "GRAY per hit.");
		return l;
	}
	
	public void onPreAttack(DamageInfo info)
	{
		if (info.defender instanceof GameEnemy)
		{
			if (owner.useMana(4)) 
			{
				owner.gainHealth(14);
			}
			else info.damage = 0;
		}
		
	}

}
