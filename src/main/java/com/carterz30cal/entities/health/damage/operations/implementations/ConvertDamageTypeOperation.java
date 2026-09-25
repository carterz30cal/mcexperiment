package com.carterz30cal.entities.health.damage.operations.implementations;

import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.health.damage.DamageType;
import com.carterz30cal.entities.health.damage.operations.DamagePacketOperation;
import com.carterz30cal.entities.health.damage.operations.DamagePacketOperationPriority;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class ConvertDamageTypeOperation implements DamagePacketOperation {
    private final DamageType from;
    private final DamageType to;
    private final double multiplier;
    private final boolean remove;

    public ConvertDamageTypeOperation(DamageType from, DamageType to) {
        this.from = from;
        this.to = to;
        this.multiplier = 1;
        this.remove = true;
    }

    /**
     *
     * @param from what damage type do we already have?
     * @param to what damage type do we want?
     * @param multiplier how much do we want to multiply <code>from</code> by?
     * @param remove do we want to remove <code>from</code> from the packet?
     * @since 1.0.0 [2]
     */
    public ConvertDamageTypeOperation(DamageType from, DamageType to, double multiplier, boolean remove) {
        this.from = from;
        this.to = to;
        this.multiplier = multiplier;
        this.remove = remove;
    }

    @Override
    public DamagePacketOperationPriority getPriority() {
        return DamagePacketOperationPriority.LINKAGES;
    }

    @Override
    public void run(DamagePacket packet) {
        long initial = packet.damages.getOrDefault(from, 0L);
        packet.addDamage(to, Math.round(initial * multiplier));
        if (remove) packet.damages.put(from, 0L);
    }
}
