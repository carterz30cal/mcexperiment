package com.carterz30cal.areas.events;

import com.carterz30cal.areas.Areas;

/**
 * Extends the abstract event class with an area that owns the event.
 * Is used to determine if events should be shown in the calendar, as well as
 * altering effects that may only happen to players within the area.
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public abstract class AbstractEventWithArea extends AbstractEvent {
    /**
     * Gets the area that owns this event.
     * @return a <code>Areas</code> enum object.
     * @since 1.0.0
     */
    public abstract Areas area();
}
