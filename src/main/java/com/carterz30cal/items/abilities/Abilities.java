package com.carterz30cal.items.abilities;

import com.carterz30cal.entities.health.damage.DamageType;
import com.carterz30cal.entities.health.status.StatusEffect;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.items.abilities.generic.*;
import com.carterz30cal.items.abilities.implementation.GameAbility;
import com.carterz30cal.items.abilities.implementation.PlayerAbilityContext;
import com.carterz30cal.items.abilities.necropolis.enchants.StatusEffectEnchantment;
import com.carterz30cal.items.abilities.necropolis.enchants.ThornsEnchantment;
import com.carterz30cal.items.abilities.necropolis.items.SandShovelAbility;
import com.carterz30cal.items.abilities.necropolis.items.TreasureDetectorAbility;
import com.carterz30cal.items.abilities.waterway.*;
import com.carterz30cal.items.abilities.waterway.factory.CatalystDustCoagulatorFactoryUpgrade;
import com.carterz30cal.items.abilities.waterway.pets.*;
import com.carterz30cal.items.abilities.waterway.sets.LeafArmourSet;
import com.carterz30cal.items.abilities.waterway.sets.ZombieArmourSet;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatOperationType;
import com.carterz30cal.stats.operations.AddStatOperation;

/**
 * @author carterz30cal
 * @version 2
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
	ENCHANT_FLIMSY_PERSISTENCE(
			new FlimsyPersistenceEnchant()
	),
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
	STORM_AXE(new StatOperationOnRainAbility("Stormlord", Stat.STRENGTH, new AddStatOperation(Stat.STRENGTH, 125), true)),
	STORM_TALISMAN(new StatOperationOnRainAbility("Storm Power", Stat.POWER, new AddStatOperation(Stat.POWER, 40))),
	STORM_PET_PASSIVE_COMMON(new StatOperationOnRainAbility("Passive: Charged!", Stat.POWER, new AddStatOperation(Stat.POWER, 30))),
	STORM_PET_ACTIVE_COMMON(new LightningBugPetActive()),
	WATERWAY_DOWNPOUR_KEY(new WaterwayDownpourSummonAbility()),
    MAGIC_SWORD(new MagicSwordAbility()),
    RAGING_AXE(new RagingAxeAbility()),
    SERAPH_SUMMON_GUIDE(new SeraphSummonGuideAbility("water_seraph_spirit", 20)),
    ADMIN_INSTANT_DEATH_SWORD(new DeadAbility()),

	CATALYST_DUST_COAGULATOR(new CatalystDustCoagulatorFactoryUpgrade()),

	// NECROPOLIS ENCHANTS
	ENCHANT_EFFICIENCY(new StatEnchantment(
			"Efficiency", 2, Stat.MINING_SPEED, 0, 20, 5, ItemType.PICKAXE, ItemType.HELMET
	)),
	ENCHANT_WETSUIT(new StatEnchantment(
			"Wetsuit", 2, Stat.INSULATION, 10, 5, 4,
			ItemType.HELMET, ItemType.CHESTPLATE, ItemType.LEGGINGS, ItemType.BOOTS
	)),
	ENCHANT_FORTUNE(new StatEnchantment(
			"Fortune", 3, Stat.MINING_FORTUNE, 0,10, 10, ItemType.PICKAXE
	)),
	ENCHANT_THORNS(new ThornsEnchantment(
			DamageType.PHYSICAL, 15,
			Stat.MIGHT, 0.75,
			1,
			4,
			ItemType.HELMET, ItemType.CHESTPLATE, ItemType.LEGGINGS,
			ItemType.BOOTS
	)),
	ENCHANT_FEROCITY(new StatEnchantment(
			"Ferocity", 2, Stat.SAVAGERY, 0, 1, 10,
			ItemType.WEAPON, ItemType.BOW, ItemType.ROD
	)),
    ENCHANT_SENSITIVITY(new StatEnchantment(
            "Sensitivity", 2, Stat.DETECTION, 0, 4, 5,
            ItemType.TOOL
    )),
    ENCHANT_FIRE_ASPECT(new StatusEffectEnchantment(
            "Fire Aspect", StatusEffect.BURN, 10, 10, 4, 3, ItemType.WEAPON, ItemType.BOW
    )),

	PET_DUSTED_COMMON(new PlayerStatAbility("Active: Dust-ball", Stat.DEFENCE, 80)),
	PET_RED_SLIME_COMMON(new PlayerStatAbility("Active: Slime Layers", Stat.INSULATION, 150)),
	LESSER_LIFE_SWORD(new GrantStatOnLevelAbility("Signs of Life", Stat.STRENGTH, 8)),
	TITAN_BLADE_LEGENDARY(new GrantStatOnLevelAbility("Irremovable Defence", Stat.DEFENCE, 4)),
	SAND_SHOVEL(new SandShovelAbility()),
	TREASURE_DETECTOR(new TreasureDetectorAbility()),
	;
	public final GameAbility ability;

	Abilities(GameAbility ability) {
		this.ability = ability;
		this.ability.source = this;
	}

    public PlayerAbilityContext getContext(GamePlayer owner, int level) {
        var context = new PlayerAbilityContext(ability);
		context.level = level;
		context.owner = owner;
		return context;
	}
}
