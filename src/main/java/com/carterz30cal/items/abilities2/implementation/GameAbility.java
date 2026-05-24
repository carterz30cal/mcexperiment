package com.carterz30cal.items.abilities2.implementation;

import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.health.status.StatusEffect;
import com.carterz30cal.items.ItemReq;
import com.carterz30cal.items.abilities2.Abilities;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatDisplayType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class GameAbility implements Ability {

    public Abilities source;

    @Deprecated
    public void onKill(PlayerAbilityContext context, GameEnemy killed)
    {

    }

    @Deprecated
    public void onAttack(PlayerAbilityContext context, DamageInfo info, GameEntity attacked)
    {

    }

    @Deprecated
    public void onPreAttack(PlayerAbilityContext context, DamageInfo info)
    {

    }

    @Deprecated
    public int onStatusBuildup(PlayerAbilityContext context, StatusEffect effect, int current) {
        return current;
    }

    @Deprecated
    public void onStatusProc(PlayerAbilityContext context, StatusEffect effect, GameEnemy enemy) {

    }

    @Deprecated
    public int onDamaged(PlayerAbilityContext context, GameEnemy damager, int damage) {
        return damage;
    }

    @Deprecated
    public void onLeftClick(PlayerAbilityContext context)
    {

    }

    @Deprecated
    public void onRightClick(PlayerAbilityContext context)
    {

    }

    @Deprecated
    public void onItemStats(PlayerAbilityContext context, StatContainer item)
    {

    }

    @Deprecated
    public void onItemStatsLate(PlayerAbilityContext context, StatContainer item)
    {

    }

    @Deprecated
    public void onPlayerStats(PlayerAbilityContext context, StatContainer item) {

    }


    public List<ItemReq> getCatalystRequirements(PlayerAbilityContext context, int desiredLevel) {
        return new ArrayList<>();
    }

    protected String formattedDisplay(Stat stat, long val) {
        String prefix = val >= 0 ? "+" : "";
        String suffix = stat.display == StatDisplayType.PERCENTAGE ? "%" : "";

        return "<" + stat.textColour.asHexString() + ">" + prefix + val + suffix + stat.getIcon() + "</" + stat.textColour.asHexString() + ">";
    }


}
