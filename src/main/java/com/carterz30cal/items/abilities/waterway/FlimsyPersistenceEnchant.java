package com.carterz30cal.items.abilities.waterway;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.health.damage.handlers.DamageableEntity;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.ItemReq;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.items.abilities.implementation.*;
import com.carterz30cal.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class FlimsyPersistenceEnchant extends GameAbstractEnchant implements AbilityWithDescription, AbilityWithKillEffect {
    public FlimsyPersistenceEnchant() {
        super("Flimsy Persistence", 1, ItemType.BOW);
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var description = "<grey>On kill, gain 1 Flimsy Arrow.";
        return StringUtils.wrapText(description, 26);
    }

    @Override
    public List<ItemReq> getCatalystRequirements(PlayerAbilityContext context, long level) {
        var reqs = new ArrayList<ItemReq>();
        reqs.add(new ItemReq("combination_catalyst_shard", 4));
        return reqs;
    }

    /**
     *
     * @param context ability context, determines level.
     * @return the amount of enchant power that the item needs to hold this enchantment.
     * @since 1.0.0
     */
    @Override
    public long getEnchantPower(PlayerAbilityContext context) {
        return 4;
    }

    @Override
    public void killEffect(ContextWithAbility<? extends GameEntity> context, DamageableEntity killed) {
        if (context.getOwner() instanceof GamePlayer player) {
            player.giveItem(ItemFactory.build("flimsy_arrow", 1));
        }
    }
}
