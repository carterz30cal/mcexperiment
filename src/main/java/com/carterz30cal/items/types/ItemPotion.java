package com.carterz30cal.items.types;

import com.carterz30cal.brewing.PotionPacket;
import com.carterz30cal.items.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class ItemPotion extends Item {
    public static final Map<String, ItemPotion> recipes = new HashMap<>();
    /**
     * The minimum elements needed to produce this potion.
     * @since 1.0.0 [1]
     */
    public final List<PotionPacket> recipe = new ArrayList<>();
    /**
     * The default duration of this potion, this can be increased by the brewing
     * process.
     * @since 1.0.0 [1]
     */
    public long duration;

    /**
     * Generate a <code>String</code> representation of this recipe and add it to the map
     * so that we can solve for it in the <code>BrewingGUI</code>.
     * @since 1.0.0 [1]
     */
    public void generate() {
        var builder = new StringBuilder();
        for (var r : recipe) {
            builder.append(r.element().name()).append("-").append(r.level()).append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        recipes.put(builder.toString(), this);
    }
}
