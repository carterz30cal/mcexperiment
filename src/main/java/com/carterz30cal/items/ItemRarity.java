package com.carterz30cal.items;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;

public enum ItemRarity
{
    COMMON("Common", NamedTextColor.GRAY, 1, 1),
    UNCOMMON("Uncommon", NamedTextColor.YELLOW, 5, 50),
    RARE("Rare", NamedTextColor.GREEN, 10, 100),
    VERY_RARE("Very Rare", NamedTextColor.BLUE, 20, 500),
    EPIC("Epic", NamedTextColor.DARK_PURPLE, 40, 1000),
    INCREDIBLE("Incredible", NamedTextColor.LIGHT_PURPLE, 80, 2500),
    LEGENDARY("Legendary", NamedTextColor.GOLD, 160, 10000),
    MYSTERIOUS("Mysterious", NamedTextColor.DARK_AQUA),
    UNOBTAINABLE("Unobtainable", NamedTextColor.DARK_RED),
    TRASH("Rubbish", TextColor.color(120, 120, 120));
	public final String name;
    public final TextColor textColor;
    @Deprecated
    public ChatColor colour;
	public int lootboxOdds = -1;
	public int lootOdds = -1;

    ItemRarity(String name, TextColor colour, int boxOdds, int lootOdds) {
        this.name = name;
        this.textColor = colour;
        this.lootboxOdds = boxOdds;
        this.lootOdds = lootOdds;
    }

    ItemRarity(String name, TextColor colour) {
        this(name, colour, -1, -1);
    }

    ItemRarity(String name, NamedTextColor colour, int boxOdds, int lootOdds) {
        this(name, TextColor.color(colour), boxOdds, lootOdds);
    }

    ItemRarity(String name, NamedTextColor colour) {
        this(name, colour, -1, -1);
    }
}
