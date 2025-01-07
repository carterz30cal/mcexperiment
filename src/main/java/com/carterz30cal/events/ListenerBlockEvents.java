package com.carterz30cal.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;

public class ListenerBlockEvents implements Listener 
{
	@EventHandler
	public void onDamageBlock(BlockDamageEvent e)
	{
		System.out.println("blocc");
	}
}
