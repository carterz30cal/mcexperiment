package com.carterz30cal.quests;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemAbility;

public abstract class AbstractQuestType extends ItemAbility
{
	public AbstractQuestType(GamePlayer owner, Quest config, String progress) {
		super(owner);
		
		loadProgress(progress);
	}
	protected abstract void loadProgress(String progress);
	public abstract String saveProgress();
	
	public int getQuestPriority() {
		return 0;
	}
	
	public boolean attemptHandIn() {
		return false;
	}
}
