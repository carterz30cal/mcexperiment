package com.carterz30cal.areas2;

import com.carterz30cal.areas2.areas.GameAreaWaterway;

public enum Areas {
    WATERWAY(new GameAreaWaterway());
    private final AbstractGameArea area;

    Areas(AbstractGameArea area) {
        this.area = area;
        this.area.parent = this;
    }

    public AbstractGameArea getArea() {
        return area;
    }

}
