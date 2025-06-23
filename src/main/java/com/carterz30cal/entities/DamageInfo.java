package com.carterz30cal.entities;

public class DamageInfo
{
	public int damage;
	public DamageType type;
	public boolean main;
	public boolean indirect = false;
	
	public GamePlayer attacker;
	public GameEntity defender;
}
