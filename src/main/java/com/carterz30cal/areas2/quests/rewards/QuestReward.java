package com.carterz30cal.areas2.quests.rewards;

import com.carterz30cal.entities.GamePlayer;

public class QuestReward {
    protected long xp;

    public QuestReward(long xp) {
        this.xp = xp;
    }

    public void GrantOneTimeRewards(GamePlayer player) {

    }

    public long GetXP() {
        return xp;
    }
}
