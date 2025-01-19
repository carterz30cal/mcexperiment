package com.carterz30cal.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.carterz30cal.entities.PlayerManager;
import org.bukkit.event.player.PlayerQuitEvent;

public class ListenerPlayerJoinLeave implements Listener 
{
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		PlayerManager.instance.registerPlayer(e.getPlayer());
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		PlayerManager.savePlayer(PlayerManager.players.get(e.getPlayer().getUniqueId()));
	}
}
