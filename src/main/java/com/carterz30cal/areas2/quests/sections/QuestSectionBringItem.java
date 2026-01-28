package com.carterz30cal.areas2.quests.sections;

import com.carterz30cal.areas2.quests.rewards.QuestReward;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.Item;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.utils.StringDescription;

import java.util.List;
import java.util.UUID;

public class QuestSectionBringItem extends QuestSectionTalking {
    protected String itemId;
    protected int amount;

    public QuestSectionBringItem(
            StringDescription startMessage, StringDescription endMessage,
            StringDescription description, QuestReward questReward,
            String itemId, int amount
    ) {
        super(startMessage, description, endMessage, questReward);
        this.itemId = itemId;
        this.amount = amount;
    }

    @Override
    public List<String> GetDescription(SectionSave save) {
        var list = super.GetDescription(save);
        list.add("WHITEBring " + this.amount + "WHITEx " + ItemFactory.getItemTypeName(itemId) + " WHITEto " + questgiver.toString() + "WHITE.");
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
