package com.carterz30cal.entities.player;

import com.carterz30cal.entities.player.interfaces.ArrowSelectionStrategy;
import com.carterz30cal.entities.player.interfaces.PlayerSavable;
import com.carterz30cal.entities.player.quiver.HighestDamageSelectionStrategy;
import com.carterz30cal.items.Item;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.ItemType;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class PlayerQuiver implements PlayerSavable {
    private final Map<ItemType, Map<Item, Long>> inventory = new HashMap<>();
    private final GamePlayer owner;
    private SelectionStrategy strategy;
    private Item cachedResult;

    public PlayerQuiver(GamePlayer owner) {
        this.owner = owner;
        this.strategy = SelectionStrategy.HIGHEST_MULTIPLIED_DAMAGE_STATS;
    }

    /**
     * Get the best arrow according to the selection strategy
     * @param type what item type do we care about?
     * @return the best arrow type that the player has, according to the selection strategy
     * @since 1.0.0 [1]
     */
    @Nullable
    public Item get(@NotNull ItemType type) {
        return get(type, strategy);
    }
    /**
     * Get the best arrow according to the selection strategy
     * @param type what item type do we care about
     * @param strategy what strategy should we use?
     * @return the best arrow type that the player has, according to the selection strategy
     * @apiNote this result may be cached, which can result in suboptimal choices if the players stats vary a lot
     * @since 1.0.0 [1]
     */
    @Nullable
    public Item get(@NotNull ItemType type, @NotNull SelectionStrategy strategy) {
        if (cachedResult != null) return cachedResult;
        var set = inventory.getOrDefault(type, new HashMap<>()).entrySet();
        var items = set.stream().filter(e -> e.getValue() > 0).map(Map.Entry::getKey).collect(Collectors.toSet());
        if (items.isEmpty()) return null;
        cachedResult = strategy.implementation.best(items, owner);
        return cachedResult;
    }

    /**
     * Consumes an arrow and potentially clears the cache if we deplete one type.
     * @param type the arrow type to consume
     * @since 1.0.0 [1]
     * @return <code>true</code> if we consumed this arrow, <code>false</code> otherwise.
     * @implSpec clears the cache if this <code>Item</code> type is consumed fully and it is the current cached type.
     */
    public boolean consume(Item type) {
        if (!inventory.containsKey(type.type)) return false;
        var remaining = inventory.get(type.type).getOrDefault(type, 0L) - 1;
        if (remaining < 1 && type.equals(cachedResult)) {
            owner.sendMessage("<red>You've just ran out of " + type.name + "!");
            clearCache();
        }
        inventory.get(type.type).put(type, remaining);
        return true;
    }

    /**
     * Adds arrows to the quiver, potentially clearing the cache
     * @param type the type to add
     * @param amount how many to add?
     * @since 1.0.0 [1]
     */
    public void add(Item type, long amount) {
        inventory.putIfAbsent(type.type, new HashMap<>());
        var map = inventory.get(type.type);
        if (!map.containsKey(type)) {
            clearCache();
            map.put(type, amount);
        }
        else {
            var cam = map.getOrDefault(type, 0L);
            map.put(type, cam + amount);
        }
    }

    /**
     * Updates the current selection strategy and clears the cache.
     * @param strategy the new selection strategy we want to apply
     * @since 1.0.0 [1]
     * @apiNote calls <code>clearCache()</code>.
     */
    public void strategy(@NotNull SelectionStrategy strategy) {
        if (this.strategy == strategy) return;
        this.strategy = strategy;
        clearCache();
    }

    /**
     * Clears the cache, for more accurate results.
     * @since 1.0.0 [1]
     */
    public void clearCache() {
        this.cachedResult = null;
    }

    @Override
    public void save(ConfigurationSection section) {
        section.set("inventory", null);
        section.createSection("inventory");
        for (var map : inventory.values()) {
            for (var item : map.entrySet()) {
                section.set("inventory." + item.getKey().id, item.getValue());
            }
        }
        section.set("strategy", strategy);
    }

    @Override
    public void load(ConfigurationSection section) {
        var inv = section.getConfigurationSection("inventory");
        if (inv != null) {
            for (var key : inv.getKeys(false)) {
                Item item = ItemFactory.getItem(key);
                if (item == null) continue;
                inventory.putIfAbsent(item.type, new HashMap<>());
                inventory.get(item.type).put(item, inv.getLong("inventory." + key));
            }
        }
        strategy = SelectionStrategy.valueOf(section.getString("strategy", "HIGHEST_MULTIPLIED_DAMAGE_STATS"));
    }

    /**
     * @author carterz30cal
     * @version 1
     * @since 1.0.0 [1]
     */
    public enum SelectionStrategy {
        HIGHEST_MULTIPLIED_DAMAGE_STATS(new HighestDamageSelectionStrategy());
        public final ArrowSelectionStrategy implementation;

        SelectionStrategy(ArrowSelectionStrategy implementation) {
            this.implementation = implementation;
        }
    }
}
