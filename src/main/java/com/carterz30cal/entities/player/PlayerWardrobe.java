package com.carterz30cal.entities.player;

import com.carterz30cal.entities.player.interfaces.PlayerSavable;
import com.carterz30cal.items.ItemFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class PlayerWardrobe implements PlayerSavable {
    public static final int DEFAULT_SLOT_COUNT = 5;
    private final List<WardrobeSlot> slots = new ArrayList<>();
    private final GamePlayer owner;
    private int selectedSlot;

    public PlayerWardrobe(GamePlayer owner) {
        this.owner = owner;
        for (int i = 0; i < DEFAULT_SLOT_COUNT; i++) {
            slots.add(new WardrobeSlot());
        }
    }

    public void save(ConfigurationSection section) {
        section.set("slot-count", slots.size());
        section.set("selected-slot", selectedSlot);
        for (int i = 0; i < slots.size(); i++) {
            section.set("slots." + i, null);
            ConfigurationSection subsection = section.createSection("slots." + i);
            slots.get(i).save(subsection);
        }
    }

    public void load(ConfigurationSection section) {
        int slotCount = Math.max(section.getInt("slot-count", DEFAULT_SLOT_COUNT), DEFAULT_SLOT_COUNT);
        this.selectedSlot = section.getInt("selected-slot", 0);
        slots.clear();
        for (int i = 0; i < slotCount; i++) {
            if (section.contains("slots." + i)) {
                slots.add(new WardrobeSlot(Objects.requireNonNull(section.getConfigurationSection("slots." + i))));
            }
            else {
                slots.add(new WardrobeSlot());
            }
        }
    }

    public void SwapSlot(int swappingToSlot) {
        slots.get(selectedSlot).update();
        selectedSlot = swappingToSlot;
        while (selectedSlot > slots.size() - 1) slots.add(new WardrobeSlot());
        slots.get(selectedSlot).equip();
    }

    public void UpdateCurrentSlot() {
        slots.get(selectedSlot).update();
    }

    public WardrobeSlot GetSlot(int slot) {
        return slots.get(slot);
    }

    public int GetSelectedSlot() {
        return selectedSlot;
    }

    /**
     * @author carterz30cal
     * @version 2
     * @since 1.0.0 [1]
     */
    public class WardrobeSlot implements PlayerSavable {
        private ItemStack[] armour;
        private List<String> talismans;

        private WardrobeSlot(ConfigurationSection section) {
            load(section);
        }

        private WardrobeSlot() {
            armour = new ItemStack[4];
            talismans = new ArrayList<>();
        }

        public void save(ConfigurationSection section) {
            section.set("talismans", talismans);
            for (int i = 0; i < 4; i++) {
                section.set("armour[" + i + "]", ItemFactory.buildStringFromItem(armour[i]));
            }
        }

        @Override
        public void load(ConfigurationSection section) {
            armour = new ItemStack[4];
            talismans = section.getStringList("talismans");
            for (int i = 0; i < 4; i++) {
                armour[i] = ItemFactory.buildItemFromString(section.getString("armour[" + i + "]", null));
            }
        }

        private void update() {
            talismans = owner.talismans;
            armour = owner.player.getInventory().getArmorContents();
        }

        private void equip() {
            owner.talismans = talismans;
            owner.player.getInventory().setArmorContents(armour);
        }

        public ItemStack[] armour() {
            return armour;
        }

        public List<String> talismans() {
            return talismans;
        }

    }
}
