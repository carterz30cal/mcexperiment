package com.carterz30cal.entities.enemies.directors;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.enemies.directors.behaviour.TargetingBehaviour;
import com.carterz30cal.entities.health.damage.handlers.AggressiveEntity;
import com.carterz30cal.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * An <code>EnemyDirector</code> that switches between multiple <code>EntityType</code>s to interact
 * with the world. An example would be using a Zombie and a Skeleton for melee/ranged combat.
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 * @see EnemyDirector
 */
public class PhasedEnemyDirector extends EnemyDirector {
    private final List<EnemyDirector> phases = new ArrayList<>();
    /**
     * This integer determines the time in which an entity stays in a phase before switching
     * to another phase. By default, the new phase must be different to the current phase.
     */
    private final int phaseTime;
    /**
     * This integer determines which phase is currently directing the mob.
     */
    private int phase;
    private int tick;

    public PhasedEnemyDirector(List<EnemyDirector> subDirectors, int phaseTime) {
        super(subDirectors.getFirst().getLocation());

        this.phaseTime = phaseTime;
        this.phase = 0;
        this.tick = 0;
        this.phases.addAll(subDirectors);
    }

    @Override
    public void tick(GameEnemy owner) {
        tick++;
        if (tick % phaseTime == 0) {
            if (phases.size() < 2) phase = 0;
            else {
                int pick;
                do {
                    pick = RandomUtils.getRandomEx(0, phases.size());
                }
                while (pick == phase);
                phase = pick;
            }
        }
        var controller = getCurrentPhase();
        controller.tick(owner);
        for (var follower : phases) {
            if (follower.equals(controller)) continue;
            follower.teleport(controller.getLocation());
        }
    }

    /**
     * Gets the current <code>EnemyDirector</code> controlling this mob.
     * @return the current <code>EnemyDirector</code>.
     * @see EnemyDirector
     * @since 1.0.0
     */
    public EnemyDirector getCurrentPhase() {
        return phases.get(phase);
    }

    @Override
    public void remove() {
        for (var p : phases) p.remove();
    }

    @Override
    public Location getLocation() {
        return getCurrentPhase().getLocation();
    }

    @Override
    public LivingEntity getTarget() {
        return getCurrentPhase().getTarget();
    }

    @Override
    public void setTarget(LivingEntity target) {
        getCurrentPhase().setTarget(target);
    }

    /**
     * @implNote this implementation sets initial speed for all sub<code>EnemyDirector</code>s.
     * @param speed initial speed to set to.
     */
    @Override
    public void setInitialSpeed(double speed) {
        for (var p : phases) p.setInitialSpeed(speed);
    }

    /**
     * @implNote this implementation sets speed for all sub<code>EnemyDirector</code>s.
     * @param speed initial speed to set to.
     */
    @Override
    public void setSpeed(double speed) {
        for (var p : phases) p.setSpeed(speed);
    }

    @Override
    public void resetSpeed() {
        for (var p : phases) p.resetSpeed();
    }

    @Override
    public double getSpeed() {
        return getCurrentPhase().getSpeed();
    }

    @Override
    public void setKnockbackResistance(int knockbackResistance) {
        for (var p : phases) p.setKnockbackResistance(knockbackResistance);
    }

    @Override
    public void knockback(double strength, Location by) {
        getCurrentPhase().knockback(strength, by);
    }

    @Override
    public void teleport(@NotNull Location location) {
        getCurrentPhase().teleport(location);
    }

    @Override
    public LivingEntity getTargetableEntity() {
        return getCurrentPhase().getTargetableEntity();
    }

    @Override
    public boolean isTargetable(AggressiveEntity by) {
        return getCurrentPhase().isTargetable(by);
    }

    @Override
    public double distance(Location location) {
        return super.distance(location);
    }

    @Override
    public void register(GameEntity entity) {
        for (var p : phases) p.register(entity);
    }

    @Override
    public void createDirector() {
        for (var p : phases) p.createDirector();
    }

    @Override
    public void setTargetingBehaviour(TargetingBehaviour behaviour) {
        for (var p : phases) p.setTargetingBehaviour(behaviour);
    }

    /**
     * @throws UnsupportedOperationException unsupported operation for this child of <code>EnemyDirector</code>
     */
    @Override
    public void setDirectorType(EntityType directorType) {
        throw new UnsupportedOperationException(
                "Phased directors don't support setting the director type directly as you must set that for each phase.");
    }
}
