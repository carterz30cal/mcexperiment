package com.carterz30cal.events;

import com.carterz30cal.entities.GameEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.world.ChunkLoadEvent;

public class ListenerBlockEvents implements Listener 
{
	@EventHandler
	public void onDamageBlock(BlockDamageEvent e)
	{
		System.out.println("blocc");
	}

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        for (Entity entity : e.getChunk().getEntities()) {
            var rep = GameEntity.get(entity);
            if (rep == null) {
                entity.remove();
            }
        }
    }
}
