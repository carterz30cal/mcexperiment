package com.carterz30cal.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.Arrays;

public class ListenerBlockEvents implements Listener 
{
	@EventHandler
	public void onDamageBlock(BlockDamageEvent e)
	{
		System.out.println("blocc");
	}

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        Arrays.stream(e.getChunk().getEntities()).iterator().forEachRemaining(Entity::remove);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        Arrays.stream(e.getChunk().getEntities()).iterator().forEachRemaining(Entity::remove);
    }
}
