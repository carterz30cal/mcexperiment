package com.carterz30cal.areas.quests2;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.utils.FileUtils;
import com.carterz30cal.utils.StringUtils;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public final class QuestCollection {
    private static final String[] files = {"waterway/quests/collections"};
    private static final Map<String, QuestCollection> collections = new HashMap<>();
    private String name;
    private String id;
    private String description;
    private List<String> quests;

    public static void init() {
        for (var file : files) {
            var c = FileUtils.getData(file);
            if (c == null) {
                continue;
            }

            for (var key : c.getKeys(false)) {
                var section = c.getConfigurationSection(key);
                var collection = new QuestCollection();
                collection.quests = section.getStringList("quests");
                collection.name = section.getString("name", "null");
                collection.description = "<grey>" + section.getString("description");
                collection.id = key;
                collections.put(key, collection);
            }
        }
    }

    public static List<QuestCollection> visibleList(GamePlayer owner) {
        List<QuestCollection> list = new ArrayList<>();
        for (var c : collections.values()) {
            if (c.visible(owner)) {
                list.add(c);
            }
        }
        return list;
    }

    public static @Nullable QuestCollection get(String cid) {
        return collections.get(cid);
    }

    public String id() {
        return id;
    }

    public int completed(GamePlayer owner) {
        int count = 0;
        for (var q : quests) {
            if (owner.questing.finished(q)) {
                count++;
            }
        }
        return count;
    }

    public boolean complete(GamePlayer owner) {
        return completed(owner) == quests.size();
    }

    public boolean visible(GamePlayer owner) {
        for (var q : quests) {
            if (owner.questing.finished(q) || owner.questing.started(q)) {
                return true;
            }
        }
        return false;
    }

    public boolean selected(GamePlayer owner) {
        if (owner.questing.selected() == null) {
            return false;
        }
        else {
            return quests.contains(Objects.requireNonNull(owner.questing.selected()).id());
        }
    }

    public List<String> incomplete(GamePlayer owner) {
        List<String> qs = new ArrayList<>();
        for (var q : quests) {
            if (owner.questing.started(q) && !owner.questing.finished(q)) {
                qs.add(q);
            }
        }
        return qs;
    }

    public long xp(GamePlayer owner) {
        long xp = 0;
        for (var q : quests) {
            if (owner.questing.finished(q)) {
                xp += Objects.requireNonNull(QuestData.get(q)).reward.xp();
            }
        }
        return xp;
    }

    public ItemStack item(GamePlayer owner) {
        var lore = new ArrayList<String>();
        var finished = completed(owner);
        if (finished == quests.size()) {
            lore.add("<grey>You have completed all <green>" + finished + "</green> quests!");
        }
        else if (finished == 0) {
            lore.add("<grey>You have finished <red>0</red>/" + quests.size() + " quests!");
        }
        else {
            lore.add("<grey>You have finished <yellow>" + finished + "</yellow>/<green>" + quests.size() + "</green> quests!");
        }
        lore.add("");
        lore.addAll(StringUtils.wrapText(description, 45));
        lore.add("");
        lore.add("<gold>Progress");
        for (int i = 0; i < quests.size(); i++) {
            var q = quests.get(i);
            if (!owner.questing.finished(q)) {
                var started = owner.questing.started(q);
                if (started) {
                    lore.add("<dark_grey>- <yellow>" + Objects.requireNonNull(QuestData.get(q)).name);
                    for (var l : StringUtils.wrapText("<grey>" + Objects.requireNonNull(owner.questing.get(q)).display(), 35)) {
                        lore.add("<dark_grey>-- </dark_grey>" + l);
                    }
                    if (i < quests.size() - 1) {
                        lore.add("<dark_grey>- <red>" + Objects.requireNonNull(QuestData.get(quests.get(i + 1))).name);
                        if (i < quests.size() - 2) {
                            var a = (quests.size() - (i + 2));
                            if (a == 1) {
                                lore.add("<dark_grey>and 1 more quest...");
                            }
                            else {
                                lore.add("<dark_grey>and " + a + " more quests...");
                            }
                        }
                    }
                }
                else {
                    lore.add("<dark_grey>- <red>" + Objects.requireNonNull(QuestData.get(q)).name);
                    if (i < quests.size() - 1) {
                        var a = (quests.size() - (i + 1));
                        if (a == 1) {
                            lore.add("<dark_grey>and 1 more quest...");
                        }
                        else {
                            lore.add("<dark_grey>and " + a + " more quests...");
                        }
                    }
                }
                break;
            }
            else {
                lore.add("<dark_grey>- <green>" + Objects.requireNonNull(QuestData.get(q)).name);
            }
        }
        long xp = xp(owner);
        if (xp > 0) {
            lore.add("");
            lore.add("<grey>You've gained <aqua>" + xp + "XP</aqua> from these quests!");
        }
        if (!incomplete(owner).isEmpty()) {
            lore.add("");
            if (selected(owner)) {
                lore.add("<gold>This is your active quest!");
            }
            else {
                lore.add("<gold>Click to select this as your active quest!");
            }
        }


        return ItemFactory.customItem("BOOK", "<green>" + name, lore);
    }
}
