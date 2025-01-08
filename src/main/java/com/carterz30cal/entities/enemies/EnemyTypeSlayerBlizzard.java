package com.carterz30cal.entities.enemies;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffectType;

import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.EntityUtils;
import com.carterz30cal.utils.ParticleUtils;

public class EnemyTypeSlayerBlizzard extends EnemyTypeSimple 
{
	public final int tier;
	public int lifeDrainTimer;
	public int lifeDrainRadius;
	public EnemyTypeSlayerBlizzard(ConfigurationSection m) {
		super(m);
		
		tier = m.getInt("slayer-tier", 1);
		lifeDrainTimer = m.getInt("life-drain-timer", 20);
		lifeDrainRadius = m.getInt("life-drain-radius", 0);
	}

	
	@Override
	public GameEnemy generate(Location location)
	{
		GameEnemy e = super.generate(location);
		e.deregisterEnemy();
		Zombie rails = (Zombie)Dungeons.w.spawnEntity(location, EntityType.ZOMBIE);
		rails.getAttribute(Attribute.MOVEMENT_SPEED).addModifier(new AttributeModifier("MOD_SPEED", speed - 1, Operation.MULTIPLY_SCALAR_1));
		rails.getEquipment().clear();
		rails.getEquipment().setHelmet(ItemFactory.build("STONE_BUTTON"));
		rails.setAdult();
		//rails.setInvulnerable(true);
		EntityUtils.applyPotionEffect(rails, PotionEffectType.FIRE_RESISTANCE, 10000000, 1, false);
		EntityUtils.applyPotionEffect(rails, PotionEffectType.INVISIBILITY, 10000000, 1, false);
		
		e.parts.add(rails);
		e.data.put("rails", rails);
		e.register();
		
		return e;
	}
	
	@Override
	public void onTick(GameEnemy enemy)
	{
		enemy.main.setFireTicks(0);
		enemy.main.setVisualFire(false);
		Zombie dest = (Zombie)enemy.data.get("rails");
		if (dest == null) return;
		
		dest.setHealth(20);
		enemy.main.teleport(dest);
		ParticleUtils.spawnAround(dest, new Particle.DustOptions(Color.BLUE, 0.9F), 4);
		ParticleUtils.spawnAround(dest, new Particle.DustOptions(Color.AQUA, 0.9F), 4);
		
		int tick = (int)enemy.data.getOrDefault("life_drain_timer", 0);
		if (tick >= lifeDrainTimer)
		{
			for (GamePlayer player : EntityUtils.getNearbyPlayers(enemy.getLocation(), lifeDrainRadius))
			{
				player.damage((int) (damage * 0.1D));
			}
			
			enemy.data.put("life_drain_timer", 0);
		}
		else enemy.data.put("life_drain_timer", tick + 1);
	}
	
	@Override
	public boolean onLaunch(GameEnemy enemy) 
	{
		return false;
	}
	
	@Override
	public void onTarget(GameEnemy enemy, GamePlayer p)
	{
		Entity dest = (Entity)enemy.data.get("rails");
		if (dest == null) return;
		
		
		Player pl = p == null ? null : p.player;
		((Mob)dest).setTarget(pl);
	}
}
