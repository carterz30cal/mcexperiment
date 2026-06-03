package com.carterz30cal.entities.player;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.health.damage.AttackType;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.health.damage.DamageType;
import com.carterz30cal.entities.health.damage.handlers.AggressiveEntity;
import com.carterz30cal.entities.health.damage.handlers.DamageableEntity;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author carterz30cal
 * @version 3
 * @since 1.0.0
 */
public class GameProjectile extends GameEntity implements AggressiveEntity {
    private static final int CHECKS = 10;
    private final AggressiveEntity owner;
    private final Projectile projectile;
    private final List<ContextWithAbility<? extends GameEntity>> contextualAbilities = new ArrayList<>();
    private StatContainer stats;
    private final BukkitRunnable runnable;
    private Location previousLocation;
    private int pierceTicks;

    public GameProjectile(AggressiveEntity owner, Projectile projectile) {
        this.owner = owner;
        this.projectile = projectile;

        uuid = projectile.getUniqueId();
        previousLocation = projectile.getLocation();
        projectile.getPersistentDataContainer().set(GameEnemy.keyEnemy, PersistentDataType.STRING, uuid.toString());
        register(uuid);
        pierceTicks = 1;
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        };
        runnable.runTaskTimer(Dungeons.instance, 0, 1);
    }

    protected void tick() {
        if (dead) {
            return;
        }
        var dir = previousLocation.clone().subtract(projectile.getLocation()).multiply(1D / CHECKS);
        Set<DamageableEntity> hit = new HashSet<>();
        for (int i = 0; i < CHECKS + 4; i++) {
            var cloc = previousLocation.clone().add(dir.clone().multiply(i + 1));
            for (var entity : EntityUtils.getNearbyDamageableEntities(cloc, 1.2)) {
                if (entity.equals(owner) || hit.contains(entity)) {
                    continue;
                }
                if (entity.isDamageable(owner)) {
                    ListenerEntityDamage.handleEntityDamageEntity(entity, this);
                    pierceTicks--;
                    hit.add(entity);
                    break;
                }
            }
        }
        previousLocation = projectile.getLocation();
        if (pierceTicks < 1) {
            remove();
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
    public void teleport(@NotNull Location location) {
        throw new IllegalCallerException("can't teleport a GameProjectile");
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
