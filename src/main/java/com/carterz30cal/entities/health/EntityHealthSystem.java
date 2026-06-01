package com.carterz30cal.entities.health;

import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.health.damage.handlers.AggressiveEntity;
import com.carterz30cal.entities.health.damage.handlers.DamageHandler;
import com.carterz30cal.entities.health.status.StatusEffect;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.abilities2.implementation.AbilityWithDefend;
import com.carterz30cal.items.abilities2.implementation.AggressiveAbility;
import com.carterz30cal.utils.EntityUtils;
import com.carterz30cal.utils.RandomUtils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Display;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static net.kyori.adventure.text.Component.text;

/**
 * Handles all health systems for entities, including DOTs, damage types and resistances.
 *
 * @author carterz30cal
 * @version 3
 * @since 1.0.0
 */
public class EntityHealthSystem {
    private final List<DamageHandler> damageHandlers;
    private final Set<AggressiveEntity> attackers = new HashSet<>();
    private final Map<StatusEffect, Long> builtUpStatusEffects = new HashMap<>();
    private long maxHealth;
    private double health;
    private AggressiveEntity lastAttacker;
    private boolean immune;

    public EntityHealthSystem(long maxHealth) {
        this.maxHealth = maxHealth;
        this.health = 1;
        this.damageHandlers = new ArrayList<>();
        this.immune = false;
    }

    public boolean damage(@NotNull DamagePacket damagePacket) {
        if (damagePacket.defender == null) {
            throw new IllegalStateException("A damagePacket must have a defender!");
        }
        if (damagePacket.aggressor != null) {
            lastAttacker = damagePacket.aggressor;
            attackers.add(damagePacket.aggressor);

            for (var context : damagePacket.aggressor.getAggressiveDamageModifiers()) {
                if (context.getAbility() instanceof AggressiveAbility aggressiveAbility) {
                    aggressiveAbility.damage(context, damagePacket);
                }
            }
        }

        if (immune) {
            var location = RandomUtils.getRandomInCircle(damagePacket.defender.getLocation().clone().add(0, 1, 0), 0.2, 0.4);
            var hologram = EntityUtils.spawnTextHologram(location, 30);
            if (hologram != null) {
                hologram.setBillboard(Display.Billboard.CENTER);
                hologram.text(text().content("IMMUNE").color(NamedTextColor.RED).build());
            }
            return false;
        }

        for (var context : damagePacket.defender.getDefensiveDamageModifiers()) {
            if (context.getAbility() instanceof AbilityWithDefend defendAbility) {
                defendAbility.defend(context, damagePacket);
            }
        }
        damagePacket.settle();
        long total = 0;
        for (var damage : damagePacket.damages.keySet()) {
            total += Math.round((double) damagePacket.damages.get(damage)
                    * damagePacket.getResistanceMultiplier(damage));
        }
        if (total > 0) {
            damage(total);
            applyStatuses(damagePacket);
            displayDamageHolograms(damagePacket);
            return true;
        }
        else {
            return false;
        }
    }

    public void tick() {

    }

    private void damage(long number) {
        double percent = (double) number / maxHealth;
        health -= percent;
        if (health < 0) {
            health = 0;
        }
    }

    private void applyStatuses(@NotNull DamagePacket damagePacket) {
        for (var status : damagePacket.statusEffects.keySet()) {
            if (damagePacket.defender.isImmune(status)) {
                continue;
            }
            long current = builtUpStatusEffects.getOrDefault(status, 0L);
            long added = current + damagePacket.getUnresistedStatusEffect(status);
            if (added > getRequiredBuildup(status)) {
                status.effect.apply(damagePacket.defender);
                builtUpStatusEffects.put(status, 0L);
            }
            else {
                builtUpStatusEffects.put(status, added);
            }
        }
    }

    private void displayDamageHolograms(@NotNull DamagePacket damagePacket) {
        Vector vector;
        if (damagePacket.aggressor == null) {
            vector = new Vector();
        }
        else {
            vector = damagePacket.defender.getLocation().clone().add(0, 0, 0).subtract(damagePacket.aggressor.getLocation()).multiply(-0.3).toVector();
        }
        for (var damage : damagePacket.damages.keySet()) {
            var location = RandomUtils.getRandomInCircle(damagePacket.defender.getLocation().clone().add(vector).add(0, 1, 0), 0.2, 0.4);
            var amount = Math.round((double) damagePacket.damages.get(damage)
                    * damagePacket.getResistanceMultiplier(damage));
            if (amount <= 0) {
                continue;
            }

            var hologram = EntityUtils.spawnTextHologram(location, 30);
            if (hologram == null) {
                continue;
            }
            hologram.setBillboard(Display.Billboard.CENTER);
            hologram.text(text().content(String.valueOf(amount)).color(damage.getColour()).build());
        }
    }

    public boolean isImmune() {
        return immune;
    }

    public void setImmune(boolean immune) {
        this.immune = immune;
    }

    public long getBuildup(StatusEffect status) {
        return builtUpStatusEffects.getOrDefault(status, 0L);
    }

    // TODO readd stacking buildup requirement
    public long getRequiredBuildup(StatusEffect status) {
        return status.defaultResistance;
    }

    public double getBuildupPercentage(StatusEffect status) {
        return (double) getBuildup(status) / getRequiredBuildup(status);
    }

    /**
     *
     * @param amount long-integer value of the amount of health to heal
     * @return returns true if the entity gained any health.
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean heal(long amount) {
        double savedHealth = health;
        double total = health * maxHealth;
        health = Math.min(1, (total + amount) / maxHealth);
        return health > savedHealth;
    }

    public boolean isDead() {
        return health <= 0 || getHealth() == 0;
    }

    public AggressiveEntity getLastAttacker() {
        return lastAttacker;
    }

    public Set<GamePlayer> getPlayerAttackers() {
        var set = new HashSet<GamePlayer>();
        for (var attacker : attackers) {
            if (attacker instanceof GamePlayer player) {
                set.add(player);
            }
        }
        return set;
    }

    public long getMaxHealth() {
        return maxHealth;
    }

    /**
     * Sets the max health, if you need to change it.
     *
     * @param maxHealth what value should maxHealth be set to?
     */
    public void setMaxHealth(long maxHealth) {
        this.maxHealth = maxHealth;
    }

    public boolean isAtMaxHealth() {
        return health >= 1;
    }

    public long getHealth() {
        return Math.round(health * maxHealth);
    }

    public double getHealthPercentage() {
        return health;
    }

    public void setHealthPercentage(double healthPercentage) {
        health = healthPercentage;
    }

    public void addDamageHandler(DamageHandler damageHandler) {
        this.damageHandlers.add(damageHandler);
    }

    public void addDamageHandlers(Collection<DamageHandler> damageHandlers) {
        this.damageHandlers.addAll(damageHandlers);
    }
}
