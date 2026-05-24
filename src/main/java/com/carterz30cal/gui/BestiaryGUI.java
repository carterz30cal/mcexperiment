package com.carterz30cal.gui;

import com.carterz30cal.entities.AbstractEnemyType;
import com.carterz30cal.entities.enemies.core.EnemyManager;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.Item;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.utils.FileUtils;
import com.carterz30cal.utils.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class BestiaryGUI extends AbstractGUI {

    private static final Map<String, BestiaryCategory> categories = new HashMap<>();
    private static final Map<String, List<BestiaryCategory>> parents = new HashMap<>();
    private static final String[] files = {
            "waterway/bestiary"
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
        if (cid.equals("NO_REGISTER")) {
            return;
        }
        categories.getOrDefault(cid, categories.get("base")).types.add(eid);
    }

    private void update() {

        clickableCategories = new String[54];

        List<String> list = new ArrayList<>();
        parents.getOrDefault(category, new ArrayList<>()).stream().map(kategory -> kategory.id).forEach(list::add);
        List<String> unsorted = new ArrayList<>(categories.get(category).types);
        unsorted.sort(Comparator.comparingInt(a -> -EnemyManager.getType(a).level));
        list.addAll(unsorted);
        inventory.initUsingTemplate(GooeyTemplate.SHOPPY_DARK);
        for (int i = 0; i < 7 * 4; i++) {
            int dex = ((page - 1) * (7 * 4)) + i;
            if (dex >= list.size()) {
                break;
            }

            int point = calc(i % 7 + 1, i / 7 + 1);

            if (categories.containsKey(list.get(dex))) {
                BestiaryCategory category = categories.get(list.get(dex));
                clickableCategories[point] = category.id;
                inventory.setSlot(generateCategory(list.get(dex)), point);
            }
            else {
                if (owner.kills.getOrDefault(list.get(dex), 0L) == 0L) {
                    inventory.setSlot(ItemFactory.customItem("BEDROCK", "<red>Not yet found!</red>"), point);
                }
                else {
                    inventory.setSlot(generateBestiaryEntry(list.get(dex)), point);
                }
            }


        }

        inventory.setSlot(generateCategory(category), calc(4, 0));
        if (!category.equals("base")) {
            inventory.setSlot(ItemFactory.customItem("ARROW", "<green>Parent Category</green>"), calc(3, 0));
        }
        if (page > 1) {
            inventory.setSlot(ItemFactory.customItem("ARROW", "<green>Previous Page</green>"), calc(1, 5));
        }
        if (list.size() - (page * 28) > 0) {
            inventory.setSlot(ItemFactory.customItem("ARROW", "<green>Next Page</green>"), calc(7, 5));
        }


        inventory.update();
    }

    private ItemStack generateCategory(String cid) {
        BestiaryCategory category = categories.get(cid);
        return ItemFactory.customItem(category.icon, "<white>" + category.name, category.description);
    }

    private ItemStack generateBestiaryEntry(String mid) {
        AbstractEnemyType type = EnemyManager.getType(mid);
        long kills = owner.getKills(mid);
        String name = "<white>[" + type.level + "] " + type.name;
        List<String> lore = new ArrayList<>();
        if (kills == 1) {
            lore.add("<dark_grey>1 kill");
        }
        else {
            lore.add("<dark_grey>" + StringUtils.addCommas(kills) + " kills");
        }
        lore.add("");
        lore.add("<grey>Health: <red>" + type.health + Stat.HEALTH.getIcon());
        lore.add("<grey>Damage: <red>" + type.damage + Stat.DAMAGE.getIcon() + "</red> <dark_grey>[" + type.damageType.toString() + "]</dark_grey></grey>");
        lore.add("");
        if (!type.loot.GetLoot().isEmpty()) {
            lore.add("<gold>Drops:");
            boolean displayLuckMessage = false;
            for (var loot : type.loot.GetLoot()) {
                Item item = ItemFactory.getItem(loot.item);
                if (item == null) {
                    continue;
                }
                String main = "<" + item.rarity.textColor.asHexString() + ">" + item.name + "</" + item.rarity.textColor.asHexString() + ">";
                String drop;
                String amount;
                if (loot.amount[0] == loot.amount[1]) {
                    if (loot.amount[0] == 1) {
                        amount = "";
                    }
                    else {
                        amount = "<dark_grey>x" + loot.amount[0] + "</dark_grey>";
                    }
                }
                else {
                    amount = "<dark_grey>x" + loot.amount[0] + "-" + loot.amount[1] + "</dark_grey>";
                }
                if ((double) loot.chance[0] / loot.chance[1] < 0.99) {
                    displayLuckMessage = true;
                    drop = "<dark_grey>(<aqua>" + StringUtils.asPercent2DP((double) loot.chance[0] / loot.chance[1]) + "</aqua>)</dark_grey>";
                }
                else {
                    drop = "";
                }
                lore.add("<dark_grey>-</dark_grey> " + main + " " + amount + " " + drop);
            }
            if (displayLuckMessage) {
                lore.add("<dark_grey>Drop chances are displayed as base rates");
                lore.add("<dark_grey>so they may be higher in reality if");
                lore.add("<dark_grey>you have any of the luck stat.");
            }
        }
        else {
            lore.add("<red>This creature doesn't drop anything!</red>");
        }

        return ItemFactory.customItem("BONE", name, lore);
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
            description = config.getStringList("description");
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
