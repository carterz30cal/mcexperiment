package com.carterz30cal.utils;

import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.main.Dungeons;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.entity.ArmorStand.LockType;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class EntityUtils
{
	public static ArmorStand spawnHologram(Location location, int lifetime)
	{
        if (!location.getChunk().isLoaded()) {
            return null;
        }
		ArmorStand armour = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

        armour.setVisible(false);
		armour.setGravity(false);
		armour.setCustomNameVisible(true);
		armour.setSmall(true);
		armour.setMarker(true);
		for (EquipmentSlot slot : EquipmentSlot.values()) armour.addEquipmentLock(slot, LockType.REMOVING_OR_CHANGING);
		
		
		if (lifetime > 0)
		{
			new BukkitRunnable()
			{

				@Override
				public void run() {
					// TODO Auto-generated method stub
					armour.remove();
				}
				
			}.runTaskLater(Dungeons.instance, lifetime);
		}
		
		return armour;
	}
	
	
	public static List<GamePlayer> getNearbyPlayers(Location l, double radius)
	{
		List<GamePlayer> nearby = new ArrayList<>();
		for (GamePlayer player : PlayerManager.players.values())
		{
			if (player.getLocation().distance(l) <= radius) nearby.add(player);
		}

		return nearby;
	}
	
	public static List<GameEnemy> getNearbyEnemies(Location l, double radius)
	{
		List<GameEnemy> enemies = new ArrayList<>();
		for (GameEntity e : GameEntity.entities.values())
		{
            if (e.dead) {
                continue;
            }
			if (e instanceof GameEnemy)
			{
				if (e.getLocation().distance(l) <= radius) enemies.add((GameEnemy)e);
			}
		}
		
		return enemies;
	}
	public static List<GameEntity> getNearbyEnemiesAsEntity(Location l, double radius)
	{
		List<GameEntity> enemies = new ArrayList<>();
		for (GameEntity e : GameEntity.entities.values())
		{
			if (e instanceof GameEnemy)
			{
				if (e.getLocation().distance(l) <= radius) enemies.add(e);
			}
		}
		
		return enemies;
	}
	 
	public static Entity spawnPart(EntityType type, Location location)
	{
        if (!location.getChunk().isLoaded()) {
            return null;
        }
		Entity part = location.getWorld().spawnEntity(new Location(location.getWorld(), 0, 0, 0), type);
		part.teleport(location, TeleportCause.PLUGIN);
		part.setSilent(true);
		if (part instanceof LivingEntity)
		{
			LivingEntity livingPart = (LivingEntity)part;
			
			livingPart.getEquipment().clear();
			EntityUtils.applyPotionEffect(livingPart, PotionEffectType.FIRE_RESISTANCE, 999999, 1, false);
			
			if (livingPart.getVehicle() != null) livingPart.getVehicle().remove();
		}
		if (part instanceof Ageable)
		{
			Ageable ager = (Ageable)part;
			ager.setAdult();
		}

        if (part instanceof Slime) {
            Slime myHomeSlime = (Slime) part;
            myHomeSlime.setSize(3);
        }
		
		return part;
	}
	public static void sayTo(List<GamePlayer> players, String message)
	{
		sayTo(players, message, 0);
	}
	public static void sayTo(List<GamePlayer> players, String message, int delay)
	{
		for (GamePlayer p : players) p.sendMessage(message, delay);
	}
	
	public static void setArmourPiece(Mob mob, EquipmentSlot slot, ItemStack item)
	{
		mob.getEquipment().setItem(slot, item);
	}
	public static void setArmourPiece(Mob mob, EquipmentSlot slot, String item)
	{
		mob.getEquipment().setItem(slot, ItemFactory.build(item));
	}
	
	public static void setEntityName(LivingEntity e, String name)
	{
		if (e == null) return;
		e.setCustomName(StringUtils.colourString(name));
	}
	
	public static void applyKnockback(GamePlayer player, GameEnemy enemy)
	{
		applyKnockback(player, enemy, 100);
	}
	
	public static void applyKnockback(GamePlayer player, GameEnemy enemy, int mkb)
	{
		double knockback = (mkb / 100D) * (enemy.type.knockback / 100D);

        if (enemy.getMain().getLocation().subtract(0, 0.1, 0).getBlock().getType() == Material.AIR) {
            knockback *= 0.4;
        }
		
		Vector kbv = enemy.getMain().getLocation().subtract(player.getLocation()).toVector().normalize();
		
		kbv.setY(0.4);
		kbv.multiply(knockback * 0.6);
		

		try
		{
			if (enemy.director != null) {
				kbv.add(enemy.director.getVelocity());
				enemy.director.setVelocity(kbv);
			}
			else {
				kbv.add(enemy.getMain().getVelocity());
				enemy.getMain().setVelocity(kbv);
			}
		}
		catch (IllegalArgumentException ignored)
		{
			
		}
		
	}

    public static void applyKnockback(GamePlayer player, LivingEntity enemy, int mkb) {
        double knockback = (mkb / 100D);

        if (enemy.getLocation().subtract(0, 0.1, 0).getBlock().getType() == Material.AIR) {
            knockback *= 0.4;
        }

        Vector kbv = enemy.getLocation().subtract(player.getLocation()).toVector().normalize();

        kbv.setY(0.4);
        kbv.multiply(knockback * 0.6);


        try {
            kbv.add(enemy.getVelocity());
            enemy.setVelocity(kbv);
        } catch (IllegalArgumentException ignored) {

        }

    }

	public static void applyPotionEffect(LivingEntity e, PotionEffectType type, int ticks, int level, boolean showParticles)
	{
		e.addPotionEffect(new PotionEffect(type, ticks, level - 1, false, showParticles));
	}
}
