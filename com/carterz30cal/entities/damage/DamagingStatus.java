package com.carterz30cal.entities.damage;

import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.DamageType;
import com.carterz30cal.entities.GameEnemy;

public class DamagingStatus extends AbstractStatus {

	public int damage;
	public double percentDamage;
	public DamageType type;
	
	public DamagingStatus(int damage, double percentDamage, DamageType type) {
		this.damage = damage;
		this.percentDamage = percentDamage;
		this.type = type;
	}
	
	@Override
	public void onProc(GameEnemy enemy) {
		DamageInfo info = new DamageInfo();
		info.defender = enemy;
		info.attacker = enemy.lastDamager;
		
		info.type = type;
		info.damage = damage + (int)Math.round(percentDamage * enemy.health);
		enemy.damage(info);
	}

}
