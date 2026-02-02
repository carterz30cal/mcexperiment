package com.carterz30cal.utils;

import com.carterz30cal.areas2.quests.rewards.QuestReward;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.Collection;
import com.carterz30cal.items.DiscoveryManager;

public class LevelUtils
{
	public final static int LEVEL_MAX = 1000;
	public static long getXpForLevel(long level)
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
        return ((level + 10) / 10) * 100;
        //return Math.round(10D * Math.pow(Math.max(0, level - 10), 3) + (50D * Math.pow(level, 2)) + (50D * level));
	}

    public static long GetTotalXP(GamePlayer player) {
        long xp = 0;

        for (var collection : player.discoveries.entrySet()) {
            long gross = 0;
            Collection discovery = DiscoveryManager.get(collection.getKey());
            for (int tier = 0; tier < discovery.getMaxTier(); tier++) {
                if (discovery.tiers.get(tier) <= collection.getValue()) {
                    gross += discovery.xpRewards.get(tier);
                }
                else {
                    break;
                }
            }
            xp += gross;
        }
        for (var questSave : player.GetQuestSaves()) {
            for (var sections : questSave.GetQuest().GetCompletedSections(questSave.currentSection)) {
                QuestReward reward = sections.GetQuestReward();
                if (reward == null) {
                    continue;
                }
                xp += reward.GetXP();
            }
        }

        return xp;
    }

    public static int GetLevelFromTotalXP(long experience)
	{
		int level = 0;
        while (experience >= getXpForLevel(level + 1) && level < LEVEL_MAX)
		{
            experience -= getXpForLevel(level + 1);
			level++;
		}
		return level;
	}

    public static long GetRemainderXP(long experience, long level) {
        long l = 0;
        while (l < level) {
            experience -= getXpForLevel(l + 1);
            l++;
        }
        return experience;
    }
	
	public static long getEnemyBaseXpReward(int enemyLevel)
	{
        return 0;
        //return Math.max(enemyLevel, (getXpForLevel(enemyLevel) - getXpForLevel(enemyLevel - 1)) / 100);
	}
}
