package com.carterz30cal.gui;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.entities.Shop;
import com.carterz30cal.items.*;
import com.carterz30cal.utils.StringUtils;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopGUI extends AbstractGUI {
    private Shop shop;
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

        int adji = (page - 1) * 7 * 4;
        for (int i = 0; i < 7*4 && i < adji + shop.items.size(); i++) {
            inventory.setSlot(generateShopItem(shop.items.get(adji + i)), calc((i % 7) + 1, (i / 7) + 1));
            System.out.println((shop.items.get(adji + i)).item);

            recipes[calc((i % 7) + 1, (i / 7) + 1)] = shop.items.get(adji + i);
        }

        if (page > 1) inventory.setSlot(GooeyInventory.produceElement("ARROW", "GREENPage " + (page-1)), calc(1, 5));
        if (shop.items.size() > page * 7*4)
        {
            inventory.setSlot(GooeyInventory.produceElement("ARROW", "GREENPage " + (page+1)), calc(5, 5));
            allowNextPage = true;
        }
        else allowNextPage = false;


        inventory.update();
    }

    private ItemStack generateShopItem(Recipe recipe) {

        long pLevel = owner.getLevel();

        List<String> requirements = new ArrayList<>();
        if (recipe.levelRequirement > pLevel) {

            return ItemFactory.buildCustom("RED_STAINED_GLASS_PANE", "REDLocked!", "REDCan't purchase this until WHITELevel " + recipe.levelRequirement);
        }

        ItemStack base = ItemFactory.build(recipe.item);
        String data;
        if (recipe.enchants != null) data = "enchants:" + recipe.enchants;
        else data = "";
        ItemFactory.setItemData(base, data);
        ItemFactory.update(base, null);

        ItemMeta meta = base.getItemMeta();

        List<String> lore = meta.getLore();
        lore.add("");
        lore.add("GOLDBOLDCost");
        if (recipe.coinCost != 0) lore.add("DARK_PURPLE- " + (recipe.coinCost == 1 ? "GOLD1 Coin" : "GOLD" + recipe.coinCost + " Coins"));
        for (String item : recipe.items.keySet())
        {
            int amountInSack = owner.sack.getOrDefault(item, 0);

            StringBuilder builder = new StringBuilder();
            builder.append("- ");
            builder.append(ItemFactory.getItemTypeName(item));
            builder.append(" DARK_GRAYx");
            builder.append(recipe.items.get(item));
            if (ItemFactory.getItem(item).type == ItemType.INGREDIENT) {
                builder.append("  [Sack has x");
                if (amountInSack >= recipe.items.get(item)) builder.append("GREEN");
                else builder.append("RED");

                builder.append(amountInSack);
                builder.append("DARK_GRAY]");
            }

            lore.add(builder.toString());
        }

        meta.setLore(StringUtils.colourList(lore));
        base.setItemMeta(meta);

        return base;
    }


    public boolean allowClick(int clickPos, ItemStack clicked) {
        if (clickPos >= 54) {
            if (clicked != null) {
                Item item = ItemFactory.getItem(clicked);
                if (item != null && item.value > 0) {
                    String data = ItemFactory.getFlatItemData(clicked);
                    //System.out.println(data);
                    if (ItemFactory.isItemBaseModel(clicked)) {
                        int am = clicked.getAmount();
                        clicked.setAmount(0);
                        owner.coins += am * item.value;
                    }
                    else owner.sendMessage("REDYou can't sell modified items to the shop.");
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
                owner.sendMessage("REDYou aren't at a high enough level to buy this!");
                owner.playSound(Sound.ENTITY_CREEPER_HURT, 0.4, 0.9);

            }
            else if (owner.player.getInventory().firstEmpty() == -1) {
                owner.sendMessage("REDFree up some inventory space!");
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
                    if (recipe.enchants == null && !data.equals("")) item.data = data;
                    owner.giveItem(item.produce());
                } else {
                    //for (String k : requirements.reqs.keySet()) System.out.println(k);
                    owner.sendMessage("REDYou can't buy this item!");
                    owner.playSound(Sound.ENTITY_CREEPER_HURT, 0.8, 0.6);
                }
            }
        }

        return false;
    }
}
