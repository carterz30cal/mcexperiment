package com.carterz30cal.gui;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.Item;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.ItemRarity;
import com.carterz30cal.items.ItemTypeUse;
import org.bukkit.inventory.ItemStack;

/**
 * The very first admin menu. Quite outdated at this point but it
 * works. At some point I'll refactor it to include enchanted books
 * and probably some common permutations of items for quicker testing.
 *
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class AdminItemGUI extends AbstractGUI 
{
	public int page = 1;
	public boolean showUnobtainable = false;
	
	public boolean nextPageAvailable = true;
	
	public AdminItemGUI(GamePlayer owner) {
		super(owner);
		
		inventory = new GooeyInventory("Items" + page, 6);
		inventory.initUsingTemplate(GooeyTemplate.SHOPPY);
		update();
	}
	
	private void update()
	{

        inventory.setSlot(ItemFactory.customItem("BARRIER", "<grey>Show Unobtainable Items: " +
                (showUnobtainable ? "<green>Yes</green></grey>" : "<red>No</green></grey>")), calc(4, 5));
		
		nextPageAvailable = true;
		int i = 0;
		int pi = 1;
		while (pi < page)
		{
			int it = 0;
			while (it < 28)
			{
				if (i >= ItemFactory.items.size()) return;
				Item current = ItemFactory.getItem(ItemFactory.getItemList(i));
				
				i++;
                if (current.type.use != ItemTypeUse.VIRTUAL_NON_EXIST) {
                    if (showUnobtainable || ((current.rarity != ItemRarity.UNOBTAINABLE) && (current.rarity != ItemRarity.MYSTERIOUS))) {
                        it++;
                    }
                }
            }
			pi++;
		}
		
		for (int p = 0; p < 28; i++)
		{
			int x = (p % 7) + 1;
			int y = (p / 7) + 1;
			
			if (i >= ItemFactory.items.size()) 
			{
				p++;
				inventory.setSlot(null, calc(x, y));
				nextPageAvailable = false;
			}
			else
			{
				Item current = ItemFactory.getItem(ItemFactory.getItemList(i));
                if (current.type.use != ItemTypeUse.VIRTUAL_NON_EXIST) {
                    if (showUnobtainable || (current.rarity != ItemRarity.UNOBTAINABLE && current.rarity != ItemRarity.MYSTERIOUS)) {
                        p++;
                        inventory.setSlot(ItemFactory.build(current), calc(x, y));
                    }
                }
			}
		}
		//i++;
		if (i == ItemFactory.items.size()) nextPageAvailable = false;

        if (page > 1) {
            inventory.setSlot(ItemFactory.customItem("ARROW", "<red>Page " + (page - 1)), calc(1, 5));
        }
        else {
            inventory.setSlot(ItemFactory.customItem("WHITE_STAINED_GLASS_PANE", " "), calc(1, 5));
        }

        if (nextPageAvailable) {
            inventory.setSlot(ItemFactory.customItem("ARROW", "<green>Page " + (page + 1)), calc(7, 5));
        }
        else {
            inventory.setSlot(ItemFactory.customItem("WHITE_STAINED_GLASS_PANE", " "), calc(7, 5));
        }
		
		inventory.update();
	}

	public boolean allowClick(int clickPos, ItemStack clicked)
	{
		if (clickPos == calc(1, 5) && page > 1) page--;
		else if (clickPos == calc(7, 5) && nextPageAvailable) page++;
		else if (clickPos == calc(4, 5)) showUnobtainable = !showUnobtainable;
		else if (clickPos < 54 && clickPos % 9 != 0 && clickPos % 9 != 8 && clickPos / 9 != 0 && clickPos / 9 != 5) owner.giveItem(clicked);
		
		update();
		return false;
	}
	
}
