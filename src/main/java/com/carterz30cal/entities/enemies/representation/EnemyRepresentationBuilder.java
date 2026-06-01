package com.carterz30cal.entities.enemies.representation;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 3
 * @since 1.0.0
 */
public class EnemyRepresentationBuilder {
    private final List<EnemyRepresentationData> data = new ArrayList<>();

    /**
     * Default blank constructor
     *
     * @since 1.0.0
     */
    public EnemyRepresentationBuilder() {

    }

    /**
     * Copy constructor
     *
     * @param existing what do we want to copy from?
     * @since 1.0.0
     */
    public EnemyRepresentationBuilder(EnemyRepresentationBuilder existing) {
        this.data.addAll(existing.data);
    }


    /**
     *
     * @param yaml configurationSection containing the entity's data
     * @return the builder for chaining
     * @since 1.0.0
     */
    @NotNull
    public EnemyRepresentationBuilder add(@NotNull ConfigurationSection yaml) {
        var entity = EnemyRepresentationData.fromYaml(yaml);
        data.add(entity);
        return this;
    }

    /**
     *
     * @param data the entity data we want to add to the representation
     * @return the builder
     * @since 1.0.0
     */
    public EnemyRepresentationBuilder add(@NotNull EnemyRepresentationData data) {
        this.data.add(data);
        return this;
    }

    /**
     * Build the representation
     * @param location where are we building it?
     * @return a <code>EnemyRepresentation</code>
     * @since 1.0.0
     */
    public EnemyRepresentation build(Location location) {
        var representation = new EnemyRepresentation();
        for (var e : data) {
            representation.entities.put(e, e.spawn(location.clone().add(e.offset)));
        }
        return representation;
    }
}
