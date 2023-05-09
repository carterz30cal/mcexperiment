package com.carterz30cal.items.abilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;

import com.carterz30cal.entities.GameParticleProjectile;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.stats.Stat;

public class AbilityTomeWaterBolt extends ItemAbility {

	public AbilityTomeWaterBolt(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "DARK_PURPLEBolt of Water";
	}
	
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		
		l.add("GOLDLeft clickGRAY to attack.");
		l.add("GRAYAttacks consume LIGHT_PURPLE4" + Stat.MANA.getIcon() + "GRAY.");
		l.add("GRAYShoots out a bolt of magical water");
		l.add("GRAYwhich pierces and damages enemies!");
		l.add("GRAYThe bolt's damage is determined by your " + Stat.DAMAGE.getReverse() + "GRAY stat");
		l.add("GRAYand then scales on your " + Stat.POWER.getReverse() + "GRAY and " + Stat.MANA.getReverse() + "GRAY stats.");
		return l;
	}

	public void onLeftClick()
	{
		if (owner.bowTick != 0 || !owner.useMana(4)) return;
		
		owner.bowTick = 4;
		int damage = owner.getMagicDamage(0.5, 3);
		GameParticleProjectile.spawnPlayerProjectile(
				owner,
				8, Color.BLUE, 0.8, damage, 2);
	}
	
}
