package com.carterz30cal.gui;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.abilities.implementation.GameAbstractEnchant;
import com.carterz30cal.utils.StringUtils;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;

import static net.kyori.adventure.text.Component.text;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class EnchantGuideGUI extends AbstractGUI {
    private int page = 1;

    public EnchantGuideGUI(GamePlayer owner) {
        super(owner);

        inventory = new GooeyInventory("Enchantment Guide", 6);
        refresh();
    }

    private void refresh() {
        inventory.initUsingTemplate(GooeyTemplate.SHOPPY_DARK);

        for (int i = (page - 1) * 28; i < page * 28 && i < GameAbstractEnchant.enchants.size(); i++) {
            var enchant = GameAbstractEnchant.enchants.get(i);
            if (enchant == null) {
                continue;
            }
            var ability = (GameAbstractEnchant) enchant.ability;
            var item = ItemFactory.buildItemFromString("enchanted_book£1£enchants:" + enchant.name() + "-" + ability.getMaximumLevel());
            item.editMeta((meta) -> {
                var lore = meta.lore();
                assert lore != null;
                lore.add(text().build());
                lore.add(text().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize("<gold>How to obtain")).build());
                var wrapped = StringUtils.wrapText("<grey>" + ability.obtaining(), 25);
                for (var text : wrapped) {
                    lore.add(text().decoration(TextDecoration.ITALIC, false).append(MiniMessage.miniMessage().deserialize(text)).build());
                }
                meta.lore(lore);
            });
            inventory.setSlot(item, calc(i % 7 + 1, i / 7 + 1));
        }
        if (page > 1) {
            inventory.setSlot(ItemFactory.customItem("ARROW", "<green>Previous Page"), calc(1, 5));
        }
        if (page * 28 < GameAbstractEnchant.enchants.size()) {
            inventory.setSlot(ItemFactory.customItem("ARROW", "<green>Next Page"), calc(7, 5));
        }
        inventory.setSlot(ItemFactory.customItem("ARROW", "<gold>Back to menu"), calc(4, 5));
        inventory.update();
    }

    @Override
    public boolean allowLeftClick(int clickPos, ItemStack current) {
        if (clickPos == calc(1, 5)) {
            page--;
        }
        else if (clickPos == calc(7, 5)) {
            page++;
        }
        else if (clickPos == calc(4, 5)) {
            owner.openGui(new MenuGUI(owner));
            return false;
        }
        refresh();
        return false;
    }
}
