package com.carterz30cal.stats;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;

@SuppressWarnings("UnnecessaryUnicodeEscape")
public enum Stat
{
    DAMAGE("\u03C8 Damage", NamedTextColor.RED, StatType.OFFENSIVE, StatDisplayType.NORMAL),
    SAVAGERY("\u00D7 Savagery", NamedTextColor.RED, StatType.OFFENSIVE, StatDisplayType.PERCENTAGE),
    STRENGTH("\u25B2 Strength", NamedTextColor.BLUE, StatType.OFFENSIVE, StatDisplayType.NORMAL),
    POWER("\u00B1 Power", NamedTextColor.BLUE, StatType.OFFENSIVE, StatDisplayType.NORMAL),
    MIGHT("\u25BC Might", NamedTextColor.BLUE, StatType.OFFENSIVE, StatDisplayType.NORMAL),

    HEALTH("\u2665 Health", NamedTextColor.RED, StatType.DEFENSIVE, StatDisplayType.NORMAL),
    DEFENCE("\u25CB Defence", NamedTextColor.GREEN, StatType.DEFENSIVE, StatDisplayType.NORMAL),
    VITALITY("\u25C6 Vitality", NamedTextColor.GREEN, StatType.DEFENSIVE, StatDisplayType.NORMAL),
    ROBUSTNESS("\u00B1 Robustness", NamedTextColor.RED, StatType.DEFENSIVE, StatDisplayType.NORMAL),

    MANA("\u00D7 Mana", NamedTextColor.LIGHT_PURPLE, StatType.OFFENSIVE, StatDisplayType.NORMAL),
    FOCUS("\u25C6 Focus", NamedTextColor.AQUA, StatType.OFFENSIVE, StatDisplayType.NORMAL),

    FISHING_POWER("\u023E Fishing Power", NamedTextColor.AQUA, StatType.ECONOMY),

    BREAKING_POWER("\u03C8 Mining Power", NamedTextColor.YELLOW, StatType.ECONOMY),
    MINING_SPEED("\u023E Mining Speed", NamedTextColor.YELLOW, StatType.ECONOMY),
    MINING_FORTUNE("\u2665 Mining Fortune", NamedTextColor.YELLOW, StatType.ECONOMY),
    PICKING("\u0194 Picking", NamedTextColor.YELLOW, StatType.ECONOMY),
    CLEARING("\u0190 Clearing", NamedTextColor.YELLOW, StatType.ECONOMY),

    BONUS_COINS("\u00D7 Extra Coins", NamedTextColor.GOLD, StatType.ECONOMY, StatDisplayType.PERCENTAGE),
    LUCK("\u00D7 Luck", NamedTextColor.GOLD, StatType.ECONOMY, StatDisplayType.PERCENTAGE),

    INVULNERABILITY_TICKS("invul ticks", NamedTextColor.BLACK, StatType.HIDDEN, StatDisplayType.NO_DISPLAY),

    ENCHANT_POWER("enchantment power", NamedTextColor.BLACK, StatType.HIDDEN, StatDisplayType.NO_DISPLAY),
    LEVEL_REQUIREMENT("level requirement", NamedTextColor.BLACK, StatType.HIDDEN, StatDisplayType.NO_DISPLAY),
    BACKPACK_PAGES("backpack pages", NamedTextColor.WHITE, StatType.HIDDEN, StatDisplayType.NO_DISPLAY),
    WARDROBE_SLOTS("wardrobe slots", NamedTextColor.WHITE, StatType.HIDDEN, StatDisplayType.NO_DISPLAY),
    SACK_SPACE("\uD83D Sack Space", NamedTextColor.WHITE, StatType.HIDDEN, StatDisplayType.NO_DISPLAY_IN_PLAYER_STATS),
    VISIBILITY("\u25CB Visibility", NamedTextColor.YELLOW, StatType.OFFENSIVE, StatDisplayType.NORMAL),
    SKILL_TREE_TOKENS("skill tree tokens", NamedTextColor.WHITE, StatType.HIDDEN, StatDisplayType.NO_DISPLAY),
	;
    public final String name;
    @Deprecated
    public final ChatColor colour;
    public final TextColor textColour;
    public final StatType type;
    public final StatDisplayType display;

    Stat(String name, NamedTextColor colour, StatType type, StatDisplayType display)
	{
		this.name = name;
        this.colour = ChatColor.STRIKETHROUGH;
        this.textColour = TextColor.color(colour);
		this.type = type;
		this.display = display;
	}

    Stat(String name, NamedTextColor colour, StatType type)
	{
		this.name = name;
        this.colour = ChatColor.STRIKETHROUGH;
        this.textColour = TextColor.color(colour);
		this.type = type;
		this.display = StatDisplayType.NORMAL;
	}
	
	public String getReverse()
	{
        return "<" + textColour.asHexString() + ">" + name.substring(2) + " " + name.charAt(0) + "</" + textColour.asHexString() + ">";
	}

    public Component getReversed() {
        return Component.text(name.substring(2) + " " + getIcon()).color(textColour);
    }
	
	public String getIcon()
	{
		return "" + name.charAt(0);
	}
}