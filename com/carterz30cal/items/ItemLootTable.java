package com.carterz30cal.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.utils.RandomUtils;

public class ItemLootTable
{
	private List<ItemLoot> loot = new ArrayList<>();
	
	public List<ItemStack> generate(GamePlayer player)
	{
		List<ItemStack> drops = new ArrayList<>();
		for (ItemLoot l : loot)
		{
			if (l.rollDrop())
			{
				ItemStack it = l.generate();
				if (l.rarity != ItemDropRarity.NORMAL) player.sendMessage(l.rarity.messagePrefix + " " + it.getItemMeta().getDisplayName());
				
				drops.add(it);
			}
		}
		
		return drops;
	}
	
	public void addDrop(String item, int[] amount, int[] chance)
	{
		ItemLoot drop = new ItemLoot();
		drop.item = item;
		drop.amount = amount;
		drop.chance = chance;
		
		double percent = ((double)(chance[0]) / chance[1]) * 100;
		for (int r = ItemDropRarity.values().length - 1; r >= 0; r--)
		{
			ItemDropRarity rarity = ItemDropRarity.values()[r];
			if (rarity.defaultRarity == -1) continue;
			if (rarity.defaultRarity >= percent)
			{
				drop.rarity = rarity;
				break;
			}
		}
		
		loot.add(drop);
	}
	public void addDrop(String item, int[] amount, int[] chance, String enchants)
	{
		ItemLoot drop = new ItemLoot();
		drop.item = item;
		drop.amount = amount;
		drop.chance = chance;
		drop.enchant = enchants;
		
		double percent = ((double)(chance[0]) / chance[1]) * 100;
		for (int r = ItemDropRarity.values().length - 1; r >= 0; r--)
		{
			ItemDropRarity rarity = ItemDropRarity.values()[r];
			if (rarity.defaultRarity == -1) continue;
			if (rarity.defaultRarity >= percent)
			{
				drop.rarity = rarity;
				break;
			}
		}
		
		loot.add(drop);
	}
	
	private class ItemLoot
	{
		public String item;
		public int[] amount;
		public int[] chance;
		public ItemDropRarity rarity;
		public String enchant;
		
		public boolean rollDrop()
		{
			int pick = RandomUtils.getRandom(1, chance[1]);
			return pick <= chance[0];
		}
		
		public ItemStack generate()
		{
			ItemStack gen = ItemFactory.build(item, RandomUtils.getRandom(amount[0], amount[1]));
			if (enchant != null) 
			{
				ItemFactory.addItemData(gen, "enchants", enchant);
			}
			
			ItemFactory.update(gen, null);
			return gen;
		}
	}
}
