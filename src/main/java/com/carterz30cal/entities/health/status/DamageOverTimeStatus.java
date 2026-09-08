package com.carterz30cal.entities.health.status;

import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.health.damage.DamageType;
import com.carterz30cal.entities.health.damage.handlers.DamageableEntity;
import com.carterz30cal.main.Dungeons;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class DamageOverTimeStatus extends AbstractStatus {
    public final long damage;
    public final DamageType damageType;
    public final int ticks;
    public final int delay;

    public DamageOverTimeStatus(long damage, DamageType damageType, int ticks, int delay) {
        this.damage = damage;
        this.damageType = damageType;
        this.ticks = ticks;
        this.delay = delay;
    }

    @Override
    public void apply(DamageableEntity entity) {
        new BukkitRunnable() {
            int t = ticks;

            @Override
            public void run() {
                if (t == 0 || !entity.isAlive()) {
                    cancel();
                }
                else {
                    t--;
                    var packet = new DamagePacket(entity, null, damage, damageType);
                    entity.damage(packet);
                }
            }
        }.runTaskTimer(Dungeons.instance, delay, delay);
    }
}
