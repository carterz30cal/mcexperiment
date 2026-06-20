package com.carterz30cal.entities.enemies.abilities.waterway.titan;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.entities.enemies.abilities.AbilityCondition;
import com.carterz30cal.entities.enemies.abilities.EnemyAbility;
import com.carterz30cal.entities.enemies.abilities.conditions.AbilityConditionAlwaysTrue;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.health.damage.AttackType;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.health.damage.DamageType;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.abilities.implementation.ContextWithAbility;
import com.carterz30cal.items.abilities.implementation.RegisterableAbility;
import com.carterz30cal.main.Dungeons;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class EnemyAbilityTitanExplosive extends EnemyAbility implements RegisterableAbility {
    private final Map<GameEntity, BukkitRunnable> tickers = new HashMap<>();
    private final int explosionRadius;
    private final long explosionDamage;
    private final DamageType explosionDamageType;
    private final int explosionDelay;
    private final int explosionCooldown;
    private final AbilityCondition condition;

    public EnemyAbilityTitanExplosive(ConfigurationSection section) {
        super(section);
        explosionRadius = section.getInt("radius");
        explosionDamage = section.getLong("damage");
        explosionDamageType = DamageType.valueOf(section.getString("damage-type"));
        explosionCooldown = section.getInt("cooldown");
        explosionDelay = section.getInt("delay");
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
            private int cooldown = explosionCooldown;

            @Override
            public void run() {
                if (!condition.hasConditionMet(context)) {
                    return;
                }
                if (cooldown-- > 0) {
                    return;
                }
                cooldown = explosionCooldown;

                Location pos = enemy.getLocation();
                enemy.getEnemyDirector().setSpeed(0);
                Map<Location, BlockData> data = new HashMap<>();
                for (int x = -explosionRadius + pos.getBlockX(); x <= explosionRadius + pos.getBlockX(); x++) {
                    for (int z = -explosionRadius + pos.getBlockZ(); z <= explosionRadius + pos.getBlockZ(); z++) {
                        if (pos.distance(new Location(Dungeons.w, x, pos.getY(), z)) > explosionRadius) {
                            continue;
                        }
                        int ty = pos.getBlockY() + 5;
                        while (Dungeons.w.getBlockAt(x, ty, z).getType() == Material.AIR) {
                            if (ty == pos.getBlockY() - 4) {
                                return; // fallback if no block exists
                            }
                            ty--;
                        }
                        var location = new Location(Dungeons.w, x, ty, z);
                        var block = Dungeons.instance.getServer().createBlockData(Material.BLACK_CONCRETE);
                        data.put(location, location.getBlock().getBlockData());
                        for (GamePlayer o : PlayerManager.getOnlinePlayers()) {
                            o.player.sendBlockChange(location, block);
                        }
                    }
                }
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        var block = Dungeons.instance.getServer().createBlockData(Material.BLACK_CONCRETE);
                        Dungeons.w.createExplosion(pos, 4, false, false);
                        for (GamePlayer o : PlayerManager.getOnlinePlayers()) {
                            for (var w : data.entrySet()) {
                                o.player.sendBlockChange(w.getKey(), w.getValue());
                            }
                            if (o.distance(pos) < explosionRadius) {
                                var packet = new DamagePacket();
                                packet.attack = AttackType.ENVIRONMENT;
                                packet.aggressor = (GameEnemy) context.getOwner();
                                packet.defender = o;
                                packet.addDamage(explosionDamageType, explosionDamage);
                                o.damage(packet);
                                enemy.getEnemyDirector().resetSpeed();
                            }
                        }
                    }
                }.runTaskLater(Dungeons.instance, explosionDelay);
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
