package com.carterz30cal.entities.enemies.abilities;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.items.abilities2.implementation.ContextWithAbility;
import com.carterz30cal.main.Dungeons;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public abstract class AbilityCondition {
    public AbilityCondition(@Nullable ConfigurationSection section) {

    }

    public static @Nullable AbilityCondition get(@NotNull String clazzName, @NotNull ConfigurationSection section) {
        try {
            return (AbilityCondition) Class.forName("com.carterz30cal.entities.enemies.abilities.conditions." + clazzName).getConstructor(ConfigurationSection.class).newInstance(section);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException ignored) {
            Dungeons.instance.getLogger().warning("Class " + clazzName + " not found!");
            return null;
        }
    }

    public abstract boolean hasConditionMet(ContextWithAbility<? extends GameEntity> context);
}
