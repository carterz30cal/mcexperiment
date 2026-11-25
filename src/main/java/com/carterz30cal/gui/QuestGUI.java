package com.carterz30cal.gui;

import com.carterz30cal.areas.quests2.QuestCollection;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author carterz30cal
 * @version 3
 * @since 1.0.0
 */
public class QuestGUI extends AbstractGUI {
    private String[] quests;
    private int page;

    public QuestGUI(GamePlayer owner) {
        super(owner);

        page = 1;
        inventory = new GooeyInventory("Quests", 6);

        update();
    }

    private void update() {
        quests = new String[54];
        inventory.initUsingTemplate(GooeyTemplate.SHOPPY_DARK);
        List<QuestCollection> visible = QuestCollection.visibleList(owner);
        visible.sort(this::sort);
        int i = 0;
        for (int j = (page - 1) * 28; j < visible.size() && j < page * 28; j++, i++) {
            int x = (i % 7) + 1;
            int y = (i / 7) + 1;
            var c = visible.get(j);
            quests[calc(x, y)] = c.id();
            inventory.setSlot(c.item(owner), calc(x, y));
        }
        if (page > 1) {
            inventory.setSlot(ItemFactory.customItem("ARROW", "<green>Previous Page"), calc(2, 5));
        }
        else if (visible.size() >= page * 28) {
            inventory.setSlot(ItemFactory.customItem("ARROW", "<green>Next Page"), calc(6, 5));
        }
        inventory.update();
    }

    @Override
    public boolean allowLeftClick(int clickPos, ItemStack current) {
        if (clickPos == calc(2, 5) && page > 1) {
            page--;
        }
        else if (clickPos == calc(6, 5)) {
            page++;
        }
        else if (quests[clickPos] != null) {
            var collection = QuestCollection.get(quests[clickPos]);
            if (collection != null) {
                owner.questing.select(collection);
            }
        }
        update();
        return false;
    }

    private int sort(QuestCollection a, QuestCollection b) {
        var selectedA = a.selected(owner);
        var selectedB = b.selected(owner);
        if (selectedA && selectedB) {
            return 0;
        }
        else if (selectedA) {
            return -1;
        }
        else if (selectedB) {
            return 1;
        }
        else {
            var completeA = a.complete(owner);
            var completeB = b.complete(owner);
            if (completeA && completeB) {
                return 0;
            }
            else if (completeA) {
                return 1;
            }
            else if (completeB) {
                return -1;
            }
            else {
                var vA = a.completed(owner);
                var vB = b.completed(owner);
                return vB - vA;
            }
        }
    }
}
