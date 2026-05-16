package com.carterz30cal.entities.damage;

import com.carterz30cal.entities.enemies.implementation.GameEnemy;

public abstract class AbstractStatus 
{
	public abstract void onProc(GameEnemy enemy);
}
