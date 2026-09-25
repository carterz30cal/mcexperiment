package com.carterz30cal.utils;

import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public interface BlockMutator {
    /**
     * Mutate existing block data into something new
     *
     * @param data the existing data
     * @return the new data
     * @since 1.0.0 [1]
     */
    @NotNull BlockState mutate(@NotNull BlockState data);
}
