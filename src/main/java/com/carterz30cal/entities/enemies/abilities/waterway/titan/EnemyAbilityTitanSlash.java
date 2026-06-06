package com.carterz30cal.entities.enemies.abilities.waterway.titan;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.entities.enemies.abilities.AbilityCondition;
import com.carterz30cal.entities.enemies.abilities.EnemyAbility;
import com.carterz30cal.entities.enemies.abilities.conditions.AbilityConditionAlwaysTrue;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.health.damage.DamageType;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.abilities2.implementation.ContextWithAbility;
import com.carterz30cal.items.abilities2.implementation.RegisterableAbility;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.MathsUtils;
import com.carterz30cal.utils.ParticleUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class EnemyAbilityTitanSlash extends EnemyAbility implements RegisterableAbility {
    public final Particle.DustOptions DUST_WARNING = new Particle.DustOptions(Color.RED, 0.6F);
    public final Particle.DustOptions DUST_WARNING_2 = new Particle.DustOptions(Color.PURPLE, 0.6F);
    public final Particle.DustOptions DUST_ATTACK = new Particle.DustOptions(Color.WHITE, 0.7F);
    private final Map<GameEntity, BukkitRunnable> tickers = new HashMap<>();
    private final double slashDistance;
    private final int slashCooldown;
    private final int slashDelay;
    private final double slashDegrees;
    private final double slashRaise;
    private final boolean doubleSlash;
    private final AbilityCondition condition;

    public EnemyAbilityTitanSlash(ConfigurationSection section) {
        super(section);
        slashDistance = section.getDouble("distance");
        slashCooldown = section.getInt("cooldown");
        slashDelay = section.getInt("delay");
        slashDegrees = section.getDouble("degrees");
        doubleSlash = section.getBoolean("double", false);
        slashRaise = section.getDouble("raise");
        if (section.contains("condition")) {
            condition = AbilityCondition.get(Objects.requireNonNull(section.getString("condition.class")), Objects.requireNonNull(section.getConfigurationSection("condition")));
        }
        else {
            condition = new AbilityConditionAlwaysTrue(null);
        }
    }

    @Override
    public void register(ContextWithAbility<? extends GameEntity> context) {
        if (!(context.getOwner() instanceof GameEnemy enemy)) {
            return;
        }
        var ticker = new BukkitRunnable() {
            private int cooldown = slashCooldown;

            @Override
            public void run() {
                if (!condition.hasConditionMet(context)) {
                    return;
                }
                if (cooldown-- > 0) {
                    return;
                }
                var target = enemy.getEnemyDirector().getTarget();
                if (target == null) {
                    return;
                }
                var distance = enemy.distance(target.getLocation()) + 0.1;
                if (distance > slashDistance) {
                    return;
                }
                cooldown = slashCooldown;

                boolean speedChanged = false;
                if (enemy.getEnemyDirector().getSpeed() > 0) {
                    enemy.getEnemyDirector().setSpeed(0.1);
                    speedChanged = true;
                }
                final boolean speedc = speedChanged;
                Location pos = enemy.getLocation();
                var tpos = target.getLocation();
                final double degrees = (Math.toDegrees(Math.atan2(tpos.getX() - pos.getX(), tpos.getZ() - pos.getZ())));
                new BukkitRunnable() {
                    int time = slashDelay;

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        time--;
                        Set<GamePlayer> hit = new HashSet<>();
                        for (double d = degrees - slashDegrees; d <= degrees + slashDegrees; d += 4) {
                            double x = pos.getX() + MathsUtils.getCircleX(d) * distance;
                            double z = pos.getZ() + MathsUtils.getCircleZ(d) * distance;
                            double y = pos.getY() + 1.3 + (slashRaise * ((d - degrees) / slashDegrees));
                            double doy = pos.getY() + 1.3 - (slashRaise * ((d - degrees) / slashDegrees));

                            Location location = new Location(pos.getWorld(), x, y, z);
                            Location location2 = new Location(pos.getWorld(), x, doy, z);
                            if (time == 0) {
                                Dungeons.w.playSound(location, Sound.BLOCK_NOTE_BLOCK_BASS, 0.08f, 0.6f);
                                for (GamePlayer o : PlayerManager.getOnlinePlayers()) {
                                    if (hit.contains(o)) {
                                        continue;
                                    }
                                    double dx = Math.abs(o.getLocation().getX() - x);
                                    double dz = Math.abs(o.getLocation().getZ() - z);
                                    double dy = y - o.getLocation().getY();

                                    double dist = Math.sqrt(dx * dx + dz * dz);
                                    if (dist < 0.9 && dy < 2) {
                                        var packet = enemy.getBlankDamagePacket();
                                        if (doubleSlash) {
                                            packet.addDamage(DamageType.BLEED, packet.damages.getOrDefault(DamageType.PHYSICAL, 0L) / 2);
                                        }
                                        packet.defender = o;
                                        o.damage(packet);
                                        hit.add(o);
                                    }
                                }
                                ParticleUtils.spawn(location, DUST_ATTACK, 0);
                                if (doubleSlash) {
                                    ParticleUtils.spawn(location2, DUST_ATTACK, 0);
                                }
                                if (speedc) {
                                    enemy.getEnemyDirector().resetSpeed();
                                }
                            }
                            else {
                                if (time % 6 == 1) {
                                    Dungeons.w.playSound(location, Sound.BLOCK_NOTE_BLOCK_BASS, 0.08f, 1.1f);
                                }

                                if (time % 3 == 0) {
                                    ParticleUtils.spawn(location, DUST_WARNING, 0);
                                    if (doubleSlash) {
                                        ParticleUtils.spawn(location2, DUST_WARNING_2, 0);
                                    }
                                }
                            }
                        }
                        if (time == 0) {
                            cancel();
                        }
                    }

                }.runTaskTimer(Dungeons.instance, 1, 1);
            }
        };
        ticker.runTaskTimer(Dungeons.instance, 1, 1);
        tickers.put(context.getOwner(), ticker);
    }

    @Override
    public void unregister(ContextWithAbility<? extends GameEntity> context) {
        if (tickers.containsKey(context.getOwner())) {
            var ticker = tickers.get(context.getOwner());
            ticker.cancel();
            tickers.remove(context.getOwner());
        }
    }
}
