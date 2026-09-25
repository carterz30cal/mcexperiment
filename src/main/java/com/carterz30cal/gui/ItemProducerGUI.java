package com.carterz30cal.gui;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.entities.player.PlayerItemProducer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.ItemReqs;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.items.types.ItemIngredientGenerator;
import com.carterz30cal.utils.StringUtils;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * @author carterz30cal
 * @version 2
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
        if (clickPos < 54) {
            if (producer.level() == 0) {
                if (clickPos == calc(4, 3)) {
                    var reqs = producer.upgradeRequirements();
                    assert reqs != null;
                    var result = reqs.areRequirementsMet(owner);
                    if (result == ItemReqs.FailureReason.NONE) {
                        reqs.execute(owner);
                        owner.playSound(Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        owner.sendMessage("<gold><b>BOOM!</b></gold><gold> You've unlocked this factory!");
                    }
                    else if (result == ItemReqs.FailureReason.MISSING_ITEMS) {
                        owner.sendMessage("<red>You don't have the items required to unlock this factory!");
                    }
                    else if (result == ItemReqs.FailureReason.NOT_ENOUGH_COINS) {
                        owner.sendMessage("<red>You can't afford this!");
                    }
                }
            }
            else {
                if (clickPos == calc(6, 1)) {
                    if (producer.getGenerator() == null) {
                        return false;
                    }
                    var produced = producer.calculate(true);
                    if (produced.isEmpty()) {
                        owner.sendMessage("<red>Your factory hasn't produced anything yet!");
                    }
                    else {
                        var attempt = owner.giveItems(produced, true);
                        if (!attempt) {
                            owner.sendMessage("<red>Couldn't add all of your items, free up some space!");
                        }
                    }
                }
            }
        }
        else {
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
                if (producer.getUpgrades().size() >= 6) {
                    owner.sendMessage("<red>You already have the maximum of 6 upgrades!");
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

    /**
     * Provides the screen for a locked factory - before the player purchases it.
     *
     * @since 1.0.0 [2]
     */
    private void lockedUpdate() {
        inventory.initUsingTemplate(GooeyTemplate.PANED_DARK);
        var lore = "<grey>The <gold>Factory</gold> is your key to passively producing huge quantities of common ingredients, for both purpose and profit. To get started, "
                + "you're going to need to first <gold>unlock</gold> the factory, which costs quite a few coins! After that, you'll need to find or forge a production core in " +
                "order to produce anything!";
        inventory.setSlot(
                ItemFactory.customItem("OAK_SIGN", "<gold>Information!", StringUtils.wrapText(lore, 50)),
                calc(4, 1)
        );

        inventory.setSlot(
                upgradeItem("Unlock!"),
                calc(4, 3)
        );

        inventory.update();
    }

    /**
     * @param name the title of this item
     * @return a display item
     * @since 1.0.0 [2]
     */
    @SuppressWarnings("SameParameterValue")
    private @NotNull ItemStack upgradeItem(String name) {
        ItemStack result;
        var lore = new ArrayList<String>();
        var reqs = producer.upgradeRequirements();
        if (reqs == null) {
            result = ItemFactory.customItem("GOLD_BLOCK", "<gold>Cannot upgrade!", "<grey>This factory is at its max level!");
        }
        else {
            if (reqs.coins > 0) {
                lore.add(StringUtils.coins(reqs.coins));
            }
            for (var r : reqs.getItems()) {
                var item = ItemFactory.getItem(r);
                if (item == null) {
                    continue;
                }
                lore.add("<" + item.rarity.textColor.asHexString() + ">" + item.name + " <dark_grey>x" + reqs.getAmount(r));
            }
            lore.add("");
            var met = reqs.areRequirementsMet(owner);
            if (met == ItemReqs.FailureReason.NONE) {
                lore.add("<green>Click to " + name);
            }
            else if (met == ItemReqs.FailureReason.MISSING_ITEMS) {
                lore.add("<red>You're missing items for this!");
            }
            else if (met == ItemReqs.FailureReason.NOT_ENOUGH_COINS) {
                lore.add("<red>You don't have enough coins!");
            }
            var c = met == ItemReqs.FailureReason.NONE ? "<green>" : "<red>";
            result = ItemFactory.customItem(met == ItemReqs.FailureReason.NONE ? "LIME_CONCRETE" : "RED_CONCRETE", c + name, lore);
        }
        return result;
    }


    public void update() {
        inventory.initUsingTemplate(GooeyTemplate.PANED_DARK);

        if (producer.level() == 0) {
            lockedUpdate();
            return;
        }

        var lore = new ArrayList<String>();
        lore.add("<grey>The <gold>Factory</gold> is your key to producing large quantities of");
        lore.add("<grey>common ingredients passively, for purpose and for profit.");
        lore.add("<grey>To get started, find or forge a production core and perhaps");
        lore.add("<grey>a few upgrades, then wait for your riches!");
        inventory.setSlot(
                ItemFactory.customItem("OAK_SIGN", "<gold>Information!", lore),
                calc(2, 1)
        );

        var produced = producer.calculate(false);
        if (producer.getGenerator() == null) {
            lore.clear();
            lore.add("<grey>You need a production core for this factory");
            lore.add("<grey>to produce anything! Find one somewhere!");
            lore.add("");
            lore.add("<grey>Different cores produce different times");
            lore.add("<grey>at varying speeds. You may also install");
            lore.add("<grey>upgrades to your factory - up to 6.");
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

            if (produced.isEmpty()) {
                lore.add("<grey>Your factory hasn't produced anything since you");
                lore.add("<grey>last collected from it. Come back later!");
                lore.add("<grey>If you want more production, consider investing");
                if (!producer.getUpgrades().isEmpty()) {
                    lore.add("<grey>in some more factory upgrades!");
                }
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
        for (int i = 0; i < 6; i++) {
            int x = i % 3;
            int y = i / 3;
            if (i >= producer.getUpgrades().size()) inventory.setSlot(null, calc(x + 1, y + 3));
            else {
                var item = ItemFactory.build(producer.getUpgrades().get(i));
                inventory.setSlot(item, calc(x + 1, y + 3));
            }
        }
        var sorted = produced.entrySet().stream().sorted(Comparator.comparingLong(stringLongEntry -> -stringLongEntry.getValue())).toList();
        for (int j = 0; (sorted.size() < 7 && j < 6) || j < 5; j++) {
            int x = (j % 3) + 5;
            int y = (j / 3) + 3;
            var c = calc(x, y);
            if (j >= sorted.size()) {
                inventory.setSlot(null, c);
            }
            else {
                var item = ItemFactory.getItem(sorted.get(j).getKey());
                var stack = ItemFactory.customItem(sorted.get(j).getKey(), "<dark_grey>" + sorted.get(j).getValue() + "x <"
                        + item.rarity.textColor.asHexString() + ">"
                        + item.name);
                stack.setAmount(Math.toIntExact(Math.min(64, sorted.get(j).getValue())));
                inventory.setSlot(stack, c);
            }
        }
        if (sorted.size() > 6) {
            var remaining = new ArrayList<String>();
            for (int k = 5; k < sorted.size(); k++) {
                var item = ItemFactory.getItem(sorted.get(k).getKey());
                remaining.add("<dark_grey>" + sorted.get(k).getValue() + "x <"
                        + item.rarity.textColor.asHexString() + ">"
                        + item.name);
            }
            inventory.setSlot(ItemFactory.customItem("CHEST", "<green>Additional items!", remaining), calc(7, 4));
        }

        inventory.update();
    }
}
