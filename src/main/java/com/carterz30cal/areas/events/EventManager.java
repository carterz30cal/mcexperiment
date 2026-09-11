package com.carterz30cal.areas.events;

import com.carterz30cal.main.Dungeons;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class EventManager extends BukkitRunnable {
    private static final List<EventPool> eventPools = new ArrayList<>();
    private static final List<AbstractEvent> events = new ArrayList<>();
    private static long globalTick = 0;


    /**
     * Register an <code>EventProvider</code> so that we may begin
     * scheduling events. Calls <code>provider.pollDuration()</code> to figure
     * out polling frequency.
     * @param provider the <code>EventProvider</code> we're registering
     * @since 1.0.0
     * @implNote we're using <code>Math.log10()</code> to determine pools in this implementation. <br>
     *  This doesn't consider that pools may jump up in polling, so some may be mismatched over time!
     */
    public static void register(
            @NotNull EventProvider provider) {
        var polling = provider.pollDuration();
        var log = (int)Math.log10(polling);

        while (eventPools.size() <= log) eventPools.add(new EventPool());
        var pool = eventPools.get(log);
        pool.register(provider);
    }

    /**
     * Register an <code>AbstractEvent</code> to be managed. This event may be inactive at time
     * of registration, or it may start immediately.
     * @param event an <code>AbstractEvent</code> that we want fired at some point.
     * @since 1.0.0
     */
    public static void register(@NotNull AbstractEvent event) {
        events.add(event);
    }

    /**
     * Gets the global time on this instance.
     * @return the global time, in ticks, since the server was opened.
     * @since 1.0.0
     */
    public static long getGlobalTime() {
        return globalTick;
    }

    /**
     * @return all scheduled events.
     * @since 1.0.0
     */
    public static Stream<AbstractEvent> getScheduledEvents() {
        return events.stream().filter(AbstractEvent::scheduled);
    }

    @Override
    public void run() {
        globalTick++;
        var removals = new ArrayList<AbstractEvent>();
        for (var event : events) {
            if (event.active()) {
                if (event.duration() <= 0) {
                    event.end();
                    removals.add(event);
                }
                else event.tick();
            }
            else {
                if (!event.scheduled()) continue;
                if (event.happening() <= getGlobalTime()) {
                    event.start();
                }
            }
        }
        events.removeAll(removals);
        for (var pool : eventPools) {
            pool.time--;
            if (pool.time < 1) pool.activate();
        }
    }

    public EventManager() {
        runTaskTimer(Dungeons.instance, 1, 1);
    }

    /**
     * @author carterz30cal
     * @version 2
     * @since 1.0.0
     */
    private static class EventPool {
        public int time;
        private final List<EventProvider> providers = new ArrayList<>();

        private void activate() {
            for (var provider : providers) {
                var next = provider.next();
                if (next != null) {
                    EventManager.register(next);
                }
            }
            update();
        }

        private void update() {
            int min = Integer.MAX_VALUE;
            for (EventProvider provider : providers) {
                min = Math.min(min, provider.pollDuration());
            }
            time = min;
        }

        /**
         * Registers an <code>EventProvider</code> with the pool, for scheduling.
         * @param provider the <code>EventProvider</code> we're adding.
         * @since 1.0.0
         */
        public void register(EventProvider provider) {
            providers.add(provider);
            activate();
        }

    }
}
