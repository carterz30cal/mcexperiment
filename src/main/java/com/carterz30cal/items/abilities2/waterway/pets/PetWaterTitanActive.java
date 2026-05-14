package com.carterz30cal.items.abilities2.waterway.pets;

import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.abilities2.implementation.GameAbility;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class PetWaterTitanActive extends GameAbility {
    public PetWaterTitanActive() {

    }

    @Override
    public String name(AbilityContext context) {
        return "Active: Bookworm";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull AbilityContext context) {
        var list = super.miniMessageDescription(context);
        list.add("<grey>You find 1 wet paper with every kill!</grey>");
        return list;
    }

    @Override
    public void onKill(AbilityContext context, GameEnemy killed) {
        context.owner.giveItem(ItemFactory.build("wet_paper"), true);
        super.onKill(context, killed);
    }
}
