package com.carterz30cal.entities.enemies.directors;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.LocatableEntity;
import com.carterz30cal.entities.TargetableEntity;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.enemies.directors.behaviour.TargetingBehaviour;
import com.carterz30cal.entities.health.damage.handlers.AggressiveEntity;
import com.carterz30cal.entities.player.summons.GameSummon;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.EntityUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.AbstractSkeleton;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import static com.carterz30cal.entities.enemies.core.GameEnemy.keyEnemy;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class EnemyDirector implements LocatableEntity, TargetableEntity {
    private static final NamespacedKey KEY_SPEED = new NamespacedKey(Dungeons.instance, "speed");
    private Mob directingEntity;
    private EntityType directorType = EntityType.ZOMBIE;
    private TargetingBehaviour behaviour;
    private double knockbackResistance;
    private Location cachedLocation;
    private double speed = 1;

    public EnemyDirector(Location location, double knockbackResistance) {
        this.knockbackResistance = knockbackResistance / 100D;
        this.cachedLocation = location;
    }

    public void setDirectorType(EntityType directorType) {
        this.directorType = directorType;
    }

    public void register(GameEntity entity) {
        var uuid = entity.getUUID();
        directingEntity.getPersistentDataContainer().set(keyEnemy, PersistentDataType.STRING, uuid.toString());
    }

    public void setTargetingBehaviour(TargetingBehaviour behaviour) {
        this.behaviour = behaviour;
    }

    public void createDirector() {
        var mob = (Mob) Dungeons.w.spawnEntity(cachedLocation, directorType, false);
        //mob.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        mob.setVisibleByDefault(false);
        mob.setSilent(true);
        if (mob instanceof AbstractSkeleton skeleton) {
            skeleton.getEquipment().setItemInMainHand(new ItemStack(Material.BOW), true);
        }
        //mob.setRemoveWhenFarAway(false);
        directingEntity = mob;
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
            if (target instanceof TargetableEntity targetable) {
                if (targetable.isTargetable(owner)) {
                    setTarget(targetable.getTargetableEntity());
                }
                else {
                    setTarget(null);
                }
            }
            else {
                setTarget(null);
            }
//                if (target instanceof GamePlayer) {
//                    type.onTarget(this, (GamePlayer) target);
//                }
        }
        if (getLocation().isChunkLoaded()) {
            cachedLocation = getLocation();
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

    public void setInitialSpeed(double speed) {
        this.speed = speed;
        resetSpeed();
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

    public void resetSpeed() {
        setSpeed(speed);
    }

    public double getSpeed() {
        return speed;
    }

    public void setKnockbackResistance(int knockbackResistance) {
        this.knockbackResistance = knockbackResistance / 100D;
    }

    public void knockback(double strength, Location by) {
        double knockback = knockbackResistance * strength;

        if (directingEntity.getLocation().subtract(0, 0.1, 0).getBlock().getType() == Material.AIR) {
            knockback *= 0.4;
        }

        Vector kbv = directingEntity.getLocation().subtract(by).toVector().normalize();

        kbv.setY(0.4);
        kbv.multiply(knockback * 0.6);


        try {
            kbv.add(directingEntity.getVelocity());
            directingEntity.setVelocity(kbv);
        } catch (IllegalArgumentException ignored) {

        }

    }

    @Override
    public Location getLocation() {
        return directingEntity.getLocation();
    }

    @Override
    public void teleport(@NotNull Location location) {
        directingEntity.teleportAsync(location);
    }

    @Override
    public LivingEntity getTargetableEntity() {
        return directingEntity;
    }

    @Override
    public boolean isTargetable(AggressiveEntity by) {
        return !(by instanceof GameEnemy) || by instanceof GameSummon;
    }
}
