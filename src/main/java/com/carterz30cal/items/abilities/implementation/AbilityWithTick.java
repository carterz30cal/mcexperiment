package com.carterz30cal.items.abilities.implementation;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.main.Dungeons;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Provides a cleaner way to interact with ticks using abilities implementing
 * <code>RegisterableAbility</code>.
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public interface AbilityWithTick extends RegisterableAbility {
    /**
     * Automatically registered as part of this interface using default methods. Overriding either <code>register()</code>
     * or <code>deregister()</code> will break this guarantee. Called every game tick.
     *
     * @param context the context we're using
     * @param tick the tick we're on
     * @since 1.0.0 [1]
     */
    void tick(ContextWithAbility<? extends GameEntity> context, int tick);

    /**
     * @implNote we register a <code>BukkitRunnable</code> that calls <code>tick()</code>
     * with the appropriate context.
     */
    @Override
    default void register(ContextWithAbility<? extends GameEntity> context) {
        var runnable = new BukkitRunnable() {
            private int tick = 0;
            @Override
            public void run() {
                tick(context, tick);
                tick++;
            }
        };
        runnable.runTaskTimer(Dungeons.instance, 0, 1);
        TickingManager.register(context.getOwner(), runnable);
    }

    /**
     * @implNote we deregister the existing <code>BukkitRunnable</code>, if one can be found.
     */
    @Override
    default void deregister(ContextWithAbility<? extends GameEntity> context) {
        var tick = TickingManager.get(context.getOwner());
        if (tick == null) return;
        tick.cancel();
        TickingManager.deregister(context.getOwner());
    }
}
