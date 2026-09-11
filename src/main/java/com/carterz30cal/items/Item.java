package com.carterz30cal.items;

import com.carterz30cal.items.abilities.Abilities;
import com.carterz30cal.items.trims.TrimMaterialWrapper;
import com.carterz30cal.items.trims.TrimPatternWrapper;
import com.carterz30cal.stats.StatContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 3
 * @since 1.0.0
 */
public class Item 
{
	public String name;
	public String id;

    /**
     *
     * @deprecated in favour of lore
     */
    @Deprecated
	public List<String> description;

    /**
     * provides a manner of adding arbitrary lore to items.
     * by default this will render in dark_grey
     * replaces description.
     *
     * @implSpec should be serialized into components using MiniMessage format.
     * @since 1.0.0
     */
    public List<String> lore;

	public List<Abilities> abilities = new ArrayList<>();
	public List<String> tags = new ArrayList<>();

	public Material material;
	public ItemType type;
	public ItemRarity rarity;
	public boolean glow;

    public TrimPatternWrapper trimPattern;
    public TrimMaterialWrapper trimMaterial;

	public long value;

	public String set;
    public String skullProfileId;

	public String discovery;
	public long discoveryProgress;
	
	public StatContainer stats;
	
	public int r;
	public int g;
	public int b;

	public TextComponent.Builder text() {
		return Component.text().content(name).color(rarity.textColor);
	}
}
