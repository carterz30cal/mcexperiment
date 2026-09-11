package com.carterz30cal.items.abilities.waterway.pets;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.health.damage.handlers.DamageableEntity;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.abilities.implementation.*;
import com.carterz30cal.skills.SkillSoulType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class PetWaterSpiderActive extends GameAbility implements AbilityWithDescription, AbilityWithKillEffect {

    @Override
    public String name(PlayerAbilityContext context) {
        return "Active: Plenty Powder";
    }

    @Override
    public void killEffect(ContextWithAbility<? extends GameEntity> context, DamageableEntity killed) {
        if (context.getOwner() instanceof GamePlayer player) {
            player.skillTree.gainSouls(SkillSoulType.WATERWAY, 3);
        }
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var lore = new ArrayList<String>();
        lore.add("<grey>You will get <aqua>3</aqua> more <blue>Waterway</blue> souls with every kill");
        lore.add("<grey>even if the creature wouldn't normally give them.");
        return lore;
    }
}
