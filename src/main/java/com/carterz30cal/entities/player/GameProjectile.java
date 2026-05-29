package com.carterz30cal.entities.player;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.health.damage.AttackType;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.health.damage.DamageType;
import com.carterz30cal.entities.health.damage.handlers.AggressiveEntity;
import com.carterz30cal.events.ListenerEntityDamage;
import com.carterz30cal.items.Item;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.abilities2.implementation.ContextWithAbility;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.utils.EntityUtils;
import org.bukkit.Location;
import org.bukkit.entity.Projectile;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class GameProjectile extends GameEntity implements AggressiveEntity {
    private final AggressiveEntity owner;
    private final Projectile projectile;
    private final List<ContextWithAbility<? extends GameEntity>> contextualAbilities = new ArrayList<>();
    private StatContainer stats;
    private final BukkitRunnable runnable;

    public GameProjectile(AggressiveEntity owner, Projectile projectile) {
        this.owner = owner;
        this.projectile = projectile;

        uuid = projectile.getUniqueId();
        projectile.getPersistentDataContainer().set(GameEnemy.keyEnemy, PersistentDataType.STRING, uuid.toString());
        register(uuid);
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        };
        runnable.runTaskTimer(Dungeons.instance, 2, 1);
    }

    protected void tick() {
        if (dead) {
            return;
        }
        for (var entity : EntityUtils.getNearbyDamageableEntities(projectile.getLocation(), 1)) {
            if (entity.isDamageable(owner)) {
                ListenerEntityDamage.handleEntityDamageEntity(entity, this);
                break;
            }
        }
    }


    public void setContextualAbilities(Item item) {
        if (owner instanceof GamePlayer player) {
            var list = new ArrayList<ContextWithAbility<? extends GameEntity>>(ItemFactory.getItemAbilities(item, player));
            contextualAbilities.addAll(list);
            stats = item.stats.clone();
        }
        else {
            throw new IllegalCallerException("item projectile contextual abilities must be set by a GamePlayer owner.");
        }
    }

    @Override
    public void remove() {
        runnable.cancel();
        projectile.remove();
    }

    @Override
    public Location getLocation() {
        return projectile.getLocation();
    }

    @Override
    public List<? extends ContextWithAbility<? extends GameEntity>> getAggressiveDamageModifiers() {
        var list = new ArrayList<ContextWithAbility<? extends GameEntity>>(owner.getAggressiveDamageModifiers());
        list.addAll(contextualAbilities);
        return list;
    }

    /**
     * This just handles any post-attack events we want the entity to work with.
     * e.g. for projectiles this will destroy the projectile, for players this will
     * trigger attack cooldowns.
     */
    @Override
    public void attack() {
        remove();
    }

    @Override
    public DamagePacket getBlankDamagePacket() {
        var packet = owner.getBlankDamagePacket();
        packet.attack = AttackType.ARROW;
        if (stats != null) {
            long physical = Math.round(getStat(Stat.DAMAGE)
                    * (1D + (getStat(Stat.STRENGTH) / 100D))
                    * (1D + (getStat(Stat.MIGHT) / 100D))
                    * (1D + (getStat(Stat.POWER) / 100D)));
            packet.addDamage(DamageType.PHYSICAL, physical);
        }
        long physical = packet.damages.getOrDefault(DamageType.PHYSICAL, 0L);
        packet.addDamage(DamageType.PROJECTILE, physical);
        packet.damages.put(DamageType.PHYSICAL, 0L);
        return packet;
    }

    @Override
    public long getStat(Stat stat) {
        if (stats == null) {
            return 0;
        }
        return stats.stat(stat);
    }
}
