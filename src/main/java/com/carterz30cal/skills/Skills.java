package com.carterz30cal.skills;

public enum Skills {
    ;
    public final TreePosition position;
    private final GameSkill skill;

    Skills(GameSkill skill, int x, int y, int page) {
        this.skill = skill;
        this.skill.skill = this;
        this.position = new TreePosition(x, y, page);
    }

    public GameSkill getSkill() {
        return skill;
    }

    public static class TreePosition {
        public final int x;
        public final int y;
        public final int page;

        public TreePosition(int x, int y, int page) {
            this.x = x;
            this.y = y;
            this.page = page;
        }
    }
}
