package com.carterz30cal.utils;

import com.carterz30cal.areas.bosses.WeightedDrop;
import org.checkerframework.dataflow.qual.Pure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Backed by a <code>HashMap</code>, as implementing my own would be stupid.
 *
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class GroupMap<T> implements Map<String, Group<T>> {
    private final Map<String, Group<T>> backing = new HashMap<>();

    /**
     * @param groups the groups to add
     * @since 1.0.0 [1]
     */
    @SafeVarargs
    public GroupMap(@NotNull Group<T>... groups) {
        for (var group : groups) {
            backing.put(group.tag(), group);
        }
    }

    public GroupMap(GroupMap<T> copy) {
        for (var group : copy.values()) {
            backing.put(group.tag(), group);
        }
    }

    /**
     *
     * @param with the map to extend with
     * @return a copied map that contains <code>with</code>s elements.
     * @since 1.0.0 [1]
     */
    @Pure
    @NotNull
    public GroupMap<T> extend(@Nullable GroupMap<T> with) {
        if (with == null || with.isEmpty()) {
            return this;
        }
        var copy = new GroupMap<>(this);
        for (var group : with.values()) {
            var c = copy.get(group.tag());
            if (group.replaces() || c == null) {
                copy.put(group.tag(), new Group<>(group));
            }
            else {
                copy.put(group.tag(), new Group<>(c, group));
            }
        }
        return copy;
    }


    /**
     * Does a group contain this value?
     *
     * @param value the value to search for
     * @return <code>false</code> if the value is <code>null</code> or not present, <code>true</code> otherwise.
     * @since 1.0.0 [1]
     */
    public boolean contains(@Nullable T value) {
        if (value == null) {
            return false;
        }
        for (var group : backing.values()) {
            if (group.contains(value)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public int size() {
        return backing.size();
    }

    @Override
    public boolean isEmpty() {
        return backing.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return backing.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return backing.containsValue(value);
    }

    @Override
    public Group<T> get(Object key) {
        return backing.get(key);
    }

    @Nullable
    public T random(@Nullable String key) {
        return random(key, null);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public T random(@Nullable String key, @Nullable String backup) {
        if (key == null) {
            return null;
        }
        if (!containsKey(key)) {
            return random(backup, null);
        }
        var contents = get(key).contents();
        if (contents.isEmpty()) {
            if (backup == null) {
                return null;
            }
            else {
                return random(backup, null);
            }
        }
        if (contents.getFirst() instanceof WeightedDrop) {
            List<WeightedDrop> drops = (List<WeightedDrop>) contents;
            int total = 0;
            for (var drop : drops) total += drop.weight;
            int pick = RandomUtils.getRandomEx(0, total);
            for (WeightedDrop drop : drops) {
                if (pick < drop.weight) {
                    return (T) drop;
                }
                else {
                    pick -= drop.weight;
                }
            }
            throw new IllegalStateException("We've not managed to pick a random weighted drop in this group map?");
        }
        else {
            return RandomUtils.getChoice(contents);
        }
    }

    @Override
    public @Nullable Group<T> put(String key, Group<T> value) {
        return backing.put(key, value);
    }

    @Override
    public Group<T> remove(Object key) {
        return backing.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ? extends Group<T>> m) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public void clear() {
        throw new UnsupportedOperationException("This doesn't make sense contextually!");
    }

    @Override
    public @NotNull Set<String> keySet() {
        return backing.keySet();
    }

    @Override
    public @NotNull Collection<Group<T>> values() {
        return backing.values();
    }

    @Override
    public @NotNull Set<Entry<String, Group<T>>> entrySet() {
        return backing.entrySet();
    }

}
