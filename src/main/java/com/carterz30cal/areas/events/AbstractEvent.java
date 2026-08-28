package com.carterz30cal.areas.events;

import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.utils.StringUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * Any event that happens in the game world.<br>
 * Does not require a save state, as events do not persist beyond server shutdown.<br>
 *
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public abstract class AbstractEvent {
    protected String name;
    protected String item;
    protected String description;
    /**
     * Should be called on the tick that the event is scheduled to begin, or when a specific
     * condition is met that should start the event.
     * @since 1.0.0
     */
    public abstract void start();

    /**
     * Should be called on the tick that the event ends, whether by a scheduled end time or
     * if a specific ending condition is met i.e. if an event boss mob is killed.
     * @since 1.0.0
     */
    public abstract void end();

    /**
      * @since 1.0.0
      */
    public abstract void tick();
    /**
     * Checks if this event is scheduled to happen anytime soon.
     * If so, we'll probably add it to the calendar screen.
     * @return whether this event is scheduled to happen
     * @since 1.0.0
     */
    public abstract boolean scheduled();

     /**
      * Is this event currently active?
      * @return whether the event is active or not.
      * @since 1.0.0
      */
    public abstract boolean active();

    /**
     * What tick is this event scheduled to happen on?
     * @return time, as a <code>long</code> in ticks, when this event should start.
     * Returns <code>-1</code> if this event is not scheduled.
     * @since 1.0.0
     */
    public abstract long happening();

    /**
     * How long will this event last?
     * @return time, as a <code>long</code> in ticks, that this event will last for.
     * @since 1.0.0
     */
    public abstract long duration();

    public ItemStack calendar() {
        var split = StringUtils.wrapText("<grey>" + description, 26);
        split.addFirst("");
        if (active()) {
            split.addFirst("<dark_grey>This event will last for another<aqua>"
                    + StringUtils.getPrettyTime(duration()) + ".");
        }
        else {
            split.addFirst("<dark_grey>It will last for<aqua>" + StringUtils.getPrettyTime(duration()) + ".");
            if (scheduled()) {
                split.addFirst("<dark_grey>Event starts in<dark_purple>"
                        + StringUtils.getPrettyTime(happening() - EventManager.getGlobalTime()) + ".");
            }
        }
        return ItemFactory.customItem(
                item,
                name,
                split
        );
    }
}
