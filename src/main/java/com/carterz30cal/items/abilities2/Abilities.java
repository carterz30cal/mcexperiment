package com.carterz30cal.items.abilities2;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.items.abilities2.generic.HealingAbility;
import com.carterz30cal.items.abilities2.generic.StatEnchantment;
import com.carterz30cal.items.abilities2.implementation.GameAbility;
import com.carterz30cal.items.abilities2.waterway.pets.PetDrenchedActive;
import com.carterz30cal.items.abilities2.waterway.pets.PetDrenchedPassive;
import com.carterz30cal.items.abilities2.waterway.sets.LeafArmourSet;
import com.carterz30cal.items.abilities2.waterway.NecromancerAbility;
import com.carterz30cal.items.abilities2.waterway.SuperLureEnchantment;
import com.carterz30cal.items.abilities2.waterway.sets.ZombieArmourSet;
import com.carterz30cal.stats.Stat;

public enum Abilities
{
	/*
	ENCHANT_SHARPNESS(Sharpness.class),
	ENCHANT_PEARLED(Pearled.class),
	ENCHANT_TITANIC(Titanic.class),
	ENCHANT_HEALTHY(Healthy.class),
	ENCHANT_SUPERLURE(SuperLure.class),

	PET_ACTIVE_DRENCHED_COMMON(PetDrenchedActiveCommon.class),
	PET_PASSIVE_DRENCHED_COMMON(PetDrenchedPassiveCommon.class),
	
	SET_LEAF(LeafArmourSet.class),
	SET_ZOMBIE(ZombieArmourSet.class),
	NECROMANCY_SWORD(NecromancerAbility.class),
	HEALING_WAND_SELF(AbilityWandVitality.class);
	*/

	ENCHANT_SHARPNESS(new StatEnchantment(
			"Sharpness", 2,
			Stat.STRENGTH, 0, 20, 5, ItemType.WEAPON, ItemType.WAND, ItemType.ROD)),
	ENCHANT_HEALTHY(new StatEnchantment(
			"Healthy", 1,
			Stat.HEALTH, 20, 10, 3,
			ItemType.HELMET, ItemType.CHESTPLATE, ItemType.LEGGINGS, ItemType.BOOTS
	)),
	ENCHANT_TITANIC(new StatEnchantment(
			"Titanic", 1,
			Stat.DEFENCE, 0, 5, 5,
			ItemType.HELMET, ItemType.CHESTPLATE, ItemType.LEGGINGS, ItemType.BOOTS
	)),
	ENCHANT_PEARLED(new StatEnchantment(
			"Pearled", 1,
			Stat.MANA, 0, 5, 5,
			ItemType.HELMET, ItemType.CHESTPLATE, ItemType.LEGGINGS, ItemType.BOOTS
	)),
	ENCHANT_BOOST(new StatEnchantment(
			"Boost", 1,
			Stat.POWER, 0, 2, 8, ItemType.BOOTS
	)),
	ENCHANT_SUPERLURE(new SuperLureEnchantment()),

	NECROMANCY_SWORD(new NecromancerAbility()),
	HEALING_WAND_WATERWAY(new HealingAbility(8, 25)),
	SET_LEAF(new LeafArmourSet()),
	SET_ZOMBIE(new ZombieArmourSet()),
	PET_DRENCHED_ACTIVE(new PetDrenchedActive()),
	PET_DRENCHED_PASSIVE(new PetDrenchedPassive()),
	;
	public final GameAbility ability;

	Abilities(GameAbility ability) {
		this.ability = ability;
		this.ability.source = this;
	}

	public GameAbility.AbilityContext getContext(GamePlayer owner) {
		return getContext(owner, 1);
	}
	public GameAbility.AbilityContext getContext(GamePlayer owner, int level) {
		var context = new GameAbility.AbilityContext(ability);
		context.level = level;
		context.owner = owner;
		return context;
	}
}
