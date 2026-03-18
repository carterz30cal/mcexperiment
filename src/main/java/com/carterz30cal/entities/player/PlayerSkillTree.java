package com.carterz30cal.entities.player;

import com.carterz30cal.items.abilities2.implementation.GameAbility;
import com.carterz30cal.skills.Skills;
import com.carterz30cal.stats.Stat;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class PlayerSkillTree {
    private final GamePlayer owner;
    private final Map<Skills, Integer> tree = new EnumMap<>(Skills.class);
    private List<GameAbility.AbilityContext> underlying;

    public PlayerSkillTree(GamePlayer owner) {
        this.owner = owner;
    }

    public void save(ConfigurationSection section) {
        section.set("skill-tree", null);
        section.createSection("skill-tree");
        section.createSection("skill-tree.skills");
        for (Skills skill : Skills.values()) {
            if (tree.getOrDefault(skill, 0) == 0) {
                continue;
            }
            section.set("skill-tree.skills." + skill.name(), tree.getOrDefault(skill, 0));
        }
    }

    public void load(ConfigurationSection section) {
        if (section.contains("skill-tree")) {
            ConfigurationSection skills = section.getConfigurationSection("skill-tree.skills");
            assert skills != null;
            for (Skills skill : Skills.values()) {
                tree.put(skill, skills.getInt("skill-tree.skills." + skill.name(), 0));
            }
        }
        refresh();
    }

    private void refresh() {
        underlying = new ArrayList<>();
        for (var entry : tree.entrySet()) {
            if (entry.getValue() == 0) {
                continue;
            }
            var context = new GameAbility.AbilityContext(entry.getKey().getSkill());
            context.owner = owner;
            context.level = entry.getValue();
            underlying.add(context);
        }
    }

    public int getSkillLevel(Skills skill) {
        return tree.getOrDefault(skill, 0);
    }

    public void setSkillLevel(Skills skill, int level) {
        tree.put(skill, level);
        refresh();
    }

    public int getTokensUsed() {
        int used = 0;
        for (Skills skill : Skills.values()) {
            if (tree.getOrDefault(skill, 0) > 0) {
                used++;
            }
        }
        return used;
    }

    public int getRemainingTokens() {
        return owner.stats.getStat(Stat.SKILL_TREE_TOKENS) - getTokensUsed();
    }

    public List<GameAbility.AbilityContext> getUnderlyingAbilities() {
        return underlying;
    }
}
