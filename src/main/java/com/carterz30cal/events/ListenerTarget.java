package com.carterz30cal.events;

import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.GameSummon;
import com.carterz30cal.entities.player.GamePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

public class ListenerTarget implements Listener {

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        GameEntity entity = GameEntity.get(event.getEntity());
        GameEntity target = GameEntity.get(event.getTarget());
        if (entity instanceof GameSummon) {

        }
        else if (entity instanceof GameEnemy) {
            if (target instanceof GameSummon || target instanceof GamePlayer) {
                // we cool
            }
            else {
                event.setCancelled(true);
            }
        }
        else {
            event.setCancelled(true);
        }
    }
}
