package com.carterz30cal.entities.enemies.representation;

import com.carterz30cal.entities.LocatableEntity;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public interface RepresentedEntity extends LocatableEntity {
    EnemyRepresentation getRepresentation();
}
