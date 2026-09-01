package com.carterz30cal.gui;

import com.carterz30cal.brewing.PotionPacket;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.items.types.ItemPotion;
import com.carterz30cal.items.types.ItemPotionBottle;
import com.carterz30cal.items.types.ItemPotionIngredient;
import com.carterz30cal.utils.StringUtils;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class BrewingGUI extends AbstractGUI {
    private @Nullable ItemPotionBottle bottle;
    private final List<PotionPacket> packets = new ArrayList<>();
    /**
     * Fumes are made when all the elements can't fit inside the bottle's capacity, so
     * we output them into a fume item - this gets added to until we brew the potion, then
     * it is output as an additional product.
     */
    private final List<PotionPacket> fumes = new ArrayList<>();

    public BrewingGUI(GamePlayer owner) {
        super(owner);

        inventory = new GooeyInventory("Brewing Stand", 6);
        update();
    }

    @Override
    public void onClose() {
        if (bottle != null && !packets.isEmpty()) {
            var gunk = fumes.size() + packets.size();
            owner.sendMessage("<red>Your potion was ruined...");
            owner.sendMessage("<red>But you got " + gunk + " potion gunk!", 20);
            owner.giveItem(ItemFactory.build("potion_gunk", gunk));
        }
        else if (bottle != null) {
            owner.giveItem(ItemFactory.build(bottle));
            owner.sendMessage("<green>Refunded your empty potion bottle!");
        }
    }

    @Override
    public boolean allowLeftClick(int clickPos, ItemStack current) {
        if (clickPos > 53) {
            var item = ItemFactory.getItem(current);
            if (bottle == null && item instanceof ItemPotionBottle potionBottle) {
                current.setAmount(current.getAmount() - 1);
                bottle = potionBottle;
            }
            else if (bottle == null) {
                owner.sendMessage("<red>You can't do anything in the brewing stand without a bottle first!");
            }
            else {
                var full = packets.size() >= bottle.capacity;
                if (item instanceof ItemPotionIngredient ingredient) {
                    if (full) owner.sendMessage("<red>Your bottle is full!");
                    else {
                        for (var packet : ingredient.elements) {
                            var copy = new PotionPacket(packet);
                            if (packets.size() < bottle.capacity) packets.add(copy);
                            else fumes.add(copy);
                        }
                        current.setAmount(current.getAmount() - 1);
                    }
                }
                else if (item.type == ItemType.POTION_FUMES) {
                    if (full) owner.sendMessage("<red>Your bottle is full!");
                    else {
                        var fume = ItemFactory.getPotionPackets(current);
                        for (var packet : fume) {
                            var copy = new PotionPacket(packet);
                            if (packets.size() < bottle.capacity) packets.add(copy);
                            else {
                                copy.level(Math.max(1, copy.level()));
                                fumes.add(copy);
                            }
                        }
                        current.setAmount(current.getAmount() - 1);
                    }
                }
            }
        } else {
            if (clickPos == 13) {
                if (bottle != null) {
                    if (packets.isEmpty() && fumes.isEmpty()) {
                        owner.giveItem(ItemFactory.build(bottle));
                        bottle = null;
                        packets.clear();
                    }
                    else {
                        var solver = solve();
                        if (solver != null) {
                            if (solver.potion != null) {
                                owner.giveItem(
                                        ItemFactory.build(solver.potion),
                                        true
                                );
                            }
                            if (solver.gunk > 0) {
                                owner.giveItem(
                                        ItemFactory.build("potion_gunk", solver.gunk)
                                );
                            }
                            if (!fumes.isEmpty()) {
                                var bot = ItemFactory.build("leftover_fumes");
                                ItemFactory.setPotionPackets(bot, fumes);
                                ItemFactory.update(bot, owner.getItemContext());
                                owner.giveItem(bot);
                            }
                            bottle = null;
                            packets.clear();
                            fumes.clear();
                            owner.playSound(Sound.BLOCK_BREWING_STAND_BREW, 1, 1);
                        }
                    }
                }
            }
        }
        update();
        return false;
    }

    private void update() {
        inventory.initUsingTemplate(GooeyTemplate.PANED_DARK);
        if (bottle == null) {
            inventory.setSlot(
                    ItemFactory.customItem("ORANGE_STAINED_GLASS", "<gold>Insert a bottle!"),
                    calc(4, 1)
            );
        } else {
            var half = bottle.capacity / 2;
            var i = 0;
            for (int r = -half; r <= half; r++) {
                if (r == 0 && bottle.capacity % 2 == 0) continue;
                ItemStack item;
                if (i >= packets.size()) {
                    item = ItemFactory.customItem("GLASS", "<white>Empty!");
                } else {
                    item = packets.get(i).item();
                }
                inventory.setSlot(item, calc( 4 + r, 3));
                i++;
            }
            if (!fumes.isEmpty()) {
                var bot = ItemFactory.build("leftover_fumes");
                ItemFactory.setPotionPackets(bot, fumes);
                ItemFactory.update(bot, owner.getItemContext());
                inventory.setSlot(bot, calc(4, 4));
            }
            var solver = solve();
            if (solver == null) {
                inventory.setSlot(
                        ItemFactory.build(bottle),
                        calc(4, 1)
                );
            }
            else {
                inventory.setSlot(
                        ItemFactory.build(bottle),
                        calc(2, 1)
                );
                if (solver.potion != null) {
                    inventory.setSlot(
                            ItemFactory.customItem("LIME_STAINED_GLASS_PANE", ""),
                            calc(3, 1)
                    );
                    inventory.setSlot(
                            ItemFactory.customItem("LIME_CONCRETE", "<green>Brew!"),
                            calc(4, 1)
                    );
                    inventory.setSlot(
                            ItemFactory.customItem("LIME_STAINED_GLASS_PANE", ""),
                            calc(5, 1)
                    );
                    inventory.setSlot(
                            ItemFactory.build(solver.potion),
                            calc(6, 1)
                    );
                    if (solver.gunk > 0) {
                        inventory.setSlot(
                                ItemFactory.build("potion_gunk", solver.gunk),
                                calc(7, 1)
                        );
                    }
                }
                else if (solver.gunk > 0){
                    inventory.setSlot(
                            ItemFactory.customItem("ORANGE_STAINED_GLASS_PANE", ""),
                            calc(3, 1)
                    );
                    inventory.setSlot(
                            ItemFactory.customItem("ORANGE_CONCRETE", "<gold>Brew?",
                                    StringUtils.wrapText(
                                            "<red>This won't produce a usable potion, but you'll still get the potion gunk, if that's what you're after?",
                                            36
                                    )),
                            calc(4, 1)
                    );
                    inventory.setSlot(
                            ItemFactory.customItem("ORANGE_STAINED_GLASS_PANE", ""),
                            calc(5, 1)
                    );
                    inventory.setSlot(
                            ItemFactory.build("potion_gunk", solver.gunk),
                            calc(6, 1)
                    );
                }

            }
        }

        inventory.update();
    }

    /**
     * Solve the brewing for a potion result and gunk if there are spare elements.
     * @return a <code>BrewingResult</code> if one exists, otherwise <code>null</code>.
     * @since 1.0.0 [1]
     */
    private @Nullable BrewingResult solve() {
        if (bottle == null || packets.isEmpty()) return null;
        ItemPotion result;
        for (int length = bottle.capacity; length > 0; length--) {
            for (int i = 0; i < bottle.capacity - length; i++) {
                var recipe = new StringBuilder();
                for (int j = 0; j < length && j < packets.size(); j++) {
                    var packet = packets.get(j);
                    recipe.append(packet.element().name()).append("-").append(packet.level()).append(",");
                }
                recipe.deleteCharAt(recipe.length() - 1);
                result = ItemPotion.recipes.getOrDefault(recipe.toString(), null);
                if (result != null) {
                    var brew = new BrewingResult();
                    brew.potion = result;
                    brew.gunk = packets.size() - recipe.toString().split(",").length;
                    return brew;
                }
            }
        }
        var fail = new BrewingResult();
        fail.potion = null;
        fail.gunk = packets.size();
        return fail;
    }

    /**
     * @author carterz30cal
     * @version 1
     * @since 1.0.0 [1]
     */
    private static class BrewingResult {
        public @Nullable ItemPotion potion;
        public int gunk;
    }
}
