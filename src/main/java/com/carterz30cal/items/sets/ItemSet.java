package com.carterz30cal.items.sets;

import com.carterz30cal.items.Item;

import java.util.List;

/**
 * A set bonus essentially works as a virtual item
 * but the name and description will be translated over to the set bonus part
 * of each item's lore.
 * Sets require that all item ids are present on the player.
 * All sets are of the {@link com.carterz30cal.items.ItemType} VIRTUAL_SET type.
 */
public class ItemSet extends Item {
    public int requireCount = 4;

}
