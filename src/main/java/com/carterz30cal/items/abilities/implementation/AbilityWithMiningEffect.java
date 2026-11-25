package com.carterz30cal.items.abilities.implementation;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.mining.OreType;
import org.bukkit.Location;

import java.util.Map;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public interface AbilityWithMiningEffect extends Ability {
    /**
     * Called on each block that is broken by a player.
     * @param context ability context.
     * @param ore what type of ore is the block?
     * @param where where was the block broken in the world?
     * @since 1.0.0 [1]
     */
    default void miningEffect(ContextWithAbility<? extends GameEntity> context, OreType ore, Location where) {

    }

    /**
     * Called on each block that is broken by a player.
     *
     * @param context ability context.
     * @param ore     what type of ore is the block?
     * @param map     current item map
     * @since 1.0.0 [2]
     */
    default void modifyItems(ContextWithAbility<? extends GameEntity> context, OreType ore, Map<String, Long> map) {

    }
}
