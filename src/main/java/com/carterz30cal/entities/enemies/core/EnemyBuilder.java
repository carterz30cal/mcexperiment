package com.carterz30cal.entities.enemies.core;

import com.carterz30cal.entities.enemies.abilities.EnemyAbility;
import com.carterz30cal.entities.enemies.directors.EnemyDirectorBuilder;
import com.carterz30cal.entities.enemies.representation.EnemyRepresentationBuilder;
import com.carterz30cal.entities.health.EntityHealthSystemBuilder;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author carterz30cal
 * @version 4
 * @since 1.0.0
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class EnemyBuilder {
    private final static Map<String, EnemyBuilder> builders = new HashMap<>();
    private final String id;
    private EnemyDirectorBuilder directorBuilder;
    private EnemyRepresentationBuilder representationBuilder;
    private EntityHealthSystemBuilder healthSystemBuilder;
    private final List<EnemyAbility> abilities = new ArrayList<>();
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
     * Copy constructor
     *
     * @param existing copy this <code>EnemyBuilder</code>
     * @since 1.0.0
     */
    public EnemyBuilder(EnemyBuilder existing) {
        this.id = existing.id;
        temporaryBuilder = true;
        abilities.addAll(existing.abilities);
        this.data = new EnemyData(existing.data);
        this.directorBuilder = new EnemyDirectorBuilder(existing.directorBuilder);
        this.representationBuilder = new EnemyRepresentationBuilder(existing.representationBuilder);
        this.healthSystemBuilder = new EntityHealthSystemBuilder(existing.healthSystemBuilder);
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

    public EnemyDirectorBuilder getDirectorBuilder() {
        return directorBuilder;
    }

    public EnemyBuilder setRepresentationBuilder(EnemyRepresentationBuilder representationBuilder) {
        this.representationBuilder = representationBuilder;
        return this;
    }

    public EnemyRepresentationBuilder getRepresentationBuilder() {
        return representationBuilder;
    }

    public EnemyBuilder setHealthSystemBuilder(EntityHealthSystemBuilder healthSystemBuilder) {
        this.healthSystemBuilder = healthSystemBuilder;
        return this;
    }

    public EnemyBuilder addAbility(ConfigurationSection abilityConfig) {
        var ability = EnemyAbility.get(Objects.requireNonNull(abilityConfig.getString("class")), abilityConfig);
        abilities.add(ability);
        return this;
    }

    public EnemyBuilder addAbility(EnemyAbility ability) {
        abilities.add(ability);
        return this;
    }

    public EntityHealthSystemBuilder getHealthSystemBuilder() {
        return healthSystemBuilder;
    }

    public EnemyBuilder setEnemyData(EnemyData data) {
        this.data = data;
        return this;
    }

    public String getId() {
        return id;
    }

    public EnemyData getEnemyData() {
        return data;
    }

    public GameEnemy build(@NotNull Location spawnLocation) {
        var enemy = new GameEnemy(
                representationBuilder.build(spawnLocation),
                healthSystemBuilder.build(),
                directorBuilder.build(spawnLocation),
                data, id);
        enemy.setAbilities(abilities);
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
