package com.carterz30cal.entities.enemies.representation;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EnemyRepresentationBuilder {
    private final List<EnemyRepresentationData> data = new ArrayList<>();

    /**
     *
     * @param yaml configurationSection containing the entity's data
     * @return the builder for chaining
     */
    @NotNull
    public EnemyRepresentationBuilder add(@NotNull ConfigurationSection yaml) {
        var entity = EnemyRepresentationData.fromYaml(yaml);
        data.add(entity);
        return this;
    }

    public EnemyRepresentation build(Location location) {
        var representation = new EnemyRepresentation();
        for (var e : data) {
            representation.entities.put(e, e.spawn(location.clone().add(e.offset)));
        }
        return representation;
    }
}
