package com.carterz30cal.gui;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

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

            var display = ItemFactory.customItem(arrow,
                    Component.text()
                            .append(
                                    Component.text(owner.quiver.get(arrow) + "x ", NamedTextColor.WHITE)
                            )
                            .append(ItemFactory.getItemNameBuilder(arrow))
            );
			arrows[y * 9 + x] = arrow;

            inventory.setSlot(display, calc(x, y));
			a++;
		}
		
		inventory.update();
	}

}
