package com.carterz30cal.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.util.Vector;

import com.carterz30cal.entities.EnemyManager;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.entities.enemies.EnemyTypeFish;
import com.carterz30cal.utils.RandomUtils;


public class ListenerFishingEvents implements Listener
{
	@EventHandler
	public void onFish(PlayerFishEvent e)
	{
		if (e.getState() == State.CAUGHT_ENTITY)
		{
			e.getHook().remove();
		}
		else if (e.getState() == State.FISHING)
		{
			e.getHook().setMinWaitTime(40);
			e.getHook().setMaxWaitTime(60);
		}
		else if (e.getState() == State.CAUGHT_FISH)
		{
			GamePlayer p = PlayerManager.players.get(e.getPlayer().getUniqueId());
			e.getCaught().remove();
			
			e.setExpToDrop(0);
			int a = RandomUtils.getRandom(1, 5);
			String fishUp = RandomUtils.getChoice(EnemyTypeFish.brackets.get(p.getFishingBracket()));
			while (a > 0) {
				
				GameEnemy hooked = EnemyManager.spawn(fishUp, e.getHook().getLocation());
				
				Vector vel = hooked.getLocation().subtract(p.getLocation()).toVector().normalize();
				vel.multiply(p.getLocation().distance(hooked.getLocation()) * -0.3);
				vel.setY(vel.getY() * 1.8);
				
				hooked.getMain().setVelocity(vel);
				a--;
			}
			
			
		}
	}
}
