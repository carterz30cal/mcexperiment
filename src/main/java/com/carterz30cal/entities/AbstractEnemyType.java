package com.carterz30cal.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Mob;

import com.carterz30cal.entities.damage.StatusEffect;
import com.carterz30cal.entities.damage.StatusEffects;
import com.carterz30cal.items.ItemLootTable;
import com.carterz30cal.utils.StringUtils;

public abstract class AbstractEnemyType implements Cloneable
{
	public static Map<String, AbstractEnemyType> types = new HashMap<>();
	
	public final String id;
	public final String name;
	public int health;
	public int regen = 0;
	public final int level;
	public final int size;
	public final double speed;
	public EntityArmour armour;
	
	public int damage;
	public final DamageType damageType;
	public final int knockback;
	public final double displayHeight;
	public final double coinMultiplier;
	public final Color bloodColour;
	public final boolean ignoreTargetLimit;
	
	public StatusEffects resistances;
	
	public List<String> tags = new ArrayList<>();
	
	public final ItemLootTable loot;
	
	public AbstractEnemyType(ConfigurationSection m)
	{
		id = m.getName();
		name = m.getString("name", "null");
		health = m.getInt("health", 1);
		regen = m.getInt("regen", 0);
		level = m.getInt("level", 1);
		knockback = m.getInt("knockback", 100);
		displayHeight = m.getDouble("display-height", 2.1);
		size = m.getInt("size", 2);
		speed = m.getDouble("speed", 1);
		
		resistances = StatusEffects.createWithDefaultResistances();
		if (m.contains("resistances")) {
			for (String key : m.getConfigurationSection("resistances").getKeys(false)) {
				StatusEffect effect = StatusEffect.valueOf(key);
				resistances.effects.put(effect, m.getInt("resistances." + key));
			}
		}
		
		if (m.contains("armour")) armour = EntityArmour.generate(m.getConfigurationSection("armour"));
		else armour = new EntityArmour();
		
		damage = m.getInt("damage", 1);
		damageType = DamageType.valueOf(m.getString("damage-type", "PHYSICAL"));
		
		int[] colour = StringUtils.convertStringToIntArray(m.getString("blood-colour", "255, 0, 0"));
		bloodColour = Color.fromRGB(colour[0], colour[1], colour[2]);
		
		coinMultiplier = m.getDouble("coin-multiplier", 1);
		ignoreTargetLimit = m.getBoolean("ignore-target-limit", false);
		
		if (m.contains("tags")) tags = m.getStringList("tags");
		
		loot = new ItemLootTable(m);
		
		types.put(id, this);
	}
	
	
	public int getMaxHealth() {
		return health;
	}
	
	public int getDamage() {
		return damage;
	}
	
	
	
	public String onName(GameEnemy enemy) {
		return name;
	}
	
	public abstract GameEnemy generate(Location location);
	
	public void onTick(GameEnemy enemy)
	{
		
	}
	
	public boolean onLaunch(GameEnemy enemy) 
	{
		return true;
	}
	
	public void onTarget(GameEnemy enemy, GamePlayer p)
	{
		if (enemy.main instanceof Mob) {
			if (p == null) ((Mob)enemy.main).setTarget(null);
			else ((Mob)enemy.main).setTarget(p.player);
		}
	}
	
	public void onStatusProc(GameEnemy enemy, StatusEffect status) {
		
	}
	
	
	public void onDamaged(GameEnemy enemy, DamageInfo info)
	{
		
	}
	
	public void onKilled(GameEnemy enemy)
	{
		
	}
}
