package com.carterz30cal.skills;

import com.carterz30cal.skills.implementations.PlayerSkillLifesteal;
import com.carterz30cal.skills.implementations.PlayerSkillPetCollector;
import com.carterz30cal.skills.implementations.PlayerSkillStat;
import com.carterz30cal.skills.implementations.PlayerSkillStatCustomDescription;
import com.carterz30cal.stats.Stat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public enum Skills {
    NUTRITION("Nutrition", new PlayerSkillStat(SkillSoulType.WATERWAY, 10, Stat.HEALTH, 20), 4, 5, 1),
    POWER_SURGE("Power Surge", new PlayerSkillStat(SkillSoulType.WATERWAY, 10, Stat.POWER, 10), 4, 4, 1, NUTRITION),
    FORGE_NOVICE("Novice Forger", new PlayerSkillStat(SkillSoulType.WATERWAY, 1, Stat.FORGE_SLOTS, 1), 5, 4, 1, POWER_SURGE),
    FLEDGLING_VAMPIRE("Fledgling Vampire", new PlayerSkillLifesteal(SkillSoulType.WATERWAY, 10), 3, 4, 1, POWER_SURGE),
    PET_COLLECTOR("Pet Collector", new PlayerSkillPetCollector(SkillSoulType.WATERWAY, 10), 6, 4, 1, FORGE_NOVICE),
    STERN_FACED("Stern-Faced", new PlayerSkillStatCustomDescription(SkillSoulType.WATERWAY, 25, Stat.INTIMIDATION, 1,
            "<grey>Intimidation will scare off enemies at or below that level. </grey>"), 2, 4, 1, FLEDGLING_VAMPIRE),
    SACKIER_SACKS1("Sackier Sacks I", new PlayerSkillStat(SkillSoulType.WATERWAY, 20, Stat.SACK_SPACE, 1000), 4, 3, 1, POWER_SURGE)
    ;
    public final TreePosition position;
    private final GameSkill skill;
    public final Set<Skills> parents = new HashSet<>();

    Skills(String name, GameSkill skill, int x, int y, int page, Skills... skills) {
        this.skill = skill;
        this.skill.skill = this;
        this.skill.name = name;
        this.position = new TreePosition(x, y, page);
        this.parents.addAll(Arrays.asList(skills));
    }

    /**
     *
     * @param skills set of skills the player has unlocked
     * @return <code>true</code> if the player has unlocked at least one of the skills required, <code>false</code> otherwise.
     */
    public boolean hasParent(Set<Skills> skills) {
        if (parents.isEmpty()) {
            return true;
        }
        boolean hasParent = false;
        for (Skills skill : skills) {
            if (parents.contains(skill)) {
                hasParent = true;
                break;
            }
        }
        return hasParent;
    }

    public GameSkill getSkill() {
        return skill;
    }

    public record TreePosition(int x, int y, int page) {
    }
}
