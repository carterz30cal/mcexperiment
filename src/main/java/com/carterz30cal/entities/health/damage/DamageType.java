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
    BLEED("<red><em>Bleed</em></red>", NamedTextColor.RED);
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
