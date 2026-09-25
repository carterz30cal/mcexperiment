package com.carterz30cal.areas.events;

import org.jetbrains.annotations.Nullable;

/**
 * Adds events to the server state.
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public interface EventProvider {
    /**
     * Get the next event that should be registered.
     * @return the next event, or <code>null</code> if there isn't one.
     * @since 1.0.0
     */
    @Nullable
    AbstractEvent next();

    /**
     * Gets the absolute maximum time that this provider can go before requiring
     * another poll. This is not guaranteed to fire at an exact time, as similar
     * providers may be bucketed together, so providers will need to account for this.
     * Managers should check this value after registering the last event, but are not required
     * to, so the value once again cannot be relied upon to be anything other than an upper bound.
     * @return a maximum time, in ticks, that this provider can sleep before needing polling.
     * @since 1.0.0
     */
    int pollDuration();
}
