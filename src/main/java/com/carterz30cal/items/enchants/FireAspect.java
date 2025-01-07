package com.carterz30cal.items.enchants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.carterz30cal.areas.BossWaterwayHydra;
import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.DamageType;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemEnchant;
import com.carterz30cal.items.ItemReq;
import com.carterz30cal.items.ItemType;

public class FireAspect extends ItemEnchant {

	public FireAspect(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getMaximumLevel() {
		// TODO Auto-generated method stub
		return 4;
	}

	@Override
	public int getEnchantPower() {
		// TODO Auto-generated method stub
		return level * 5;
	}

	@Override
	public List<ItemReq> getCatalystRequirements(int level) 
	{
		List<ItemReq> reqs = new ArrayList<>();
		reqs.add(new ItemReq("combination_catalyst_shard", 2 * level, level * 75));
		reqs.add(new ItemReq("jalapeno_powder", 3));
		return reqs;
	}

	@Override
	public Set<ItemType> getAppliableTypes() {
		Set<ItemType> types = new HashSet<>();
		
		types.add(ItemType.WEAPON);
		types.add(ItemType.WAND);
		return types;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return formatName("Fire Aspect");
	}
	
	@Override
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYAlso deals GOLD" + (level * 5) + " fire damageGRAY.");
		l.add("DARK_GRAYDisabled whilst fighting a Hydra.");
		return l;
	}
	
	public void onAttack(DamageInfo info)
	{
		//if (info.type != DamageType.PHYSICAL && info.type != DamageType.PROJECTILE && info.type != DamageType.MAGICAL) return;
		if (info.defender instanceof GameEnemy && !BossWaterwayHydra.hasParticipated(info.attacker))
		{
			GameEnemy enemy = (GameEnemy)info.defender;
			
			DamageInfo additional = new DamageInfo();
			additional.damage = level * 5;
			additional.attacker = info.attacker;
			additional.defender = info.defender;
			additional.type = DamageType.FIRE;
			
			enemy.damage(additional);
		}
	}

}
