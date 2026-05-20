package com.carterz30cal.entities.enemies.core;

import com.carterz30cal.entities.enemies.directors.EnemyDirectorBuilder;
import com.carterz30cal.entities.enemies.representation.EnemyRepresentationBuilder;
import com.carterz30cal.entities.health.EntityHealthSystemBuilder;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class EnemyBuilder {
    private final static Map<String, EnemyBuilder> builders = new HashMap<>();
    private final String id;
    private EnemyDirectorBuilder directorBuilder;
    private EnemyRepresentationBuilder representationBuilder;
    private EntityHealthSystemBuilder healthSystemBuilder;
    private EnemyData data;
    private boolean temporaryBuilder;

    public EnemyBuilder(String id) {
        this.id = id;
        builders.put(id, this);
        temporaryBuilder = false;
    }

    public EnemyBuilder() {
        this(UUID.randomUUID().toString());
        temporaryBuilder = true;
    }

    /**
     *
     * @param id the id of the EnemyBuilder we're trying to fetch.
     * @return the EnemyBuilder with the appropriate id, if one exists.
     */
    @Nullable
    public static EnemyBuilder getBuilder(@NotNull String id) {
        return builders.getOrDefault(id, null);
    }

    public EnemyBuilder setDirectorBuilder(EnemyDirectorBuilder directorBuilder) {
        this.directorBuilder = directorBuilder;
        return this;
    }

    public EnemyBuilder setRepresentationBuilder(EnemyRepresentationBuilder representationBuilder) {
        this.representationBuilder = representationBuilder;
        return this;
    }

    public EnemyBuilder setHealthSystemBuilder(EntityHealthSystemBuilder healthSystemBuilder) {
        this.healthSystemBuilder = healthSystemBuilder;
        return this;
    }

    public EnemyBuilder setEnemyData(EnemyData data) {
        this.data = data;
        return this;
    }

    public GameEnemy build(@NotNull Location spawnLocation) {
        var enemy = new GameEnemy(representationBuilder.build(spawnLocation), healthSystemBuilder.build(), directorBuilder.build(spawnLocation), id);
        enemy.setEnemyData(data);
        return enemy;
    }

    public boolean isTemporaryBuilder() {
        return temporaryBuilder;
    }

    /**
     * If we don't want this builder to clog up the builder map (temporary, one-off enemies)
     */
    public void deregisterBuilder() {
        builders.remove(id);
    }
}
