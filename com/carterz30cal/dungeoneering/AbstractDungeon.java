package com.carterz30cal.dungeoneering;

import com.carterz30cal.entities.GamePlayer;

public class AbstractDungeon {
	
	public GamePlayer owner;
	public int step;
	
	public String uuid;
	
	
	public AbstractDungeon(GamePlayer player)
	{
		owner = player;
		
		uuid = DungeonManager.registerDungeon(this);
		player.dungeonId = uuid;
	}
}
