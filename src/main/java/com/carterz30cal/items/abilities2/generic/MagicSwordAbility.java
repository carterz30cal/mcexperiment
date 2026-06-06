package com.carterz30cal.items.abilities2.generic;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.health.damage.AttackType;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.health.damage.DamageType;
import com.carterz30cal.entities.health.damage.operations.implementations.ConvertDamageTypeOperation;
import com.carterz30cal.items.abilities2.implementation.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class MagicSwordAbility extends GameAbility implements AbilityWithDescription, AggressiveAbility {
    @Override
    public String name(PlayerAbilityContext context) {
        return "Magical Blade";
    }

    @Override
    public List<String> miniMessageDescription(@NotNull PlayerAbilityContext context) {
        var list = new ArrayList<String>();
        list.add("<grey>This weapon converts <white>physical</white> damage</grey>");
        list.add("<grey>into <aqua>magic</aqua> damage.</grey>");
        return list;
    }

    @Override
    public void damage(ContextWithAbility<? extends GameEntity> context, DamagePacket packet) {
        if (packet.attack != AttackType.MELEE) {
            return;
        }
        packet.addOperation(new ConvertDamageTypeOperation(DamageType.PHYSICAL, DamageType.MAGIC));
    }


}
