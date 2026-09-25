package com.carterz30cal.areas.quests.sections;

import com.carterz30cal.areas.quests.rewards.QuestReward;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.Item;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.utils.StringDescription;

import java.util.List;
import java.util.UUID;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class QuestSectionBringItem extends QuestSectionTalking {
    protected String itemId;
    protected int amount;
    protected Item item;
    protected String itemName;

    public QuestSectionBringItem(
            StringDescription startMessage, StringDescription endMessage,
            StringDescription description, QuestReward questReward,
            String itemId, int amount
    ) {
        super(startMessage, description, endMessage, questReward);
        this.itemId = itemId;
        this.amount = amount;
        this.item = ItemFactory.getItem(itemId);

        var colour = item.rarity.textColor.asHexString();
        itemName = "<" + colour + ">" + item.name + "</" + colour + ">";

    }

    @Override
    public List<String> GetDescription(SectionSave save) {
        var list = super.GetDescription(save);
        var check = "<white>Bring " + this.amount + "x " + itemName + " to " + questgiver.toString() + ".";
        if (check.length() > 24) {
            list.add("<white>Bring " + this.amount + "x ");
            list.add("<white>" + itemName + " to ");
            list.add("<white>" + questgiver.toString() + ".</white>");
        }
        else {
            list.add(check);
        }
        return list;
    }

    @Override
    public SectionSave CreateBlankSectionSave(GamePlayer player) {
        return new BringSectionSave(player);
    }

    private class BringSectionSave extends SectionSave {
        private boolean handedIn = false;

        private BringSectionSave(GamePlayer player) {
            this.player = player;
            this.uuid = UUID.randomUUID();
        }

        @Override
        public boolean IsFinished() {
            return handedIn;
        }

        @Override
        public void Interact() {
            if (handedIn) {
                return;
            }
            Item item = ItemFactory.getItem(player.getMainItem());
            if (item == null) {
                return;
            }
            if (item.id.equals(itemId)) {
                int handAmount = player.getMainItem().getAmount();
                if (handAmount >= amount) {
                    player.getMainItem().setAmount(handAmount - amount);
                    handedIn = true;
                }
            }
        }
    }
}
