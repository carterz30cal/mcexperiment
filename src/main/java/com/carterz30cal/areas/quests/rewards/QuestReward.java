package com.carterz30cal.areas.quests.rewards;

import com.carterz30cal.entities.player.GamePlayer;

import java.util.ArrayList;
import java.util.List;

public class QuestReward {
    protected long xp;

    public QuestReward(long xp) {
        this.xp = xp;
    }

    public void grantOneTimeRewards(GamePlayer player) {

    }

    public List<String> getRewardDescription() {
        List<String> list = new ArrayList<>();
        list.add("<aqua>-- +" + GetXP() + "XP!");
        return list;
    }

    public long GetXP() {
        return xp;
    }
}
