package com.carterz30cal.items.types;

import com.carterz30cal.items.Item;
import com.carterz30cal.items.ItemFactory;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class ItemIngredientGenerator extends Item {
    public long time;
    public String generates;
    public long timePerItem() {
     return time;
    }
    public String generates() {
        return generates;
    }
}
