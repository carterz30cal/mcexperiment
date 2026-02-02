package com.carterz30cal.gui;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.entities.player.PlayerWardrobe;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.stats.Stat;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

public class WardrobeGUI extends AbstractGUI {
    private static final int EQUIP_BUTTON_POS = calc(5, 4);
    private static final int BACK_BUTTON_POS = calc(4, 4);
    private static final int NEXT_BUTTON_POS = calc(6, 4);
    private int page;


    public WardrobeGUI(GamePlayer owner) {
        super(owner);

        page = owner.wardrobe.GetSelectedSlot() + 1;
        inventory = new GooeyInventory("Wardrobe", 6);
        owner.wardrobe.UpdateCurrentSlot();
        update();
    }

    @Override
    public boolean allowLeftClick(int clickPos, ItemStack current) {
        if (clickPos == EQUIP_BUTTON_POS) {
            boolean alreadySelected = page - 1 == owner.wardrobe.GetSelectedSlot();
            if (!alreadySelected) {
                owner.wardrobe.SwapSlot(page - 1);
                owner.playSound(Sound.ITEM_ARMOR_EQUIP_IRON, 0.5, 1);
            }
            else {
                owner.sendMessage("REDYou're already wearing this!");
            }
        }
        else if (clickPos == BACK_BUTTON_POS && page > 1) {
            page--;
            owner.playSound(Sound.BLOCK_DISPENSER_DISPENSE, 0.3, 1);
        }
        else if (clickPos == NEXT_BUTTON_POS && page < owner.stats.getStat(Stat.WARDROBE_SLOTS)) {
            page++;
            owner.playSound(Sound.BLOCK_DISPENSER_DISPENSE, 0.3, 1);
        }
        update();
        return false;
    }

    private void update() {
        inventory.initUsingTemplate(GooeyTemplate.PANED_DARK);
        PlayerWardrobe.WardrobeSlot wardrobe = owner.wardrobe.GetSlot(page - 1);

        for (int i = 0; i < 4; i++) {
            ItemStack original = wardrobe.GetArmour()[3 - i];
            if (original != null) {
                inventory.setSlot(original.clone(), calc(1, 1 + i));
            }
            else {
                inventory.setSlot(ItemFactory.buildCustom("RED_STAINED_GLASS_PANE", "REDEmpty Armour Slot"), calc(1, 1 + i));
            }
        }
        for (int i = 0; i < 5; i++) {
            if (wardrobe.GetTalismans().size() <= i) {
                inventory.setSlot(ItemFactory.buildCustom("RED_STAINED_GLASS_PANE", "REDEmpty Talisman Slot"), calc(3 + i, 1));
            }
            else {
                inventory.setSlot(ItemFactory.build(wardrobe.GetTalismans().get(i)), calc(3 + i, 1));
            }
        }
        if (page > 1) {
            inventory.setSlot(ItemFactory.buildCustom("ARROW", "GREENBack"), BACK_BUTTON_POS);
        }
        boolean current = page - 1 == owner.wardrobe.GetSelectedSlot();
        inventory.setSlot(ItemFactory.buildCustom(current ? "GREEN_CONCRETE" : "YELLOW_CONCRETE", current ? "GREENEquipped!" : "YELLOWClick to equip!"), EQUIP_BUTTON_POS);
        if (page < owner.stats.getStat(Stat.WARDROBE_SLOTS)) {
            inventory.setSlot(ItemFactory.buildCustom("ARROW", "GREENNext"), NEXT_BUTTON_POS);
        }
        inventory.update();
    }
}
