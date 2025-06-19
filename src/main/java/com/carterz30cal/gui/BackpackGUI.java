package com.carterz30cal.gui;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.*;
import com.carterz30cal.stats.Stat;
import org.bukkit.Sound;
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
        int adji = (page - 1) * 9 * 5;

        for (int i = 0; i < 9*5;i++) {
            inventory.setSlot(owner.getBackpackItem(adji + i), i);
        }
        for (int i = 0; i < 9; i++) {
            inventory.setSlot(GooeyInventory.produceElement("WHITE_STAINED_GLASS_PANE", ""), i + (9*5));
        }

        if (page > 1) inventory.setSlot(GooeyInventory.produceElement("ARROW", "GREENPage " + (page-1)), calc(1, 5));
        if (page < owner.stats.getStat(Stat.BACKPACK_PAGES))
        {
            inventory.setSlot(GooeyInventory.produceElement("ARROW", "GREENPage " + (page+1)), calc(5, 5));
            allowNextPage = true;
        }
        else allowNextPage = false;


        inventory.update();
    }

    private void savePage() {
        int i = (page - 1) * 9 * 5;
        inventory.updateUsingContents();
        for (int c = 0; c < 9*5; c++) {
            //if (inventory.getSlot(c) == null || ItemFactory.getItem(inventory.getSlot(c)) == null) continue;

            owner.setBackpackItem(i + c, inventory.getSlot(c));
        }
    }

    @Override
    public boolean allowClick(int clickPos, ItemStack clicked) {

        if (clickPos >= calc(0, 5) && clickPos < 54) {
            if (clickPos == calc(1, 5) && page > 1) {
                savePage();
                page--;
                update();
            }
            else if (clickPos == calc(7, 5) && allowNextPage) {
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
