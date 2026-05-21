package com.carterz30cal.entities.enemies.representation;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.main.Dungeons;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.carterz30cal.entities.enemies.core.GameEnemy.keyEnemy;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class EnemyRepresentation {
    public Map<EnemyRepresentationData, Entity> entities = new HashMap<>();
    public GameEnemy owner;
    private double tallestPoint = -1;

    public void tick(Location baseLocation) {
        for (var e : entities.entrySet()) {
            var offset = e.getKey().offset.clone();
            var yaw = baseLocation.getYaw() * Math.PI / 180;
            var ox = (offset.getX() * Math.cos(yaw)
                    + (offset.getZ() * Math.sin(yaw)));
            var oz = (-offset.getX() * Math.sin(yaw)
                    + (offset.getZ() * Math.cos(yaw)));
            var adjustedOffset = new Vector(ox, offset.getY(), oz);
            e.getValue().teleportAsync(baseLocation.clone().add(adjustedOffset)).thenAccept(success -> {

            });
        }
    }

    public double getTallestPoint() {
        if (tallestPoint == -1) {
            for (var e : entities.entrySet()) {
                tallestPoint = Math.max(tallestPoint, e.getKey().offset.getY() + e.getValue().getHeight());
            }
            if (tallestPoint == -1) {
                throw new IllegalStateException("Tried to calculate tallest point with an empty representation!");
            }
        }
        return tallestPoint;
    }

    public void damage() {
        for (var e : entities.entrySet()) {
            e.getValue().broadcastHurtAnimation((Collection<Player>) Dungeons.instance.getServer().getOnlinePlayers());
        }
    }

    public void kill() {
        for (var e : entities.values()) {
            if (e instanceof LivingEntity livingEntity) {
                livingEntity.setHealth(0);
            }
            else {
                e.remove();
            }
        }
    }

    public void remove() {
        for (var e : entities.values()) {
            e.remove();
        }
    }

    public void register(GameEntity entity) {
        for (var e : entities.values()) {
            e.getPersistentDataContainer().set(keyEnemy, PersistentDataType.STRING, entity.getUUID().toString());
        }
    }
}
