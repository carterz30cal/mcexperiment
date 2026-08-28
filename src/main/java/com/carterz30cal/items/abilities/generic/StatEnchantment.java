package com.carterz30cal.items.abilities.generic;

import com.carterz30cal.entities.StatHavingEntity;
import com.carterz30cal.items.ItemReq;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.items.abilities.implementation.*;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;
import com.carterz30cal.stats.operations.AddStatOperation;
import com.carterz30cal.stats.operations.LegacyStatOperation;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

/**
 * Simple enchantment that grants one type of stat, with optional amount granted independent of stat level.
 * <br>Always costs one combination catalyst shard.
 * @author carterz30cal
 * @version 4
 * @since 1.0.0
 */
public class StatEnchantment extends GameAbstractEnchant
        implements AbilityWithDescription, AbilityWithStats {
    public long powerPerLevel;
    public Stat statGranted;
    public long flatStat;
    public long statPerLevel;
    public StatOperationType statOperation;

    public StatEnchantment(String name, long powerPerLevel, Stat granted, long flat, long statPerLevel, int maxLevel, ItemType... types) {
        super(name, maxLevel, types);
        this.powerPerLevel = powerPerLevel;
        this.statGranted = granted;
        this.flatStat = flat;
        this.statPerLevel = statPerLevel;
        this.statOperation = StatOperationType.ADD;
    }

    public StatEnchantment(String name, long powerPerLevel, Stat granted, long flat, long statPerLevel, int maxLevel, StatOperationType statOperation, ItemType... types) {
        super(name, maxLevel, types);
        this.powerPerLevel = powerPerLevel;
        this.statGranted = granted;
        this.flatStat = flat;
        this.statPerLevel = statPerLevel;
        this.statOperation = statOperation;
    }

    @Override
    public List<TextComponent.Builder> componentDescription(@NotNull PlayerAbilityContext context) {
        var description = text();

        description.append(text("Grants ", NamedTextColor.GRAY));
        description.append(text(getStat(context) > 0 ? "+" : "-").append(text(getStat(context))).color(statGranted.textColour));
        if (statOperation == StatOperationType.MULTIPLY) {
            description.append(text("% ", statGranted.textColour));
        }
        description.append(text(statGranted.getIcon(), statGranted.textColour)).append(text(".", NamedTextColor.GRAY));

        var d = new ArrayList<TextComponent.Builder>();
        d.add(description);
        return d;
    }

    @Override
    public long getEnchantPower(PlayerAbilityContext context) {
        return powerPerLevel * context.level;
    }

    @Override
    public List<ItemReq> getCatalystRequirements(PlayerAbilityContext context, long level) {
        List<ItemReq> reqs = new ArrayList<>();
        reqs.add(new ItemReq("combination_catalyst_shard", 1));
        return reqs;
    }


    private long getStat(PlayerAbilityContext context) {
        return flatStat + (context.level * statPerLevel);
    }

    /**
     *
     * @param context   ability context, for level only.
     * @param stats     the container we'll be operating on
     * @param situation where is this being fired.
     */
    @Override
    public void modifyStats(ContextWithAbility<? extends StatHavingEntity> context, StatContainer stats, Situation situation) {
        if (situation != Situation.ITEM) {
            return;
        }
        long level = context.getLevel();
        long stat = flatStat + (level * statPerLevel);
        if (statOperation == StatOperationType.MULTIPLY) {
            stats.operation(new LegacyStatOperation(StatOperationType.MULTIPLY, (100 + stat) / 100D, statGranted));
        }
        else {
            stats.operation(new LegacyStatOperation(statOperation, stat, statGranted));
        }
    }
}
