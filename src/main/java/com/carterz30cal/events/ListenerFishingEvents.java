package com.carterz30cal.events;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.health.damage.DamageSource;
import com.carterz30cal.entities.health.damage.handlers.AggressiveEntity;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.fishing.FishingArea;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.EntityUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author carterz30cal
 * @version 3
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
                var attacker = GameEntity.get((Entity) e.getHook().getShooter());
				GameEntity entity = GameEntity.get(e.getCaught());
                if (entity instanceof GameEnemy caught && attacker instanceof GamePlayer aggro)
				{
                    ListenerEntityDamage.handleEntityDamageEntity(caught, aggro, DamageSource.FISHING_ROD);
                    EntityUtils.applyKnockback(aggro, caught.getTargetableEntity(), -125);
                    e.setCancelled(caught.isAlive());
				}
			}
		}
		else if (e.getState() == State.FISHING)
		{
            e.getHook().setMinWaitTime(10);
            e.getHook().setMaxWaitTime(50);
            new BukkitRunnable() {
                private final FishHook hook = e.getHook();
                private final AggressiveEntity shooter = (AggressiveEntity) GameEntity.get(e.getPlayer());

                public void run() {
                    if (hook.isValid()) {
                        if (hook.getHookedEntity() != null && hook.getHookedEntity().isValid()) {
                            return;
                        }
                        var nearby = EntityUtils.getNearbyDamageableEntities(hook.getLocation(), 0.7);
                        for (var n : nearby) {
                            if (n.isDamageable(shooter)) {
                                ListenerEntityDamage.handleEntityDamageEntity(n, shooter, DamageSource.FISHING_ROD);
                                if (n.isAlive()) {
                                    hook.setHookedEntity(n.getTargetableEntity());
                                    break;
                                }
                            }
                        }
                    }
                    else {
                        cancel();
                    }
                }
            }.runTaskTimer(Dungeons.instance, 0, 1);
        }
        else if (e.getState() == State.CAUGHT_FISH && e.getCaught() != null)
		{
			GamePlayer p = PlayerManager.players.get(e.getPlayer().getUniqueId());
			e.getCaught().remove();
			e.setExpToDrop(0);

            if (p.area == null) {
                p.sendMessage("<red>There's nothing to catch here!");
            }
            else if (p.bobber != null) {
                p.sendMessage("<red>Kill everything from your current bobber first!");
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
