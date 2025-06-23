package com.carterz30cal.events;

import java.util.List;
import java.util.ArrayList;

import com.carterz30cal.entities.*;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.carterz30cal.entities.damage.StatusEffect;
import com.carterz30cal.entities.enemies.EnemyTypeDamageCapped;
import com.carterz30cal.items.Item;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.utils.MathsUtils;
import com.carterz30cal.utils.ParticleUtils;
import com.carterz30cal.utils.RandomUtils;

public class ListenerEntityDamage implements Listener
{
	@EventHandler
	public void onEntityDamageEntity(EntityDamageByEntityEvent e)
	{
		GameEntity damager = GameEntity.get(e.getDamager());
		GameEntity damaged = GameEntity.get(e.getEntity());
		
		if (damaged == null || damager == null) return;
		if (damager instanceof GamePlayer)
		{
			GamePlayer player = (GamePlayer)damager;
			//GameEnemy enemy = (GameEnemy)damaged;
			GameEntity enemy = damaged;
			
			ItemStack main = player.getMainItem();
			Item item = ItemFactory.getItem(main);
			
			DamageInfo info = new DamageInfo();
			e.setCancelled(true);
			
			
			
			List<ItemAbility> abilities = new ArrayList<>(player.abilities);
			if (e.getDamager() instanceof AbstractArrow)
			{
				String arrowType = e.getDamager().getPersistentDataContainer().getOrDefault(GameEnemy.keyArrowType, PersistentDataType.STRING, "arrow");
				
				Item arrow = ItemFactory.getItem(arrowType);
				abilities.addAll(ItemFactory.getItemAbilities(arrow));
				arrow.stats.pushIntoContainer(player.stats);
				
				e.getDamager().remove();
			}
			else if (player.attackTick > 0) return;
			
			player.attackTick = 4;
			info.defender = damaged;
			info.attacker = player;
			info.main = true;
			
			if (item == null)
			{
				info.damage = 1;
				info.type = DamageType.PHYSICAL;
			}
			else
			{
				info.type = item.type.damageType;
				
				
				for (ItemAbility a : abilities) a.onPreAttack(info);
				player.stats.executeOperations();
				
				if (e.getDamager() instanceof Player && info.type == DamageType.PROJECTILE || item.type == ItemType.TOME) info.damage = 1;
				else
				{
					double multiplier = ((100 + player.stats.getStat(Stat.STRENGTH))/100D) * ((100 + player.stats.getStat(Stat.POWER))/100D)
							* ((100 + player.stats.getStat(Stat.MIGHT))/100D);
					
					int damage = (int)Math.round(player.stats.getStat(Stat.DAMAGE) * multiplier);
					
					info.damage = damage;
				}
			}
			
			for (ItemAbility a : abilities) 
			{
				a.onAttack(info);
				a.onLeftClick();
			}
			player.stats.executeOperations();
			
			int hits = player.stats.getStat(Stat.SAVAGERY) / 100;
			int cha = player.stats.getStat(Stat.SAVAGERY) % 100;
			if (RandomUtils.getRandom(1, 100) <= cha) hits++;
			
			final int post = hits + 1;
			
			new BukkitRunnable()
			{
				int h = post;
				@Override
				public void run() {
					if (damaged.getHealth() == 0 || damaged.dead || info.damage == 0) 
					{
						cancel();
						return;
					}
					if (damaged instanceof GameEnemy) {
						((GameEnemy)damaged).lastDamager = player;
						for (StatusEffect effect : player.stats.statuses.effects.keySet()) {
							int value = player.stats.statuses.getStatus(effect);
							for (ItemAbility ab : abilities) {
								value = ab.onStatusBuildup(effect, value);
							}
							
							((GameEnemy) damaged).applyStatusEffect(effect, value);
						}
					}
					
					damaged.damage(info);
					info.damage *= 0.85;
					if (h != post)
					{
						int deg;
						if (RandomUtils.getRandom(1, 2) == 1) deg = RandomUtils.getRandom(30, 150);
						else deg = RandomUtils.getRandom(360-150, 360-30);
						
						int degOffset = RandomUtils.getRandom(160, 200);
						
						Vector dis = damager.getLocation().subtract(damaged.getLocation()).toVector().normalize();
						dis.setY(0);
						
						
						Vector rot = dis.clone().rotateAroundY(Math.toRadians(90));
						dis = dis.multiply(0.75);
						Location p1 = damaged.getLocation().add(0, enemy.getHeight() / 2, 0);
						p1 = p1
								.add(dis)
								.add(rot.getX() * MathsUtils.getCircleX(deg), MathsUtils.getCircleZ(deg) * 0.9, rot.getZ() * MathsUtils.getCircleX(deg));
						Location p2 = damaged.getLocation().add(0, enemy.getHeight() / 2, 0);
						p2 = p2
								.add(dis)
								.add(rot.getX() * MathsUtils.getCircleX(deg + degOffset), MathsUtils.getCircleZ(deg + degOffset) * 0.9, rot.getZ() * MathsUtils.getCircleX(deg + degOffset));
						
						ParticleUtils.spawnLine(p1, p2, new Particle.DustOptions(enemy.getBloodColour(), 0.8F), 11);
						player.playSound(Sound.ENTITY_PLAYER_ATTACK_CRIT, 0.9, 0.7);
					}
					
					h--;
					if (h == 0) cancel();
				}
				
			}.runTaskTimer(Dungeons.instance, 0, 3);
			
			
		}
		else if (damager instanceof GameSummon) {
			if (damaged instanceof GamePlayer) {
				e.setCancelled(true);
			} else {
				GameEnemy enemy = (GameEnemy)damager;
				e.setDamage(0);

				DamageInfo info = enemy.getAttack();
				info.attacker = ((GameSummon)damager).getOwner();
				info.indirect = true;
				damaged.damage(info);
			}
		}
		else if (damager instanceof GameEnemy)
		{
			GameEnemy enemy = (GameEnemy)damager;
			
			
			if (damaged instanceof GamePlayer) ((GamePlayer)damaged).lastDamager = enemy;
			e.setDamage(0);
			damaged.damage(enemy.getAttack());
		}
		
		
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e)
	{
		e.setCancelled(true);
		Dungeons.w.createExplosion(e.getEntity().getLocation(), 5, false, false);
	}
	
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e)
	{
		if (e.getEntity() instanceof AbstractArrow)
		{
			AbstractArrow ar = (AbstractArrow)e.getEntity();
			ar.setPickupStatus(PickupStatus.DISALLOWED);
			
			if (e.getHitEntity() != null && e.getHitEntity().getType() == EntityType.ENDERMAN) 
			{
				EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent((LivingEntity)ar.getShooter(), e.getHitEntity(), DamageCause.ENTITY_ATTACK, 1);
				onEntityDamageEntity(ev);
				
				e.getEntity().remove();
			}
			
			
			if (e.getHitBlock() != null) 
			{
				new BukkitRunnable()
				{

					@Override
					public void run() {
						// TODO Auto-generated method stub
						e.getEntity().remove();
					}
					
				}.runTaskLater(Dungeons.instance, 20);
			}
		}
		else if (e.getEntity() instanceof FishHook)
		{
			FishHook hook = (FishHook)e.getEntity();
			if (e.getHitEntity() != null)
			{
				GameEntity entity = GameEntity.get(e.getHitEntity());
				if (entity != null && entity instanceof GameEnemy)
				{
					EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent((LivingEntity)hook.getShooter(), e.getHitEntity(), DamageCause.ENTITY_ATTACK, 1);
					onEntityDamageEntity(ev);
					
					e.getEntity().remove();
				}
			}
		}
		
		e.setCancelled(false);
	}
	
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e)
	{
		GameEntity entity = GameEntity.get(e.getEntity());
		
		if (entity instanceof GamePlayer)
		{
			GamePlayer player = (GamePlayer)entity;
			//System.out.println(e.getCause());
			switch (e.getCause())
			{
			case CUSTOM:
			case SUICIDE:
			case ENTITY_ATTACK:
				return;
			case FALL:
				int damage = (int)(player.player.getFallDistance() * player.player.getFallDistance() * 0.09);
				player.damage(damage);
				e.setCancelled(true);
				break;
			case VOID:
				player.kill();
			default:
				e.setCancelled(true);
			}
		}
		else if (entity instanceof GameEnemy)
		{
			GameEnemy enemy = (GameEnemy)entity;
			switch (e.getCause())
			{
			case CUSTOM:
			case ENTITY_ATTACK:
				break;
			case DROWNING:
				if (RandomUtils.getRandom(1, 5) == 1)
				{
					DamageInfo info = new DamageInfo();
					info.type = DamageType.FROST;
					info.damage = 25;
					enemy.damage(info);
				}

				e.setCancelled(true);
				break;
			case VOID:
				if (enemy.type instanceof EnemyTypeDamageCapped) enemy.remove();
				else enemy.damage(1000000);
			case FALL:
			default:
				e.setCancelled(true);
			}
		}
		
		
	}
	
	@EventHandler
	public void onEntityTransform(EntityTransformEvent e)
	{
		e.setCancelled(true);
	}
	
	
	@EventHandler
	public void onSplit (SlimeSplitEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityLaunch(ProjectileLaunchEvent e) {
		Entity shooter = (Entity)e.getEntity().getShooter();
		if (shooter instanceof Player) return;
		
		GameEnemy enemy = (GameEnemy)GameEnemy.get(shooter);
		if (!enemy.type.onLaunch(enemy))
		{
			e.setCancelled(true);
			e.getEntity().remove();
		}
		else {
			e.getEntity().getPersistentDataContainer().set(GameEnemy.keyEnemy, PersistentDataType.STRING, enemy.main.getUniqueId().toString());
		}
	}
	
	
	@EventHandler
	public void onEntityTeleport(EntityTeleportEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onHeal(EntityRegainHealthEvent e)
	{
		if (e.getRegainReason() != RegainReason.CUSTOM) e.setCancelled(true);
	}
}
