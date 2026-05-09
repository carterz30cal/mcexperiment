package com.carterz30cal.gui;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.Item;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.ItemRarity;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.main.Dungeons;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.kyori.adventure.text.Component.text;

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
            ab.sort((a, b) -> owner.sack.get(b).compareTo(owner.sack.get(a)));
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
            var display = ItemFactory.customItem(
                    s,
                    text().append(ItemFactory.getItemNameBuilder(s))
                            .append(text(" x" + owner.sack.get(s), NamedTextColor.WHITE))
            );
            display.setAmount(Math.min(owner.sack.get(s), 64));
			arrows[y * 9 + x] = s;

            inventory.setSlot(display, calc(x, y));
		}

        if (page > 1) {
            inventory.setSlot(ItemFactory.customItem("ARROW", "Previous Page", NamedTextColor.RED), calc(1, 5));
        }
        if (page * 28 <= cont.size()) {
            inventory.setSlot(ItemFactory.customItem("ARROW", "Next Page", NamedTextColor.GREEN), calc(7, 5));
        }

        inventory.setSlot(
                ItemFactory.customItem("CHEST", "Insert items from inventory", NamedTextColor.GOLD),
                calc(4, 5));


		
		inventory.update();
	}

	public boolean allowClick(int clickPos, ItemStack clicked)
	{
		if (clickPos == calc(4, 5)) {
			for (ItemStack it : owner.player.getInventory().getContents())
			{
                handleSackItemClick(clicked, it);
            }
		}
		else if (clickPos < 54 && arrows[clickPos] != null && owner.player.getInventory().firstEmpty() != -1) {
			int am = Math.min(64, owner.sack.getOrDefault(arrows[clickPos], 0));
			owner.sack.put(arrows[clickPos], owner.sack.getOrDefault(arrows[clickPos], 0) - am);

            var item = ItemFactory.build(arrows[clickPos], am);
            if (item != null) {
                owner.player.getInventory().addItem(item);
            }
            else {
                Dungeons.instance.getLogger().severe("SackGUI: item not found! - " + arrows[clickPos]);
            }
		}
		else if (clickPos >= 54) {
            handleSackItemClick(clicked, clicked);
        }
        update();
        return false;
    }

    private void handleSackItemClick(ItemStack clicked, ItemStack it) {
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
