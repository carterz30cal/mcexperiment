package com.carterz30cal.utils;

import com.carterz30cal.entities.TagHavingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains a tag and entities. <code>T</code> can be anything really
 *
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class Group<T> implements TagHavingEntity {
    private final String tag;
    private final List<T> contents;
    private boolean replaces = false;

    @SafeVarargs
    public Group(@NotNull String tag, @NotNull T... contents) {
        this.tag = tag;
        this.contents = List.of(contents);
    }

    @SafeVarargs
    public Group(@NotNull Group<T>... groups) {
        if (groups.length == 0) {
            throw new IllegalArgumentException("must have at least one group");
        }
        this.tag = groups[0].tag;
        contents = new ArrayList<>();
        for (var group : groups) {
            if (!group.tag.equals(this.tag)) {
                throw new IllegalArgumentException("group tags don't match");
            }
            contents.addAll(group.contents);
        }
    }

    public Group<T> replaces(boolean replaces) {
        this.replaces = replaces;
        return this;
    }

    public boolean replaces() {
        return replaces;
    }

    public List<T> contents() {
        return contents;
    }

    public String tag() {
        return tag;
    }

    public boolean contains(@Nullable T value) {
        return contents.contains(value);
    }

    @Override
    public boolean tag(@Nullable String tag) {
        return this.tag.equals(tag);
    }
}
