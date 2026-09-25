package com.carterz30cal.items.abilities.waterway.pets;

import com.carterz30cal.entities.health.damage.DamageType;
import com.carterz30cal.items.abilities.generic.DamageToTagAbility;

/**
 * @author carterz30cal
 * @version 3
 * @since 1.0.0
 */
public class PetDrenchedPassive extends DamageToTagAbility {
    public PetDrenchedPassive() {
        super("Passive: Hunting Nemo", "FISHING", "fishing", 1.1, 0.1, DamageType.PHYSICAL);
    }
}
