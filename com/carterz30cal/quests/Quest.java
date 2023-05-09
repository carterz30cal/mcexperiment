package com.carterz30cal.quests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.entities.GameQuestgiver;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.RandomUtils;
import com.carterz30cal.utils.StringUtils;

public class Quest 
{
	public static final Map<String, Quest> quests = new HashMap<>();
	public static final Map<String, Quest> ids = new HashMap<>();
	
	public static final int TIME_BETWEEN_LINES = 18;
	
	public String id;
	public String name;
	
	public List<String> prerequisites = new ArrayList<>();
	
	public String questgiverName;
	public String questgiverMob;
	public Location questgiverLocation;
	public Map<EquipmentSlot, String> questgiverEquipment = new HashMap<>();
	public boolean showQuestgiverBefore; // before prereqs are met
	public boolean showQuestgiverAfter; // after completion of the quest
	
	public Sound speechSound;
	public List<String> speechGiving; // first click
	public List<String> speechDuring; // click during quest
	public List<String> speechThanking; // click when turning quest in.
	
	public QuestTypes questType;
	public ConfigurationSection questConfig;
	
	public int rewardCoins;
	public int rewardXp;
	public List<String> rewards = new ArrayList<>();
	
	public final GameQuestgiver instance;
	
	public Quest(ConfigurationSection c) {
		id = c.getCurrentPath();
		name = c.getString("name");
		prerequisites = c.getStringList("prerequisites");
		questgiverName = c.getString("questgiver.name");
		questgiverMob = c.getString("questgiver.mob");
		questgiverLocation = StringUtils.getLocationFromString(c.getString("questgiver.location"));
		showQuestgiverBefore = c.getBoolean("questgiver.show-before-reqs", false);
		showQuestgiverAfter = c.getBoolean("questgiver.show-after-complete", false);
		if (c.contains("questgiver.equipment")) {
			for (String k : c.getConfigurationSection("questgiver.equipment").getKeys(false))
			{
				questgiverEquipment.put(EquipmentSlot.valueOf(k), c.getString("questgiver.equipment." + k));
			}
		}
		
		speechSound = Sound.valueOf(c.getString("questgiver.sound"));
		speechGiving = c.getStringList("dialog-start");
		speechDuring = c.getStringList("dialog-during");
		speechThanking = c.getStringList("dialog-finishing");
		
		rewardCoins = c.getInt("rewards.coins", 0);
		rewardXp = c.getInt("rewards.xp", 0);
		if (c.contains("rewards.items")) rewards = c.getStringList("rewards.items");
		
		questType = QuestTypes.valueOf(c.getString("config.type"));
		questConfig = c.getConfigurationSection("config.config");
		
		instance = GameQuestgiver.spawn(this);
		quests.put(instance.u.toString(), this);
		ids.put(id, this);
	}
	
	public void startQuest(GamePlayer p) {
		int i = 0;
		for (String l : speechGiving) {
			p.sendMessage("DARK_GRAY[GOLD" + questgiverName + "DARK_GRAY]: WHITE" + l, speechSound, i);
			i += TIME_BETWEEN_LINES;
		}
		p.sendMessage("AQUAQuest Started: " + name + "!", i + 1);
		
		p.questTick = i + 1;
		
		p.quests.put(id, questType.generate(p, this, ""));
	}
	
	public void duringQuest(GamePlayer p) {
		AbstractQuestType qu = p.quests.get(id);
		p.questTick = TIME_BETWEEN_LINES;
		if (qu.attemptHandIn()) finishQuest(p);
		else {
			String l = RandomUtils.getChoice(speechDuring);
			p.sendMessage("DARK_GRAY[GOLD" + questgiverName + "DARK_GRAY]: WHITE" + l, speechSound, 0);
			
		}
	}
	
	public void finishQuest(GamePlayer p) {
		int i = 0;
		for (String l : speechThanking) {
			p.sendMessage("DARK_GRAY[GOLD" + questgiverName + "DARK_GRAY]: WHITE" + l, speechSound, i);
			i += TIME_BETWEEN_LINES;
		}
		i += 1;
		
		String complete = "AQUAQuest Completed!";
		if (rewardCoins != 0 || rewardXp != 0)
		{
			complete += "DARK_GRAY [";
			if (rewardCoins != 0)
			{
				complete += "GOLD" + StringUtils.commaify(rewardCoins) + " coins";
				if (rewardXp != 0) complete += "DARK_GRAY, AQUA" + rewardXp + " XP";
				complete += "DARK_GRAY]";
			}
			else complete += "AQUA" + rewardXp + " XPDARK_GRAY]";
		}
		
		p.sendMessage(complete, i);
		for (String it : rewards) {
			String item = it.split(",")[0];
			int am = Integer.parseInt(it.split(",")[1]);
			p.sendMessage("DARK_GRAY- WHITE" + am + "x " + ItemFactory.getItemTypeName(item), i);
		}
		
		
		new BukkitRunnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				p.coins += rewardCoins;
				p.gainXp(rewardXp);
				
				p.lastCoinReward = rewardCoins;
				p.lastXpReward = rewardXp;
				p.rewardTick = 40;
				p.playSound(Sound.ENTITY_PLAYER_LEVELUP, 0.9, 0.7);
				
				for (String it : rewards) {
					String[] spl = it.split(",");
					int amount = Integer.parseInt(spl[1]);
					String item = spl[0];
					
					p.giveItem(ItemFactory.build(item, amount), true);
				}
				
				p.quests.remove(id);
				p.completedQuests.add(id);
				
				for (Quest q : quests.values()) q.instance.refresh();
			}
			
		}.runTaskLater(Dungeons.instance, i + 2);
		
	}
	
	
	public static Quest get(Entity questgiver) {
		GameEntity e = GameEntity.get(questgiver);
		if (e instanceof GameQuestgiver) return quests.get(((GameQuestgiver)e).u.toString());
		else return null;
	}
}
