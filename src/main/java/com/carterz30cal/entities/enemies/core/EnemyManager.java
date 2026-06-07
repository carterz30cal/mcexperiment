package com.carterz30cal.entities.enemies.core;

import com.carterz30cal.entities.enemies.directors.EnemyDirectorBuilder;
import com.carterz30cal.entities.enemies.directors.behaviour.SimpleTargetingBehaviour;
import com.carterz30cal.entities.enemies.representation.EnemyRepresentationBuilder;
import com.carterz30cal.entities.health.EntityHealthSystemBuilder;
import com.carterz30cal.entities.health.damage.DamageType;
import com.carterz30cal.fishing.FishingArea;
import com.carterz30cal.gui.BestiaryGUI;
import com.carterz30cal.items.ItemLootTable;
import com.carterz30cal.items.ItemRarity;
import com.carterz30cal.skills.SkillSoulType;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.utils.FileUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.util.HashSet;
import java.util.Objects;

/**
 * @author carterz30cal
 * @version 3
 * @since 1.0.0
 */
public class EnemyManager 
{
	public static String[] files = {
            "waterway/mobs/lunatics", "waterway/mobs/titans",
            "waterway/mobs/spiders",
            "waterway/mobs/seraph/boss", "waterway/mobs/seraph/summons",
            "waterway/mobs/fishing/fishing_common",
            "waterway/mobs/fishing/fishing_uncommon",
            "waterway/mobs/fishing/fishing_rare",
            "waterway/mobs/fishing/fishing_very_rare",
            "necropolis/mobs/dusted"
	};
	
	public static EnemyManager instance;
	
	public EnemyManager()
	{
		instance = this;
		
		for (String file : files)
		{
			FileConfiguration c = FileUtils.getData(file);
            assert c != null;
            for (String p : c.getKeys(false))
			{
                var health = new EntityHealthSystemBuilder();
                health.setMaxHealth(c.getLong(p + ".health"));

                var representationBuilder = new EnemyRepresentationBuilder();
                var entitiesSection = c.getConfigurationSection(p + ".entities");
                assert entitiesSection != null;
                for (var e : entitiesSection.getKeys(false)) {
                    representationBuilder.add(Objects.requireNonNull(entitiesSection.getConfigurationSection(e)));
                }

                var director = new EnemyDirectorBuilder();
                director.setKnockback(c.getInt(p + ".knockback", 100))
                        .setSpeed(c.getDouble(p + ".speed", 1D))
                        .setEntityType(EntityType.valueOf(c.getString(p + ".director", "ZOMBIE").toUpperCase()))
                        .setTargetingBehaviour(new SimpleTargetingBehaviour(false));

                var data = new EnemyData();
                data.name = MiniMessage.miniMessage().deserialize(c.getString(p + ".name", "null"));
                data.mmName = c.getString(p + ".name", "<red>null</red>");
                data.level = c.getLong(p + ".level", 1L);
                data.alwaysDisplayHealth = c.getBoolean(p + ".always-display-health", false);
                data.coinMultiplier = c.getDouble(p + ".coin-multiplier", 1D);
                if (c.contains(p + ".loot")) {
                    data.lootTable = new ItemLootTable(Objects.requireNonNull(c.getConfigurationSection(p + ".loot")));
                }
                if (c.contains(p + ".souls")) {
                    var d = c.getConfigurationSection(p + ".souls");
                    assert d != null;
                    for (var s : d.getKeys(false)) {
                        data.souls.put(SkillSoulType.valueOf(s), d.getLong(s, 0L));
                    }
                }

                if (c.contains(p + ".tags")) {
                    data.tags = new HashSet<>(c.getStringList(p + ".tags"));
                }
                else {
                    data.tags = new HashSet<>();
                }
                if (c.contains(p + ".damage")) {
                    var d = c.getConfigurationSection(p + ".damage");
                    assert d != null;
                    for (var dtype : DamageType.values()) {
                        var value = d.getLong(dtype.name(), 0L);
                        if (value < 1) {
                            continue;
                        }
                        data.damages.put(dtype, value);
                    }
                }
                if (c.contains(p + ".stats")) {
                    var d = c.getConfigurationSection(p + ".stats");
                    assert d != null;
                    for (var dtype : Stat.values()) {
                        var value = d.getLong(dtype.name(), 0L);
                        if (value == 0) {
                            continue;
                        }
                        data.stats.put(dtype, value);
                    }
                }


                EnemyBuilder enemy = new EnemyBuilder(p);
                if (c.contains(p + ".abilities")) {
                    var d = c.getConfigurationSection(p + ".abilities");
                    assert d != null;
                    for (var e : d.getKeys(false)) {
                        enemy.addAbility(d.getConfigurationSection(e));
                    }
                }
                if (c.contains(p + ".bestiary")) {
                    data.bestiaryCategory = c.getString(p + ".bestiary.category");
                    assert data.bestiaryCategory != null;
                    BestiaryGUI.registerTypeIntoCategory(enemy.getId(), data.bestiaryCategory);
                }
                if (c.contains(p + ".fishing")) {
                    ConfigurationSection section = c.getConfigurationSection(p + ".fishing");
                    if (section != null) {
                        for (String area : section.getKeys(false)) {
                            ItemRarity rarity = ItemRarity.valueOf(section.getString(area));
                            FishingArea.getFishingArea(area).addToBracket(rarity, enemy.getId());
                        }
                    }
                }
                enemy.setHealthSystemBuilder(health).setRepresentationBuilder(representationBuilder).setDirectorBuilder(director).setEnemyData(data);
			}
		}
	}
	
	public static GameEnemy spawn(String type, Location l)
	{
        var enemy = EnemyBuilder.getBuilder(type).build(l);
        enemy.register();
        return enemy;
	}
}
