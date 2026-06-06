package com.carterz30cal.entities.health.damage.operations.implementations;

import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.health.damage.DamageType;
import com.carterz30cal.entities.health.damage.operations.DamagePacketOperation;
import com.carterz30cal.entities.health.damage.operations.DamagePacketOperationPriority;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class MultiplyDamageTypeOperation implements DamagePacketOperation {
    private final DamageType damageType;
    private final double multiplier;

    public MultiplyDamageTypeOperation(DamageType damageType, double multiplier) {
        this.damageType = damageType;
        this.multiplier = multiplier;
    }

    @Override
    public DamagePacketOperationPriority getPriority() {
        return DamagePacketOperationPriority.MULTIPLICATION;
    }

    @Override
    public void run(DamagePacket packet) {
        long current = packet.damages.getOrDefault(damageType, 0L);
        packet.damages.put(damageType, Math.round(current * multiplier));
    }
}
