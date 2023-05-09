package com.carterz30cal.items.abilities;

import java.util.ArrayList;
import java.util.List;

import com.carterz30cal.areas.AreaWaterway;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.items.ItemReq;
import com.carterz30cal.items.ItemReqs;
import com.carterz30cal.main.Dungeons;

public class AbilityRainSummon extends ItemAbility {

	public AbilityRainSummon(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "AQUABOLDRaincall";
	}

	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYConsumes this item and brings the rain to");
		l.add("GRAYWaterway for a full weather cycle.");
		l.add("DARK_GRAYRight click to use!");
		return l;
	}
	
	public void onRightClick()
	{
		ItemReqs req = new ItemReqs();
		req.addRequirement(new ItemReq("rain_summon", 1));
		
		if (req.areRequirementsMet(owner) && !AreaWaterway.isRaining)
		{
			req.execute(owner);
			AreaWaterway.instance.toggleRain();
			
			Dungeons.w.strikeLightningEffect(owner.getLocation());
		}
	}
}
