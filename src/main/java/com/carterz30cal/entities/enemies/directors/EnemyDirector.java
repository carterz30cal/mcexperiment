package com.carterz30cal.entities.enemies.directors;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.LocatableEntity;
import com.carterz30cal.entities.TargetableEntity;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.enemies.directors.behaviour.TargetingBehaviour;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.EntityUtils;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import static com.carterz30cal.entities.enemies.core.GameEnemy.keyEnemy;

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

    public void register(GameEntity entity) {
        var uuid = entity.getUUID();
        directingEntity.getPersistentDataContainer().set(keyEnemy, PersistentDataType.STRING, uuid.toString());
    }

    public void setTargetingBehaviour(TargetingBehaviour behaviour) {
        this.behaviour = behaviour;
    }

    public void tick(GameEnemy owner) {
        var target = GameEntity.get(getTarget());
        if (target == null || target.dead) {
            var attempt = behaviour.findTarget(owner, getLocation());
            if (attempt == null) {
                EntityUtils.applyPotionEffect(directingEntity, PotionEffectType.SLOWNESS, 19, 50, false);
                setTarget(null);
            }
            else {
                setTarget(attempt);
            }
        }
        else {
            if (!target.isTargetable(owner)) {
                setTarget(null);
            }
            else {
                if (target instanceof TargetableEntity targetable) {
                    setTarget(targetable.getTargetableEntity());
                }
//                if (target instanceof GamePlayer) {
//                    type.onTarget(this, (GamePlayer) target);
//                }
            }
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
