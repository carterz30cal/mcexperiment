package com.carterz30cal.entities.enemies.representation;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.EntityUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 3
 * @since 1.0.0
 */
public class EnemyInformationDisplay extends GameEntity {
    private static final double TEXT_GAP = 0.25;
    private final RepresentedEntity owner;
    private final List<TextDisplay> displays = new ArrayList<>();
    private final List<Component> components = new ArrayList<>();
    private final List<Boolean> updated = new ArrayList<>();

    public EnemyInformationDisplay(RepresentedEntity owner) {
        this.owner = owner;
    }

    public void reset() {
        updated.clear();
        components.clear();
    }

    public void setLine(int index, @Nullable Component component) {
        while (components.size() <= index) {
            components.add(null);
            updated.add(false);
        }
        components.set(index, component);
        updated.set(index, true);
    }

    public void setLine(int index, @NotNull String component) {
        while (components.size() <= index) {
            components.add(null);
            updated.add(false);
        }
        components.set(index, MiniMessage.miniMessage().deserialize(component));
        updated.set(index, true);
    }


    public void removeLine(int index) {
        components.remove(index);
        if (displays.size() > index) {
            displays.get(index).remove();
            displays.remove(index);
        }
        updated.remove(index);
    }

    public void tick() {
        for (int i = 0; i < updated.size(); i++) {
            if (!updated.get(i)) {
                removeLine(i);
            }
        }
        while (displays.size() < components.size()) {
            var text = EntityUtils.spawnTextHologram(owner.getLocation(), -1);
            assert text != null;
            text.setTeleportDuration(1);
            text.setBillboard(Display.Billboard.CENTER);
            displays.add(text);
        }
        for (int i = 0; i < components.size(); i++) {
            var display = displays.get(i);
            display.text(components.get(i));

            var y = (TEXT_GAP * (components.size() - i)) + owner.getRepresentation().getTallestPoint();
            var location = owner.getLocation().clone().add(0, y + TEXT_GAP, 0);
            location.setPitch(0);
            display.teleportAsync(location);
        }
    }

    public void display(GamePlayer viewer, boolean visible) {
        for (var display : displays) {
            if (visible) {
                viewer.showEntity(display);
            }
            else {
                viewer.hideEntity(display);
            }
        }
    }

    public void remove(boolean force) {
        if (force) {
            for (var display : displays) {
                display.remove();
            }
            displays.clear();
            reset();
        }
        else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (var display : displays) {
                        display.remove();
                    }
                }
            }.runTaskLater(Dungeons.instance, 20 * 2);
        }
    }

    /**
     * Check for world validity
     *
     * @return <code>true</code> if all entities making up this <code>GameEntity</code> are valid, or if there are no entities; <code>false</code> otherwise.
     * @since 1.0.0 [3]
     */
    public boolean valid() {
        for (var display : displays) {
            if (!display.isValid() || display.isDead()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void remove() {
        remove(true);
    }

    @Override
    public Location getLocation() {
        return owner.getLocation();
    }
}
