package com.carterz30cal.items.abilities.waterway.factory;

import com.carterz30cal.entities.player.PlayerItemProducer;
import com.carterz30cal.items.abilities.implementation.AbilityWithDescription;
import com.carterz30cal.items.abilities.implementation.AbilityWithItemProducer;
import com.carterz30cal.items.abilities.implementation.GameAbility;
import com.carterz30cal.items.abilities.implementation.PlayerAbilityContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class CatalystDustCoagulatorFactoryUpgrade extends GameAbility implements AbilityWithDescription, AbilityWithItemProducer {
    @Override
    public void modifyProducingContext(PlayerItemProducer.ProducingContext producingContext) {
        producingContext.weights.put("combination_catalyst_shard", 1L);
    }

    @Override
    public String name(PlayerAbilityContext context) {
        return "Spontaneous Accumulation";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var lore = new ArrayList<String>();
        lore.add("<grey>Enables the factory to produce <gold>Combination Catalyst Shards</gold> at");
        lore.add("<grey>a very slow rate. Expect 1 shard per ~1000 other items.");
        return lore;
    }
}
