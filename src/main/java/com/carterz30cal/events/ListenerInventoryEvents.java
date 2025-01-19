package com.carterz30cal.events;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
//import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.gui.MenuGUI;
import com.carterz30cal.items.ItemFactory;

public class ListenerInventoryEvents implements Listener
{
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		GamePlayer p = (GamePlayer)GameEntity.get(e.getWhoClicked());
		if (p != null && e.getSlot() == 8 && p.player.getGameMode() == GameMode.SURVIVAL) {
			p.openGui(new MenuGUI(p));
			e.setCancelled(true);
			return;
		}
		else if ((p.gui != null && e.getClick() == ClickType.NUMBER_KEY) || e.getRawSlot() == -999)
		{
			e.setCancelled(true);
			return;
		}
		if (p == null || p.gui == null || e.isCancelled() || e.getClick() == ClickType.DOUBLE_CLICK) return;
		
		e.setCancelled(!p.gui.allowClick(e.getRawSlot(), e.getCurrentItem()));
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
