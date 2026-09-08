package com.carterz30cal.items.abilities.necropolis.enchants;

import com.carterz30cal.entities.StatHavingEntity;
import com.carterz30cal.entities.health.status.StatusEffect;
import com.carterz30cal.items.ItemReq;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.items.abilities.implementation.*;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class StatusEffectEnchantment extends GameAbstractEnchant implements AbilityWithStats, AbilityWithDescription {
    protected StatusEffect status;
    protected long flat;
    protected long perLevel;
    protected long enchantPower;

    public StatusEffectEnchantment(String name, StatusEffect effect, long flat, long perLevel, long enchantPower, int maximumLevel, ItemType... itemTypes) {
        super(name, maximumLevel, itemTypes);
        this.status = effect;
        this.flat = flat;
        this.perLevel = perLevel;
        this.enchantPower = enchantPower;
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var d = "<grey>Grants <" + status.textColour.asHexString() + ">" + buildup(context.getLevel()) + " " + status.name + " " + status.symbol + "</" + status.textColour.asHexString()
                + "> status buildup to this item, which is inflicted upon hitting an enemy.";
        return StringUtils.wrapText(d, 45);
    }

    @Override
    public void modifyStats(ContextWithAbility<? extends StatHavingEntity> context, StatContainer stats, Situation situation) {
        if (situation != Situation.ITEM) {
            return;
        }
        stats.statuses.effects.put(status, (int) (stats.statuses.effects.getOrDefault(status, 0) + buildup(context.getLevel())));
    }

    @Override
    public List<ItemReq> getCatalystRequirements(PlayerAbilityContext context, long level) {
        List<ItemReq> reqs = new ArrayList<>();
        reqs.add(new ItemReq("combination_catalyst_shard", (int) level));
        return reqs;
    }

    @Override
    public long getEnchantPower(PlayerAbilityContext context) {
        return enchantPower * context.getLevel();
    }

    protected long buildup(long level) {
        return flat + (perLevel * level);
    }
}
