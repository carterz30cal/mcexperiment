package com.carterz30cal.entities.health.damage.handlers;

import com.carterz30cal.entities.health.damage.DamagePacket;
import org.jetbrains.annotations.NotNull;

public interface DamageModifier {
    void modifyDamagePacket(@NotNull DamagePacket damagePacket);
}
