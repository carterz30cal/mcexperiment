package com.carterz30cal.items.abilities;

import java.util.ArrayList;
import java.util.List;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemAbility;

public class AbilityWardenWhip extends ItemAbility {

	public AbilityWardenWhip(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "DARK_PURPLEWain Wain Go Away!";
	}
	
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYWhilst it is raining, this item loses RED50%GRAY of");
		l.add("GRAYit's BLUEMightGRAY, but gains a flat BLUE40 PowerGRAY.");
		return l;
	}

}
