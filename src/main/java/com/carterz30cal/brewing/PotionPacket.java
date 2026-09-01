package com.carterz30cal.brewing;

import org.bukkit.inventory.ItemStack;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class PotionPacket {
    /**
     * What element is this packet?
     * @since 1.0.0 [1]
     */
    private final PotionElement element;
    /**
     * How strong is this packet? Determines final potion strength.
     * @since 1.0.0 [1]
     */
    private int level;

    /**
     * Basic constructor for <code>PotionPacket</code>, sets level to <code>1</code>.
     * @param element what element are we trying to make a packet for?
     * @since 1.0.0 [1]
     */
    public PotionPacket(PotionElement element) {
        this(element, 1);
    }

    /**
     * Constructor for <code>PotionPacket</code>
     * @param element what element are we trying to make a packet for?
     * @param level how strong is it?
     * @since 1.0.0 [1]
     */
    public PotionPacket(PotionElement element, int level) {
        this.element = element;
        this.level = 1;
    }

    /**
     * Copy constructor
     * @param parent what we're copying from
     * @since 1.0.0 [1]
     */
    public PotionPacket(PotionPacket parent) {
        this(parent.element, parent.level);
    }

    /**
     * Make a pretty item representation of this packet.
     * @return an <code>ItemStack</code> that tells us the element and the potency.
     * @since 1.0.0 [1]
     */
    public ItemStack item() {
        return element.item(level);
    }

    /**
     * @return the level, or potency, of this packet.
     * @since 1.0.0 [1]
     */
    public int level() {
        return level;
    }

    /**
     * Change the level, or potency, of this potion packet.
     * @param level the level we're setting
     * @since 1.0.0 [1]
     */
    public void level(int level) {
        this.level = level;
    }

    /**
     * @return the element of this packet.
     * @since 1.0.0 [1]
     */
    public PotionElement element() {
        return element;
    }
}
