package com.carterz30cal.items;

import com.carterz30cal.entities.DamageType;

public enum ItemType 
{
	INGREDIENT(ItemTypeUse.NORMAL),
	QUEST_ITEM("Quest Item"),
	ENCHANTMENT(ItemTypeUse.NORMAL),
	ATTUNER(ItemTypeUse.NORMAL),
	CATALYST(ItemTypeUse.CONSUMABLE),
	WEAPON(ItemTypeUse.WIELDABLE),
	BOW(ItemTypeUse.WIELDABLE, DamageType.PROJECTILE),
	TOME(ItemTypeUse.WIELDABLE, DamageType.MAGICAL),
	WAND(ItemTypeUse.WIELDABLE, DamageType.MAGICAL),
	TOOL(ItemTypeUse.WIELDABLE, DamageType.PHYSICAL),
	ARTEFACT(ItemTypeUse.WIELDABLE_CONSUMABLE),
	LOOTBOX(ItemTypeUse.CONSUMABLE),
	TALISMAN(ItemTypeUse.TALISMAN),
	SHIELD(ItemTypeUse.OFFHAND),
	ARROW(ItemTypeUse.CONSUMABLE),
	ROD(ItemTypeUse.WIELDABLE),
	HELMET(ItemTypeUse.WEARABLE),
	CHESTPLATE(ItemTypeUse.WEARABLE),
	LEGGINGS(ItemTypeUse.WEARABLE),
	BOOTS(ItemTypeUse.WEARABLE),;
	public ItemTypeUse use;
	public DamageType damageType;
	public String cute;
	
	private ItemType(ItemTypeUse use)
	{
		this.use = use;
		this.damageType = DamageType.PHYSICAL;
	}
	
	private ItemType(ItemTypeUse use, DamageType damageType)
	{
		this.use = use;
		this.damageType = damageType;
	}
	
	private ItemType(String cute) {
		this.use = ItemTypeUse.NORMAL;
		this.damageType = DamageType.PHYSICAL;
		this.cute = cute;
	}
	
	@Override
	public String toString()
	{
		if (cute != null) return cute;
		return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
	}
}
