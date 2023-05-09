package com.carterz30cal.quests;


import java.util.ArrayList;
import java.util.List;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.ItemReq;
import com.carterz30cal.items.ItemReqs;

public class MissionFetchQuest extends AbstractQuestType 
{
	public String item;
	public int amount;
	public boolean consume;
	
	private String qgname;
	public MissionFetchQuest(GamePlayer owner, Quest config, String progress) {
		super(owner, config, progress);
		
		item = config.questConfig.getString("item");
		amount = config.questConfig.getInt("amount");
		consume = config.questConfig.getBoolean("consumed");
		qgname = config.questgiverName;
	}

	@Override
	protected void loadProgress(String progress) {
		// TODO Auto-generated method stub

	}

	@Override
	public String saveProgress() {
		// TODO Auto-generated method stub
		return "N/A";
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<String> description() {
		List<String> l = new ArrayList<>();
		l.add("GRAYFetch " + amount + "x " + ItemFactory.getItemTypeName(item));
		l.add("GRAYfor GOLD" + qgname + "GRAY.");
		
		return l;
	}
	
	
	public int getQuestPriority() {
		return 10;
	}
	
	public boolean attemptHandIn() {
		ItemReqs reqs = new ItemReqs();
		reqs.addRequirement(new ItemReq(item, amount, 0));
		
		if (reqs.areRequirementsMet(owner))
		{
			if (consume) reqs.execute(owner);
			return true;
		}
		else return false;
	}

}
