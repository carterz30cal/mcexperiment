package com.carterz30cal.items.abilities;

import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.stats.Stat;

import java.util.ArrayList;
import java.util.List;

public class AbilityWandHealingLesser extends ItemAbility 
{
	public static final int HEAL_RADIUS = 12;
	public AbilityWandHealingLesser(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "YELLOWSelfless Healing Strike";
	}
	
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYAttacks consume LIGHT_PURPLE5" + Stat.MANA.getIcon() + "GRAY.");
		l.add("GRAYYou heal GREEN10" + Stat.HEALTH.getIcon() + "GRAY per hit to");
		l.add("GRAYall other players within GREEN12GRAY blocks.");
		return l;
	}
	
	public void onPreAttack(DamageInfo info)
	{
		if (info.defender instanceof GameEnemy)
		{
			if (owner.useMana(5)) 
			{
				
				for (GamePlayer p : PlayerManager.players.values())
				{
					if (p == owner) continue;
					if (p.getLocation().distance(owner.getLocation()) > HEAL_RADIUS) continue;
					
					p.gainHealth(10);
				}
			}
			else info.damage = 0;
		}
		
	}

}
