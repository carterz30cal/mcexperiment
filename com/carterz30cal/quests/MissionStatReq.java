package com.carterz30cal.quests;

import java.util.ArrayList;
import java.util.List;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.stats.Stat;

public class MissionStatReq extends AbstractQuestType 
{
	public Stat stat;
	public int amount;
	
	private String qgname;
	public MissionStatReq(GamePlayer owner, Quest config, String progress) {
		super(owner, config, progress);
		
		stat = Stat.valueOf(config.questConfig.getString("stat"));
		amount = config.questConfig.getInt("amount");
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
		l.add("GRAYAcquire " + stat.colour + amount + stat.getIcon() + "GRAY and return");
		l.add("GRAYto " + qgname + "GRAY.");
		l.add("GRAYCurrently, you have " + stat.colour + owner.stats.getStat(stat) + stat.getIcon() + "GRAY!");
		return l;
	}
	
	public int getQuestPriority() {
		return 10;
	}
	
	public boolean attemptHandIn() {
		return owner.stats.getStat(stat) >= amount;
	}
	
	

}
