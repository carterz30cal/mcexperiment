package com.carterz30cal.gui;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.stats.Stat;
import org.bukkit.inventory.ItemStack;

public class BackpackGUI extends AbstractGUI {
    public int page = 1;
    private boolean allowNextPage;
    public BackpackGUI(GamePlayer owner) {
        super(owner);

        inventory = new GooeyInventory("Backpack", 6);
        update();
    }

    private void update() {
        int j = (page - 1) * 9 * 5;

        for (int i = 0; i < 9*5;i++) {
            inventory.setSlot(owner.getBackpackItem(j + i), i);
        }
        for (int i = 0; i < 9; i++) {
            inventory.setSlot(ItemFactory.customItem("WHITE_STAINED_GLASS_PANE", ""), i + (9 * 5));
        }

        if (page > 1) {
            inventory.setSlot(ItemFactory.customItem("ARROW", "<green>Page " + (page - 1) + "</green>"), calc(2, 5));
        }
        if (page < owner.stats.getStat(Stat.BACKPACK_PAGES))
        {
            inventory.setSlot(ItemFactory.customItem("ARROW", "<green>Page " + (page + 1) + "</green>"), calc(6, 5));
            allowNextPage = true;
        }
        else allowNextPage = false;


        inventory.update();
    }

    private void savePage() {
        int i = (page - 1) * 9 * 5;
        inventory.updateUsingContents();
        for (int c = 0; c < 9*5; c++) {
            owner.setBackpackItem(i + c, inventory.getSlot(c));
        }
    }

    @Override
    public boolean allowClick(int clickPos, ItemStack clicked) {

        if (clickPos >= calc(0, 5) && clickPos < 54) {
            if (clickPos == calc(2, 5) && page > 1) {
                savePage();
                page--;
                update();
            }
            else if (clickPos == calc(6, 5) && allowNextPage) {
                savePage();
                page++;
                update();
            }
            return false;
        }
        else return true;
    }

    @Override
    public void onClose()
    {
        savePage();
    }

}
