package com.carterz30cal.entities.enemies.core;

import com.carterz30cal.entities.AbstractEnemyType;
import com.carterz30cal.entities.EnemyTypes;
import com.carterz30cal.entities.enemies.directors.EnemyDirectorBuilder;
import com.carterz30cal.entities.enemies.representation.EnemyRepresentationBuilder;
import com.carterz30cal.entities.health.EntityHealthSystemBuilder;
import com.carterz30cal.entities.health.damage.DamageType;
import com.carterz30cal.items.ItemLootTable;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.utils.FileUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.Objects;

public class EnemyManager 
{
	public static String[] files = {
            "waterway/mobs/lunatics", "waterway/mobs/titans",
            "waterway/mobs/seraph/boss", "waterway/mobs/seraph/summons",
            "waterway/mobs/fishing/fishing_common",
            "waterway/mobs/fishing/fishing_uncommon",
            "waterway/mobs/fishing/fishing_rare",
            "waterway/mobs/fishing/fishing_very_rare",
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
				EnemyTypes type = EnemyTypes.valueOf(c.getString(p + ".type", file));
				type.generate(c.getConfigurationSection(p));

                var health = new EntityHealthSystemBuilder();
                health.setMaxHealth(c.getLong(p + ".health"));

                var representationBuilder = new EnemyRepresentationBuilder();
                var entitiesSection = c.getConfigurationSection(p + ".entities");
                assert entitiesSection != null;
                for (var e : entitiesSection.getKeys(false)) {
                    representationBuilder.add(Objects.requireNonNull(entitiesSection.getConfigurationSection(e)));
                }

                var director = new EnemyDirectorBuilder();
                director.setKnockback(c.getInt(p + ".knockback", 100));
                director.setSpeed(c.getDouble(p + ".speed", 1D));

                var data = new EnemyData();
                data.name = MiniMessage.miniMessage().deserialize(c.getString(p + ".name", "null"));
                data.level = c.getLong(p + ".level", 1L);
                data.alwaysDisplayHealth = c.getBoolean(p + ".always-display-health", false);
                data.coinMultiplier = c.getDouble(p + ".coin-multiplier", 1D);
                if (c.contains(p + ".loot")) {
                    data.lootTable = new ItemLootTable(Objects.requireNonNull(c.getConfigurationSection(p + ".loot")));
                }
                if (c.contains(p + ".bestiary")) {
                    data.bestiaryCategory = c.getString(p + ".bestiary.category");
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
                enemy.setHealthSystemBuilder(health).setRepresentationBuilder(representationBuilder).setDirectorBuilder(director);
			}
		}
	}
	
	public static GameEnemy spawn(String type, Location l)
	{
		return AbstractEnemyType.types.get(type).generate(l);
	}
	
	public static AbstractEnemyType getType(String type) {
		return AbstractEnemyType.types.get(type);
	}
}
