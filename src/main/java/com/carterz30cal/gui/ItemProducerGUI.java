package com.carterz30cal.gui;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.entities.player.PlayerItemProducer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.items.types.ItemIngredientGenerator;
import com.carterz30cal.main.Dungeons;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class ItemProducerGUI extends AbstractGUI {
    private final GamePlayer owner;
    private final PlayerItemProducer producer;
    private int tick;
    public ItemProducerGUI(GamePlayer owner, PlayerItemProducer producer) {
        super(owner);

        this.owner = owner;
        this.producer = producer;
        this.inventory = new GooeyInventory("Factory", 6);

        update();
    }

    @Override
    public void onTick() {
        super.onTick();

        if (tick % 20*5 == 0) {
            tick = 1;
            update();
        }
        else tick++;
    }

    @Override
    public boolean allowLeftClick(int clickPos, ItemStack current) {
        if (clickPos == calc(6, 1)) {
            if (producer.getGenerator() == null) return false;
            var produced = producer.calculate(true);
            if (produced.isEmpty()) {
                owner.sendMessage("<red>Your factory hasn't produced anything yet!");
            }
            else owner.giveItems(produced, true);
        }
        else if (clickPos > 53) {
            var item = ItemFactory.getItem(current);
            if (item == null) return false;
            else if (item instanceof ItemIngredientGenerator generator) {
                if (producer.getGenerator() == null) {
                    producer.setGenerator(generator);
                    current.setAmount(0);
                    producer.reset();
                }
                else {
                    var stack = ItemFactory.build(producer.getGenerator());
                    owner.giveItem(stack, false);
                    owner.giveItems(producer.calculate(true), true);
                }
            }
            else if (item.type == ItemType.FACTORY_UPGRADE) {
                if (producer.getUpgrades().size() > 14) {
                    owner.sendMessage("<red>You already have the maximum of 14 upgrades!");
                }
                else {
                    producer.addUpgrade(item);
                    current.setAmount(current.getAmount() - 1);
                }
            }
        }
        tick = 1;
        update();
        return false;
    }

    public void update() {
        inventory.initUsingTemplate(GooeyTemplate.PANED_DARK);

        var lore = new ArrayList<String>();
        lore.add("<grey>The <gold>Factory</gold> is your key to producing large quantities of");
        lore.add("<grey>common ingredients passively, for purpose and for profit.");
        lore.add("<grey>To get started, find or forge a production core and perhaps");
        lore.add("<grey>a few upgrades, then wait for your riches!");
        inventory.setSlot(
                ItemFactory.customItem("OAK_SIGN", "<gold>Information!", lore),
                calc(2, 1)
        );

        if (producer.getGenerator() == null) {
            lore.clear();
            lore.add("<grey>You need a production core for this factory");
            lore.add("<grey>to produce anything! Find one somewhere!");
            lore.add("");
            lore.add("<grey>Different cores produce different times");
            lore.add("<grey>at varying speeds. You may also install");
            lore.add("<grey>upgrades to your factory - up to 20.");
            inventory.setSlot(
                     ItemFactory.customItem("RED_STAINED_GLASS_PANE", "<red>No core!", lore),
                    calc(4, 1)
            );

            lore.clear();
            lore.add("<grey>This factory isn't producing anything!");
            inventory.setSlot(
                    ItemFactory.customItem("RED_CONCRETE", "<red>Collect", lore),
                    calc(6, 1)
            );
        }
        else {
            inventory.setSlot(
                    ItemFactory.build(producer.getGenerator()),
                    calc(4, 1)
            );

            lore.clear();
            var produced = producer.calculate(false);
            if (produced.isEmpty()) {
                lore.add("<grey>Your factory hasn't produced anything since you");
                lore.add("<grey>last collected from it. Come back later!");
                lore.add("<grey>If you want more production, consider investing");
                if (!producer.getUpgrades().isEmpty()) lore.add("<grey>in some more upgrades!");
                else lore.add("<grey>in factory upgrades, which offer various benefits to you.");

                inventory.setSlot(
                        ItemFactory.customItem("YELLOW_CONCRETE", "<yellow>Collect", lore),
                        calc(6, 1)
                );
            }
            else {
                lore.add("<grey>The fruits of your factory are ready to be picked!");
                lore.add("");
                lore.add("<#FFA500>Production");
                for (var produce : produced.entrySet()) {
                    var item = ItemFactory.getItem(produce.getKey());
                    lore.add(" <" + item.rarity.textColor.asHexString() + ">" + item.name + "<dark_grey> x" + produce.getValue());
                }
                lore.add("");
                lore.add("<gold>Click to collect these items!");

                inventory.setSlot(
                        ItemFactory.customItem("LIME_CONCRETE", "<green>Collect", lore),
                        calc(6, 1)
                );
            }

        }
        for (int i = 0; i < 14; i++) {
            int x = i % 7;
            int y = i / 7;
            if (i >= producer.getUpgrades().size()) inventory.setSlot(null, calc(x + 1, y + 3));
            else {
                var item = ItemFactory.build(producer.getUpgrades().get(i));
                inventory.setSlot(item, calc(x + 1, y + 3));
            }
        }
        inventory.update();
    }
}
