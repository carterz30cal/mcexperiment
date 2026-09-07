package com.carterz30cal.gui;

import com.carterz30cal.entities.enemies.core.EnemyBuilder;
import com.carterz30cal.entities.health.damage.DamageResistance;
import com.carterz30cal.entities.health.damage.DamageType;
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
 * @version 3
 * @since 1.0.0
 */
public class BestiaryGUI extends AbstractGUI {

    private static final Map<String, BestiaryCategory> categories = new HashMap<>();
    private static final Map<String, List<BestiaryCategory>> parents = new HashMap<>();
    private static final String[] files = {
            "waterway/bestiary", "necropolis/bestiary"
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

    public static void registerTypeIntoCategory(String eid, String cid) {
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
        unsorted.sort(Comparator.comparingLong(a -> -Objects.requireNonNull(EnemyBuilder.getBuilder(a)).getEnemyData().level));
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
        var builder = EnemyBuilder.getBuilder(mid);
        assert builder != null;
        var data = builder.getEnemyData();
        var health = builder.getHealthSystemBuilder();
        long kills = owner.getKills(mid);
        String name = "<white>[" + data.level + "] " + data.mmName;
        List<String> lore = new ArrayList<>();
        if (kills == 1) {
            lore.add("<dark_grey>1 kill");
        }
        else {
            lore.add("<dark_grey>" + StringUtils.addCommas(kills) + " kills");
        }
        lore.add("");
        lore.add("<grey>Health: <red>" + health.getMaxHealth() + Stat.HEALTH.getIcon());
        StringBuilder str = new StringBuilder();
        for (var dmg : data.damages.entrySet()) {
            str.append("<")
                    .append(dmg.getKey().getColour().asHexString())
                    .append(">")
                    .append(dmg.getValue())
                    .append("</")
                    .append(dmg.getKey().getColour().asHexString())
                    .append("> + ");
        }
        str.deleteCharAt(str.length() - 2);
        lore.add("<grey>Damage: </grey><dark_grey>" + str + "</dark_grey>");
        if (!data.souls.isEmpty()) {
            lore.add("<grey>Souls: </grey><aqua>" + data.getTotalSouls() + "</aqua>");
        }
        var resistances = new HashMap<DamageType, Long>();
        List<DamageType> weak = new ArrayList<>();
        List<DamageType> strong = new ArrayList<>();
        for (var resistance : DamageResistance.values()) {
            var r = builder.getEnemyData().stats.getOrDefault(resistance.getResistanceStat(), 0L);
            resistances.put(resistance.getDamageType(), resistances.getOrDefault(resistance.getDamageType(), 0L) + r);
        }
        for (var r : resistances.entrySet()) {
            var v = r.getValue();
            if (v > 0) {
                strong.add(r.getKey());
            }
            else if (v < 0) {
                weak.add(r.getKey());
            }
        }
        if (!weak.isEmpty() || !strong.isEmpty()) {
            lore.add("");
            StringBuilder sb = new StringBuilder();
            StringBuilder wb = new StringBuilder();
            for (var w : weak) {
                if (!wb.isEmpty()) {
                    wb.append(", ");
                }
                wb.append(w.getName());
            }
            for (var s : strong) {
                if (!sb.isEmpty()) {
                    sb.append(", ");
                }
                sb.append(s.getName());
            }
            if (!sb.isEmpty()) {
                lore.addAll(StringUtils.wrapText("<grey>Strong against " + sb + ".", 70));
            }
            if (!wb.isEmpty()) {
                lore.addAll(StringUtils.wrapText("<grey>Weak to " + wb + ".", 70));
            }
        }

        lore.add("");
        if (data.lootTable != null && !data.lootTable.GetLoot().isEmpty()) {
            lore.add("<gold>Drops:");
            boolean displayLuckMessage = false;
            for (var loot : data.lootTable.GetLoot()) {
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
                    var ch = (loot.chance[0] / (double) loot.chance[1]) * 100;
                    int dp = Math.max(1, (int) Math.log10(1 / ch) + 1);
                    drop = "<dark_grey>(<aqua>" + StringUtils.truncate(ch, dp) + "%</aqua>)</dark_grey>";
                }
                else {
                    drop = "";
                }
                lore.add("<dark_grey>-</dark_grey> " + main + (amount.isEmpty() ? "" : (" " + amount)) + " " + drop);
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
            name = config.getString("name", "<red>null</red>");
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
