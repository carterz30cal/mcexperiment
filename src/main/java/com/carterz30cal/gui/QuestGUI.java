package com.carterz30cal.gui;

import com.carterz30cal.areas.quests.Quests;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
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
        if (owner.getSelectedQuest() != null) {
            quests.add(owner.getQuestSave(owner.getSelectedQuest()));
        }
        for (var q : owner.getQuestSaves()) {
            if (owner.getSelectedQuest() == q.GetQuest()) {
                continue;
            }
            if (q.completedQuest) {
                complete.add(q);
            }
            else {
                if (q.sectionSave == null || (q.currentSection == 0 && !q.sectionSave.HasTalkedTo())) {
                    continue;
                }
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
            inventory.setSlot(
                    ItemFactory.customItem("ARROW", "Previous Page", NamedTextColor.RED),
                    calc(1, 5)
            );
        }
        if (j + 28 < quests.size()) {
            inventory.setSlot(
                    ItemFactory.customItem("ARROW", "Previous Page", NamedTextColor.GREEN),
                    calc(7, 5)
            );
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
            owner.setSelectedQuest(save.GetQuest());
            update();
        }
        else if (clickPos == calc(1, 5) && page > 1) {
            page--;
            update();
        }
        else if (clickPos == calc(7, 5)) { // TODO: Maybe put in a bounds check?
            page++;
            update();
        }

        return false;
    }

    private ItemStack getQuestDisplay(Quests.QuestSave q) {
        var loreList = new ArrayList<TextComponent.Builder>();
        var lore = text();
        int completedCount = q.GetQuest().getCompletedSections(q.currentSection).size();

        lore.append(
                text("You've completed ", NamedTextColor.GRAY)
        ).append(
                text(completedCount, q.completedQuest ? NamedTextColor.GREEN : (completedCount == 0 ? NamedTextColor.RED : NamedTextColor.YELLOW))
        ).append(
                text("/", NamedTextColor.GRAY)
        ).append(
                text(q.GetQuest().getTotalSectionCount(), NamedTextColor.GREEN)
        ).append(
                text(" quests!", NamedTextColor.GRAY)
        );
        loreList.add(lore);
        if (!q.GetQuest().getDescription().isEmpty()) {
            loreList.add(text());
            for (var description : q.GetQuest().getDescription())
                loreList.add(text().append(text(description, NamedTextColor.GRAY)));
        }
        var section = q.GetQuest().getQuestSection(q.currentSection);
        if (!q.completedQuest && section != null && !section.GetDescription(q.sectionSave).isEmpty()) {
            loreList.add(text());
            loreList.add(text().content("Current goal:").color(NamedTextColor.GOLD));
            for (var description : section.GetDescription(q.sectionSave)) {
                loreList.add(text().color(NamedTextColor.GRAY).append(MiniMessage.miniMessage().deserialize(description)));
            }
            loreList.add(text());
            if (q.GetQuest() != owner.getSelectedQuest()) {
                loreList.add(text().content("Click to select this quest!").color(NamedTextColor.GOLD));
            }
            else {
                loreList.add(text().content("This is your active quest!").color(NamedTextColor.GOLD));
            }
        }

        return ItemFactory.customItem(
                q.completedQuest ? "BOOK" : "WRITTEN_BOOK",
                text().content("Quest: " + q.GetQuest().getName()).color(NamedTextColor.GREEN),
                loreList.toArray(new TextComponent.Builder[0])
        );
    }
}
