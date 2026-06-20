package com.carterz30cal.items.abilities;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.items.abilities.generic.DeadAbility;
import com.carterz30cal.items.abilities.generic.HealingAbility;
import com.carterz30cal.items.abilities.generic.MagicSwordAbility;
import com.carterz30cal.items.abilities.generic.StatEnchantment;
import com.carterz30cal.items.abilities.implementation.GameAbility;
import com.carterz30cal.items.abilities.implementation.PlayerAbilityContext;
import com.carterz30cal.items.abilities.waterway.*;
import com.carterz30cal.items.abilities.waterway.pets.PetDrenchedActive;
import com.carterz30cal.items.abilities.waterway.pets.PetDrenchedPassive;
import com.carterz30cal.items.abilities.waterway.pets.PetWaterSpiderActive;
import com.carterz30cal.items.abilities.waterway.pets.PetWaterTitanActive;
import com.carterz30cal.items.abilities.waterway.sets.LeafArmourSet;
import com.carterz30cal.items.abilities.waterway.sets.ZombieArmourSet;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatOperationType;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public enum Abilities
{
	ENCHANT_SHARPNESS(new StatEnchantment(
			"Sharpness", 2,
            Stat.STRENGTH, 0, 20, 5, ItemType.WEAPON, ItemType.BOW, ItemType.ROD)),
	ENCHANT_HEALTHY(new StatEnchantment(
			"Healthy", 1,
            Stat.HEALTH, 30, 10, 3,
			ItemType.HELMET, ItemType.CHESTPLATE, ItemType.LEGGINGS, ItemType.BOOTS
	)),
	ENCHANT_TITANIC(new StatEnchantment(
			"Titanic", 1,
            Stat.DEFENCE, 10, 5, 5,
			ItemType.HELMET, ItemType.CHESTPLATE, ItemType.LEGGINGS, ItemType.BOOTS
	)),
	ENCHANT_PEARLED(new StatEnchantment(
            "Pearled", 2,
            Stat.MANA, 0, 10, 5,
			ItemType.HELMET, ItemType.CHESTPLATE, ItemType.LEGGINGS, ItemType.BOOTS
	)),
	ENCHANT_BOOST(new StatEnchantment(
			"Boost", 1,
			Stat.POWER, 0, 2, 8, ItemType.BOOTS
	)),
	ENCHANT_SUPERLURE(new SuperLureEnchantment()),
    ENCHANT_SLIMED(new StatEnchantment(
            "Slimed", 2, Stat.DEFENCE,
            5, 5, 4, StatOperationType.MULTIPLY, ItemType.CHESTPLATE
    )),
    ENCHANT_LUCK(new StatEnchantment(
            "Luck", 1, Stat.LUCK, 0, 1, 5, ItemType.WEAPON, ItemType.ROD, ItemType.WAND, ItemType.BOW
    )),
    ENCHANT_CONCENTRATION(new StatEnchantment(
            "Concentration", 3, Stat.FOCUS, 1, 1, 3, ItemType.HELMET, ItemType.WEAPON
    )),
    ENCHANT_LAST_CHANCE(new LastChanceEnchantment()),
    ENCHANT_POWER(new StatEnchantment("Power", 2, Stat.DAMAGE, 0, 1, 5, ItemType.BOW)),
    ENCHANT_BLADE(new StatEnchantment("Blade", 2, Stat.DAMAGE, 0, 1, 4, ItemType.WEAPON)),

    ENCHANT_REGROWTH(new StatEnchantment("Regrowth", 2, Stat.VITALITY, 1, 1, 2, ItemType.WEAPON, ItemType.BOW, ItemType.ROD)),

	NECROMANCY_SWORD(new NecromancerAbility()),
    HEALING_WAND_WATERWAY(new HealingAbility(15, 10, 5)),
	SET_LEAF(new LeafArmourSet()),
	SET_ZOMBIE(new ZombieArmourSet()),
	PET_DRENCHED_ACTIVE(new PetDrenchedActive()),
	PET_DRENCHED_PASSIVE(new PetDrenchedPassive()),
    PET_WATER_TITAN_ACTIVE(new PetWaterTitanActive()),
    PET_WATER_SPIDER_ACTIVE(new PetWaterSpiderActive()),
    WATERWAY_SERAPH_SWORD(new SeraphSwordAbility()),
    WATERWAY_SERAPH_KEY(new SeraphKeyAbility()),
    MAGIC_SWORD(new MagicSwordAbility()),
    RAGING_AXE(new RagingAxeAbility()),
    SERAPH_SUMMON_GUIDE(new SeraphSummonGuideAbility("water_seraph_spirit", 20)),
    ADMIN_INSTANT_DEATH_SWORD(new DeadAbility()),

    PET_DUSTED_COMMON(new StatEnchantment("Dustball", 0, Stat.DEFENCE, 75, 0, 1))
	;
	public final GameAbility ability;

	Abilities(GameAbility ability) {
		this.ability = ability;
		this.ability.source = this;
	}

    public PlayerAbilityContext getContext(GamePlayer owner) {
		return getContext(owner, 1);
	}

    public PlayerAbilityContext getContext(GamePlayer owner, int level) {
        var context = new PlayerAbilityContext(ability);
		context.level = level;
		context.owner = owner;
		return context;
	}
}
