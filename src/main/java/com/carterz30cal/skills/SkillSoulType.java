package com.carterz30cal.skills;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public enum SkillSoulType {
    WATERWAY("<blue>Waterway</blue>");
    private final String name;

    SkillSoulType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
