package com.carterz30cal.entities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.utils.RandomUtils;

public class EnemyLootTable
{
	public List<LootDrop> drops = new ArrayList<>();
	
	public void execute(GamePlayer player)
	{
		for (LootDrop drop : drops)
		{
			player.giveItem(drop.item);
			
			switch (drop.message)
			{
			case RARE:
				player.sendMessage("BLUEBOLDLUCKY! GRAYYou got a " + ItemFactory.getItemName(drop.item) + "GRAY!");
				break;
			case EPIC:
				player.sendMessage("DARK_PURPLEBOLDEPIC! GRAYYou got a " + ItemFactory.getItemName(drop.item) + "GRAY!!");
				break;
			case LEGENDARY:
				player.sendMessage("GOLDBOLDWow! GRAYYou got a " + ItemFactory.getItemName(drop.item) + "GRAY!!!");
			default: break;
			}
		}
	}
	
	public void addDropWithChance(String item, int amount, int c1, int c2)
	{
		if (RandomUtils.getRandom(1, c2) > c1) return;
		
		drops.add(new LootDrop(item, amount, ((double)c1)/c2));
	}
	
	public void addDropWithAmount(String item, int am1, int am2)
	{
		
		drops.add(new LootDrop(item, RandomUtils.getRandom(am1, am2)));
	}
	
	@SuppressWarnings("unused")
	private class LootDrop
	{
		public ItemStack item;
		public LootMessage message;
		
		
		public LootDrop(String template, int amount)
		{
			item = ItemFactory.build(template, amount);
			message = LootMessage.NONE;
		}
		public LootDrop(String template, int amount, double dropChance)
		{
			item = ItemFactory.build(template, amount);
			
			if (dropChance < 0.00075) message = LootMessage.LEGENDARY; // 0.075%
			else if (dropChance < 0.005) message = LootMessage.EPIC; // 0.5%
			else if (dropChance < 0.05) message = LootMessage.RARE;// 5%
			else message = LootMessage.NONE;
		}
	}
	
	private enum LootMessage
	{
		NONE,
		RARE,
		EPIC,
		LEGENDARY;
	}
}


