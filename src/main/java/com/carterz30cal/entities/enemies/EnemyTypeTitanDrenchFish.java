package com.carterz30cal.entities.enemies;

import com.carterz30cal.fishing.FishingArea;
import com.carterz30cal.items.ItemRarity;
import com.carterz30cal.utils.*;
import org.bukkit.configuration.ConfigurationSection;

public class EnemyTypeTitanDrenchFish extends EnemyTypeTitanDrench
{

	public EnemyTypeTitanDrenchFish(ConfigurationSection m) {
		super(m);

		ConfigurationSection section = m.getConfigurationSection("fishing-brackets");
		if (section != null) {
			for (String area : section.getKeys(false)) {
				ItemRarity rarity = ItemRarity.valueOf(section.getString(area));
				FishingArea.getFishingArea(area).addToBracket(rarity, id);
			}
		}
	}


}
