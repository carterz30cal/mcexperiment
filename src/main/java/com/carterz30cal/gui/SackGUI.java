package com.carterz30cal.gui;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.Item;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.ItemRarity;
import com.carterz30cal.items.ItemType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SackGUI extends AbstractGUI
{
	public int page = 1;
	public String[] arrows;
	public SackGUI(GamePlayer owner) {
		super(owner);
		
		inventory = new GooeyInventory("Ingredient Sack", 6);
		
		update();
	}
	
	public void update()
	{
		arrows = new String[54];
		inventory.initUsingTemplate(GooeyTemplate.SHOPPY);
		
		List<String> cont = new ArrayList<>(owner.sack.keySet());
		cont.removeIf((a) -> owner.sack.get(a) <= 0);
		Map<Integer, List<String>> rarities = new HashMap<>();
		for (String c : cont) {
			int r = ItemRarity.values().length - ItemFactory.getItem(c).rarity.ordinal() - 1;
			rarities.putIfAbsent(r, new ArrayList<>());
			rarities.get(r).add(c);
		}
		
		for (List<String> ab : rarities.values()) {
			ab.sort((a, b) -> owner.sack.get(a) > owner.sack.get(b) ? -1 : (owner.sack.get(a) == owner.sack.get(b) ? 0 : 1));
		}
		cont.clear();
		for (int r = 0; r < ItemRarity.values().length; r++) {
			cont.addAll(rarities.getOrDefault(r, new ArrayList<>()));
		}
		
		for (int i = (page - 1) * 28; i < page * 28 && i < cont.size(); i++) {
			int b = i % 28;
			int x = b % 7 + 1;
			int y = b / 7 + 1;
			
			String s = cont.get(i);
            ItemStack disp = ItemFactory.buildCustom(s, ItemFactory.getItemTypeName(s) + " WHITEx" + owner.sack.get(s));
			disp.setAmount(Math.min(owner.sack.get(s), 64));
			arrows[y * 9 + x] = s;
			
			inventory.setSlot(disp, calc(x, y));
		}
		
		if (page > 1) inventory.setSlot(GooeyInventory.produceElement("ARROW", "REDPrevious Page"), calc(1, 5));
		inventory.setSlot(GooeyInventory.produceElement("CHEST", "GOLDInsert items from inventory"), calc(4, 5));
		if (page * 28 <= cont.size()) inventory.setSlot(GooeyInventory.produceElement("ARROW", "GREENNext Page"), calc(7, 5));
		
		inventory.update();
	}

	public boolean allowClick(int clickPos, ItemStack clicked)
	{
		if (clickPos == calc(4, 5)) {
			for (ItemStack it : owner.player.getInventory().getContents())
			{
				Item i = ItemFactory.getItem(it);
				if (i != null && i.type == ItemType.INGREDIENT) {
					int am = it.getAmount();
					if (owner.hasSackSpace(am)) {
						owner.sack.put(i.id, owner.sack.getOrDefault(i.id, 0) + am);
						it.setAmount(0);
					}
					else if (owner.getSackSpaceRemaining() > 0) {
						int ram = owner.getSackSpaceRemaining();
						owner.sack.put(i.id, owner.sack.getOrDefault(i.id, 0) + ram);
						clicked.setAmount(am - ram);
					}
				}
			}
		}
		else if (clickPos < 54 && arrows[clickPos] != null && owner.player.getInventory().firstEmpty() != -1) {
			int am = Math.min(64, owner.sack.getOrDefault(arrows[clickPos], 0));
			owner.sack.put(arrows[clickPos], owner.sack.getOrDefault(arrows[clickPos], 0) - am);
			owner.player.getInventory().addItem(ItemFactory.build(arrows[clickPos], am));
		}
		else if (clickPos >= 54) {
			Item i = ItemFactory.getItem(clicked);
			if (i != null && i.type == ItemType.INGREDIENT) {
				int am = clicked.getAmount();
				if (owner.hasSackSpace(am)) {
					owner.sack.put(i.id, owner.sack.getOrDefault(i.id, 0) + am);
					clicked.setAmount(0);
				}
				else if (owner.getSackSpaceRemaining() > 0) {
					int ram = owner.getSackSpaceRemaining();
					owner.sack.put(i.id, owner.sack.getOrDefault(i.id, 0) + ram);
					clicked.setAmount(am - ram);
				}
			}
		}
		update();
		return false;
	}
	
	
}
