package com.carterz30cal.areas.quests.rewards;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 3
 * @since 1.0.0
 */
public class ItemQuestReward extends QuestReward {

    protected final List<String> item = new ArrayList<>();

    public ItemQuestReward(long xp, String item, int amount) {
        super(xp);

        var i = item + "£" + amount;
        this.item.add(i);
    }

    public ItemQuestReward(long xp, String... items) {
        super(xp);
        this.item.addAll(List.of(items));
    }

    @Override
    public List<String> getRewardDescription() {
        var list = super.getRewardDescription();
        for (var i : item) {
            var j = ItemFactory.buildItemFromString(i);
            var k = ItemFactory.getItem(j);
            list.add("<aqua>-- <" + k.rarity.textColor.asHexString() + ">" + k.name + "<dark_grey> x" + j.getAmount() + "</dark_grey>!");
        }
        return list;
    }

    @Override
    public void grantOneTimeRewards(GamePlayer player) {
        super.grantOneTimeRewards(player);

        for (var i : item) {
            var stack = ItemFactory.buildItemFromString(i);
            player.giveItem(stack, true);
        }
    }
}
