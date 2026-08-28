package com.carterz30cal.areas;

import com.carterz30cal.areas.areas.GameAreaNecropolis;
import com.carterz30cal.areas.areas.GameAreaWaterway;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public enum Areas {
    WATERWAY(new GameAreaWaterway()),
    NECROPOLIS(new GameAreaNecropolis());
    private final AbstractGameArea area;

    Areas(AbstractGameArea area) {
        this.area = area;
        this.area.parent = this;
    }

    public AbstractGameArea getArea() {
        return area;
    }

}
