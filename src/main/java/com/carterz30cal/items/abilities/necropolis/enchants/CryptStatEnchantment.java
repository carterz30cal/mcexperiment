package com.carterz30cal.items.abilities.necropolis.enchants;

import com.carterz30cal.items.ItemReq;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.items.abilities.generic.StatEnchantment;
import com.carterz30cal.items.abilities.implementation.PlayerAbilityContext;
import com.carterz30cal.stats.Stat;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class CryptStatEnchantment extends StatEnchantment {
    public CryptStatEnchantment(String name, long powerPerLevel, Stat granted, long flat, long statPerLevel, int maxLevel, ItemType... types) {
        super(name, powerPerLevel, granted, flat, statPerLevel, maxLevel, types);
    }

    @Override
    public List<TextComponent.Builder> componentDescription(@NotNull PlayerAbilityContext context) {
        var d = super.componentDescription(context);
        d.add(text().content("Only active in a crypt!").color(NamedTextColor.DARK_GRAY));
        return d;
    }

    @Override
    public List<ItemReq> getCatalystRequirements(PlayerAbilityContext context, long level) {
        List<ItemReq> reqs = new ArrayList<>();
        reqs.add(new ItemReq("combination_catalyst_shard", (int) (level * 2)));
        return reqs;
    }
}
