package com.carterz30cal.areas.quests2.rewards;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class ItemQuestReward extends QuestReward {
    protected final List<String> items;

    public ItemQuestReward(ConfigurationSection config) {
        super(config);

        items = config.getStringList("items");
    }

    @Override
    public void completion(GamePlayer player) {
        super.completion(player);

        for (var i : items) {
            var j = ItemFactory.buildItemFromString(i);
            var k = ItemFactory.getItem(j);
            player.sendMessage("<dark_grey>- - <" + k.rarity.textColor.asHexString() + ">" + k.name + "<dark_grey> x" + j.getAmount() + "</dark_grey>");
            player.giveItem(j, true);
        }
    }
}
