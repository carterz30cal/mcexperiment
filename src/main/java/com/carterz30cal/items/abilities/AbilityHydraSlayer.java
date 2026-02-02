package com.carterz30cal.items.abilities;

import com.carterz30cal.areas.BossHydra;
import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.DamageType;
import com.carterz30cal.entities.damage.StatusEffect;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.stats.StatContainer;

import java.util.ArrayList;
import java.util.List;

public class AbilityHydraSlayer extends ItemAbility
{
	public AbilityHydraSlayer(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "GOLDHydra Hunt";
	}
	
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYThis weapon deals YELLOWHolyGRAY damage.");
		l.add("GRAYWhilst fighting a Hydra, this weapon has BLUE5xGRAY status buildup.");
		return l;
	}
	
	@Override
	public void onItemStats(StatContainer item)
	{
		if (!BossHydra.participants.contains(owner) || !BossHydra.active) return;
		
		for (StatusEffect e : item.statuses.effects.keySet()) {
			item.statuses.effects.put(e, item.statuses.getStatus(e) * 5);
		}
	}
	
	public void onPreAttack(DamageInfo info)
	{
		info.type = DamageType.HOLY;
	}

}
