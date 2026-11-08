package com.carterz30cal.items.abilities.necropolis.enchants;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemReq;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.items.abilities.implementation.*;
import com.carterz30cal.mining.OreType;
import com.carterz30cal.utils.RandomUtils;
import com.carterz30cal.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class SlimaliciousEnchant extends GameAbstractEnchant implements AbilityWithMiningEffect, AbilityWithDescription {
    private final ItemReq[] slimeReq = new ItemReq[]{
            null,
            new ItemReq("yellow_slime", 1),
            new ItemReq("yellow_slime", 4),
            new ItemReq("yellow_slime", 16),
            new ItemReq("yellow_slime", 32),
            new ItemReq("yellow_slime", 64),
            new ItemReq("blue_slime", 2),
            new ItemReq("blue_slime", 4),
            new ItemReq("blue_slime", 8),
            new ItemReq("blue_slime", 16),
            new ItemReq("blue_slime", 32),
            new ItemReq("blue_slime", 64),
            new ItemReq("purple_slime", 2),
            new ItemReq("purple_slime", 3),
            new ItemReq("purple_slime", 4),
            new ItemReq("purple_slime", 5),
            new ItemReq("purple_slime", 6),
            new ItemReq("purple_slime", 7),
            new ItemReq("purple_slime", 8),
            new ItemReq("purple_slime", 16),
    };

    public SlimaliciousEnchant() {
        super("Slimalicious", 20, ItemType.PICKAXE);
    }

    @Override
    public void modifyItems(ContextWithAbility<? extends GameEntity> context, OreType ore, Map<String, Long> map) {
        if (ore.item.equals("red_slime") && RandomUtils.getRandom(0, 100) < context.getLevel()) {
            var red = map.getOrDefault("red_slime", 0L);
            map.put("yellow_slime", red);
            ((GamePlayer) context.getOwner()).sendMessage("<dark_aqua><b>SLIMALICIOUS!</b><aqua> You've just got " + red + "x yellow slime!");
        }
    }

    @Override
    public String obtaining() {
        return "Levels 1-3 can be found in crypts, and you may rarely find higher-tier books elsewhere. Don't try to max this enchant out, it will go badly!";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull ContextWithAbility<? extends GameEntity> context) {
        return StringUtils.wrapText("<grey>When mining red slime, you have a <green>" + context.getLevel() + "%</green> chance to convert it into yellow slime.", 30);
    }

    @Override
    public List<ItemReq> getCatalystRequirements(PlayerAbilityContext context, long level) {
        return List.of(new ItemReq("combination_catalyst_shard", (int) (1 + (level / 4))), slimeReq[(int) level]);
    }

    @Override
    public long getEnchantPower(PlayerAbilityContext context) {
        return context.getLevel() * 5;
    }
}
