package com.carterz30cal.dungeoneering;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DungeonManager
{
	public static Map<String, AbstractDungeon> dungeons = new HashMap<>();
	
	public static String registerDungeon(AbstractDungeon dungeon) {
		String uuid = UUID.randomUUID().toString();
		
		dungeons.put(uuid, dungeon);
		
		return uuid;
	}
}
