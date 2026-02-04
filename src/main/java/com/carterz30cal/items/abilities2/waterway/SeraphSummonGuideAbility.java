package com.carterz30cal.items.abilities2.waterway;

import com.carterz30cal.entities.AbstractEnemyType;
import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.EnemyManager;
import com.carterz30cal.entities.GameSummon;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.abilities2.implementation.GameAbility;
import com.carterz30cal.utils.RandomUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeraphSummonGuideAbility extends GameAbility {

    private final Map<GamePlayer, GameSummon> spirits = new HashMap<>();
    private final String mid;

    public SeraphSummonGuideAbility(String mid) {
        this.mid = mid;
    }

    @Override
    public String name(AbilityContext context) {
        return "AQUAInherent Spirit";
    }

    @Override
    public List<String> description(AbilityContext context) {
        var l = super.description(context);
        l.add("GRAYA spirit will always follow you.");
        return l;
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
            GameSummon spawn = GameSummon.SpawnSummonFromEnemy(context.owner, RandomUtils.getRandomInCircle(context.owner.getLocation(), 2, 3), type);
            spirits.put(context.owner, spawn);
        }
        super.onPreAttack(context, info);
    }
}
