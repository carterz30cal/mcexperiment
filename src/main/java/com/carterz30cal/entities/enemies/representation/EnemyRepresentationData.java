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
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class EnemyRepresentationData {
    private static final NamespacedKey KEY_SCALE = new NamespacedKey(Dungeons.instance, "scale");
    public double scale;
    public Vector offset;
    public EntityType type;
    public Map<EquipmentSlot, String> equipment = new HashMap<>();

    public EnemyRepresentationData() {

    }

    public EnemyRepresentationData(@NotNull ConfigurationSection yaml) {
        scale = yaml.getDouble("scale");
        var list = yaml.getDoubleList("offset");
        offset = new Vector(list.get(0), list.get(1), list.get(2));
        type = EntityType.valueOf(Objects.requireNonNull(yaml.getString("type")).toUpperCase());

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
            default -> new EnemyRepresentationData(yaml);
        };

    }

    public Entity spawn(Location base) {
        var world = base.getWorld();
        var entity = world.spawnEntity(base, type, false);
        entity.setSilent(true);
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.setCollidable(false);
            livingEntity.setAI(false);
            EntityUtils.applyPotionEffect(livingEntity, PotionEffectType.FIRE_RESISTANCE, 999999, 1, false);
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
