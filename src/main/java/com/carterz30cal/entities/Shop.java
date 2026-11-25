package com.carterz30cal.entities;

import com.carterz30cal.areas.quests.requirements.QuestRequirement;
import com.carterz30cal.areas.quests2.requirements.AlwaysTrueQuestRequirement;
import com.carterz30cal.areas.quests2.requirements.ConfigurableQuestRequirement;
import com.carterz30cal.entities.enemies.representation.EnemyRepresentationBuilder;
import com.carterz30cal.entities.interactable.GameShopkeeper;
import com.carterz30cal.items.recipes.Recipe;
import com.carterz30cal.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author carterz30cal
 * @version 3
 * @since 1.0.0
 */
public class Shop {
    public static Map<String, Shop> shops = new HashMap<>();

    public Map<Integer, Recipe> items = new HashMap<>();

    public String shopName;
    public String shopId;

    public String shopkeeperName;
    public EnemyRepresentationBuilder representationBuilder;
    public Location shopkeeperLocation;
    public final QuestRequirement requirement;

    public Shop(ConfigurationSection section) {
        shopName = section.getString("shop-name", "null");

        shopId = section.getCurrentPath();
        shopkeeperName = section.getString("shopkeeper-name", "null");

        representationBuilder = new EnemyRepresentationBuilder();
        var entitiesSection = section.getConfigurationSection("entities");
        assert entitiesSection != null;
        for (var e : entitiesSection.getKeys(false)) {
            representationBuilder.add(Objects.requireNonNull(entitiesSection.getConfigurationSection(e)));
        }

        shopkeeperLocation = StringUtils.getLocationFromString(Objects.requireNonNull(section.getString("location")));

        if (section.contains("requirement")) {
            this.requirement = ConfigurableQuestRequirement.create(Objects.requireNonNull(section.getConfigurationSection("requirement")));
        }
        else {
            this.requirement = AlwaysTrueQuestRequirement.instance;
        }

        int i = 0;
        for (String item : section.getConfigurationSection("items").getKeys(false)) {
            items.put(i++, new Recipe(section.getConfigurationSection("items." + item), false));
        }

        new GameShopkeeper(this);
    }
}
