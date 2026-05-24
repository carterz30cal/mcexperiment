package com.carterz30cal.entities;

import org.jetbrains.annotations.Nullable;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public interface TagHavingEntity {
    /**
     *
     * @param tag what tag do we want to check?
     * @return true if the tag is present, false otherwise.
     */
    boolean tag(@Nullable String tag);
}
