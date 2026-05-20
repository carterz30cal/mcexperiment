package com.carterz30cal.entities.health.damage.operations;

import com.carterz30cal.entities.health.damage.DamagePacket;

public interface DamagePacketOperation {
    DamagePacketOperationPriority getPriority();

    void run(DamagePacket packet);
}
