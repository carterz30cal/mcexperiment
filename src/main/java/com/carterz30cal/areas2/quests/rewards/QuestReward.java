package com.carterz30cal.areas2.quests.rewards;

import com.carterz30cal.entities.GamePlayer;

import java.util.ArrayList;
import java.util.List;

public class QuestReward {
    protected long xp;

    public QuestReward(long xp) {
        this.xp = xp;
    }

    public void GrantOneTimeRewards(GamePlayer player) {

    }

    public List<String> GetRewardDescription() {
        List<String> list = new ArrayList<>();
        list.add("AQUA-- +" + GetXP() + "XP!");
        return list;
    }

    public long GetXP() {
        return xp;
    }
}
