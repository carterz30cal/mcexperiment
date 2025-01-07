package com.carterz30cal.entities.damage;

import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;

import com.carterz30cal.entities.DamageType;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.utils.EntityUtils;
import com.carterz30cal.utils.RandomUtils;

public class DeathStatus extends AbstractStatus {
	
	public DeathStatus() {

	}
	
	@Override
	public void onProc(GameEnemy enemy) {
		if (enemy.hasTag("HYDRA")) {
			enemy.damage(800, DamageType.WITHER);
		}
		else if (enemy.type.level > enemy.lastDamager.getLevel() + 10 || RandomUtils.getRandom(1, 3) == 1) {
			ArmorStand death = EntityUtils.spawnHologram(enemy.getLocation().add(0, 1, 0), 65);
			death.setCustomName(ChatColor.WHITE + "" + ChatColor.BOLD + "\u2620 RESISTED!");
		}
		else {
			ArmorStand death = EntityUtils.spawnHologram(enemy.getLocation().add(0, 1, 0), 65);
			death.setCustomName(ChatColor.WHITE + "" + ChatColor.BOLD + "\u2620 DEATH!");
			enemy.kill();
		}
		
	}
}
