package com.carterz30cal.gui;

import com.carterz30cal.entities.AbstractEnemyType;
import com.carterz30cal.entities.EnemyManager;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.Item;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.utils.FileUtils;
import com.carterz30cal.utils.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BestiaryGUI extends AbstractGUI {

    private static final Map<String, BestiaryCategory> categories = new HashMap<>();
    private static final Map<String, List<BestiaryCategory>> parents = new HashMap<>();
    private static final String[] files = {
            "waterway2/bestiary"
    };

    static {
        categories.put("base", new BestiaryCategory());
        for (String file : files) {
            FileConfiguration c = FileUtils.getData(file);
            assert c != null;
            for (String p : c.getKeys(false)) {
                BestiaryCategory category = new BestiaryCategory(p, Objects.requireNonNull(c.getConfigurationSection(p)));
                categories.put(p, category);
                parents.putIfAbsent(category.parent, new ArrayList<>());
                parents.get(category.parent).add(category);
            }
        }
    }

    private int page;
    private String[] clickableCategories;
    private String category;

    public BestiaryGUI(GamePlayer owner) {
        super(owner);

        inventory = new GooeyInventory("Bestiary", 6);
        page = 1;
        category = "base";

        update();
    }

    public static void RegisterTypeIntoCategory(String eid, String cid) {
        categories.getOrDefault(cid, categories.get("base")).types.add(eid);
    }

    private void update() {

        clickableCategories = new String[54];

        List<String> mids = new ArrayList<>();
        parents.getOrDefault(category, new ArrayList<>()).stream().map(kategory -> kategory.id).forEach(mids::add);
        List<String> unsorted = new ArrayList<>(categories.get(category).types);
        unsorted.sort(Comparator.comparingInt(a -> -EnemyManager.getType(a).level));
        mids.addAll(unsorted);
        inventory.initUsingTemplate(GooeyTemplate.SHOPPY_DARK);
        for (int i = 0; i < 7 * 4; i++) {
            int dex = ((page - 1) * (7 * 4)) + i;
            if (dex >= mids.size()) {
                break;
            }

            int point = calc(i % 7 + 1, i / 7 + 1);

            if (categories.containsKey(mids.get(dex))) {
                BestiaryCategory category = categories.get(mids.get(dex));
                clickableCategories[point] = category.id;
                inventory.setSlot(generateCategory(mids.get(dex)), point);
            }
            else {
                if (owner.kills.getOrDefault(mids.get(dex), 0L) == 0L) {
                    inventory.setSlot(ItemFactory.buildCustom("BEDROCK", "REDNot yet found!"), point);
                }
                else {
                    inventory.setSlot(generateBestiaryEntry(mids.get(dex)), point);
                }
            }


        }

        inventory.setSlot(generateCategory(category), calc(4, 0));
        if (!category.equals("base")) {
            inventory.setSlot(ItemFactory.buildCustom("ARROW", "GREENParent Category"), calc(3, 0));
        }
        if (page > 1) {
            inventory.setSlot(ItemFactory.buildCustom("ARROW", "GREENPrevious Page"), calc(1, 5));
        }
        if (mids.size() - (page * 28) > 0) {
            inventory.setSlot(ItemFactory.buildCustom("ARROW", "GREENNext Page"), calc(7, 5));
        }


        inventory.update();
    }

    private ItemStack generateCategory(String cid) {
        BestiaryCategory category = categories.get(cid);
        return ItemFactory.buildCustom(category.icon, "WHITE" + category.name, category.description);
    }

    private ItemStack generateBestiaryEntry(String mid) {
        AbstractEnemyType type = EnemyManager.getType(mid);
        long kills = owner.GetKills(mid);
        String name = "WHITE[" + type.level + "WHITE] " + type.name + " " + owner.GetBestiaryLevel(mid);
        String colour = owner.GetBestiaryLevel(mid) == 5 ? "GOLD" : "GREEN";
        List<String> lore = new ArrayList<>();
        if (kills == 1) {
            lore.add("DARK_GRAY1 kill");
        }
        else {
            lore.add("DARK_GRAY" + StringUtils.commaify(kills) + " kills");
        }
        lore.add("");
        lore.add("GRAYHealth: RED" + type.health + Stat.HEALTH.getIcon());
        lore.add("GRAYDamage: RED" + type.damage + Stat.DAMAGE.getIcon() + " DARK_GRAY[" + type.damageType.toString() + "DARK_GRAY]");
        lore.add("");
        if (!type.loot.GetLoot().isEmpty()) {
            lore.add("GOLDDrops:");
            for (var loot : type.loot.GetLoot()) {
                Item item = ItemFactory.getItem(loot.item);
                if (item == null) {
                    continue;
                }
                String main = item.rarity.colour + item.name;
                String drop;
                String amount;
                if (loot.amount[0] == loot.amount[1]) {
                    if (loot.amount[0] == 1) {
                        amount = "";
                    }
                    else {
                        amount = "GRAY" + loot.amount[0] + "x ";
                    }
                }
                else {
                    amount = "GRAY" + loot.amount[0] + "-" + loot.amount[1] + "x ";
                }
                if ((double) loot.chance[0] / loot.chance[1] < 0.99) {
                    drop = " DARK_GRAY(AQUA" + StringUtils.asPercent2DP((double) loot.chance[0] / loot.chance[1]) + "DARK_GRAY)";
                }
                else {
                    drop = "";
                }
                lore.add("GRAY- " + amount + main + drop);
            }
            lore.add("DARK_GRAYLuck not applied here!");
        }
        else {
            lore.add("REDThis creature doesn't drop anything!");
        }


        return ItemFactory.buildCustom("BONE", colour + name, lore);
    }

    @Override
    public boolean allowLeftClick(int clickPos, ItemStack current) {
        if (clickPos == calc(3, 0) && !category.equals("base")) {
            category = categories.get(category).parent;
        }
        else if (clickPos == calc(1, 5) && page > 1) {
            page--;
        }
        else if (clickPos == calc(7, 5)) {
            page++;
        }
        else if (clickPos < 54) {
            if (clickableCategories[clickPos] != null) {
                category = clickableCategories[clickPos];
            }
        }

        update();
        return false;
    }

    private static class BestiaryCategory {
        public String id;
        public String icon;
        public String name;
        public List<String> description;
        public String parent;
        public List<String> types;

        private BestiaryCategory(String id, ConfigurationSection config) {
            this.id = id;
            icon = config.getString("icon", "BEDROCK");
            name = config.getString("name", "REDnull");
            description = new ArrayList<>();
            config.getStringList("description").forEach(s -> description.add("GRAY" + s));
            parent = config.getString("parent", "base");
            types = new ArrayList<>();
        }

        private BestiaryCategory() {
            icon = "LEAD";
            name = "Bestiary";
            description = new ArrayList<>();
            parent = null;
            types = new ArrayList<>();
        }
    }
}
