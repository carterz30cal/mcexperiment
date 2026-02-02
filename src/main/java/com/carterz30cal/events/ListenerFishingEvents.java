package com.carterz30cal.events;

import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.PlayerManager;
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
                p.sendMessage("REDThere's no fish to catch!");
            }
            else {
//                if (p.bobber != null && !p.bobber.isCancelled()) {
//                    p.sendMessage("REDReplacing current bobber!");
//                    p.bobber.remove();
//                }
                FishingArea.FishingBobber bobber = FishingArea.getFishingArea(p.area.name()).getBobberUsingPower(e.getHook().getLocation(), p);
                p.bobber = bobber;
                p.sendMessage("You've fished up a " + p.bobber.rarity.colour + "BOLD" + p.bobber.rarity.name.toUpperCase() + " WHITEBobber!");
            }


			
		}
	}
}
