package com.carterz30cal.items;

import com.carterz30cal.entities.player.GamePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ItemReqs 
{
	public Map<String, Integer> reqs = new HashMap<>();
	public int coins;
	
	public void addRequirement(ItemReq requirement)
	{
		if (requirement == null) return;
		
		coins += requirement.coins;
		reqs.put(requirement.item, reqs.getOrDefault(requirement.item, 0) + requirement.amount);
	}
	
	public void addRequirement(List<ItemReq> requirement)
	{
		if (requirement == null) return;
		for (ItemReq req : requirement) addRequirement(req);
	}
	
	public Set<String> getItems()
	{
		return reqs.keySet();
	}
	public int getAmount(String item)
	{
		return reqs.getOrDefault(item, 0);
	}
	
	
	public void execute(GamePlayer by)
	{
		by.coins -= coins;
		
		Map<String, Integer> working = new HashMap<>(reqs);
		for (String w : working.keySet()) {
			int sack = by.sack.getOrDefault(w, 0);
			//System.out.println(w + " - " + sack);
			if (sack > 0) {
				int am = working.get(w);
				working.put(w, am - sack);
				by.sack.put(w, Math.max(0, sack - am));
			}
		}
		for (ItemStack i : by.player.getInventory().getContents())
		{
			Item item = ItemFactory.getItem(i);
			if (item == null) continue;
			if (!working.containsKey(item.id)) continue;
			if (working.get(item.id) == 0) working.remove(item.id);
			
			int amLeft = working.getOrDefault(item.id, 0);
			
			int setAm = i.getAmount() - Math.min(Math.max(0, amLeft), i.getAmount());
			
			int am = working.getOrDefault(item.id, 0) - i.getAmount();
			if (am <= 0) working.remove(item.id);
			else working.put(item.id, am);
			
			i.setAmount(setAm);
		}
	}
	
	public boolean areRequirementsMet(GamePlayer by)
	{
		if (by.coins < coins) return false;
		
		Map<String, Integer> working = new HashMap<>(reqs);
		for (String w : working.keySet()) {
			int sack = by.sack.getOrDefault(w, 0);
			if (sack > 0) {
				working.put(w, working.get(w) - sack);
			}
		}
		for (ItemStack i : by.player.getInventory().getContents())
		{
			Item item = ItemFactory.getItem(i);
			if (item == null) continue;
			if (!working.containsKey(item.id)) continue;
			if (working.get(item.id) == 0) working.remove(item.id);
			
			int am = working.getOrDefault(item.id, 0) - i.getAmount();
			if (am <= 0) working.remove(item.id);
			else working.put(item.id, am);
		}
		
		
		for (int w : working.values()) {
			if (w > 0) return false;
		}
		return true;
	}
	
	public String grabDataFromRequirements(GamePlayer by)
	{
		ItemStack dataHolder = ItemFactory.build("flimsy_sword");
		
		Map<String, Integer> working = new HashMap<>(reqs);
		for (ItemStack i : by.player.getInventory().getContents())
		{
			Item item = ItemFactory.getItem(i);
			if (item == null) continue;
			if (!working.containsKey(item.id)) continue;
			
			Map<String, String> data = ItemFactory.getItemData(i);
			if (data.size() != 0) 
			{
				ItemFactory.setItemData(dataHolder, data);
			}
			
			int am = working.getOrDefault(item.id, 0) - i.getAmount();
			if (am <= 0) working.remove(item.id);
			else working.put(item.id, am);
		}
		
		return ItemFactory.getFlatItemData(dataHolder);
	}
}
