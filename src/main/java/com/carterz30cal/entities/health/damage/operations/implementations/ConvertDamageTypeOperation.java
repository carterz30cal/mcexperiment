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
public class ConvertDamageTypeOperation implements DamagePacketOperation {
    private final DamageType from;
    private final DamageType to;

    public ConvertDamageTypeOperation(DamageType from, DamageType to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public DamagePacketOperationPriority getPriority() {
        return DamagePacketOperationPriority.LINKAGES;
    }

    @Override
    public void run(DamagePacket packet) {
        long initial = packet.damages.getOrDefault(from, 0L);
        packet.addDamage(to, initial);
        packet.damages.put(from, 0L);
    }
}
