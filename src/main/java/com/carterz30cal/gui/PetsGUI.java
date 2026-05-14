package com.carterz30cal.gui;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.Item;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.types.ItemPet;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PetsGUI extends AbstractGUI {
    public int page = 1;
    public String[] selectablePets;

    public final int BACK_PAGE_POS = calc(1, 5);
    public final int NEXT_PAGE_POS = calc(7,5);


    public PetsGUI(GamePlayer owner) {
        super(owner);

        inventory = new GooeyInventory("Pets", 6);

        refresh();
    }

    private void refresh() {
        inventory.initUsingTemplate(GooeyTemplate.SHOPPY_DARK);
        selectablePets = new String[54];

        if (owner.activePet != null) {
            inventory.setSlot(ItemFactory.build(owner.activePet), calc(4, 0));
        }
        for (int i = 0; i < 7 * 4; i++) {
            int pi = ((page - 1) * 7 * 4) + i;
            if (pi >= owner.pets.size()) break;

            int x = (i % 7) + 1;
            int y = (i / 7) + 1;
            int c = calc(x,y);
            selectablePets[c] = owner.pets.get(pi);
            inventory.setSlot(ItemFactory.build(owner.pets.get(pi)), c);
        }

        List<String> lore = getLore();
        inventory.setSlot(
                ItemFactory.customItem("REDSTONE_TORCH", "<red>Information!</red>", lore)
                , calc(4, 5));

        if (page > 1) {
            inventory.setSlot(
                    ItemFactory.customItem("ARROW", "Page " + (page - 1), NamedTextColor.RED),
                    calc(1, 5));
        }

        if (owner.pets.size() >= (page * 7 * 4)) {
            inventory.setSlot(
                    ItemFactory.customItem("ARROW", "Page " + (page + 1), NamedTextColor.GREEN),
                    calc(7, 5));
        }

        inventory.update();
    }

    private @NotNull List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add("<grey>All pets in your pets menu grant their</grey>");
        lore.add("<grey><light_purple>passive</light_purple> abilities and <blue>stats</blue>, but</grey>");
        lore.add("<grey>you may activate one pet to take</grey>");
        lore.add("<grey>advantage of their <gold>active</gold> ability!</grey>");

        int petCount = owner.pets.size() + (owner.activePet == null ? 0 : 1);
        if (petCount == 0) {
            lore.add("<grey>You don't have any pets.</grey>");
        }
        else if (petCount == 1) {
            lore.add("<grey>You have <green>1</green> pet.</grey>");
        }
        else {
            lore.add("<grey>You have <green>" + petCount + "</green> pets.</grey>");
        }
        return lore;
    }

    @Override
    public boolean allowLeftClick(int clickPos, ItemStack clicked) {
        if (clickPos >= 54) {
            Item cli = ItemFactory.getItem(clicked);
            if (cli instanceof ItemPet) {
                Set<String> lines = owner.getPetLines();
                if (lines.contains(((ItemPet) cli).petLine)) {
                    owner.sendMessage("You can't add this pet!", NamedTextColor.RED, 0);
                }
                else {
                    clicked.setAmount(clicked.getAmount() - 1);
                    owner.pets.add(cli.id);
                }
            }
        }
        else if (clickPos == BACK_PAGE_POS && page > 1) page--;
        else if (clickPos == NEXT_PAGE_POS && owner.pets.size() >= (page * 7 * 4)) page++;
        else if (clickPos == calc(4, 0) && owner.activePet != null) {
            owner.pets.add(owner.activePet);
            owner.activePet = null;
        }
        else {
            String choice = selectablePets[clickPos];
            if (choice != null) {
                if (owner.activePet != null) owner.pets.add(owner.activePet);
                owner.activePet = choice;
                owner.pets.remove(choice);
            }
        }

        refresh();
        return false;
    }

    @Override
    public boolean allowRightClick(int clickPos, ItemStack clicked) {
        if (clickPos >= 54) return false;
        else if (selectablePets[clickPos] != null) {
            owner.giveItem(ItemFactory.build(selectablePets[clickPos]), false);
            owner.pets.remove(selectablePets[clickPos]);
        }
        else if (clickPos == calc(4, 0) && owner.activePet != null) {
            owner.giveItem(ItemFactory.build(owner.activePet), false);
            owner.activePet = null;
        }
        refresh();
        return false;
    }
}
