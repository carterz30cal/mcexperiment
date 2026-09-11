package com.carterz30cal.gui;

import com.carterz30cal.entities.Shop;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.*;
import com.carterz30cal.items.discoveries.Collection;
import com.carterz30cal.items.discoveries.DiscoveryManager;
import com.carterz30cal.items.recipes.Recipe;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static net.kyori.adventure.text.format.NamedTextColor.*;

public class ShopGUI extends AbstractGUI {
    private final Shop shop;
    private int page;

    private Recipe[] recipes;
    private boolean allowNextPage;

    public ShopGUI(GamePlayer owner, Shop shop) {
        super(owner);
        this.shop = shop;
        this.page = 1;

        inventory = new GooeyInventory(shop.shopName, 6);
        inventory.initUsingTemplate(GooeyTemplate.SHOPPY);

        update();
    }

    private void update() {
        recipes = new Recipe[54];

        int offset = (page - 1) * 7 * 4;
        for (int i = 0; i < 7 * 4 && i < offset + shop.items.size(); i++) {
            inventory.setSlot(generateShopItem(shop.items.get(offset + i)), calc((i % 7) + 1, (i / 7) + 1));
            recipes[calc((i % 7) + 1, (i / 7) + 1)] = shop.items.get(offset + i);
        }

        if (page > 1) {
            inventory.setSlot(
                    ItemFactory.customItem(
                            "ARROW",
                            "Page " + (page - 1),
                            GREEN),
                    calc(1, 5));
        }
        if (shop.items.size() > page * 7*4)
        {
            inventory.setSlot(
                    ItemFactory.customItem(
                            "ARROW",
                            "Page " + (page + 1),
                            GREEN),
                    calc(5, 5));
            allowNextPage = true;
        }
        else allowNextPage = false;


        inventory.update();
    }

    private ItemStack generateShopItem(Recipe recipe) {

        long pLevel = owner.getLevel();

        if (recipe.levelRequirement > pLevel) {
            return ItemFactory.buildCustom(
                    "RED_STAINED_GLASS_PANE",
                    "Locked!",
                    RED,
                    text().append(text("Can't purchase this until ", RED))
                            .append(text("Level " + recipe.levelRequirement, WHITE)));
        }

        ItemStack base = ItemFactory.build(recipe.item);
        String data;
        if (recipe.enchants != null) data = "enchants:" + recipe.enchants;
        else data = "";
        ItemFactory.setItemData(base, data);
        ItemFactory.update(base, ItemFactory.FactoryBuildContext.NULL);

        ItemMeta meta = base.getItemMeta();
        var lore = meta.lore();
        assert lore != null;

        lore.add(text().content("").build());
        lore.add(text("This shopkeeper wants:", GOLD).decorate(TextDecoration.BOLD));
        if (recipe.coinCost != 0) {
            lore.add(text().append(text("- ", DARK_GRAY))
                    .append(text(recipe.coinCost == 1 ? "1 coin" : recipe.coinCost + " coins", GOLD))
                    .build());
        }
        for (String item : recipe.items.keySet())
        {
            long amountInSack = owner.sack.getOrDefault(item, 0L);
            var l = text().append(text("- ", DARK_GRAY))
                    .append(ItemFactory.getItemNameBuilder(item))
                    .append(text(" x" + recipe.items.get(item), DARK_GRAY));

            if (ItemFactory.getItem(item).type == ItemType.INGREDIENT) {
                l.append(text(" [Sack has x", DARK_GRAY));
                l.append(text(
                        String.valueOf(amountInSack),
                        amountInSack >= recipe.items.get(item) ? GREEN : RED));
                l.append(text("]", DARK_GRAY));
            }

            lore.add(l.build());
        }
        meta.lore(lore);
        base.setItemMeta(meta);

        return base;
    }

    private TextComponent.Builder text() {
        return Component.text().decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    private TextComponent text(String msg, NamedTextColor colour) {
        return text().content(msg).color(colour).build();
    }


    public boolean allowClick(int clickPos, ItemStack clicked) {
        if (clickPos >= 54) {
            if (clicked != null) {
                Item item = ItemFactory.getItem(clicked);
                if (item != null && item.value > 0) {
                    if (ItemFactory.isItemBaseModel(clicked)) {
                        int am = clicked.getAmount();
                        clicked.setAmount(0);
                        owner.gainCoins(am * item.value);
                    }
                    else owner.sendMessage("<red>You can't sell modified items to the shop.");
                }
            }
            return false;
        }

        if (clickPos == calc(1, 5) && page > 1) {
            page--;
            update();
        } else if (clickPos == calc(5, 5) && allowNextPage) {
            page++;
            update();
        } else {
            if (recipes[clickPos] == null) return false;

            Recipe recipe = recipes[clickPos];
            boolean collectionUnlocked = true;
            if (recipe.discoveryReq != null) {
                Collection col = DiscoveryManager.get(recipe.discoveryReq);
                if (owner.getDiscoveryLevel(col) < recipe.discoveryReqLevel) {
                    collectionUnlocked = false;
                }
            }


            if (recipe.levelRequirement > owner.getLevel() || !collectionUnlocked) {
                owner.sendMessage("<red>You aren't at a high enough level to buy this!");
                owner.playSound(Sound.ENTITY_CREEPER_HURT, 0.4, 0.9);

            }
            else if (owner.player.getInventory().firstEmpty() == -1) {
                owner.sendMessage("<red>Free up some inventory space!");
                owner.playSound(Sound.ENTITY_CREEPER_HURT, 0.4, 0.9);
            }else {
                ItemReqs requirements = new ItemReqs();
                requirements.coins = recipe.coinCost;
                for (String i : recipe.items.keySet()) requirements.addRequirement(new ItemReq(i, recipe.items.get(i)));

                if (requirements.areRequirementsMet(owner)) {
                    String data = requirements.grabDataFromRequirements(owner);
                    requirements.execute(owner);

                    owner.playSound(Sound.ENTITY_VILLAGER_TRADE, 0.9, 1.1);

                    ForgingItem item = new ForgingItem(recipe);
                    if (recipe.enchants == null && !data.isEmpty()) {
                        item.data = data;
                    }
                    owner.giveItem(item.produce(), false);
                } else {
                    owner.sendMessage("<red>You can't buy this item!");
                    owner.playSound(Sound.ENTITY_CREEPER_HURT, 0.8, 0.6);
                }
            }
        }

        return false;
    }
}
