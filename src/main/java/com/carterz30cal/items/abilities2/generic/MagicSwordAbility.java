package com.carterz30cal.items.abilities2.generic;

import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.DamageType;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.items.abilities2.implementation.GameAbility;

import java.util.List;

public class MagicSwordAbility extends GameAbility {
    @Override
    public String name(AbilityContext context) {
        return "AQUAMagic Weapon";
    }

    @Override
    public List<String> description(AbilityContext context) {
        var l = super.description(context);
        l.add("GRAYThis weapon always deals AQUAmagicGRAY damage.");
        return l;
    }

    @Override
    public void onAttack(AbilityContext context, DamageInfo info, GameEntity attacked) {
        if (info.type == DamageType.PHYSICAL) {
            info.type = DamageType.MAGICAL;
        }
    }
}
