package com.carterz30cal.entities;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import com.carterz30cal.main.Dungeons;
import com.carterz30cal.quests.Quest;
import com.carterz30cal.utils.EntityUtils;

import javax.swing.text.html.CSS;

public class GameQuestgiver extends GameOwnable {
	private Quest q;
	public GameQuestgiver(String name, Location location, EntityType type) {
		super(name, location, type);
		// TODO Auto-generated constructor stub
	}
	
	public static GameQuestgiver spawn(Quest q) {
		GameQuestgiver n = new GameQuestgiver(q.questgiverName, q.questgiverLocation, EntityType.valueOf(q.questgiverMob));
		
		n.q = q;
		new BukkitRunnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				n.refresh();
			}
			
		}.runTaskTimer(Dungeons.instance, 5, 20);
		
		return n;
	}
	
	public void refresh() {
		owners.clear();
		for (GamePlayer p : PlayerManager.players.values()) {
			
			if (!q.showQuestgiverAfter && p.completedQuests.contains(q.id)) continue;
			if (!q.showQuestgiverBefore)
			{
				boolean met = true;
				for (String pre : q.prerequisites) if (!p.completedQuests.contains(pre)) met = false;
				
				if (!met) continue;
			}
			//System.out.println(p.player.getName());
			addOwner(p, false);
		}
		refreshVisibility();
		
		if (main instanceof Mob) {
			Mob e = (Mob)main;
			for (EquipmentSlot s : q.questgiverEquipment.keySet()) EntityUtils.setArmourPiece(e, s, q.questgiverEquipment.get(s));
		}
	}

}
