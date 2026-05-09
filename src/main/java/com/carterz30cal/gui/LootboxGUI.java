package com.carterz30cal.gui;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.ItemLootTable;
import com.carterz30cal.items.ItemRarity;
import com.carterz30cal.items.types.ItemLootbox;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class LootboxGUI extends AbstractGUI {
    private static final Material[] RARITY_PANELS = {
            Material.LIGHT_GRAY_STAINED_GLASS_PANE, // COMMON
            Material.ORANGE_STAINED_GLASS_PANE, // UNCOMMON
            Material.LIME_STAINED_GLASS_PANE, // RARE
            Material.BLUE_STAINED_GLASS_PANE, // VERY_RARE
            Material.PURPLE_STAINED_GLASS_PANE, // EPIC
            Material.PINK_STAINED_GLASS_PANE, // INCREDIBLE
            Material.YELLOW_STAINED_GLASS_PANE // LEGENDARY
    };
    public LootboxGUI(GamePlayer owner, ItemLootbox lootbox) {
        super(owner);

        List<ItemLootTable.ContextualDrop> items = lootbox.table.generateWithContexts(owner);

        for (ItemLootTable.ContextualDrop drop : items) {
            owner.giveItem(drop.getItemStack(), true);
        }

        int inventorySize = 3 + ((items.size() / 7) * 2);

        inventory = new GooeyInventory(lootbox.name, inventorySize);
        inventory.initUsingTemplate(GooeyTemplate.PANED_DARK);

        if (items.isEmpty()) {
            inventory.setSlot(
                    ItemFactory.customItem("BARRIER", "<red>Unlucky!</red>", "<red>You somehow didn't get anything this time!</red>"),
                    calc(4, 1));
        }
        else {
            int rows = 0;
            while (!items.isEmpty() && rows < 3) {
                List<ItemLootTable.ContextualDrop> subset = new ArrayList<>();
                for (int i = 0; i < 7 && i < items.size(); i++) subset.add(items.get(i));

                if (subset.size() % 2 == 1) {
                    ItemStack invalidDisplayItem = subset.getFirst().getItemStack();
                    ItemFactory.makeInvalid(invalidDisplayItem);

                    inventory.setSlot(getPanel(subset.getFirst()), calc(4, rows * 2));
                    inventory.setSlot(invalidDisplayItem, calc(4, rows * 2 + 1));
                    items.remove(subset.removeFirst());
                }
                int offset = subset.size() / 2;
                int j = 0;
                for (int x = 4 - offset; x <= 4 + offset && x != 4; j++) {
                    ItemStack invalidDisplayItem = subset.get(j).getItemStack();
                    ItemFactory.makeInvalid(invalidDisplayItem);

                    inventory.setSlot(getPanel(subset.get(j)), calc(x, rows * 2));
                    inventory.setSlot(invalidDisplayItem, calc(x, rows * 2 + 1));
                    if (x == 3) x = 5;
                    else x++;
                }

                items.removeAll(subset);
                rows++;
            }
        }

        owner.playSound(Sound.BLOCK_NOTE_BLOCK_CHIME, 0.5, 0.5);
        owner.playSound(Sound.BLOCK_NOTE_BLOCK_CHIME, 0.6, 0.7, 4);
        owner.playSound(Sound.BLOCK_NOTE_BLOCK_CHIME, 0.7, 0.9, 8);
        inventory.update();
    }

    private ItemStack getPanel(ItemLootTable.ContextualDrop drop) {
        ItemRarity rarity = drop.getItemRarity();
        return ItemFactory.customItem(
                RARITY_PANELS[rarity.ordinal()].toString(),
                text().append(text().color(rarity.textColor).append(text(rarity.name + " drop!").decorate(TextDecoration.BOLD))),
                text().append(text(drop.getItemStack().getAmount() + "x ", NamedTextColor.GRAY)).append(drop.getItemStack().displayName())
        );
    }
}
