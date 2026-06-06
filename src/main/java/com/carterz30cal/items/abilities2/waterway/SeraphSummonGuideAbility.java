package com.carterz30cal.items.abilities2.waterway;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.enemies.core.EnemyBuilder;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.entities.player.summons.GameSummon;
import com.carterz30cal.items.abilities2.implementation.*;
import com.carterz30cal.utils.RandomUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class SeraphSummonGuideAbility extends GameAbility implements AbilityWithDescription, AggressiveAbility {

    private final Map<GamePlayer, GameSummon> spirits = new HashMap<>();
    private final Map<GamePlayer, Integer> cooldowns = new HashMap<>();
    private final EnemyBuilder builder;
    private final int cooldown;

    public SeraphSummonGuideAbility(String builderId, int cooldown) {
        this.cooldown = cooldown;
        this.builder = EnemyBuilder.getBuilder(builderId);
    }

    @Override
    public String name(PlayerAbilityContext context) {
        return "Inherent Spirit";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var list = new ArrayList<String>();
        list.add("<grey>A helpful soul will follow you.");
        list.add("<dark_grey>" + cooldown + " second cooldown if killed.");
        return list;
    }

    @Override
    public void damage(ContextWithAbility<? extends GameEntity> context, DamagePacket packet) {
        if (builder == null) {
            throw new NullPointerException("seraph summon guide ability: builder is null!");
        }
        if (!(context.getOwner() instanceof GamePlayer player)) {
            return;
        }
        GameSummon summon = spirits.get(player);
        if (summon == null || summon.dead) {
            if (!cooldowns.containsKey(player)) {
                cooldowns.put(player, 20 * cooldown);
            }
            else if (cooldowns.get(player) > 0) {
                cooldowns.put(player, cooldowns.get(player) - 1);
            }
            else {
                GameSummon spawn = GameSummon.spawn(player, RandomUtils.getRandomInCircle(player.getLocation(), 2, 3), builder);
                spirits.put(player, spawn);
                cooldowns.remove(player);
            }
        }
    }
}
