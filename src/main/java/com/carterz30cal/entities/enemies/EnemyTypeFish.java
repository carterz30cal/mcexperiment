package com.carterz30cal.entities.enemies;

import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.fishing.FishingArea;
import com.carterz30cal.items.ItemRarity;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;

public class EnemyTypeFish extends EnemyTypeSimple
{
	public Map<EquipmentSlot, String> equipment = new HashMap<>();
	
	public EnemyTypeFish(ConfigurationSection m) 
	{
		super(m);

		ConfigurationSection section = m.getConfigurationSection("fishing-brackets");
		if (section != null) {
			for (String area : section.getKeys(false)) {
				ItemRarity rarity = ItemRarity.valueOf(section.getString(area));
				FishingArea.getFishingArea(area).addToBracket(rarity, id);
			}
		}
	}

    @Override
    public void onTick(GameEnemy enemy) {
        super.onTick(enemy);

        int lifetime = (int) enemy.data.getOrDefault("lifetime", 20 * 60 * 5);
        if (lifetime <= 0) {
            enemy.remove();
        }
        else {
            enemy.data.put("lifetime", --lifetime);
        }
    }
}
