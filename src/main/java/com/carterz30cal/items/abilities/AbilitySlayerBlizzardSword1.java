package com.carterz30cal.items.abilities;

import java.util.ArrayList;
import java.util.List;

import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.stats.Stat;

public class AbilitySlayerBlizzardSword1 extends ItemAbility {

	public AbilitySlayerBlizzardSword1(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String name() {
		return "AQUABlizzard Hunter";
	}
	
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYThis dagger deals GOLD2.5x GRAYdamage to Blizzards.");
		l.add("GRAYRegain GREEN5" + Stat.HEALTH.getIcon() + " GRAYon hit.");
		return l;
	}
	
	public void onAttack(DamageInfo info)
	{
		if (info.defender instanceof GameEnemy)
		{
			GameEnemy enemy = (GameEnemy)info.defender;
			
			info.attacker.gainHealth(5);
			if (enemy.hasTag("SLAYER_BLIZZARD"))
			{
				info.damage *= 2.5;
			}
		}
	}

}
