package com.carterz30cal.entities.player.quiver;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.entities.player.interfaces.ArrowSelectionStrategy;
import com.carterz30cal.items.Item;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class HighestDamageSelectionStrategy implements ArrowSelectionStrategy {

    /**
     * @param items the list of items we're trying to find the best out of
     * @param owner the user of this arrow
     * @return the best <code>Item</code> according to this strategy
     * @implNote damage * strength * might * power, highest wins. ignores abilities. this may affect the best choice, such as when its raining in waterway and using storm arrows
     */
    @Override
    public @NotNull List<Item> best(@NotNull Set<Item> items, @Nullable GamePlayer owner) {
        var stats = owner == null ? new StatContainer() : owner.lastStats.clone();
        if (items.isEmpty()) throw new UnsupportedOperationException("cannot have an empty list of items!");

        return items.stream().sorted((a, b) -> (int) (value(stats, a) - value(stats, b))).toList();
    }

    private long value(StatContainer stats, Item item) {
        var damage = Math.max(1, stats.stat(Stat.DAMAGE) + item.stats.stat(Stat.DAMAGE));
        var strength = stats.stat(Stat.STRENGTH) + item.stats.stat(Stat.STRENGTH);
        var might = stats.stat(Stat.MIGHT) + item.stats.stat(Stat.MIGHT);
        var power = stats.stat(Stat.POWER) + item.stats.stat(Stat.POWER);
        return damage * strength * might * power;
    }
}
