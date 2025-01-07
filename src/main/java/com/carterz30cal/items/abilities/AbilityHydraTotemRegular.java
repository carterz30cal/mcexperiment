package com.carterz30cal.items.abilities;

import java.util.ArrayList;
import java.util.List;

import com.carterz30cal.areas.BossHydra;
import com.carterz30cal.areas.BossWaterwayHydra;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.items.ItemReq;
import com.carterz30cal.items.ItemReqs;

public class AbilityHydraTotemRegular extends ItemAbility {

	public AbilityHydraTotemRegular(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "GREENHydra's Call";
	}

	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYConsumes this item and summons a hydra.");
		l.add("DARK_GRAYRight click to use.");
		return l;
	}
	
	public void onRightClick()
	{
		ItemReqs req = new ItemReqs();
		req.addRequirement(new ItemReq("hydra_totem", 1));
		
		if (req.areRequirementsMet(owner) && BossHydra.startFight(1))
		{
			req.execute(owner);
		}
	}
}
