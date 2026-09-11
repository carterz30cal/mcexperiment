package com.carterz30cal.entities.enemies.representation;

import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.EntityUtils;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author carterz30cal
 * @version 3
 * @since 1.0.0
 */
public class EnemyRepresentationData {
    private static final NamespacedKey KEY_SCALE = new NamespacedKey(Dungeons.instance, "scale");
    public double scale;
    public Vector offset;
    public EntityType type;
    public boolean invisible;
    public boolean allowAI = false;
    public Map<EquipmentSlot, String> equipment = new HashMap<>();

    /**
     * Default blank constructor
     *
     * @since 1.0.0
     */
    public EnemyRepresentationData() {

    }

    /**
     * Copy constructor
     *
     * @param existing what are we copying from?
     * @since 1.0.0
     */
    public EnemyRepresentationData(EnemyRepresentationData existing) {
        this.scale = existing.scale;
        this.offset = existing.offset;
        this.type = existing.type;
        this.invisible = existing.invisible;
        this.equipment.putAll(existing.equipment);
        this.allowAI = existing.allowAI;
    }

    public EnemyRepresentationData(@NotNull ConfigurationSection yaml) {
        scale = yaml.getDouble("scale", 1);
        var list = yaml.getDoubleList("offset");
        offset = new Vector(list.get(0), list.get(1), list.get(2));
        type = EntityType.valueOf(Objects.requireNonNull(yaml.getString("type")).toUpperCase());
        invisible = yaml.getBoolean("invisible", false);
        allowAI = yaml.getBoolean("allow-ai", false);

        if (yaml.contains("equipment")) {
            ConfigurationSection e = yaml.getConfigurationSection("equipment");
            assert e != null;
            for (String eq : e.getKeys(false)) {
                String item = e.getString(eq);
                if (Objects.equals(item, "null")) {
                    continue;
                }

                equipment.put(EquipmentSlot.valueOf(eq), item);
            }
        }
    }

    public static EnemyRepresentationData fromYaml(@NotNull ConfigurationSection yaml) {
        EntityType type = EntityType.valueOf(Objects.requireNonNull(yaml.getString("type")).toUpperCase());
        return switch (type) {
            case ITEM_DISPLAY -> new ItemDisplayRepresentationData(yaml);
            default -> new EnemyRepresentationData(yaml);
        };

    }

    public Entity spawn(Location base) {
        var world = base.getWorld();
        var entity = world.spawnEntity(base, type, false);
        entity.setSilent(true);
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.setCollidable(false);
            livingEntity.setAI(allowAI);
            EntityUtils.applyPotionEffect(livingEntity, PotionEffectType.FIRE_RESISTANCE, 999999, 1, false);
            if (invisible) {
                livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0, false, false));
            }
        }
        if (entity instanceof Mob mob) {
            for (EquipmentSlot slot : equipment.keySet()) {
                EntityUtils.setArmourPiece(mob, slot, equipment.get(slot));
            }
            //mob.setRemoveWhenFarAway(false);

            var scaleAttribute = mob.getAttribute(Attribute.SCALE);
            if (scaleAttribute != null) {
                scaleAttribute.addModifier(
                        new AttributeModifier(KEY_SCALE,
                                scale - 1,
                                AttributeModifier.Operation.MULTIPLY_SCALAR_1,
                                EquipmentSlotGroup.ANY)
                );
            }
        }
        return entity;
    }
}
