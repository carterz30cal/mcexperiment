package com.carterz30cal.entities.display;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.LocatableEntity;
import com.carterz30cal.items.ItemFactory;
import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class GameItemDisplay extends GameEntity implements LocatableEntity {
    protected final ItemDisplay display;

    public GameItemDisplay(String item, Location location) {
        display = location.getWorld().spawn(location, ItemDisplay.class);
        display.setItemStack(ItemFactory.build(item));
        register(display.getUniqueId());
    }


    @Override
    public void remove() {
        display.remove();
        deregister(display.getUniqueId());
    }

    @Override
    public Location getLocation() {
        return display.getLocation();
    }

    @Override
    public void teleport(@NotNull Location location) {
        display.teleport(location);
    }
}
