package com.carterz30cal.gui;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.Item;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.ItemType;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class QuiverGUI extends AbstractGUI
{
    public Item[] items;
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
        items = new Item[54];
		
		int a = 0;

        var sorted = owner.quiver2.sorted(ItemType.ARROW);
        for (var item : sorted) {
            int x = a % 7 + 1;
            int y = a / 7 + 1;
            items[calc(x, y)] = item;
            inventory.setSlot(ItemFactory.build(item.id, Math.min((int) owner.quiver2.get(item), 64)), calc(x, y));
            a++;
        }
		
		inventory.update();
	}

}
