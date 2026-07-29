package com.carterz30cal.items.abilities.necropolis.enchants;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.health.damage.DamageType;
import com.carterz30cal.entities.health.damage.handlers.AggressiveEntity;
import com.carterz30cal.entities.health.damage.handlers.DamageableEntity;
import com.carterz30cal.items.ItemReq;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.items.abilities.implementation.*;
import com.carterz30cal.stats.Stat;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>GameAbstractEnchant</code> that deals return damage to enemies that attack the player.<br>
 * Constructor determines damage type, damage amount, maximum level, applicable item types.
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class ThornsEnchantment extends GameAbstractEnchant implements AbilityWithDefend, AbilityWithDescription {
    private final DamageType damageType;
    private final long damageAmount;
    private final Stat scalingStat;
    private final double scalingAmount;
    private final long enchantPower;

    /**
     * Default constructor for Thorns enchantment.
     * @param damageType what damage type should it deal?
     * @param damageAmount how much flat damage should it deal
     * @param scalingStat what stat should we scale the damage with
     * @param scalingAmount how much should the stat be multiplied by
     * @param enchantPowerPerLevel how much enchant power should this enchant consume?
     * @param maximumLevel what is the maximum level?
     * @param itemTypes what item types should this enchant be applicable to?
     * @since 1.0.0
     */
    public ThornsEnchantment(DamageType damageType, long damageAmount,
                             Stat scalingStat, double scalingAmount,
                             long enchantPowerPerLevel,
                             int maximumLevel, ItemType... itemTypes) {
        super("Thorns", maximumLevel, itemTypes);
        this.damageType = damageType;
        this.damageAmount = damageAmount;
        this.scalingStat = scalingStat;
        this.scalingAmount = scalingAmount;
        this.enchantPower = enchantPowerPerLevel;
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        List<String> description = new ArrayList<>();
        description.add("<grey>When you are attacked, return <" + this.damageType.getColour().asHexString()
                            + ">" + getDamageAmount(context.getLevel(), context.getOwner()) + " <grey>damage.");
        description.add("<grey>This damage scales with enchantment level");
        description.add("<grey>and your <" + this.scalingStat.textColour.asHexString() + ">" + this.scalingStat.name + "<grey> stat!");
        return description;
    }

    @Override
    public List<ItemReq> getCatalystRequirements(PlayerAbilityContext context, long level) {
        List<ItemReq> reqs = new ArrayList<>();
        reqs.add(new ItemReq("combination_catalyst_shard", (int) (2L * level)));
        return reqs;
    }

    /**
     *
     * @param context ability context, determines level.
     * @return the amount of enchant power that the item needs to hold this enchantment.
     * @since 1.0.0
     */
    @Override
    public long getEnchantPower(PlayerAbilityContext context) {
        return context.getLevel() * this.enchantPower;
    }

    /**
     * Calculate how much damage, in raw numbers, to deal to the attacking enemy.
     * @since 1.0.0
     * @param thorny usually the <code>GamePlayer</code> being attacked.
     * @return a long integer telling us how much damage to put in the damage packet
     */
    private long getDamageAmount(long level, AggressiveEntity thorny) {
        long stat = thorny == null ? 0 : thorny.getStat(scalingStat);
        return damageAmount + Math.round(level * scalingAmount * stat);
    }

    @Override
    public void defend(@NotNull ContextWithAbility<? extends GameEntity> context, @NotNull DamagePacket packet) {
        var damager = packet.aggressor;
        if (damager instanceof DamageableEntity damageable
            && packet.defender instanceof AggressiveEntity thorny
            && damageable.isDamageable(thorny)) {
            var thorns = new DamagePacket(damageable, thorny, getDamageAmount(context.getLevel(), thorny)
                    , damageType);
            damageable.damage(thorns);
        }
    }
}
