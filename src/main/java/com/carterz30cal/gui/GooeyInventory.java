package com.carterz30cal.gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.utils.StringUtils;

public class GooeyInventory
{
	private Inventory underlying;
	private ItemStack[] contents;
	private int size;
	
	public GooeyInventory(String name, int size)
	{
		size *= 9;
		underlying = Bukkit.createInventory(null, size, StringUtils.colourString(name));
		contents = new ItemStack[size];
		this.size = size;
	}
	
	public void open(GamePlayer player)
	{
		player.player.openInventory(underlying);
	}
	
	public void initUsingTemplate(GooeyTemplate template)
	{
		switch (template)
		{
		case PANED:
			for (int i = 0; i < size; i++) contents[i] = produceElement("WHITE_STAINED_GLASS_PANE", " ");
			break;
		case PANED_DARK:
			for (int i = 0; i < size; i++) contents[i] = produceElement("BLACK_STAINED_GLASS_PANE", " ");
			break;
		case SHOPPY:
			for (int i = 0; i < size; i++)
			{
				if (i % 9 == 0 || i % 9 == 8 || i / 9 == 0 || i / 9 == (size / 9) - 1) contents[i] = produceElement("WHITE_STAINED_GLASS_PANE", " ");
				else contents[i] = null;
			}
		case EMPTY:
		default:
			return;
		}
		
		update();
	}
	
	public void setSlot(ItemStack item, int slot)
	{
		contents[slot] = item;
	}
	public ItemStack getSlot(int slot)
	{
		return contents[slot];
	}

	public void updateUsingContents() {
		for (int i = 0; i < size; i++) {
			contents[i] = underlying.getItem(i);
		}
	}
	
	public void update()
	{
		underlying.setContents(contents);
	}
	
	
	public static ItemStack produceElement(String type, String name)
	{
		ItemStack base = ItemFactory.build(type);
		
		if (base == null) return null;
		ItemMeta meta = base.getItemMeta();
		meta.setDisplayName(StringUtils.colourString(name));
		base.setItemMeta(meta);
		
		return base;
	}
}
