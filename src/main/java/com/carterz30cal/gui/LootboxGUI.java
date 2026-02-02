package com.carterz30cal.gui;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.ItemLootTable;
import com.carterz30cal.items.ItemLootbox;
import com.carterz30cal.items.ItemRarity;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LootboxGUI extends AbstractGUI {
    private static Material[] rarityPanels = {
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


        // while drops
        // get upto 7 items
        // place those seven items
        // repeat 2 y down
        // if too big, add disclaimer item at bottom




        if (items.size() == 0) {
            ItemStack unlucky = ItemFactory.buildCustom("BARRIER", "REDUnlucky!", "REDYou didn't get anything this time..");
            inventory.setSlot(unlucky, calc(4, 1));
        }
        else {
            int rows = 0;
            while (items.size() > 0 && rows < 3) {
                List<ItemLootTable.ContextualDrop> subset = new ArrayList<>();
                for (int i = 0; i < 7 && i < items.size(); i++) subset.add(items.get(i));

                if (subset.size() % 2 == 1) {
                    ItemStack invalidDisplayItem = subset.get(0).getItemStack();
                    ItemFactory.makeInvalid(invalidDisplayItem);

                    inventory.setSlot(getPanel(subset.get(0)), calc(4, rows * 2));
                    inventory.setSlot(invalidDisplayItem, calc(4, rows * 2 + 1));
                    items.remove(subset.remove(0));
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
        ItemStack panel = ItemFactory.buildCustom(rarityPanels[drop.getItemRarity().ordinal()].toString(),
                rarity.colour + "BOLD" + rarity.name + " Drop!",
                drop.getItemStack().getItemMeta().getDisplayName() + " GRAYx" + drop.getItemStack().getAmount());
        return panel;
    }
}
