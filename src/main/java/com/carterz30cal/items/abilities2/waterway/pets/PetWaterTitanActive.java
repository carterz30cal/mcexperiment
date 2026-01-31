package com.carterz30cal.items.abilities2.waterway.pets;

import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.abilities2.implementation.GameAbility;

import java.util.ArrayList;
import java.util.List;

public class PetWaterTitanActive extends GameAbility {
    public PetWaterTitanActive() {

    }

    @Override
    public String name(AbilityContext context) {
        return "GOLDActive: Bookworm";
    }

    @Override
    public List<String> description(AbilityContext context) {
        var list = new ArrayList<String>();
        list.add("GRAYGet 1 wet paper with every kill!");
        return list;
    }

    @Override
    public void onKill(AbilityContext context, GameEnemy killed) {
        context.owner.giveItem(ItemFactory.build("wet_paper"), true);
        super.onKill(context, killed);
    }
}
