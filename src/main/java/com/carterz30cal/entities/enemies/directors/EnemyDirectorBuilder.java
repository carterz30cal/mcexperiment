package com.carterz30cal.entities.enemies.directors;

import com.carterz30cal.entities.enemies.directors.behaviour.TargetingBehaviour;
import org.bukkit.Location;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class EnemyDirectorBuilder {
    private int knockback;
    private double speed;
    private TargetingBehaviour targetingBehaviour;

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

    public EnemyDirector build(Location baseLocation) {
        var director = new EnemyDirector(baseLocation, knockback);
        director.setSpeed(speed);
        director.setTargetingBehaviour(targetingBehaviour);
        return director;
    }
}
