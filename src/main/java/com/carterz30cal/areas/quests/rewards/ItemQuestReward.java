package com.carterz30cal.areas.quests.rewards;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;

import java.util.List;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
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
    public List<String> getRewardDescription() {
        var list = super.getRewardDescription();
        list.add("<aqua>-- " + ItemFactory.getItemTypeName(item) + "<dark_grey> x" + amount + "</dark_grey>!");
        return list;
    }

    @Override
    public void grantOneTimeRewards(GamePlayer player) {
        super.grantOneTimeRewards(player);

        player.giveItem(ItemFactory.build(item, amount), true);
    }
}
