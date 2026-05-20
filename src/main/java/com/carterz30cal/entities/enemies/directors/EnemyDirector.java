package com.carterz30cal.entities.enemies.directors;

import com.carterz30cal.entities.LocatableEntity;
import com.carterz30cal.entities.TargetableEntity;
import com.carterz30cal.entities.enemies.directors.behaviour.TargetingBehaviour;
import com.carterz30cal.main.Dungeons;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.EquipmentSlotGroup;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class EnemyDirector implements LocatableEntity, TargetableEntity {
    private static final NamespacedKey KEY_SPEED = new NamespacedKey(Dungeons.instance, "speed");
    private final Mob directingEntity;
    private TargetingBehaviour behaviour;
    private double knockbackResistance;

    public EnemyDirector(Mob director, TargetingBehaviour behaviour, double knockbackResistance) {
        this.directingEntity = director;
        this.behaviour = behaviour;
        this.knockbackResistance = knockbackResistance;
    }

    public void setTargetingBehaviour(TargetingBehaviour behaviour) {
        this.behaviour = behaviour;
    }

    public void tick() {
        var target = getTarget();
        if (target == null) {
            setTarget(behaviour.findTarget(directingEntity.getLocation()));
        }
    }

    public void remove() {
        directingEntity.remove();
    }

    public LivingEntity getTarget() {
        return directingEntity.getTarget();
    }

    public void setTarget(LivingEntity target) {
        directingEntity.setTarget(target);
    }

    public void setSpeed(double speed) {
        var scaleAttribute = directingEntity.getAttribute(Attribute.MOVEMENT_SPEED);
        if (scaleAttribute != null) {
            scaleAttribute.removeModifier(KEY_SPEED);
            scaleAttribute.addModifier(
                    new AttributeModifier(KEY_SPEED,
                            speed - 1,
                            AttributeModifier.Operation.MULTIPLY_SCALAR_1,
                            EquipmentSlotGroup.ANY)
            );
        }
    }

    public void setKnockbackResistance(int knockbackResistance) {
        this.knockbackResistance = knockbackResistance / 100D;
    }

    public void knockback(double strength, Location self, Location target) {
        double dirX = target.getX() - self.getX();
        double dirZ = target.getZ() - self.getZ();
        double knockback = knockbackResistance * strength;
        if (knockback > 0) {
            directingEntity.knockback(knockback, dirX, dirZ);
        }
    }

    @Override
    public Location getLocation() {
        return directingEntity.getLocation();
    }

    @Override
    public LivingEntity getTargetableEntity() {
        return directingEntity;
    }
}
