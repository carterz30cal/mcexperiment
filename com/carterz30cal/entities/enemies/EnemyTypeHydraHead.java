package com.carterz30cal.entities.enemies;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

import com.carterz30cal.areas.BossHydra;
import com.carterz30cal.entities.AbstractEnemyType;
import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.EnemyManager;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.damage.StatusEffect;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.EntityUtils;
import com.carterz30cal.utils.RandomUtils;

public class EnemyTypeHydraHead extends AbstractEnemyType {
	public static final Location root = new Location(Dungeons.w, -159.5, 76, -24.5,0,0);
	public final int BODY_PARTS = 25;
	
	public int regenPerTick;
	
	public EnemyTypeHydraHead(ConfigurationSection m) {
		super(m);
		
		regenPerTick = m.getInt("regen", 2);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public GameEnemy generate(Location location) {
		GameEnemy mob = new GameEnemy(location, this);
		
		mob.main = EntityUtils.spawnPart(EntityType.SLIME, root);
		Slime head = (Slime)mob.main;
		head.setSize(2);
		head.setGravity(false);
		head.setAI(false);
		
		mob.data.put("target", location);
		mob.data.put("original", location);
		
		mob.doTick();
		mob.register();
		
		mob.data.put("swaytick", RandomUtils.getRandomEx(0, 360));
		return mob;
	}
	
	public void onTick(GameEnemy enemy)
	{
		
		
		Location head = (Location)enemy.data.get("target");
		head = head.clone();
		int swaytick = (int)enemy.data.getOrDefault("swaytick", 0);
		
		double sin = Math.sin(Math.toRadians(swaytick));
		sin *= sin;
		sin *= 0.75;
		
		
		
		
		
		
		int parts = enemy.parts.size();
		
		if (parts < BODY_PARTS) {
			Slime body = (Slime)EntityUtils.spawnPart(EntityType.SLIME, root);
			body.setSize(1);
			body.setGravity(false);
			body.setAI(false);
			body.setInvulnerable(true);
			enemy.parts.add(body);
		}
		enemy.deregisterEnemy();
		enemy.register();
		
		//enemy.setLocation(head);
		
		
		boolean onGround = (boolean)enemy.data.getOrDefault("onground", false);
		
		
		if (onGround) {
			if (enemy.health == 1) {
				enemy.data.put("onground", false);
				enemy.data.put("target", (Location)enemy.data.get("original"));
				onTick(enemy);
				return;
			}
		}
		else head = head.add(0, 0, sin);
		
		Vector travel = head.clone().subtract(root.clone()).toVector();
		travel.setY(0);
		
		double target = head.getY() - root.getY();
		int i;
		for (i = 0; i < parts; i++) {
			Location pos = root.clone().add(travel.clone().multiply((i+1) / (double)(BODY_PARTS + 2)));
			double sqdist = (pos.getX() - root.getX()) * (pos.getX() - root.getX()) + (pos.getZ() - root.getZ()) * (pos.getZ() - root.getZ());
			
			pos.setY(getY(enemy, target, sqdist));
			enemy.parts.get(i).teleport(pos, TeleportCause.PLUGIN);
		}
		
		i++;
		Location pos = root.clone().add(travel.clone().multiply((i+1) / (double)(BODY_PARTS + 2)));
		double sqdist = (pos.getX() - root.getX()) * (pos.getX() - root.getX()) + (pos.getZ() - root.getZ()) * (pos.getZ() - root.getZ());
		
		pos.setY(getY(enemy, target, sqdist));
		pos.setYaw(90);
		enemy.main.teleport(pos, TeleportCause.PLUGIN);
		
		if (onGround) {
			enemy.data.put("groundtick", (int)enemy.data.getOrDefault("groundtick", 0) + 1);
			enemy.setHealth(enemy.getHealth() + regenPerTick * 5);
		}
		else enemy.setHealth(enemy.getHealth() + regenPerTick);
		enemy.data.put("swaytick", swaytick + 1);
	}
	
	
	public void onStatusProc(GameEnemy enemy, StatusEffect status) {
		if (status == StatusEffect.DEATH) BossHydra.headsKilled += 2;
		if (RandomUtils.getRandom(1, 3) == 1) enemy.data.put("onground", true);
	}
	
	public double getY(GameEnemy enemy, double target, double sqdist) {
		boolean onGround = (boolean)enemy.data.getOrDefault("onground", false);
		
		if (onGround) {
			int ogtick = (int)enemy.data.getOrDefault("groundtick", 1);
			//enemy.data.put("groundtick", ogtick + 1);
			
			double dist = Math.sqrt(sqdist);
			
			Location head = (Location)enemy.data.get("target");
			head = head.clone();
			head.setY(root.getY());
			
			double hdist = root.distance(head);
			double ntarget = 75 - root.getY();
			
			double groundy = root.getY() + ntarget * (dist / hdist);
			double airy = root.getY() + Math.max(target - (target / sqdist), Math.min(Math.sqrt(sqdist), target - 1));
			
			return groundy + (airy - groundy) * (1 - (Math.min(ogtick, 15) / 15D));
		}
		else return root.getY() + Math.max(target - (target / sqdist), Math.min(Math.sqrt(sqdist), target - 1));
	}
	
	public void onDamaged(GameEnemy enemy, DamageInfo info)
	{
		BossHydra.participants.add(info.attacker);
		if (enemy.health < 0.5 && RandomUtils.getRandom(1, 9) < 3) enemy.data.put("onground", true);;
		
		if (enemy.health > 0.7 && RandomUtils.getRandom(1, 18) == 1) BossHydra.minions.add(EnemyManager.spawn("hydra_servant", enemy.main.getLocation()));
	}
	
	public void onKilled(GameEnemy enemy)
	{
		for (int m = 0; m < 8; m++) BossHydra.minions.add(EnemyManager.spawn("hydra_servant", enemy.main.getLocation()));
		
		BossHydra.headsKilled++;
		BossHydra.heads.remove(enemy);
		BossHydra.respawnHead((Location)enemy.data.get("original"));
	}

}
