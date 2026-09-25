package com.carterz30cal.entities.display;

import com.carterz30cal.main.Dungeons;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class AnimatedGameItemDisplay extends GameItemDisplay {
    private final BukkitRunnable ticker;
    protected double rotationSpeed;
    protected double bobSpeed;
    protected Location baseLocation;

    public AnimatedGameItemDisplay(String item, Location location) {
        super(item, location);
        baseLocation = location;
        display.setTeleportDuration(1);
        ticker = new BukkitRunnable() {
            int clock = 0;

            @Override
            public void run() {
                tick(clock++);
            }
        };
        ticker.runTaskTimer(Dungeons.instance, 0, 1);
    }

    public AnimatedGameItemDisplay setRotationSpeed(double degreesPerSecond) {
        rotationSpeed = degreesPerSecond;
        return this;
    }

    public AnimatedGameItemDisplay setBobSpeed(double degreesPerSecond) {
        bobSpeed = degreesPerSecond;
        return this;
    }

    @Override
    public void remove() {
        super.remove();
        ticker.cancel();
    }

    protected void tick(int clock) {
        display.teleport(baseLocation.clone().add(0,
                        Math.sin(Math.toRadians(bobSpeed * (clock / 20D))) * 0.25,
                        0
                ).setRotation((float) (rotationSpeed * (clock / 20D)), 0)
        );
    }

    @Override
    public void teleport(@NotNull Location location) {
        super.teleport(location);
        baseLocation = location;
    }

    @Override
    public Location getLocation() {
        return baseLocation;
    }
}
