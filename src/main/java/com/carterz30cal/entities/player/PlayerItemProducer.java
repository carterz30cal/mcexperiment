package com.carterz30cal.entities.player;

import com.carterz30cal.entities.player.interfaces.PlayerSavable;
import com.carterz30cal.items.Item;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.abilities.implementation.AbilityWithItemProducer;
import com.carterz30cal.items.types.ItemIngredientGenerator;
import com.carterz30cal.main.Dungeons;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 * @see AbilityWithItemProducer
 * @see com.carterz30cal.entities.player.interfaces.PlayerSavable
 */
public class PlayerItemProducer implements PlayerSavable {
    /**
     * Set to <code>0</code> if the producer has not yet been unlocked.
     */
    private long level;
    /**
     * Determines the main item generator, and generation speed.
     */
    private ItemIngredientGenerator generator;
    /**
     * Last time, in milliseconds, that we checked the items generated.
     */
    private long lastCheck;
    private List<Item> upgrades;

    /**
     * Calculate what items have been produced between the last calculation and
     * the latest attempt. If it is zero, don't update the last calculation time.
     * @return a map of all the items produced.
     * @param update should we update the last production time?
     * @since 1.0.0
     * @see Item
     * @see GamePlayer
     */
    public Map<String, Long> calculate(boolean update) {
        Map<String, Long> items = new HashMap<>();
        long current = System.currentTimeMillis();
        long timeDiff = current - lastCheck;
        var context = new ProducingContext();
        context.weights.put(generator.generates(), 1000L);
        context.timePerItem = generator.timePerItem();
        for (var upgrade : upgrades) {
            for (var a : upgrade.abilities) {
                if (a.ability instanceof AbilityWithItemProducer producer) {
                    producer.modifyProducingContext(context);
                }
            }
        }
        long expectedItems = timeDiff / context.timePerItem;
        long totalWeight = 0;
        for (var w : context.weights.values()) totalWeight += w;
        for (var i : context.weights.keySet()) {
            long amount = (long) Math.floor(expectedItems * ((double)context.weights.get(i) / totalWeight));
            if (amount < 1) continue;
            items.put(i, amount);
        }
        if (update && expectedItems > 0) {
            long diff = timeDiff - (expectedItems * context.timePerItem);
            lastCheck = current - diff;
        }
        return items;
    }

    @Override
    public void save(ConfigurationSection section) {
        section.set("generator", null);
        section.createSection("generator");
        section.set("generator.level", level);
        section.set("generator.last-check", lastCheck);
        section.set("generator.primary-item", generator == null ? null : generator.id);
        var idList = upgrades.stream().map((i) -> i.id).toList();
        section.set("generator.upgrades", idList);
    }

    /**
     * @implNote <code>ItemIngredientGenerator generator</code> must be set separately.
     */
    @Override
    public void load(ConfigurationSection section) {
        if (section.contains("generator")) {
            level = section.getLong("generator.level", 0);
            lastCheck = section.getLong("generator.last-check", System.currentTimeMillis());
            var idList = section.getStringList("generator.upgrades");
            upgrades = new ArrayList<>();
            for (String id : idList) {
                var item = ItemFactory.getItem(id);
                if (item == null) {
                    Dungeons.instance.getLogger().warning("Item " + id + " not found for item generator!");
                    continue;
                }
                upgrades.add(item);
            }
            var gid = section.getString("generator.primary-item", null);
            if (gid != null) generator = (ItemIngredientGenerator) ItemFactory.getItem(gid);
            else generator = null;
        }
        else {
            level = 0;
            lastCheck = System.currentTimeMillis();
            upgrades = new ArrayList<>();
            generator = null;
        }
    }

    /**
     * Sets the <code>ItemIngredientGenerator</code> generator.
     * @since 1.0.0
     * @param generator what are we setting?
     */
    public void setGenerator(ItemIngredientGenerator generator) {
        this.generator = generator;
    }

    /**
     * @since 1.0.0
     * @return the current <code>ItemIngredientGenerator</code>.
     */
    @Nullable
    public ItemIngredientGenerator getGenerator() {
        return generator;
    }

    /**
     * @since 1.0.0
     * @param upgrade what <code>Item</code> are we adding to the upgrades list?
     */
    public void addUpgrade(Item upgrade) {
        upgrades.add(upgrade);
    }

    /**
     * @since 1.0.0
     * @return a list of all upgrades that the factory has.
     * Should be between 0-14 items.
     */
    @NotNull
    public List<Item> getUpgrades() {
        return upgrades;
    }

    /**
     * Resets <code>lastCheck</code> to the current system time.
     * @since 1.0.0
     */
    public void reset() {
        lastCheck = System.currentTimeMillis();
    }

    public final class ProducingContext {
        public Map<String, Long> weights = new HashMap<>();
        public long timePerItem;
    }
}
