package com.carterz30cal.quests;

import java.lang.reflect.InvocationTargetException;

import com.carterz30cal.entities.GamePlayer;

public enum QuestTypes {
	QUEST_TYPE_FETCH(MissionFetchQuest.class),
	QUEST_TYPE_KILL(MissionKillQuest.class),
	QUEST_TYPE_STAT_REQ(MissionStatReq.class),
	;
	private Class<? extends AbstractQuestType> abClass;
	
	private QuestTypes(Class<? extends AbstractQuestType> abClass)
	{
		this.abClass = abClass;
	}
	
	public AbstractQuestType generate(GamePlayer owner, Quest quest, String progress)
	{
		try {
			return abClass.getConstructor(GamePlayer.class, Quest.class, String.class).newInstance(owner, quest, progress);
		} catch(InvocationTargetException e) {
			e.getTargetException().printStackTrace();
			return null;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| NoSuchMethodException | SecurityException e) {
			
			e.printStackTrace();
			return null;
		}
	}
}
