package com.carterz30cal.entities.damage;

import org.bukkit.scheduler.BukkitRunnable;

import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.DamageType;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.main.Dungeons;

public class DotStatus extends AbstractStatus {

	public int damage;
	public double percentDamage;
	public int ticks;
	public int delay;
	public DamageType type;
	
	public DotStatus(int damage, double percentDamage, int ticks, int delay, DamageType type) {
		this.damage = damage;
		this.percentDamage = percentDamage;
		this.type = type;
		this.ticks = ticks;
		this.delay = delay;
	}
	
	@Override
	public void onProc(GameEnemy enemy) {
		
		new BukkitRunnable() {
			int ticksRemaining = ticks;
			@Override
			public void run() {
				if (ticksRemaining > 0) {
					ticksRemaining--;
					
					DamageInfo info = new DamageInfo();
					info.defender = enemy;
					info.attacker = enemy.lastDamager;
					
					info.type = type;
					info.damage = damage + (int)Math.round(percentDamage * enemy.health);
					enemy.damage(info);
				}
				else cancel();
			}
			
		}.runTaskTimer(Dungeons.instance, delay, delay);
	}
}
