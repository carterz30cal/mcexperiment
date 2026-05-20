package com.carterz30cal.entities.enemies.directors;

import com.carterz30cal.entities.enemies.directors.behaviour.SimpleTargetingBehaviour;
import com.carterz30cal.main.Dungeons;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EnemyDirectorBuilder {
    public int knockback;
    public double speed;

    public void setKnockback(int knockback) {
        this.knockback = knockback;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public EnemyDirector build(Location baseLocation) {
        var mob = (Mob) Dungeons.w.spawnEntity(baseLocation, EntityType.ZOMBIE, false);
        mob.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        var director = new EnemyDirector(mob, new SimpleTargetingBehaviour(false), knockback);
        director.setSpeed(speed);
        return director;
    }
}
