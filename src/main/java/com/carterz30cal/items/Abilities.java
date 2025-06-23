package com.carterz30cal.items;

import java.lang.reflect.InvocationTargetException;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.abilities.*;
import com.carterz30cal.items.abilities.talismans.TalismanFishCharm;
import com.carterz30cal.items.abilities.talismans.TalismanHydra;
import com.carterz30cal.items.abilities2.waterway.LeafArmourSet;
import com.carterz30cal.items.abilities2.waterway.NecromancerAbility;
import com.carterz30cal.items.enchants.*;

public enum Abilities
{
	ENCHANT_SHARPNESS(Sharpness.class),
	ENCHANT_PEARLED(Pearled.class),
	ENCHANT_TITANIC(Titanic.class),
	ENCHANT_HEALTHY(Healthy.class),
	
	SET_LEAF(LeafArmourSet.class),
	NECROMANCY_SWORD(NecromancerAbility.class),
	HEALING_WAND_SELF(AbilityWandVitality.class);
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
