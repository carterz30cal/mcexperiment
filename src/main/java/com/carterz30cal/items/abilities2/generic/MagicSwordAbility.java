package com.carterz30cal.items.abilities2.generic;

import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.DamageType;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.items.abilities2.implementation.GameAbility;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class MagicSwordAbility extends GameAbility {
    @Override
    public String name(AbilityContext context) {
        return "Magic Weapon";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull AbilityContext context) {
        var list = super.miniMessageDescription(context);
        list.add("<grey>This weapon deals <aqua>magical</aqua> damage");
        list.add("<grey>instead of <white>physical</white> damage.");
        list.add("<dark_grey>Other damage types are unaffected.");
        return list;
    }

    @Override
    public void onAttack(AbilityContext context, DamageInfo info, GameEntity attacked) {
        if (info.type == DamageType.PHYSICAL) {
            info.type = DamageType.MAGICAL;
        }
    }
}
