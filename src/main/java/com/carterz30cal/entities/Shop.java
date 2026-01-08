package com.carterz30cal.entities;

import com.carterz30cal.items.Recipe;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Shop {
    public static Map<String, Shop> shops = new HashMap<>();
    private static Map<UUID, Shop> shent = new HashMap<>();

    public Map<Integer, Recipe> items = new HashMap<>();

    public String shopName;
    public String shopId;

    public String shopkeeperName;
    public EntityType shopkeeperType;
    public Location shopkeeperLocation;

    public String requiredQuest;
    public int requiredLevel;

    public Shop(ConfigurationSection section) {
        shopName = section.getString("shop-name", "null");

        shopId = section.getCurrentPath();

        shopkeeperName = section.getString("shopkeeper-name", "null");
        shopkeeperType = EntityType.valueOf(section.getString("shopkeeper-type", "ZOMBIE").toUpperCase());

        shopkeeperLocation = StringUtils.getLocationFromString(section.getString("location"));

        requiredQuest = section.getString("required-quest", null);
        requiredLevel = section.getInt("required-level", 0);

        int i = 0;
        for (String item : section.getConfigurationSection("items").getKeys(false)) {
            items.put(i++, new Recipe(section.getConfigurationSection("items." + item), false));
        }

        GameShopkeeper s = GameShopkeeper.spawn(this);
        shops.put(shopId, this);
        shent.put(s.u, this);
    }

    public static Shop getShop(Entity questgiver) {
        GameEntity e = GameEntity.get(questgiver);
        if (e instanceof GameShopkeeper) return shent.getOrDefault(((GameShopkeeper)e).u, null);
        else return null;
    }
}
