package com.carterz30cal.entities.health.damage.operations;

import com.carterz30cal.entities.health.damage.DamagePacket;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public interface DamagePacketOperation {
    DamagePacketOperationPriority getPriority();

    void run(DamagePacket packet);
}
