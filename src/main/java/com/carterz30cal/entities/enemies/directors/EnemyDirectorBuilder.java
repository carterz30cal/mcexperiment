package com.carterz30cal.entities.enemies.directors;

import com.carterz30cal.entities.enemies.directors.behaviour.TargetingBehaviour;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class EnemyDirectorBuilder {
    private int knockback;
    private double speed;
    private TargetingBehaviour targetingBehaviour;
    private EntityType entityType;

    public EnemyDirectorBuilder setKnockback(int knockback) {
        this.knockback = knockback;
        return this;
    }

    public EnemyDirectorBuilder setSpeed(double speed) {
        this.speed = speed;
        return this;
    }

    public EnemyDirectorBuilder setTargetingBehaviour(TargetingBehaviour targetingBehaviour) {
        this.targetingBehaviour = targetingBehaviour;
        return this;
    }

    public EnemyDirectorBuilder setEntityType(EntityType entityType) {
        this.entityType = entityType;
        return this;
    }

    public EnemyDirector build(Location baseLocation) {
        var director = new EnemyDirector(baseLocation, knockback);
        director.setDirectorType(entityType);
        director.setTargetingBehaviour(targetingBehaviour);
        director.createDirector();
        director.setInitialSpeed(speed);
        return director;
    }
}
