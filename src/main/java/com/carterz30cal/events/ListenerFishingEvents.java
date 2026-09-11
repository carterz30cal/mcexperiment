package com.carterz30cal.events;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.fishing.FishingArea;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class ListenerFishingEvents implements Listener
{
	@EventHandler
	public void onFish(PlayerFishEvent e)
	{
		if (e.getState() == State.CAUGHT_ENTITY)
		{
			if (e.getCaught() != null)
			{
				GameEntity entity = GameEntity.get(e.getCaught());
				if (entity instanceof GameEnemy)
				{
					EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent(
							(LivingEntity)e.getHook().getShooter(),
							e.getCaught(),
							EntityDamageEvent.DamageCause.ENTITY_ATTACK,
							DamageSource.builder(org.bukkit.damage.DamageType.PLAYER_ATTACK).build(),
							1);
					ListenerEntityDamage.Instance.onEntityDamageEntity(ev);
				}
			}
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

            if (p.area == null) {
                p.sendMessage("<red>There's nothing to catch here!");
            }
            else {
                p.bobber = FishingArea.getFishingArea(p.area.name()).getBobberUsingPower(e.getHook().getLocation(), p);
				if (p.bobber == null) {
					p.sendMessage("<red>You do not have enough fishing power to fish here!");
				}
                else p.sendMessage("<white>You've fished up a <" + p.bobber.rarity.textColor.asHexString() + "><b>" + p.bobber.rarity.name.toUpperCase() + "</b> <white>bobber!");
            }


			
		}
	}
}
