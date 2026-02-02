package com.carterz30cal.areas2.quests.rewards;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;

import java.util.List;

public class ItemQuestReward extends QuestReward {

    protected String item;
    protected int amount;

    public ItemQuestReward(long xp, String item, int amount) {
        super(xp);

        this.item = item;
        this.amount = amount;
    }

    public ItemQuestReward(long xp, String item) {
        super(xp);
        this.item = item;
        this.amount = 1;
    }

    @Override
    public List<String> GetRewardDescription() {
        var list = super.GetRewardDescription();
        list.add("AQUA-- " + ItemFactory.getItemTypeName(item) + "DARK_GRAYx" + amount + "AQUA!");
        return list;
    }

    @Override
    public void GrantOneTimeRewards(GamePlayer player) {
        super.GrantOneTimeRewards(player);

        player.giveItem(ItemFactory.build(item, amount), true);
    }
}
