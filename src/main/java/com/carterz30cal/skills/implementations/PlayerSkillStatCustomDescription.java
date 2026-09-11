package com.carterz30cal.skills.implementations;

import com.carterz30cal.items.abilities.implementation.PlayerAbilityContext;
import com.carterz30cal.skills.SkillSoulType;
import com.carterz30cal.stats.Stat;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class PlayerSkillStatCustomDescription extends PlayerSkillStat {
    private final List<String> description;

    public PlayerSkillStatCustomDescription(SkillSoulType soulType, int maxLevel, Stat stat, long perLevel, double scaling, String... description) {
        super(soulType, maxLevel, stat, perLevel, scaling);
        this.description = Arrays.asList(description);
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var lore = super.miniMessageDescription(context);
        lore.addAll(description);
        return lore;
    }
}
