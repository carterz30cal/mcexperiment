package com.carterz30cal.areas.quests2.implementations;

import com.carterz30cal.areas.quests2.QuestData;
import com.carterz30cal.areas.quests2.extensions.QuestWithDialog;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.Item;
import com.carterz30cal.items.ItemFactory;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class CollectQuest extends QuestWithDialog {
    protected final String item;
    protected final Item itemData;
    protected final long total;
    protected long given;
    protected boolean consumes;

    public CollectQuest(QuestData data, GamePlayer owner, ConfigurationSection specifics) {
        super(data, owner, specifics);
        this.item = specifics.getString("item");
        this.itemData = ItemFactory.getItem(this.item);
        this.total = specifics.getLong("count");
        this.consumes = specifics.getBoolean("consume", true);
    }

    @Override
    public boolean finished() {
        return given >= total;
    }

    @Override
    protected String display() {
        var colour = given == 0 ? "red>" : "yellow>";
        return "Bring <" + colour + given + "</" + colour + "<grey>/</grey><green>" + total + "</green> " + itemData.name + " to " + voice + ".";
    }

    @Override
    public void progress() {
        if (!finished()) {
            Item hand = ItemFactory.getItem(owner.getOwner().getMainItem());
            if (itemData.equals(hand)) {
                long diff = total - given;
                int amount = owner.getOwner().getMainItem().getAmount();
                if (consumes) {
                    long take = Math.min(diff, amount);
                    given += take;
                    owner.getOwner().getMainItem().setAmount((int) (amount - take));
                }
                else if (amount >= diff) {
                    given = amount;
                }
            }
        }
        super.progress();
    }

    @Override
    public void save(ConfigurationSection section) {
        section.set("given", given);
    }

    @Override
    public void load(ConfigurationSection section) {
        given = section.getLong("given", 0L);
    }
}
