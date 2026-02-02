package com.carterz30cal.gui;

import com.carterz30cal.areas2.quests.Quests;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class QuestGUI extends AbstractGUI {
    private Quests.QuestSave[] saves;
    private int page;

    public QuestGUI(GamePlayer owner) {
        super(owner);

        page = 1;
        inventory = new GooeyInventory("Quests", 6);

        update();
    }

    private void update() {
        List<Quests.QuestSave> quests = new ArrayList<>();
        List<Quests.QuestSave> complete = new ArrayList<>();
        if (owner.GetSelectedQuest() != null) {
            quests.add(owner.GetQuestSave(owner.GetSelectedQuest()));
        }
        for (var q : owner.GetQuestSaves()) {
            if (q.completedQuest) {
                complete.add(q);
            }
            else {
                quests.add(q);
            }
        }
        quests.addAll(complete);

        inventory.initUsingTemplate(GooeyTemplate.SHOPPY_DARK);
        saves = new Quests.QuestSave[54];
        int j = (page - 1) * (7 * 4);
        for (int i = 0; i < 28; i++) {
            if (j >= quests.size()) {
                break;
            }
            int k = calc((i % 7) + 1, (i / 7) + 1);
            saves[k] = quests.get(j);
            inventory.setSlot(getQuestDisplay(quests.get(j)), k);
            j++;
        }
        if (page > 1) {
            inventory.setSlot(ItemFactory.buildCustom("ARROW", "GREENPrevious Page"), calc(1, 5));
        }
        if (j + 28 < quests.size()) {
            inventory.setSlot(ItemFactory.buildCustom("ARROW", "GREENNext Page"), calc(7, 5));
        }
        inventory.update();
    }

    @Override
    public boolean allowLeftClick(int clickPos, ItemStack current) {
        if (clickPos >= 54) {
            return false;
        }
        Quests.QuestSave save = saves[clickPos];
        if (save != null) {
            owner.SetSelectedQuest(save.GetQuest());
            update();
        }

        return false;
    }

    private ItemStack getQuestDisplay(Quests.QuestSave q) {
        List<String> lore = new ArrayList<>();
        int completedCount = q.GetQuest().GetCompletedSections(q.currentSection).size();
        String colour = q.completedQuest ? "GREEN" : (completedCount == 0 ? "RED" : "YELLOW");
        lore.add("GRAYYou've completed " + colour + completedCount + "GRAY/GREEN" + q.GetQuest().GetTotalSectionCount() + " GRAYquests!");
        lore.add("");
        lore.addAll(q.GetQuest().GetDescription());
        if (!q.completedQuest) {
            lore.add("");
            lore.add("GOLDCurrent goal:");
            lore.addAll(q.GetQuest().GetQuestSection(q.currentSection).GetDescription(q.sectionSave));
            lore.add("");
            if (q.GetQuest() != owner.GetSelectedQuest()) {
                lore.add("GOLDClick to select quest!");
            }
            else {
                lore.add("GOLDSelected!");
            }
        }

        ItemStack quest = ItemFactory.buildCustom(q.completedQuest ? "BOOK" : "WRITTEN_BOOK", "GREENQuest: " + q.GetQuest().GetName(), lore);
        return quest;
    }
}
