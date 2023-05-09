package com.carterz30cal.items.abilities;

import java.util.ArrayList;
import java.util.List;

import com.carterz30cal.areas.AreaWaterway;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.items.ItemReq;
import com.carterz30cal.items.ItemReqs;

public class AbilityHydraTotemCorrupted extends ItemAbility {

	public AbilityHydraTotemCorrupted(GamePlayer owner) {
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
		l.add("GRAYConsumes this item and summons a corrupted hydra.");
		l.add("DARK_GRAYRight click to use.");
		return l;
	}
	
	public void onRightClick()
	{
		ItemReqs req = new ItemReqs();
		req.addRequirement(new ItemReq("corrupted_hydra_totem", 1));
		
		if (req.areRequirementsMet(owner) && AreaWaterway.attemptSpawnHydra("hydra_2"))
		{
			req.execute(owner);
		}
	}
}
