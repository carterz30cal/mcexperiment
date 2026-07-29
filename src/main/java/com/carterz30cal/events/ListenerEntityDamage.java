package com.carterz30cal.events;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.health.damage.AttackType;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.health.damage.handlers.AggressiveEntity;
import com.carterz30cal.entities.health.damage.handlers.DamageableEntity;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.entities.player.GameProjectile;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.utils.EntityUtils;
import com.carterz30cal.utils.MathsUtils;
import com.carterz30cal.utils.ParticleUtils;
import com.carterz30cal.utils.RandomUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK;

/**
 * @author carterz30cal
 * @version 3
 * @since 1.0.0
 */
public class ListenerEntityDamage implements Listener
{
	public static ListenerEntityDamage Instance;

	public ListenerEntityDamage() {
		Instance = this;
	}

	@EventHandler
	public void onEntityDamageEntity(EntityDamageByEntityEvent e)
	{
		GameEntity damager = GameEntity.get(e.getDamager());
		GameEntity damaged = GameEntity.get(e.getEntity());

        if (damaged == null || damager == null) {
            e.setCancelled(true);
        }
        if (!(damager instanceof AggressiveEntity aggressor) || !(damaged instanceof DamageableEntity victim)) {
            e.setCancelled(true);
            return;
        }
        if (!victim.isDamageable(aggressor) || e.getCause() == ENTITY_SWEEP_ATTACK) {
            e.setCancelled(true);
            return;
        }
        e.setCancelled(true);
        handleEntityDamageEntity(victim, aggressor);
    }

    public static void handleEntityDamageEntity(DamageableEntity victim, AggressiveEntity aggressor) {
        var packet = aggressor.getBlankDamagePacket();
        packet.defender = victim;
        aggressor.attack();
        victim.damage(packet);
        long hits = packet.aggressor.getStat(Stat.SAVAGERY) / 100;
        long cha = packet.aggressor.getStat(Stat.SAVAGERY) % 100;
        if (RandomUtils.getRandom(1, 100) <= cha) {
            hits++;
        }
        final long post = hits;
        if (post > 0) {
            new BukkitRunnable() {
                long h = post;

                @Override
                public void run() {
                    if (!packet.isValid()) {
                        cancel();
                        return;
                    }
                    packet.multiply(0.8D);
                    victim.damage(packet);

                    int deg;
                    if (RandomUtils.getRandom(1, 2) == 1) {
                        deg = RandomUtils.getRandom(30, 150);
                    }
                    else {
                        deg = RandomUtils.getRandom(360 - 150, 360 - 30);
                    }

                    int degOffset = RandomUtils.getRandom(160, 200);

                    Vector dis = aggressor.getLocation().subtract(victim.getLocation()).toVector().normalize();
                    dis.setY(0);


                    Vector rot = dis.clone().rotateAroundY(Math.toRadians(90));
                    dis = dis.multiply(0.75);
                    Location p1 = victim.getLocation().add(0, 1, 0);
                    p1 = p1
                            .add(dis)
                            .add(rot.getX() * MathsUtils.getCircleX(deg), MathsUtils.getCircleZ(deg) * 0.9, rot.getZ() * MathsUtils.getCircleX(deg));
                    Location p2 = victim.getLocation().add(0, 1, 0);
                    p2 = p2
                            .add(dis)
                            .add(rot.getX() * MathsUtils.getCircleX(deg + degOffset), MathsUtils.getCircleZ(deg + degOffset) * 0.9, rot.getZ() * MathsUtils.getCircleX(deg + degOffset));

                    ParticleUtils.spawnLine(p1, p2, new Particle.DustOptions(Color.RED, 0.8F), 11);
                    if (packet.aggressor instanceof GamePlayer player) {
                        player.playSound(Sound.ENTITY_PLAYER_ATTACK_CRIT, 0.65, 0.7);
                    }

                    h--;
                    if (h == 0) {
                        cancel();
                    }
                }

            }.runTaskTimer(Dungeons.instance, 3, 3);
        }
    }

    @EventHandler
	public void onEntityCombust(EntityCombustEvent e) {
		e.setCancelled(true);
	}

    @EventHandler
    public void onEntityPrime(ExplosionPrimeEvent e) {
        GameEntity entity = GameEntity.get(e.getEntity());
        if (entity instanceof AggressiveEntity aggressor) {
            var entities = EntityUtils.getNearbyEntities(aggressor.getLocation(), 4.5);
            entities.removeIf((b) -> !(b instanceof DamageableEntity damageable) || !damageable.isDamageable(aggressor));
            for (var v : entities) {
                var victim = (DamageableEntity) v;
                handleEntityDamageEntity(victim, aggressor);
            }
            if (aggressor instanceof DamageableEntity body) {
                body.kill();
            }
            Dungeons.w.createExplosion(aggressor.getLocation(), 4.5F, false, false);
        }
        e.setCancelled(true);
    }
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e)
	{
        if (e.getEntity() instanceof BreezeWindCharge) {
            var p = GameEntity.get(e.getEntity());
            e.setCancelled(true);
            if (p instanceof AggressiveEntity projectile) {
                var entities = EntityUtils.getNearbyDamageableEntities(projectile.getLocation(), 2);
                entities.removeIf((b) -> !b.isDamageable(projectile));
                for (var victim : entities) {
                    if (victim.equals(projectile)) {
                        continue;
                    }
                    handleEntityDamageEntity(victim, projectile);
                }
            }
        }
        else {
            e.setCancelled(true);
            Dungeons.w.createExplosion(e.getEntity().getLocation(), 5, false, false);
        }
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e)
	{
        var projectile = GameEntity.get(e.getEntity());
        if (projectile instanceof GameProjectile gameProjectile) {
            if (e.getEntity() instanceof AbstractArrow ar) {
                ar.setPickupStatus(PickupStatus.DISALLOWED);

                if (e.getHitEntity() != null && e.getHitEntity().getType() == EntityType.ENDERMAN) {
                    var v = GameEntity.get(e.getHitEntity());
                    if (v instanceof DamageableEntity victim) {
                        handleEntityDamageEntity(victim, gameProjectile);
                    }
                }

                if (e.getHitBlock() != null) {
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            projectile.remove();
                        }

                    }.runTaskLater(Dungeons.instance, 20);
                }
            }
        }
        else if (e.getEntity() instanceof AbstractArrow arrow) {
            if (e.getHitBlock() != null) {
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        arrow.remove();
                    }
                }.runTaskLater(Dungeons.instance, 20);
            }
        }
        else if (e.getEntity() instanceof FishHook hook) {
            if (hook.getOwnerUniqueId() == null) {
                return;
            }
            if (e.getHitEntity() != null) {
                var player = (GamePlayer) GameEntity.get(hook.getOwnerUniqueId());
                GameEntity entity = GameEntity.get(e.getHitEntity());
                if (entity instanceof DamageableEntity victim) {
                    handleEntityDamageEntity(victim, player);
                }
                e.getEntity().remove();
            }
        }

		e.setCancelled(false);
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e)
	{
		GameEntity entity = GameEntity.get(e.getEntity());
        if (!(entity instanceof DamageableEntity victim)) {
            return;
        }
        e.setCancelled(true);

        DamagePacket packet = new DamagePacket();
        packet.aggressor = null;
        packet.defender = victim;
        packet.attack = AttackType.ENVIRONMENT;
        switch (e.getCause()) {
            case KILL:
            case VOID:
                victim.kill();
                break;
            case FALL:
                if (e.getEntity().getFallDistance() < 5) {
                    break;
                }
                long fallDamage = Math.round(e.getEntity().getFallDistance() * e.getEntity().getFallDistance() * 0.15);
                packet.addDamage(com.carterz30cal.entities.health.damage.DamageType.FALL, fallDamage);
                e.setCancelled(false);
                e.setDamage(1);
                break;
            case DROWNING:
                packet.addDamage(com.carterz30cal.entities.health.damage.DamageType.SUFFOCATION, 5);
                break;
            default:
                break;
        }
        victim.damage(packet);
    }

    @EventHandler
	public void onEntityTransform(EntityTransformEvent e)
	{
		e.setCancelled(true);
	}
	
	
	@EventHandler
	public void onSplit (SlimeSplitEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityLaunch(ProjectileLaunchEvent e) {
		Entity shooter = (Entity)e.getEntity().getShooter();
        if (e.getEntity() instanceof AbstractArrow arrow) {
            arrow.setPickupStatus(PickupStatus.DISALLOWED);
        }
		if (shooter instanceof Player) return;
		
		GameEnemy enemy = (GameEnemy)GameEnemy.get(shooter);
        if (enemy == null)
		{
			e.setCancelled(true);
			e.getEntity().remove();
		}
		else {
            if (e.getEntity() instanceof LivingEntity living) {
                living.setCollidable(false);
            }
			e.getEntity().getPersistentDataContainer().set(GameEnemy.keyEnemy, PersistentDataType.STRING, enemy.getUUID().toString());
		}
	}
	
	
	@EventHandler
	public void onEntityTeleport(EntityTeleportEvent e)
	{
        e.setCancelled(false);
	}
	
	@EventHandler
	public void onHeal(EntityRegainHealthEvent e)
	{
		if (e.getRegainReason() != RegainReason.CUSTOM) e.setCancelled(true);
	}
}
