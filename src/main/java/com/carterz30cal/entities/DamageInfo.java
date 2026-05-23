package com.carterz30cal.entities;

import com.carterz30cal.entities.player.GamePlayer;

@Deprecated
public class DamageInfo
{
	public int damage;
	public DamageType type;
	public boolean main;
	public boolean indirect = false;
	
	public GamePlayer attacker;
	public GameEntity defender;
}
