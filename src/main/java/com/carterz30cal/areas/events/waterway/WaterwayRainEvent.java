package com.carterz30cal.areas.events.waterway;

import com.carterz30cal.areas.Areas;
import com.carterz30cal.areas.events.AbstractEventWithArea;
import com.carterz30cal.areas.events.EventManager;
import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.utils.ParticleUtils;
import org.bukkit.Particle;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class WaterwayRainEvent extends AbstractEventWithArea {
    private boolean active = false;
    private long scheduledFor;
    private long duration;

    public WaterwayRainEvent() {
        name = "Waterway Downpour!";
        item = "WATER_BUCKET";
        description = "The heavens have opened up! Fight special mobs for new drops!";
        scheduledFor = EventManager.getGlobalTime() + (20 * 2);
        duration = 20 * 60 * 20;
    }

    public WaterwayRainEvent(long when, long duration) {
        this();
        name = "Waterway Downpour!";
        item = "WATER_BUCKET";
        description = "The heavens have opened up! Fight special mobs for new drops!";
        scheduledFor = when;
        this.duration = duration;
    }

    @Override
    public Areas area() {
        return Areas.WATERWAY;
    }

    @Override
    public void start() {
        active = true;
        Areas.WATERWAY.getArea().context().spawningMode = "RAIN";
    }

    @Override
    public void end() {
        duration = 0;
        Areas.WATERWAY.getArea().context().spawningMode = "NORMAL";
    }

    /**
     * @since 1.0.0
     */
    @Override
    public void tick() {
        duration--;
        for (var player : PlayerManager.getOnlinePlayers()) {
            if (player.area != area()) continue;
            ParticleUtils.spawn(player.getLocation(), Particle.RAIN, 16, 120);
        }
    }

    /**
     * Checks if this event is scheduled to happen anytime soon.
     * If so, we'll probably add it to the calendar screen.
     *
     * @return whether this event is scheduled to happen
     * @since 1.0.0
     */
    @Override
    public boolean scheduled() {
        return true;
    }

    /**
     * Is this event currently active?
     *
     * @return whether the event is active or not.
     * @since 1.0.0
     */
    @Override
    public boolean active() {
        return active;
    }

    /**
     * What tick is this event scheduled to happen on?
     *
     * @return time, as a <code>long</code> in ticks, when this event should start.
     * Returns <code>-1</code> if this event is not scheduled.
     * @since 1.0.0
     */
    @Override
    public long happening() {
        return scheduledFor;
    }

    /**
     * How long will this event last?
     *
     * @return time, as a <code>long</code> in ticks, that this event will last for. Returns <code>0</code> if this event hasn't yet started.
     * @since 1.0.0
     */
    @Override
    public long duration() {
        return duration;
    }
}
