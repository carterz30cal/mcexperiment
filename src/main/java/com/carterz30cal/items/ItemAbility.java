package com.carterz30cal.items;

import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.damage.StatusEffect;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatDisplayType;

import java.util.ArrayList;
import java.util.List;

public abstract class ItemAbility 
{
	public GamePlayer owner;
	
	public ItemAbility(GamePlayer owner)
	{
		this.owner = owner;
	}
	
	public abstract String name();
	public List<String> description()
	{
		return new ArrayList<>();
	}
	
	protected String display(Stat stat, int val)
	{
		String prefix = val >= 0 ? "+" : "";
		String suffix = stat.display == StatDisplayType.PERCENTAGE ? "%" : "";
		
		return stat.colour + prefix + val + suffix + stat.getIcon();
	}
	
	public void onKill(GameEnemy killed)
	{
		
	}
	
	public void onAttack(DamageInfo info)
	{
		
	}
	
	public void onPreAttack(DamageInfo info)
	{
		
	}
	
	public int onStatusBuildup(StatusEffect effect, int current) {
		return current;
	}
	
	public void onStatusProc(StatusEffect effect, GameEnemy enemy) {
		
	}
	
	
	public int onDamaged(GameEnemy damager, int damage) {
		return damage;
	}
	
	public void onLeftClick()
	{
		
	}
	
	public void onRightClick()
	{
		
	}
	
	public void onItemStats(StatContainer item)
	{
		
	}
	public void onItemStatsLate(StatContainer item)
	{
		
	}
	
	public void onPlayerStats(StatContainer item) {
		
	}
}
