package com.carterz30cal.items;

/**
 * @author carterz30cal
 * @version 4
 * @since 1.0.0
 */
public enum ItemType 
{
	INGREDIENT(ItemTypeUse.NORMAL),
	QUEST_ITEM("Quest Item"),
	ENCHANTMENT(ItemTypeUse.NORMAL),
	ATTUNER(ItemTypeUse.NORMAL),
	CATALYST(ItemTypeUse.CONSUMABLE),
	WEAPON(ItemTypeUse.WIELDABLE),
    BOW(ItemTypeUse.WIELDABLE),
    TOME(ItemTypeUse.WIELDABLE),
    WAND(ItemTypeUse.WIELDABLE),
    TOOL(ItemTypeUse.WIELDABLE),
    PICKAXE(ItemTypeUse.WIELDABLE),
	ARTEFACT(ItemTypeUse.WIELDABLE_CONSUMABLE),
    KEY(ItemTypeUse.WIELDABLE_CONSUMABLE),
	LOOTBOX(ItemTypeUse.CONSUMABLE),
	TALISMAN(ItemTypeUse.TALISMAN),
	SHIELD(ItemTypeUse.OFFHAND),
	ARROW(ItemTypeUse.CONSUMABLE),
	ROD(ItemTypeUse.WIELDABLE),
	HELMET(ItemTypeUse.WEARABLE),
	CHESTPLATE(ItemTypeUse.WEARABLE),
	LEGGINGS(ItemTypeUse.WEARABLE),
	BOOTS(ItemTypeUse.WEARABLE),
	PET(ItemTypeUse.NORMAL),
	PRODUCTION_CORE("Production Core"),
	FACTORY_UPGRADE("Factory Upgrade"),
	POTION_INGREDIENT("Potion Ingredient", ItemTypeUse.CONSUMABLE),
	POTION_FUMES("Potion Fumes"),
	POTION_BOTTLE("Bottle", ItemTypeUse.CONSUMABLE),
	POTION(ItemTypeUse.TALISMAN),
	VIRTUAL_SET(ItemTypeUse.VIRTUAL_NON_EXIST);
    public final ItemTypeUse use;
	public String cute;
	private final int maxStackSize = 64;
	
	ItemType(ItemTypeUse use)
	{
		this.use = use;
	}
	
	ItemType(String cute) {
		this.use = ItemTypeUse.NORMAL;
		this.cute = cute;
	}

	/**
	 * @since 1.0.0 [4]
	 */
	ItemType(String cute, ItemTypeUse use) {
		this.use = use;
		this.cute = cute;
	}
	
	@Override
	public String toString()
	{
		if (cute != null) return cute;
		return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
	}

    public String toPlural() {
        String name = toString();
        if (name.charAt(name().length() - 1) == 's') {
            return name;
        }
        else {
            return name + 's';
        }
    }

	/**
	 * Gets the defined maximum stack size for this item type.
	 * This is typically 1 for most items, but 64 for ingredients, catalysts and keys.
	 * @return the maximum stack size allowed for this item type.
	 * @since 1.0.0 [3]
	 */
	public int maxStackSize() {
		return maxStackSize;
	}
}
