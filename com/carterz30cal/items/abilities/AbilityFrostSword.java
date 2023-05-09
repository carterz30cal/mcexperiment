package com.carterz30cal.items.abilities;

import java.util.ArrayList;
import java.util.List;

import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemAbility;

public class AbilityFrostSword extends ItemAbility {

	public AbilityFrostSword(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "BLUEVerglasian Might";
	}
	
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYDeals RED1.35xGRAY damage to BLUEVerglasianGRAY monsters.");
		return l;
	}
	
	public void onAttack(DamageInfo info)
	{
		if (info.defender instanceof GameEnemy)
		{
			GameEnemy enemy = (GameEnemy)info.defender;
			
			if (enemy.hasTag("VERGLASIAN"))
			{
				info.damage *= 1.35;
			}
		}
	}

}
