package com.carterz30cal.entities.player.interfaces;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public interface ArrowSelectionStrategy {
    @NotNull
    Item best(@NotNull Set<Item> items, @Nullable GamePlayer owner);
}
