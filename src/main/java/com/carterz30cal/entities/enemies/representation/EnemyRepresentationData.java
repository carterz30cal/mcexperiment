package com.carterz30cal.entities.enemies.representation;

import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.EntityUtils;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
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
 * @version 5
 * @since 1.0.0
 */
public class EnemyRepresentationData {
    private static final NamespacedKey KEY_SCALE = new NamespacedKey(Dungeons.instance, "scale");
    public double scale;
    public Vector offset;
    public EntityType type;
    public boolean invisible;
    public boolean hidden;
    public boolean allowAI = false;
    public boolean contributeHeight = true;
    public Map<EquipmentSlot, String> equipment = new HashMap<>();
    public String skull;
    public Pose pose;

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
        this.hidden = existing.hidden;
        this.allowAI = existing.allowAI;
        this.skull = existing.skull;
        this.contributeHeight = existing.contributeHeight;
        this.pose = existing.pose;
    }

    public EnemyRepresentationData(@NotNull ConfigurationSection yaml) {
        scale = yaml.getDouble("scale", 1);
        var list = yaml.getDoubleList("offset");
        offset = new Vector(list.get(0), list.get(1), list.get(2));
        type = EntityType.valueOf(Objects.requireNonNull(yaml.getString("type")).toUpperCase());
        invisible = yaml.getBoolean("invisible", false);
        hidden = yaml.getBoolean("hidden", false);
        allowAI = yaml.getBoolean("allow-ai", false);
        contributeHeight = yaml.getBoolean("contribute-height", !invisible && !hidden);
        skull = yaml.getString("skull", null);
        pose = Pose.valueOf(yaml.getString("pose", "STANDING"));

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
            if (hidden) {
                livingEntity.setVisibleByDefault(false);
            }
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
        if (entity instanceof Mannequin mannequin) {
            for (EquipmentSlot slot : equipment.keySet()) {
                EntityUtils.setArmourPiece(mannequin, slot, equipment.get(slot));
            }
            if (skull != null) {
                var player = ItemFactory.getSkullProfile(skull);
                if (player != null) {
                    mannequin.setProfile(ResolvableProfile.resolvableProfile(player));
                }
                else {
                    Dungeons.instance.getLogger().warning("EnemyRepresentationData: could not find playerprofile: " + skull);
                }
            }
            mannequin.setPose(pose);
            var scaleAttribute = mannequin.getAttribute(Attribute.SCALE);
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
