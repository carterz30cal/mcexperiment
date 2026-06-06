package com.carterz30cal.items.abilities2.waterway.pets;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.health.damage.handlers.DamageableEntity;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.abilities2.implementation.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class PetWaterTitanActive extends GameAbility implements AbilityWithDescription, AbilityWithKillEffect {
    @Override
    public String name(PlayerAbilityContext context) {
        return "Active: Bookworm";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var list = new ArrayList<String>();
        list.add("<grey>You find 1 wet paper with every kill!</grey>");
        return list;
    }

    @Override
    public void killEffect(ContextWithAbility<? extends GameEntity> context, DamageableEntity killed) {
        if (!(context.getOwner() instanceof GamePlayer player)) {
            throw new IllegalStateException("NecromancerAbility must have a GamePlayer owner");
        }
        player.giveItem(ItemFactory.build("wet_paper"), true);
    }
}
