package com.carterz30cal.entities.enemies.abilities.generic;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.enemies.abilities.EnemyAbility;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.items.abilities.implementation.AbilityWithTick;
import com.carterz30cal.items.abilities.implementation.ContextWithAbility;
import com.carterz30cal.utils.EntityUtils;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class EnemyAbilityDamageAura extends EnemyAbility implements AbilityWithTick {
    /**
     * How much of the enemy's base damage do we do as a percentage?
     * @since 1.0.0 [1]
     */
    protected final double damagePercent;
    /**
     * How far does this ability deal damage?
     */
    protected final double radius;
    /**
     * Does this ability have damage drop-off?
     */
    protected final boolean dropoff;
    /**
     * How often does this ability tick?
     */
    protected final int period;
    public EnemyAbilityDamageAura(ConfigurationSection section) {
        super(section);

        damagePercent = section.getDouble("damage-multiplier", 1);
        radius = section.getDouble("radius", 1);
        dropoff = section.getBoolean("dropoff", false);
        period = section.getInt("period", 1);
    }

    @Override
    public void tick(ContextWithAbility<? extends GameEntity> context, int tick) {
        if (!(context.getOwner() instanceof GameEnemy enemy)) return;
        if (tick % period == 0) {
            var players = EntityUtils.getNearbyPlayers(context.getOwner().getLocation(), radius);
            for (var player : players) {
                var packet = enemy.getBlankDamagePacket();
                double falloff = dropoff ? (1D / Math.max(1, player.distance(enemy.getLocation()))): 1;
                packet.multiply(damagePercent);
                packet.multiply(falloff);
                packet.defender = player;
                player.damage(packet);
            }
        }
    }
}
