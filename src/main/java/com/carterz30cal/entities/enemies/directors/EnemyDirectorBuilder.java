package com.carterz30cal.entities.enemies.directors;

import com.carterz30cal.entities.enemies.directors.behaviour.TargetingBehaviour;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 4
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class EnemyDirectorBuilder {
    private int knockback;
    private double speed;
    private TargetingBehaviour targetingBehaviour;
    private List<EntityType> entityTypes = new ArrayList<>();
    private boolean summon = false;
    private int phaseTime;

    /**
     * Default blank constructor
     *
     * @since 1.0.0
     */
    public EnemyDirectorBuilder() {

    }

    /**
     * Copy constructor
     *
     * @param existing what do we want to copy from?
     */
    public EnemyDirectorBuilder(EnemyDirectorBuilder existing) {
        this.knockback = existing.knockback;
        this.speed = existing.speed;
        this.targetingBehaviour = existing.targetingBehaviour;
        this.entityTypes = existing.entityTypes;
        this.summon = existing.summon;
        this.phaseTime = existing.phaseTime;
    }

    public EnemyDirectorBuilder setKnockback(int knockback) {
        this.knockback = knockback;
        return this;
    }

    public EnemyDirectorBuilder setSpeed(double speed) {
        this.speed = speed;
        return this;
    }

    public EnemyDirectorBuilder setSummon(boolean summon) {
        this.summon = summon;
        return this;
    }

    public EnemyDirectorBuilder setPhaseTime(int phaseTime) {
        this.phaseTime = phaseTime;
        return this;
    }

    public EnemyDirectorBuilder setTargetingBehaviour(TargetingBehaviour targetingBehaviour) {
        this.targetingBehaviour = targetingBehaviour;
        return this;
    }

    /**
     * Clears <code>entityTypes</code> and adds just one <code>EntityType</code>
     * @param entityType what <code>EntityType</code> do we want?
     * @return this
     * @see EntityType
     */
    public EnemyDirectorBuilder setEntityType(EntityType entityType) {
        this.entityTypes.clear();
        this.entityTypes.add(entityType);
        return this;
    }

    public EnemyDirectorBuilder addEntityType(EntityType entityType) {
        this.entityTypes.add(entityType);
        return this;
    }

    public EnemyDirector build(Location baseLocation) {
        if (this.entityTypes.isEmpty()) {
            throw new IllegalStateException("This builder has no EntityType!");
        }
        else if (this.entityTypes.size() == 1) {
            var director = new EnemyDirector(baseLocation);
            director.setDirectorType(entityTypes.getFirst());
            director.setTargetingBehaviour(targetingBehaviour);
            director.setKnockbackResistance(knockback);
            director.createDirector();
            director.setInitialSpeed(speed);
            director.setSummon(summon);
            return director;
        }
        else {
            List<EnemyDirector> directors = new ArrayList<>();
            for (var t : entityTypes) {
                var director = new EnemyDirector(baseLocation);
                director.setDirectorType(t);
                director.createDirector();
                directors.add(director);
            }
            var phaser = new PhasedEnemyDirector(directors, this.phaseTime);
            phaser.setTargetingBehaviour(targetingBehaviour);
            phaser.setKnockbackResistance(knockback);
            phaser.setInitialSpeed(speed);
            phaser.setSummon(summon);
            return phaser;
        }
    }
}
