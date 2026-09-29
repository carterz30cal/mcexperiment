package com.carterz30cal.entities.player;

import com.carterz30cal.areas.quests2.Quest;
import com.carterz30cal.areas.quests2.QuestCollection;
import com.carterz30cal.areas.quests2.QuestData;
import com.carterz30cal.areas.quests2.extensions.FailableQuest;
import com.carterz30cal.entities.player.interfaces.PlayerSavable;
import com.carterz30cal.items.abilities.implementation.PlayerAbilityContext;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.RandomUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public final class PlayerQuests implements PlayerSavable {
    private final GamePlayer owner;
    private final Map<String, Quest> progressing = new HashMap<>();
    private List<String> completed;
    private String selected;

    public PlayerQuests(GamePlayer owner) {
        this.owner = owner;
    }

    /**
     * Complete the quest, and grant one-time rewards.
     *
     * @param quest what quest are we completing?
     */
    public void finish(Quest quest) {
        var data = QuestData.get(quest.id());
        assert data != null;
        progressing.remove(quest.id());
        if (!data.repeatable) {
            completed.add(quest.id());
        }
        if (quest.id().equals(selected)) {
            if (progressing.isEmpty()) {
                selected = null;
            }
            else {
                selected = RandomUtils.getChoice(progressing.keySet());
            }
        }
        owner.sendMessage("<dark_grey>- - - <gold><b>Quest Complete!</b></gold> - - -");
        data.reward.completion(owner);
    }

    public boolean finished(Quest quest) {
        return finished(quest.id());
    }

    public boolean finished(String quest) {
        return completed.contains(quest);
    }

    public boolean started(String quest) {
        return progressing.containsKey(quest);
    }

    public @Nullable Quest get(String quest) {
        return progressing.getOrDefault(quest, null);
    }

    public @Nullable Quest selected() {
        if (selected == null) {
            return null;
        }
        else {
            return progressing.get(selected);
        }
    }

    /**
     * Select a random, available quest from a collection. This will usually be fine as most collections are linear
     *
     * @param from the collection to select from
     * @since 1.0.0 [1]
     */
    public void select(QuestCollection from) {
        if (from.complete(owner)) {
            return;
        }
        selected = RandomUtils.getChoice(from.incomplete(owner));
    }

    public void select(String qid) {
        selected = qid;
    }

    public List<PlayerAbilityContext> contexts() {
        var list = new ArrayList<PlayerAbilityContext>();
        for (var entry : progressing.entrySet()) {
            list.add(new PlayerAbilityContext(owner, entry.getValue()));
        }
        return list;
    }

    public void start(Quest quest) {
        if (completed.contains(quest.id())) {
            return;
        }
        quest.start();
        progressing.put(quest.id(), quest);
        selected = quest.id();
    }

    public void remove(Quest quest) {
        if (quest instanceof FailableQuest failable) {
            failable.fail();
        }
        progressing.remove(quest.id());
        if (quest.id().equals(selected)) {
            selected = null;
        }
    }

    public void clear() {
        selected = null;
        progressing.clear();
        completed.clear();
    }

    public long xp() {
        long total = 0;
        for (var c : completed) {
            var data = QuestData.get(c);
            assert data != null;
            total += data.reward.xp();
        }
        return total;
    }


    @Override
    public void save(ConfigurationSection section) {
        section.set("completed", completed);
        section.set("selected", selected);
        for (var entry : progressing.entrySet()) {
            section.set("progressing." + entry.getKey(), null);
            entry.getValue().save(section.createSection("progressing." + entry.getKey()));
        }
    }

    @Override
    public void load(ConfigurationSection section) {
        completed = section.getStringList("completed");
        selected = section.getString("selected", null);
        if (section.contains("progressing")) {
            var s = section.getConfigurationSection("progressing");
            assert s != null;
            for (var key : s.getKeys(false)) {
                var data = QuestData.get(key);
                if (data == null) {
                    Dungeons.instance.getLogger().warning("tried to create quest " + key + " for player " + owner.player.getName() + ", but couldn't find it!");
                    continue;
                }
                var quest = Quest.create(data, owner);
                quest.load(s.getConfigurationSection(key));
                progressing.put(key, quest);
            }
        }
    }
}
