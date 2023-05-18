package com.carterz30cal.items;

import java.lang.reflect.InvocationTargetException;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.abilities.*;
import com.carterz30cal.items.abilities.talismans.TalismanFishCharm;
import com.carterz30cal.items.abilities.talismans.TalismanHydra;
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
	
	FLAMING_SWORD(AbilityFlamingSword.class),
	
	TOME_WATERBOLT(AbilityTomeWaterBolt.class),
	
	// everything below here is new
	FROSTED(AbilityFrosted.class),
	WARDEN_WHIP(AbilityWardenWhip.class),
	TITAN_HAMMER(AbilityTitanHammer.class),
	FISH_BRAIN(AbilityFishBrain.class),
	FISH_CHARM(TalismanFishCharm.class),
	HYDRA_TALISMAN(TalismanHydra.class),
	
	// below is old stuff
	
	FROST_AXE(AbilityFrostAxe.class),
	WAND_OF_LESSER_VITALITY(AbilityWandVitality.class),
	WAND_OF_VITALITY(AbilityWandVitalityGreater.class),
	WAND_OF_GREATER_VITALITY(AbilityWandVitalityGreaterGreater.class),
	WAND_OF_LESSER_HEALING(AbilityWandHealingLesser.class),
	HAMMERHEAD_SWORD(AbilityHammerheadSword.class),
	ARMOUR_REGROWTH(AbilityRegrowthArmour.class),
	GREAT_WHITE_SCYTHE(AbilityGreatWhiteScythe.class),
	WATERWAY_KING(AbilityWaterwayKing.class),
	
	HYDRA_SLAYER(AbilityHydraSlayer.class),
	HYDRA_HELMET(AbilityHydraHelmet.class),
	HYDRA_CHESTPLATE(AbilityHydraChestplate.class),
	HYDRA_LEGGINGS(AbilityHydraLeggings.class),
	HYDRA_BOOTS(AbilityHydraBoots.class),
	
	FROST_SWORD(AbilityFrostSword.class),
	
	HYDRA_TOTEM(AbilityHydraTotemRegular.class),
	HYDRA_TOTEM_CORRUPTED(AbilityHydraTotemCorrupted.class),
	SERAPH_SUMMON(AbilitySeraphSummon.class),
	SERAPH_SUMMON_CORRUPT(AbilitySeraphSummonCorrupt.class),
	RAIN_SUMMON(AbilityRainSummon.class),
	
	SLAYER_BLIZZARD_SWORD_1(AbilitySlayerBlizzardSword1.class),
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
