package com.carterz30cal.entities;

import com.carterz30cal.entities.enemies.representation.EnemyRepresentationBuilder;
import com.carterz30cal.entities.interactable.GameShopkeeper;
import com.carterz30cal.items.recipes.Recipe;
import com.carterz30cal.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class Shop {
    public static Map<String, Shop> shops = new HashMap<>();

    public Map<Integer, Recipe> items = new HashMap<>();

    public String shopName;
    public String shopId;

    public String shopkeeperName;
    public EntityType shopkeeperType;
    public EnemyRepresentationBuilder representationBuilder;
    public Location shopkeeperLocation;
    public String skullProfileId;

    public String requiredQuest;
    public int requiredLevel;

    public Shop(ConfigurationSection section) {
        shopName = section.getString("shop-name", "null");

        shopId = section.getCurrentPath();

        shopkeeperName = section.getString("shopkeeper-name", "null");
        shopkeeperType = EntityType.valueOf(section.getString("shopkeeper-type", "ZOMBIE").toUpperCase());
        skullProfileId = section.getString("skull-profile-id");

        representationBuilder = new EnemyRepresentationBuilder();
        var entitiesSection = section.getConfigurationSection("entities");
        assert entitiesSection != null;
        for (var e : entitiesSection.getKeys(false)) {
            representationBuilder.add(Objects.requireNonNull(entitiesSection.getConfigurationSection(e)));
        }

        shopkeeperLocation = StringUtils.getLocationFromString(Objects.requireNonNull(section.getString("location")));

        requiredQuest = section.getString("required-quest", null);
        requiredLevel = section.getInt("required-level", 0);

        int i = 0;
        for (String item : section.getConfigurationSection("items").getKeys(false)) {
            items.put(i++, new Recipe(section.getConfigurationSection("items." + item), false));
        }

        new GameShopkeeper(this);
    }
}
