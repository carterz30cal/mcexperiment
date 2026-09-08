package com.carterz30cal.entities.health.damage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public enum DamageType {
    PHYSICAL("<white>Physical</white>", NamedTextColor.WHITE),
    PROJECTILE("<white>Projectile</white>", NamedTextColor.WHITE),
    EXPLOSIVE("<#FFA500>Explosive</#FFA500>", TextColor.color(0xFFA500)),
    FIRE("<dark_red>Fire</dark_red>", NamedTextColor.DARK_RED),
    FROST("<dark_blue>Frost</dark_blue>", NamedTextColor.DARK_BLUE),
    MAGIC("<aqua>Magic</aqua>", NamedTextColor.AQUA),
    HOLY("<gold>Holy</gold>", NamedTextColor.GOLD),
    LIGHTNING("<yellow>Lightning</yellow>", NamedTextColor.YELLOW),
    CORRUPTION("<dark_purple>Corruption</dark_purple>", NamedTextColor.DARK_PURPLE),
    BLEED("<red><em>Bleed</em></red>", NamedTextColor.RED),
    FALL("<grey>Fall</grey>", NamedTextColor.GRAY),
    SUFFOCATION("<yellow>Suffocation</yellow>", NamedTextColor.YELLOW);
    private final Component component;
    private final String name;
    private final TextColor colour;

    DamageType(String name, TextColor colour) {
        this.component = MiniMessage.miniMessage().deserialize(name).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        this.name = name;
        this.colour = colour;
    }

    DamageType(String name, NamedTextColor colour) {
        this(name, TextColor.color(colour));
    }

    public Component getComponent() {
        return component;
    }

    /**
     * @return the unformatted name of this <code>DamageType</code>, typically in <code>MiniMessage</code> format.
     * @since 1.0.0 [2]
     */
    public String getName() {
        return this.name;
    }

    public TextColor getColour() {
        return colour;
    }
}
