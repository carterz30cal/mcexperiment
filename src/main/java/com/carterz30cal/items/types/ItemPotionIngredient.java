package com.carterz30cal.items.types;

import com.carterz30cal.brewing.PotionPacket;
import com.carterz30cal.items.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class ItemPotionIngredient extends Item {
    public List<PotionPacket> elements = new ArrayList<>();
}
