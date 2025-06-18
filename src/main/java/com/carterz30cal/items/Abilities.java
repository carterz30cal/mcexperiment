package com.carterz30cal.items;

import java.lang.reflect.InvocationTargetException;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.abilities.*;
import com.carterz30cal.items.abilities.talismans.TalismanFishCharm;
import com.carterz30cal.items.abilities.talismans.TalismanHydra;
import com.carterz30cal.items.abilities2.waterway.LeafArmourSet;
import com.carterz30cal.items.enchants.*;

public enum Abilities
{
	ENCHANT_SHARPNESS(Sharpness.class),
	ENCHANT_SHOCKED(Shocked.class),
	ENCHANT_ELECTRICHEALTH(ElectricHealth.class),
	ENCHANT_PEARLED(Pearled.class),
	ENCHANT_TITANIC(Titanic.class),
	ENCHANT_STEALTH(Stealth.class),
	ENCHANT_LURE(Lure.class),
	ENCHANT_SHARKING(Sharking.class),
	ENCHANT_BLADE(Blade.class),
	ENCHANT_FIREASPECT(FireAspect.class),
	ENCHANT_CORRUPTMIGHT(CorruptMight.class),
	ENCHANT_SNOWSTORM(Snowstorm.class),
	ENCHANT_SCYTHELIKE(Scythelike.class),
	ENCHANT_HEALTHY(Healthy.class),
	ENCHANT_FLOWING(Flowing.class),
	ENCHANT_DEATHPOKE(DeathPoke.class),
	
	SET_LEAF(LeafArmourSet.class),
	;
	private Class<? extends ItemAbility> abClass;
	
	private Abilities(Class<? extends ItemAbility> abClass)
	{
		this.abClass = abClass;
	}
	
	public ItemAbility generate(GamePlayer owner)
	{
		try {
			return abClass.getConstructor(GamePlayer.class).newInstance(owner);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
			return null;
		}
	}
}
