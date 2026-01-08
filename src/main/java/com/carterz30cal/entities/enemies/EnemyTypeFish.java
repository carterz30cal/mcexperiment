package com.carterz30cal.entities.enemies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.carterz30cal.fishing.FishingArea;
import com.carterz30cal.items.ItemRarity;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;

public class EnemyTypeFish extends EnemyTypeSimple
{
	public static Map<Integer, List<String>> brackets = new HashMap<>();
	public static int maxBracket = 0;
	
	public Map<EquipmentSlot, String> equipment = new HashMap<>();
	
	public int fishingBracket;
	
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



		fishingBracket = m.getInt("fishing-bracket", 0);
		if (fishingBracket > maxBracket) maxBracket = fishingBracket;
		
		brackets.putIfAbsent(fishingBracket, new ArrayList<>());
		brackets.get(fishingBracket).add(id);
	}
}
