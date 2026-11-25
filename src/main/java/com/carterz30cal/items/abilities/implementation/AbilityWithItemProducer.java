package com.carterz30cal.items.abilities.implementation;

import com.carterz30cal.entities.player.PlayerItemProducer;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public interface AbilityWithItemProducer extends Ability {
    void modifyProducingContext(PlayerItemProducer.ProducingContext producingContext);
}
