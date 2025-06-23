package com.carterz30cal.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.carterz30cal.areas.AbstractArea;
import com.carterz30cal.entities.damage.StatusEffect;
import com.carterz30cal.entities.damage.StatusEffects;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.EntityUtils;
import com.carterz30cal.utils.LevelUtils;
import com.carterz30cal.utils.RandomUtils;
import com.carterz30cal.utils.StringUtils;

public class GameEnemy extends GameEntity
{
	public static NamespacedKey keyEnemy = new NamespacedKey(Dungeons.instance, "keyEnemy");
	public static NamespacedKey keyArrowType = new NamespacedKey(Dungeons.instance, "keyArrowType");
	
	public AbstractEnemyType type;
	
	public Entity main;
	public List<Entity> parts = new ArrayList<>();
	protected ArmorStand display;
	
	protected ArmorStand displayName;
	protected ArmorStand displayHealth;
	protected ArmorStand displayStatuses;
	
	public GameEntity target;
	
	public GamePlayer lastDamager;
	public int timesHit;
	
	public StatusEffects statuses;
	public StatusEffects resistances;
	
	public Map<String, Object> data = new HashMap<>();
	public AbstractArea spawnedArea;
	
	private BukkitRunnable ticker;
	
	protected void destroy()
	{
		if (dead) return;
		health = 0;
		dead = true;
		
		//String name = "RED" + type.name + "GRAY : RED0\u2665";
		//if (type.level != 0) name = "WHITE[" + type.level + "] " + name;
		//display.setCustomName(StringUtils.colourString(name));
		
		List<Entity> all = new ArrayList<>(getParts());
		all.add(getMain());
		for (Entity e : all) 
		{
			if (e instanceof LivingEntity) ((LivingEntity)e).setHealth(0);
			else e.remove();
		}
		
		if (spawnedArea != null && lastDamager != null) spawnedArea.onKill(this);
		
		if (lastDamager != null) lastDamager.attackTick = 0;
		ticker.cancel();
		type.onKilled(this);
		dropItems(lastDamager);
		
		deregister();
	}
	
	public double getHeight() {
		return type.displayHeight;
	}
	
	public Color getBloodColour() {
		return type.bloodColour;
	}
	
	@Override
	public void remove()
	{
		health = 0;
		dead = true;
		
		List<Entity> all = new ArrayList<>(getParts());
		all.add(getMain());
		for (Entity e : all) e.remove();
		
		ticker.cancel();
		deregister();
	}
	
	public List<Entity> getParts()
	{
		List<Entity> e = new ArrayList<>(parts);
		if (display != null) e.add(display);
		
		if (displayName != null) e.add(displayName);
		if (displayHealth != null) e.add(displayHealth);
		if (displayStatuses != null) e.add(displayStatuses);
		return e;
	}
	
	public void setImmune(boolean v)
	{
		main.setInvulnerable(v);
		if (main instanceof LivingEntity) ((LivingEntity)main).setAI(!v);
	}
	
	public void kill()
	{
		destroy();
	}
	
	public int getHealth()
	{
		return (int)(health * type.getMaxHealth());
	}
	
	public Entity getMain()
	{
		return main;
	}
	
	public DamageInfo getAttack()
	{
		DamageInfo info = new DamageInfo();
		info.damage = type.getDamage();
		info.type = type.damageType;
		
		return info;
	}
	
	public void applyStatusEffect(StatusEffect effect, int amount) {
		if (resistances.getImmune(effect)) return;
		
		
		int value = statuses.getStatus(effect) + amount;
		if (value >= resistances.getStatus(effect)) {
			lastDamager.playSound(Sound.BLOCK_FIRE_EXTINGUISH, 0.6, 1.3, 1);
			lastDamager.playSound(Sound.BLOCK_FIRE_EXTINGUISH, 0.7, 1.1, 4);
			
			effect.effect.onProc(this);
			statuses.effects.put(effect, 0);
			
			int resistance = (int)Math.round(resistances.getStatus(effect) * effect.resistanceMultiplier);
			resistances.effects.put(effect, resistance);
			
			type.onStatusProc(this, effect);
			for (ItemAbility a : lastDamager.abilities) a.onStatusProc(effect, this);
		}
		else {
			statuses.effects.put(effect, value);
		}
	}
	
	public void setHealth(int value)
	{
		double prog = (double)value / (double)type.getMaxHealth();

		if (prog > 1) prog = 1;
		
		if (prog <= 0) kill();
		
		health = prog;
	}
	
	public final boolean hasTag(String tag)
	{
		return type.tags.contains(tag);
	}
	
	
	public final void deregisterEnemy()
	{
		deregister();
	}
	
	protected final void deregister()
	{
		UUID uuid = getMain().getUniqueId();
		
		deregister(uuid);
	}
	public final void register()
	{
		if (getMain() == null) return;
		
		UUID uuid = getMain().getUniqueId();
		
		List<Entity> all = new ArrayList<>(getParts());
		all.add(getMain());
		
		super.register(uuid);
		for (Entity e : all)
		{
			e.getPersistentDataContainer().set(keyEnemy, PersistentDataType.STRING, uuid.toString());
		}
	}

	protected String getName() {
		String name = "RED" + type.onName(this);
		if (type.level != 0) name = "WHITE[" + type.level + "] " + name;

		return name;
	}
	
	protected void tick()
	{
		if (!main.isValid()) destroy();
		
		if (displayName == null && (lastDamager != null || health < 0.99)) {
			Location d = main.getLocation().add(0, main.getHeight() + 0.4, 0);

			displayName = EntityUtils.spawnHologram(d, -1);
			d.subtract(0, 0.25, 0);
			displayHealth = EntityUtils.spawnHologram(d, -1);
			d.subtract(0, 0.25, 0);
			displayStatuses = EntityUtils.spawnHologram(d, -1);
		}
		if (displayName != null) {
			String name = getName();
			
			String healthed = "RED" + getHealth() + "\u2665";
			
			boolean activeStatuses = !statuses.isEmpty();
			String statusText = "";
			double offset = 0.1;
			boolean hasEffectAlready = false;
			if (activeStatuses) {
				for (StatusEffect effect : statuses.effects.keySet()) {
					
					int value = statuses.effects.getOrDefault(effect, 0);
					if (value == 0) continue;
					
					if (hasEffectAlready) statusText += " ";
					statusText += effect.colour + effect.symbol + " " + effect.shortName;
					statusText += " " + StringUtils.progressBar((double)value / resistances.effects.getOrDefault(effect, effect.defaultResistance), 5, effect.colour);
					
					hasEffectAlready = true;
				}
				offset = 0.3;
			}
			else statusText = " ";
			Location d = main.getLocation().add(0, type.displayHeight + 0.4 + offset, 0);
			
			displayName.teleport(d, TeleportCause.PLUGIN);
			displayName.setCustomName(StringUtils.colourString(name));
			d.subtract(0, 0.25, 0);
			displayHealth.teleport(d, TeleportCause.PLUGIN);
			displayHealth.setCustomName(StringUtils.colourString(healthed));
			d.subtract(0, 0.25, 0);
			displayStatuses.teleport(d, TeleportCause.PLUGIN);
			displayStatuses.setCustomName(StringUtils.colourString(statusText));
		}

		if (target == null) {
			target = findTarget();
			if (target == null)  {
				type.onTarget(this, null);
				EntityUtils.applyPotionEffect((LivingEntity)main, PotionEffectType.SLOWNESS, 19, 50, false);
			}
			else ((Mob)getMain()).setTarget(target.getTargetable());
		}
		else {
			if (target.isTargetable(this)) target = null;
			else if (target instanceof GamePlayer) {
				type.onTarget(this, (GamePlayer)target);
			}
		}
	}

	@Override
	public LivingEntity getTargetable() {
		return (LivingEntity) getMain();
	}

	protected GameEntity findTarget() {
		//GameEntity en;
		List<GameEnemy> enemies = EntityUtils.getNearbyEnemies(getLocation(), 10);
		enemies.removeIf((e) -> !(e instanceof GameSummon));
		if (enemies.size() > 0) return enemies.get(0);
		else return null;
	}

	
	public void setLocation(Location l)
	{
		main.teleport(l);
	}
	
	
	public void doTick()
	{
		type.onTick(this);
		tick();
	}
	
	public GameEnemy(Location spawn, AbstractEnemyType type)
	{
		this.type = type;
		health = 1;
		
		this.statuses = new StatusEffects();
		this.resistances = type.resistances.clone();
		
		ticker = new BukkitRunnable()
		{

			@Override
			public void run() {
				doTick();
			}
			
		};
		
		ticker.runTaskTimer(Dungeons.instance, 1, 1);
	}
	
	@Override
	public Location getLocation()
	{
		return main.getLocation();
	}
	
	public void dropItems(GamePlayer killer)
	{
		if (killer == null) return;
		
		for (ItemAbility a : killer.abilities) a.onKill(this);
		
		for (ItemStack it : type.loot.generate(killer)) killer.giveItem(it);
		
		killer.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8, 1.4);
		killer.lastXpReward = killer.gainXp(LevelUtils.getEnemyBaseXpReward(type.level));
		
		killer.lastCoinReward = killer.gainCoins(this);
		
		killer.rewardTick = 30;
	}

	
	
	
	public void damage(int damage)
	{
		DamageInfo info = new DamageInfo();
		info.damage = damage;
		info.type = DamageType.HOLY;
		
		damage(info);
	}
	public void damage(int damage, DamageType type)
	{
		DamageInfo info = new DamageInfo();
		info.damage = damage;
		info.type = type;
		
		damage(info);
	}
	
	@SuppressWarnings({ "deprecation", "unused" })
	@Override
	public void damage(DamageInfo info) {
		if (dead) return;
		if (main.isInvulnerable())
		{
			ArmorStand hologram = EntityUtils.spawnHologram(main.getLocation().add(RandomUtils.getDouble(-0.7F, 0.7F),
					RandomUtils.getDouble(0.4F, 0.8F),
					RandomUtils.getDouble(-0.7F, 0.7F)), 30);
			hologram.setCustomName(ChatColor.getLastColors(info.type.name) + "IMMUNE");
			return;
		}
		
		if (info.attacker != null) 
		{
			if (lastDamager != info.attacker)
			{
				lastDamager = info.attacker;
				timesHit = 0;
			}
			else timesHit++;
			
			if (!info.indirect) {
				EntityUtils.applyKnockback(lastDamager, this, info.type.knockbackModifier);

				if (target == null) {
					target = info.attacker;
					info.attacker.targeted.add(this);
				}
			}

		}
		
		DamageInfo modified = new DamageInfo();
		modified.damage = (int)Math.round(info.damage * type.armour.getModifier(info.type));
		modified.damage = Math.max(0, modified.damage);
		modified.type = info.type;
		modified.attacker = info.attacker;
		modified.defender = this;
		type.onDamaged(this, modified);
		
		ArmorStand hologram = EntityUtils.spawnHologram(main.getLocation().add(RandomUtils.getDouble(-0.7F, 0.7F),
				RandomUtils.getDouble(0.4F, 0.8F),
				RandomUtils.getDouble(-0.7F, 0.7F)), 30);
		
		health -= (double)(modified.damage)/type.getMaxHealth();
		
		health = Math.max(0, health);
		
		Entity main = getMain();
		if (main instanceof LivingEntity)
		{
			((LivingEntity)main).playHurtAnimation(0);
			//((LivingEntity)main).damage(1);
			//((LivingEntity)main).setHealth(((LivingEntity)main).getMaxHealth());
		}
		
		hologram.setCustomName(ChatColor.getLastColors(modified.type.name) + modified.damage);

		if (getHealth() == 0) destroy();
		else if (lastDamager != null) lastDamager.playSound(Sound.ENTITY_PLAYER_HURT, 0.7, 0.9);
		
	}
}
