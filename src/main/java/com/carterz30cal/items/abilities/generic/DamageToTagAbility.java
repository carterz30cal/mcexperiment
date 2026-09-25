package com.carterz30cal.items.abilities.generic;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.TagHavingEntity;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.health.damage.DamageType;
import com.carterz30cal.entities.health.damage.operations.implementations.MultiplyDamageTypeOperation;
import com.carterz30cal.items.abilities.implementation.*;
import com.carterz30cal.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A generic version of damage against tags
 *
 * @author carterz30cal
 * @version 1
 * @see com.carterz30cal.items.abilities.waterway.pets.PetDrenchedPassive
 * @since 1.0.0
 */
public class DamageToTagAbility extends GameAbility implements AggressiveAbility, AbilityWithDescription {
    protected String name;
    protected String prettyTag;
    protected String tag;
    protected double baseMultiplier;
    protected double gainPerLevel;
    protected DamageType damageType;

    /**
     *
     * @param name           the ability name, displayed on items
     * @param tag            the tag we want to gain damage against
     * @param prettyTag      how the tag is displayed in the description
     * @param baseMultiplier the base damage multiplier
     * @param gainPerLevel   how much multiplier to gain per level
     * @param damageType     the damage type we're increasing
     */
    public DamageToTagAbility(String name, String tag, String prettyTag, double baseMultiplier, double gainPerLevel, DamageType damageType) {
        this.name = name;
        this.tag = tag;
        this.prettyTag = prettyTag;
        this.baseMultiplier = baseMultiplier;
        this.gainPerLevel = gainPerLevel;
        this.damageType = damageType;
    }


    @Override
    public String name(PlayerAbilityContext context) {
        return this.name;
    }

    @Override
    public List<String> miniMessageDescription(@NotNull ContextWithAbility<? extends GameEntity> context) {
        var lore = "<grey>Deal <red>" + StringUtils.truncate(multiplier(context), 2) + "x</red> more " + this.damageType.getName() + " damage to " + this.prettyTag + " mobs.";

        return StringUtils.wrapText(lore, 45);
    }

    protected double multiplier(@NotNull ContextWithAbility<? extends GameEntity> context) {
        return this.baseMultiplier + (context.getLevel() * gainPerLevel);
    }

    @Override
    public void damage(ContextWithAbility<? extends GameEntity> context, DamagePacket packet) {
        if (packet.defender instanceof TagHavingEntity taggable) {
            if (taggable.tag(this.tag)) {
                packet.addOperation(new MultiplyDamageTypeOperation(this.damageType, multiplier(context)));
            }
        }
    }
}
