package com.carterz30cal.items;

/**
 * @author carterz30cal
 * @version 2
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
	VIRTUAL_SET(ItemTypeUse.VIRTUAL_NON_EXIST);
    public final ItemTypeUse use;
	public String cute;
	
	ItemType(ItemTypeUse use)
	{
		this.use = use;
	}
	
	ItemType(String cute) {
		this.use = ItemTypeUse.NORMAL;
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
}
