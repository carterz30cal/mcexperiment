package com.carterz30cal.gui;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import org.bukkit.inventory.ItemStack;

public class QuiverGUI extends AbstractGUI
{
	public String[] arrows;
	public QuiverGUI(GamePlayer owner) {
		super(owner);
		
		inventory = new GooeyInventory("Quiver", 6);
		inventory.initUsingTemplate(GooeyTemplate.SHOPPY);
		
		update();
	}
	
	public void update()
	{
		arrows = new String[54];
		
		int a = 0;
		for (String arrow : owner.quiver.keySet())
		{
			int x = a % 7 + 1;
			int y = a / 7 + 1;

            ItemStack disp = ItemFactory.buildCustom(arrow, "WHITE" + owner.quiver.get(arrow) + "x " + ItemFactory.getItemTypeName(arrow));
			arrows[y * 9 + x] = arrow;
			
			inventory.setSlot(disp, calc(x, y));
			a++;
		}
		
		inventory.update();
	}

}
