package com.carterz30cal.items.abilities;

import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.DamageType;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemAbility;

import java.util.ArrayList;
import java.util.List;

public class AbilityFrosted extends ItemAbility {

	public AbilityFrosted(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "BLUEFrosted";
	}

	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYAll damage is dealt as BLUEfrostGRAY damage.");
		return l;
	}
	
	public void onPreAttack(DamageInfo info)
	{
		info.type = DamageType.FROST;
	}
}
