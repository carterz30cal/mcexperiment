package com.carterz30cal.entities.enemies.abilities;

import com.carterz30cal.items.abilities.implementation.Ability;
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
public abstract class EnemyAbility implements Ability {
    public EnemyAbility(ConfigurationSection section) {

    }

    /**
     * Attempts to instantiate an EnemyAbility class using a class name and
     * a configuration section
     *
     * @param clazzName the true name of the class we're after
     * @return hopefully, the EnemyAbility derivative we want.
     */
    public static @Nullable EnemyAbility get(@NotNull String clazzName, @NotNull ConfigurationSection section) {
        try {
            return (EnemyAbility) Class.forName("com.carterz30cal.entities.enemies.abilities." + clazzName).getConstructor(ConfigurationSection.class).newInstance(section);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException ignored) {
            Dungeons.instance.getLogger().warning("Class " + clazzName + " not found!");
            return null;
        }
    }
}
