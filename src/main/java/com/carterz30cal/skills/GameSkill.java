package com.carterz30cal.skills;

import com.carterz30cal.items.abilities.implementation.AbilityWithDescription;
import com.carterz30cal.items.abilities.implementation.GameAbility;
import com.carterz30cal.items.abilities.implementation.PlayerAbilityContext;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public abstract class GameSkill extends GameAbility implements AbilityWithDescription {
    public Skills skill;
    public final SkillSoulType soulType;
    public final int maxLevel;
    public String name;

    public GameSkill(SkillSoulType soulType, int maxLevel) {
        this.maxLevel = maxLevel;
        this.soulType = soulType;
    }

    public String name(PlayerAbilityContext context) {
        return name;
    }

    /**
     * Gets the number of <code>soulType</code> souls needed for
     * an individual level. The total souls spent is the sum
     * of this function for each level obtained greater than one.
     *
     * @param level what level are we asking about?
     * @return the amount of souls needed for this one level
     */
    public abstract long getSoulsNeedForLevel(long level);

    /**
     * Gets the total souls consumed to level up this skill to its current level.
     *
     * @param level what level is the skill?
     * @return total souls used
     */
    public final long getTotalSoulsUsed(long level) {
        if (level < 2) {
            return 0;
        }
        long total = 0;
        while (level > 1) {
            total += getSoulsNeedForLevel(level);
            level--;
        }
        return total;
    }
}
