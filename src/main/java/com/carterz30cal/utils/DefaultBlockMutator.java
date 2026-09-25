package com.carterz30cal.utils;

import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * No-op block mutator
 *
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public final class DefaultBlockMutator implements BlockMutator {
    public static DefaultBlockMutator instance = new DefaultBlockMutator();

    @Override
    public @NotNull BlockState mutate(@NotNull BlockState data) {
        return data;
    }
}
