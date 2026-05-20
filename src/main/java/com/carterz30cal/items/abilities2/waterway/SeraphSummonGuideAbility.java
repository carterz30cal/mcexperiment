package com.carterz30cal.items.abilities2.waterway;

import com.carterz30cal.entities.AbstractEnemyType;
import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.GameSummon;
import com.carterz30cal.entities.enemies.core.EnemyManager;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.abilities2.implementation.GameAbility;
import com.carterz30cal.utils.RandomUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class SeraphSummonGuideAbility extends GameAbility {

    private final Map<GamePlayer, GameSummon> spirits = new HashMap<>();
    private final Map<GamePlayer, Integer> cooldowns = new HashMap<>();
    private final String mid;
    private final int cooldown;

    public SeraphSummonGuideAbility(String mid, int cooldown) {
        this.mid = mid;
        this.cooldown = cooldown;
    }

    @Override
    public String name(AbilityContext context) {
        return "Inherent Spirit";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull AbilityContext context) {
        var list = super.miniMessageDescription(context);
        list.add("<grey>A helpful soul will follow you.");
        list.add("<dark_grey>" + cooldown + " second cooldown if killed.");
        return list;
    }

    @Override
    public void onPreAttack(AbilityContext context, DamageInfo info) {
        AbstractEnemyType type = EnemyManager.getType(mid);
        if (type == null) {
            throw new NullPointerException("seraph summon guide ability: type is null!");
        }
        if (context == null || context.owner == null) {
            return;
        }
        GameSummon summon = spirits.get(context.owner);
        if (summon == null || summon.dead) {
            if (!cooldowns.containsKey(context.owner)) {
                cooldowns.put(context.owner, 20 * cooldown);
            }
            else if (cooldowns.get(context.owner) > 0) {
                cooldowns.put(context.owner, cooldowns.get(context.owner) - 1);
            }
            else {
                GameSummon spawn = GameSummon.SpawnSummonFromEnemy(context.owner, RandomUtils.getRandomInCircle(context.owner.getLocation(), 2, 3), type);
                spirits.put(context.owner, spawn);
                cooldowns.remove(context.owner);
            }
        }
        super.onPreAttack(context, info);
    }
}
