package com.carterz30cal.entities.enemies.representation;

import com.carterz30cal.items.ItemFactory;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class ItemDisplayRepresentationData extends EnemyRepresentationData {
    public String itemInformation;

    public ItemDisplayRepresentationData() {
        super();
        type = EntityType.ITEM_DISPLAY;
    }

    public ItemDisplayRepresentationData(ItemDisplayRepresentationData copy) {
        super(copy);
        this.itemInformation = copy.itemInformation;
    }

    public ItemDisplayRepresentationData(@NotNull ConfigurationSection yaml) {
        super(yaml);
        type = EntityType.ITEM_DISPLAY;
        itemInformation = yaml.getString("item");
    }

    @Override
    public Entity spawn(Location base) {
        ItemDisplay entity = (ItemDisplay) super.spawn(base);
        entity.setItemStack(ItemFactory.buildItemFromString(itemInformation));
        entity.setInvisible(false);
        entity.setTeleportDuration(1);
        Matrix4f mat = new Matrix4f().scale((float) scale);
        entity.setTransformationMatrix(mat.rotateY((float) Math.toRadians(180)));
        //entity.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(0, 0, (float) Math.toRadians(180), 0), new Vector3f(), new AxisAngle4f()));
        entity.setInterpolationDuration(1);
        entity.setGravity(false);
        entity.setNoPhysics(true);
        return entity;
    }
}
