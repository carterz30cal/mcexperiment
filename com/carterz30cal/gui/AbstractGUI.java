package com.carterz30cal.gui;

import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.carterz30cal.entities.GamePlayer;

public class AbstractGUI extends BukkitRunnable
{
	public final GamePlayer owner;
	public GooeyInventory inventory;
	
	public AbstractGUI(GamePlayer owner)
	{
		this.owner = owner;
	}
	
	public boolean allowClick(int clickPos, ItemStack clicked)
	{
		return false;
	}
	
	public boolean allowDrag()
	{
		return false;
	}
	
	public void onTick()
	{
		
	}
	
	public void onClose()
	{
		
	}
	
	public void onOpen()
	{
		
	}
	
	public final void open()
	{
		onOpen();
		inventory.open(owner);
	}

	@Override
	public final void run()
	{
		if (owner.gui != this)
		{
			cancel();
		}
		else onTick();
	}
	
	protected final int calc(int x, int y)
	{
		return y * 9 + x;
	}
}
