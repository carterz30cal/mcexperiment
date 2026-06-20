package com.carterz30cal.entities.player;

import com.carterz30cal.entities.enemies.core.EnemyData;
import com.carterz30cal.items.abilities.implementation.PlayerAbilityContext;
import com.carterz30cal.skills.SkillSoulType;
import com.carterz30cal.skills.Skills;
import com.carterz30cal.stats.Stat;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

/**
 * TODO FINISH!
 *
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class PlayerSkillTree {
    private final GamePlayer owner;
    private final Map<Skills, Integer> tree = new EnumMap<>(Skills.class);
    private final Map<SkillSoulType, Long> souls = new EnumMap<>(SkillSoulType.class);
    private final List<PlayerAbilityContext> underlying;
    private long lastSoulReward = 0;

    public PlayerSkillTree(GamePlayer owner) {
        this.owner = owner;
        this.underlying = new ArrayList<>();
    }

    public void save(ConfigurationSection section) {
        section.set("skill-tree", null);
        section.createSection("skill-tree");
        section.createSection("skill-tree.skills");
        section.createSection("skill-tree.souls");
        for (Skills skill : Skills.values()) {
            if (tree.getOrDefault(skill, 0) == 0) {
                continue;
            }
            section.set("skill-tree.skills." + skill.name(), tree.getOrDefault(skill, 0));
        }
        for (var soul : souls.keySet()) {
            section.set("skill-tree.souls." + soul.name(), souls.getOrDefault(soul, 0L));
        }
    }

    public void load(ConfigurationSection section) {
        if (section.contains("skill-tree")) {
            ConfigurationSection skills = section.getConfigurationSection("skill-tree");
            assert skills != null;
            for (Skills skill : Skills.values()) {
                if (!skills.contains("skills." + skill.name())) {
                    continue;
                }
                tree.put(skill, skills.getInt("skills." + skill.name(), 0));
            }
            for (var soul : SkillSoulType.values()) {
                souls.put(soul, skills.getLong("souls." + soul.name(), 0L));
            }
        }
        refresh();
    }

    private void refresh() {
        underlying.clear();
        for (var entry : tree.entrySet()) {
            if (entry.getValue() == 0) {
                continue;
            }
            var context = new PlayerAbilityContext(entry.getKey().getSkill());
            context.owner = owner;
            context.level = entry.getValue();
            underlying.add(context);
        }
    }

    public void resetTree() {
        refresh();
        for (var entry : tree.entrySet()) {
            if (entry.getValue() < 2) {
                continue;
            }
            long val = souls.getOrDefault(entry.getKey().getSkill().soulType, 0L) + entry.getKey().getSkill().getTotalSoulsUsed(entry.getValue());
            souls.put(entry.getKey().getSkill().soulType, val);
        }
        tree.clear();
        refresh();
    }

    /**
     * @param type what soul type do we care about?
     * @return the amount of that soul we have.
     * @since 1.0.0
     */
    public long getSoulCount(SkillSoulType type) {
        return souls.getOrDefault(type, 0L);
    }

    public int getSkillLevel(Skills skill) {
        return tree.getOrDefault(skill, 0);
    }

    /**
     * This assumes that you have already called canLevel and it has returned true.
     *
     * @param skill what skill are we levelling up?
     */
    public void addSkillLevel(Skills skill) {
        var level = getSkillLevel(skill);
        if (level == 0) {
            setSkillLevel(skill, 1);
        }
        else {
            var remaining = souls.getOrDefault(skill.getSkill().soulType, 0L) - skill.getSkill().getSoulsNeedForLevel(level + 1);
            souls.put(skill.getSkill().soulType, remaining);
            setSkillLevel(skill, level + 1);
        }
    }

    public void setSkillLevel(Skills skill, int level) {
        tree.put(skill, level);
        refresh();
    }

    public boolean canLevel(Skills skill) {
        var level = getSkillLevel(skill);
        if (level == 0) {
            return getRemainingTokens() > 0 && skill.hasParent(getSkills());
        }
        else if (level == skill.getSkill().maxLevel) {
            return false;
        }
        else {
            var s = souls.getOrDefault(skill.getSkill().soulType, 0L);
            return s >= skill.getSkill().getSoulsNeedForLevel(level + 1);
        }
    }

    public int getTokensUsed() {
        return getSkills().size();
    }

    /**
     * Add the contents of <code>data.souls</code> to our <code>souls</code> map.
     *
     * @param data the <code>EnemyData</code> we're extracting from.
     * @since 1.0.0
     */
    public void gainSouls(EnemyData data) {
        for (var s : data.souls.keySet()) {
            souls.put(s, souls.getOrDefault(s, 0L) + data.souls.get(s));
        }
        lastSoulReward = data.getTotalSouls();
    }

    /**
     * Add the contents of <code>data.souls</code> to our <code>souls</code> map.
     *
     * @param type   what soul type are we granting?
     * @param amount how many?
     * @since 1.0.0
     */
    public void gainSouls(SkillSoulType type, long amount) {
        souls.put(type, souls.getOrDefault(type, 0L) + amount);
        lastSoulReward = amount;
    }

    /**
     * The amount of souls we should show on the player's action bar.
     *
     * @return the sum of all soul types rewarded to this player last.
     * @since 1.0.0
     */
    public long getLastSoulReward() {
        return lastSoulReward;
    }

    /**
     * @return A set of all skills that the player has unlocked
     * @since 1.0.0
     */
    public Set<Skills> getSkills() {
        Set<Skills> skills = new HashSet<>();
        for (var entry : tree.entrySet()) {
            if (entry.getValue() == 0) {
                continue;
            }
            skills.add(entry.getKey());
        }
        return skills;
    }

    public long getRemainingTokens() {
        return owner.stats.stat(Stat.SKILL_TREE_TOKENS) - getTokensUsed();
    }

    public List<PlayerAbilityContext> getUnderlyingAbilities() {
        return underlying;
    }
}
