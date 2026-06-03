package com.carterz30cal.skills;

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
