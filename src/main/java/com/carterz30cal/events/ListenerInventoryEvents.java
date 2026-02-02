package com.carterz30cal.events;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.gui.MenuGUI;
import com.carterz30cal.items.Item;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.ItemType;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

public class ListenerInventoryEvents implements Listener
{
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		GamePlayer p = (GamePlayer)GameEntity.get(e.getWhoClicked());
		if (p != null && e.getSlot() == 8 && p.player.getGameMode() == GameMode.SURVIVAL && e.getClickedInventory() == p.player.getInventory()
				) {
			if (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.RIGHT) p.openGui(new MenuGUI(p));
			e.setCancelled(true);
			return;
		}
		else if ((p.gui != null && e.getClick() == ClickType.NUMBER_KEY) || e.getRawSlot() == -999)
		{
			e.setCancelled(true);
			return;
		}
        if (p.gui == null) {
            if (e.getSlotType() == InventoryType.SlotType.ARMOR && e.getRawSlot() == 5) {
                ItemStack cursor = e.getCursor();
                ItemStack place = e.getCurrentItem();
                if (cursor == null || cursor.getType() == Material.AIR) {
                    return;
                }
                if (e.isShiftClick()) {
                    return;
                }
                Item cursorItem = ItemFactory.getItem(cursor);
                if (cursorItem == null || cursorItem.type != ItemType.HELMET) {
                    return;
                }
                e.setCancelled(true);
                e.setCurrentItem(cursor);
                e.getWhoClicked().setItemOnCursor(place);
            }
        }
		if (p.gui == null || e.isCancelled() || e.getClick() == ClickType.DOUBLE_CLICK) return;

		if (e.isLeftClick()) e.setCancelled(!p.gui.allowLeftClick(e.getRawSlot(), e.getCurrentItem()));
		else if (e.isRightClick()) e.setCancelled(!p.gui.allowRightClick(e.getRawSlot(), e.getCurrentItem()));
		else e.setCancelled(!p.gui.allowClick(e.getRawSlot(), e.getCurrentItem()));
	}
	
	@EventHandler
	public void onInventoryDrag(InventoryDragEvent e)
	{
		GamePlayer p = (GamePlayer)GameEntity.get(e.getWhoClicked());
		if (p == null || p.gui == null) return;
		
		e.setCancelled(!p.gui.allowDrag());
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e)
	{
		GamePlayer p = (GamePlayer)GameEntity.get(e.getPlayer());
		if (p == null || p.gui == null || p.flagIgnoreInvClose) return;
		
		p.gui.onClose();
		p.gui = null;
	}
}
