package com.carterz30cal.utils;

public class LevelUtils 
{
	public final static int LEVEL_MAX = 1000;
	public static int getXpForLevel(int level)
	{
		/*
		 * level 1 = 100 xp
		 * level 2 = 100 + 200 xp
		 * level 3 = 300 + 300 xp
		 * level 4 = 600 + 400 xp
		 * level 5 = 1000 + 500 xp
		 * level 6 = 1500 + 600 xp
		 * level 7 = 2100 + 700
		 * 
		 * 
		 * 100 300 600 
		 *   200  300
		 *     100
		 * a = 50
		 * b = 50
		 * c = 0
		 */
		return (10 * (int)Math.pow(Math.max(0, level - 10), 3)) + (50 * (int)Math.pow(level, 2)) + (50 * level);
	}
	
	public static int getLevel(int experience)
	{
		int level = 0;
		int exp = experience;
		while (exp >= getXpForLevel(level + 1) && level < LEVEL_MAX)
		{
			exp -= getLevel(level + 1);
			level++;
		}
		
		return level;
	}
	
	public static int getEnemyBaseXpReward(int enemyLevel)
	{
		return Math.max(enemyLevel, (getXpForLevel(enemyLevel) - getXpForLevel(enemyLevel - 1)) / 100);
	}
}
