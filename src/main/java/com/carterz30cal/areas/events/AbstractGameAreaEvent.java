package com.carterz30cal.areas.events;

import com.carterz30cal.areas.Areas;

public abstract class AbstractGameAreaEvent {
    public boolean isPossibleInArea(Areas area) {
        return false;
    }
}
