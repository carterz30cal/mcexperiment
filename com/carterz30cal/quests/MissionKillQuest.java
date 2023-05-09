package com.carterz30cal.quests;


import java.util.ArrayList;
import java.util.List;

import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GamePlayer;

public class MissionKillQuest extends AbstractQuestType 
{
	public String tag;
	public String pretty;
	public int amount;
	
	public int progress;
	
	private String qgname;
	public MissionKillQuest(GamePlayer owner, Quest config, String progress) {
		super(owner, config, progress);
		
		tag = config.questConfig.getString("tag");
		pretty = config.questConfig.getString("pretty");
		amount = config.questConfig.getInt("amount");
		
		qgname = config.questgiverName;
	}

	@Override
	protected void loadProgress(String progress) {
		// TODO Auto-generated method stub
		if (progress == null || progress == "") this.progress = 0;
		else this.progress = Integer.parseInt(progress);
	}

	@Override
	public String saveProgress() {
		// TODO Auto-generated method stub
		return "" + progress;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<String> description() {
		List<String> l = new ArrayList<>();
		
		if (progress >= amount) {
			l.add("GRAYReturn to " + qgname);
			l.add("GRAYto collect your reward!");
		}
		else {
			l.add("GRAYSlay GREEN" + amount + " " + pretty + "GRAY.");
			if (progress == 0) l.add("GRAYProgress: (RED0GRAY/GREEN" + amount + "GRAY).");
			else l.add("GRAYProgress: (YELLOW" + progress + "GRAY/GREEN" + amount + "GRAY)");
		}
		return l;
	}
	
	public void onKill(GameEnemy killed)
	{
		if (killed.hasTag(tag)) progress++;
	}
	
	
	public int getQuestPriority() {
		return 2 + progress;
	}
	
	public boolean attemptHandIn() {
		return progress >= amount;
	}

}
