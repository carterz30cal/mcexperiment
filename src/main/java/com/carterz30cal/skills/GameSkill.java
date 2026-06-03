package com.carterz30cal.skills;

import com.carterz30cal.items.abilities2.implementation.AbilityWithDescription;
import com.carterz30cal.items.abilities2.implementation.GameAbility;
import com.carterz30cal.items.abilities2.implementation.PlayerAbilityContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class GameSkill extends GameAbility implements AbilityWithDescription {
    public Skills skill;
    public final int maxLevel;

    public GameSkill(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public String name(PlayerAbilityContext context) {
        return "";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var list = new ArrayList<String>();
        list.add("<dark_grey>Level " + context.getLevel() + "/" + maxLevel + "</dark_grey>");
        list.add("");
        list.addAll(skillDescription(context));
        list.add("");
        if (context.getLevel() == 0) {
            list.add("<red>Use a skill point to unlock this skill!");
        }
        else if (context.getLevel() == maxLevel) {
            list.add("<gold>Max level!");
        }
        return list;
    }

    public List<String> skillDescription(@NotNull PlayerAbilityContext context) {
        return new ArrayList<>();
    }
}
