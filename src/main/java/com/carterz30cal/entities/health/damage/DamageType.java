package com.carterz30cal.entities.health.damage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

/**
 * @author carterz30cal
 * @version 1
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
    private final Component name;
    private final TextColor colour;

    DamageType(String name, TextColor colour) {
        this.name = MiniMessage.miniMessage().deserialize(name).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        this.colour = colour;
    }

    DamageType(String name, NamedTextColor colour) {
        this(name, TextColor.color(colour));
    }

    public Component getName() {
        return name;
    }

    public TextColor getColour() {
        return colour;
    }
}
