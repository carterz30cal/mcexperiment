package com.carterz30cal.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class ListenerLootDrop implements Listener
{
	@EventHandler
	public void onLoot(EntityDeathEvent e)
	{
		e.getDrops().clear();
		e.setDroppedExp(0);
	}
}
