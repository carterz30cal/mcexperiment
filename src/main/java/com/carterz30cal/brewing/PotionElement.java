package com.carterz30cal.brewing;

import com.carterz30cal.items.ItemFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Range;

/**
 * These are fundamental building blocks of all potions.
 * There are the original four, which are introduced in <b>Necropolis</b>, then we will add
 * more later on for more complex potions. There's also the void element, which has no interactions.
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public enum PotionElement {
    EARTH("♠", NamedTextColor.DARK_GREEN, Material.GREEN_TERRACOTTA),
    WATER("◆", NamedTextColor.BLUE, Material.BLUE_TERRACOTTA),
    FIRE("△", NamedTextColor.RED, Material.RED_TERRACOTTA),
    AIR("▽", NamedTextColor.WHITE, Material.WHITE_TERRACOTTA),
    VOID("○", NamedTextColor.GRAY, Material.BLACK_CONCRETE),
    ;
    private final String icon;
    private final TextColor colour;
    private final Material material;
    private final String mmf;
    private final String mffn;

    PotionElement(String icon, TextColor colour, Material material) {
        this.icon = icon;
        this.colour = colour;
        this.material = material;
        this.mmf = "<" + colour.asHexString() + ">" + icon + "</" + colour.asHexString() + ">";
        this.mffn = "<" + colour.asHexString() + ">" + capitalised() + "</" + colour.asHexString() + ">";
    }
    PotionElement(String icon, NamedTextColor colour, Material material) {
        this(icon, TextColor.color(colour), material);
    }

    /**
     * Get a cached minimessage-style icon for usage in displays.
     * @return our mini-message format string
     * @since 1.0.0 [1]
     */
    public String miniMessage() {
        return mmf;
    }

    /**
     * Helper function that returns the capitalised name of the string.
     * @return a capitalised string of the name of this enum
     */
    private String capitalised() {
        var fl = name().substring(0, 1).toUpperCase();
        var ll = name().substring(1).toLowerCase();
        return fl + ll;
    }

    /**
     * @return a MiniMessage-form pretty name string
     * @since 1.0.0 [1]
     */
    public String pretty() {
        return mffn;
    }

    /**
     * Get a <code>Component</code> form icon for usage in component displays.
     * @apiNote generally prefer using <code>miniMessage()</code> if at all possible.
     * @return a <code>Component</code> that would display the icon for this element.
     * @since 1.0.0 [1]
     */
    public Component component() {
        return Component.text().content(icon).color(colour).build();
    }

    /**
     * @param level the potency of this element. Must be between 1-64.
     * @return a pretty <code>ItemStack</code> representation of this element, for use in the brewing stand gui.
     * @since 1.0.0
     * @see ItemFactory
     */
    public ItemStack item(@Range(from=1, to=64) int level) {
        var rep = ItemFactory.customItem(material.toString(), pretty(), "<dark_grey>Potency " + level);
        rep.setAmount(level);
        return rep;
    }

    /**
     * Gets the colour of this element.
     * @return a <code>TextColor</code>.
     * @since 1.0.0 [1]
     * @apiNote color is wrong.
     */
    public TextColor colour() {
        return colour;
    }
}
