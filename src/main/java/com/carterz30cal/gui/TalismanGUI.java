package com.carterz30cal.gui;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.Item;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.ItemTypeUse;
import com.carterz30cal.utils.StringUtils;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TalismanGUI extends AbstractGUI 
{
	public int page = 1;
	public String[] talismans = new String[54];
	
	public TalismanGUI(GamePlayer owner) {
		super(owner);
		
		inventory = new GooeyInventory("Talismans", 6);
		inventory.initUsingTemplate(GooeyTemplate.SHOPPY);
		
		update();
	}
	
	public void update()
	{
		talismans = new String[54];
		for (int i = 0; i < 28; i++)
		{
			int x = i % 7 + 1;
			int y = i / 7 + 1;
			
			if (owner.talismans.size() <= (page - 1) * 28 + i) 
			{
				inventory.setSlot(null, calc(x, y));
			}
			else
			{
				String tali = owner.talismans.get((page - 1) * 28 + i);
				ItemStack talisman = ItemFactory.build(tali);
				ItemMeta meta = talisman.getItemMeta();
				List<String> lore = meta.getLore();
				lore.add("");
				lore.add("GOLDClick to remove from bag!");
				
				meta.setLore(StringUtils.colourList(lore));
				talisman.setItemMeta(meta);
				
				talismans[y * 9 + x] = tali;
				inventory.setSlot(talisman, calc(x, y));
			}
		}

        if (page > 1) {
            inventory.setSlot(ItemFactory.buildCustom("ARROW", "REDPrevious Page"), calc(1, 5));
        }
        if (owner.talismans.size() >= page * 28) {
            inventory.setSlot(ItemFactory.buildCustom("ARROW", "GREENNext Page"), calc(7, 5));
        }
		
		inventory.update();
	}
	
	public boolean allowClick(int clickPos, ItemStack clicked)
	{
		if (clickPos < 54)
		{
			if (talismans[clickPos] != null)
			{
				String tali = talismans[clickPos];
				owner.talismans.remove(tali);
				
				ItemStack click = clicked.clone();
				ItemFactory.update(click, owner);
				owner.giveItem(click);
				owner.playSound(Sound.BLOCK_DISPENSER_DISPENSE, 0.7, 1);
			}
			else if (clickPos == calc(1, 5) && page > 1) page--;
			else if (clickPos == calc(7, 5)) page++;
		}
		else if (clickPos >= 54 && clicked != null)
		{
			Item cli = ItemFactory.getItem(clicked);
			if (cli.type.use != ItemTypeUse.TALISMAN)
			{
				owner.sendMessage("REDOnly talismans may go in the bag!");
			}
			else
			{
				List<String> taliTags = new ArrayList<>();
				for (String talisman : owner.talismans) 
				{
					taliTags.addAll(ItemFactory.getItem(talisman).tags);
				}
				
				List<String> check = new ArrayList<>(cli.tags);
				check.removeIf((t) -> !taliTags.contains(t));
				
				if (check.size() != 0 || owner.talismans.contains(cli.id))
				{
					owner.sendMessage("REDYou have an incompatible talisman already in the bag!");
				}
				else
				{
					owner.talismans.add(ItemFactory.getItem(clicked).id);
					clicked.setAmount(clicked.getAmount() - 1);
					owner.playSound(Sound.BLOCK_DISPENSER_DISPENSE, 0.7, 1);
				}
			}
		}
		update();
		
		return false;
	}
}
