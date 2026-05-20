package com.carterz30cal.entities.enemies.core;

import com.carterz30cal.areas.AbstractGameArea;
import com.carterz30cal.entities.*;
import com.carterz30cal.entities.damage.StatusEffects;
import com.carterz30cal.entities.enemies.EnemyTypeSimple;
import com.carterz30cal.entities.enemies.directors.EnemyDirector;
import com.carterz30cal.entities.enemies.representation.EnemyInformationDisplay;
import com.carterz30cal.entities.enemies.representation.EnemyRepresentation;
import com.carterz30cal.entities.health.EntityHealthSystem;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.health.damage.handlers.AggressiveEntity;
import com.carterz30cal.entities.health.damage.handlers.DamageModifier;
import com.carterz30cal.entities.health.damage.handlers.DamageableEntity;
import com.carterz30cal.entities.health.status.StatusEffect;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.utils.EntityUtils;
import com.carterz30cal.utils.LevelUtils;
import com.carterz30cal.utils.RandomUtils;
import com.carterz30cal.utils.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("UnnecessaryUnicodeEscape")
public class GameEnemy extends GameEntity implements AggressiveEntity, DamageableEntity
{
	public static NamespacedKey keyEnemy = new NamespacedKey(Dungeons.instance, "keyEnemy");
	public static NamespacedKey keyArrowType = new NamespacedKey(Dungeons.instance, "keyArrowType");

    private final BukkitRunnable ticker;
    private EnemyRepresentation representation;
    private EntityHealthSystem healthSystem;
    private EnemyDirector enemyDirector;
    private EnemyData enemyData;
    private EnemyInformationDisplay enemyInformationDisplay;


	public AbstractEnemyType type;
	
	public Entity main;
	public List<Entity> parts = new ArrayList<>();


    public Mob director;

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
    public AbstractGameArea spawnedArea;
    private String typeId;

    public GameEnemy(EnemyRepresentation representation, EntityHealthSystem healthSystem, EnemyDirector director, String typeId) {
        this.representation = representation;
        this.healthSystem = healthSystem;
        this.enemyDirector = director;
        this.enemyInformationDisplay = new EnemyInformationDisplay(this);
        this.typeId = typeId;

        this.ticker = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        };
        this.ticker.runTaskTimer(Dungeons.instance, 0, 1);
    }

    public void tick() {
        enemyDirector.tick();
        representation.tick(enemyDirector.getLocation());
        healthSystem.tick();

        enemyInformationDisplay.reset();
        enemyInformationDisplay.setLine(0, getName());
        if (enemyData.alwaysDisplayHealth || !healthSystem.isAtMaxHealth()) {
            enemyInformationDisplay.setLine(1, text(healthSystem.getHealth() + "\u2665", NamedTextColor.RED));
        }
        int i = 1;
        for (var status : StatusEffect.values()) {
            var value = healthSystem.getBuildup(status);
            if (value < 1) {
                continue;
            }
            i++;
            enemyInformationDisplay.setLine(i, text().append(
                    text(status.symbol + " " + status.shortName, status.textColour),
                    StringUtils.progressBar(5, healthSystem.getBuildupPercentage(status), status.textColour, NamedTextColor.DARK_GRAY)
            ).build());
        }
        enemyInformationDisplay.tick();
    }

    protected Component getName() {
        return text().append(
                text("[", NamedTextColor.DARK_GRAY),
                text(enemyData.level, NamedTextColor.AQUA),
                text("]", NamedTextColor.DARK_GRAY),
                text(" "),
                enemyData.name
        ).build();
    }


    public EnemyDirector getEnemyDirector() {
        return enemyDirector;
    }

    public EnemyRepresentation getRepresentation() {
        return representation;
    }

    public EnemyData getEnemyData() {
        return enemyData;
    }

    public void setEnemyData(EnemyData data) {
        this.enemyData = data;
    }

    @Override
    public void damage(@NotNull DamagePacket damagePacket) {
        if (healthSystem.damage(damagePacket)) {
            if (healthSystem.isDead()) {
                kill();
            }
            representation.damage();
        }
    }

    @Override
    public List<DamageModifier> getDefensiveDamageModifiers() {
        return List.of();
    }

    @Override
    public boolean isImmune(StatusEffect effect) {
        return false;
    }

    @Override
    public double getHealthPercentage() {
        return healthSystem.getHealthPercentage();
    }

    /**
     *
     * @return the LivingEntity that we want the vanilla targeting system to target for us.
     */
    @Override
    public LivingEntity getTargetableEntity() {
        return enemyDirector.getTargetableEntity();
    }

    @Override
    public List<DamageModifier> getAggressiveDamageModifiers() {
        return List.of();
    }

    @Override
    public long getStat(Stat stat) {
        if (enemyData == null) {
            return 0;
        }
        else {
            return enemyData.stats.getOrDefault(stat, 0L);
        }
    }

    public long getCoinValue(GamePlayer rewardee) {
        long health = healthSystem.getMaxHealth() / 500;
        long damage = enemyData.getTotalRawDamage() / 30;
        long levels = enemyData.level;
        double multiplier = (100D + rewardee.getStat(Stat.BONUS_COINS)) / 100D;
        return Math.round((health + damage + levels) * multiplier);
    }

    protected void destroy()
	{
		if (dead) return;
		health = 0;
		dead = true;

        enemyDirector.remove();
        representation.kill();
        enemyInformationDisplay.remove(false);
        var lastAttacker = healthSystem.getLastAttacker();
        if (lastAttacker instanceof GamePlayer player) {
            player.attackTick = 0;
        }

        if (spawnedArea != null && lastDamager != null) {
            spawnedArea.OnKill(this);
        }
        dropLoot();

		ticker.cancel();
		deregister();
	}

    public void dropLoot() {
        for (var attacker : healthSystem.getPlayerAttackers()) {
            var builder = EnemyBuilder.getBuilder(typeId);
            assert builder != null;
            if (!builder.isTemporaryBuilder()) {
                attacker.IncrementKill(typeId);
            }
            for (var a : attacker.abilities)
                a.ability.onKill(a, this);
            for (var eh : attacker.GetEventHandlers())
                eh.OnKill(attacker, this, attacker.area);
            if (enemyData.lootTable != null) {
                for (ItemStack it : enemyData.lootTable.generate(attacker)) attacker.giveItem(it);
            }

            attacker.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8, 1.4);
            attacker.lastCoinReward = attacker.gainCoins(this);
            attacker.rewardTick = 30;
        }
    }

    @Override
    public void remove() {
        health = 0;
        dead = true;

        enemyDirector.remove();
        representation.remove();
        enemyInformationDisplay.remove(true);

        ticker.cancel();
        deregister();
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

	public void SetSpeed(double speed) {
		if (main instanceof Mob)
		{
			var speedAttribute = ((Mob)main).getAttribute(Attribute.MOVEMENT_SPEED);
			if (speedAttribute != null) {
                speedAttribute.getModifiers().forEach(speedAttribute::removeModifier);
                speedAttribute.addModifier(new AttributeModifier(EnemyTypeSimple.KEY_SPEED, speed - 1, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.ANY));
            }
        }
        if (director != null) {
            var speedAttribute = (director).getAttribute(Attribute.MOVEMENT_SPEED);
            if (speedAttribute != null) {
                speedAttribute.getModifiers().forEach(speedAttribute::removeModifier);
                speedAttribute.addModifier(new AttributeModifier(EnemyTypeSimple.KEY_SPEED, speed - 1, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.ANY));
            }
        }

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
            lastDamager.playSound(Sound.BLOCK_NOTE_BLOCK_SNARE, 0.3, 0.8);
			
			effect.effect.onProc(this);
			statuses.effects.put(effect, 0);
			
			int resistance = (int)Math.round(resistances.getStatus(effect) * effect.resistanceMultiplier);
			resistances.effects.put(effect, resistance);
			
			type.onStatusProc(this, effect);
			for (var a : lastDamager.abilities) a.ability.onStatusProc(a, effect, this);
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
	public GameEnemy(Location spawn, AbstractEnemyType type)
	{
		this.type = type;
		health = 1;
        this.uuid = UUID.randomUUID();

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

	protected String getName() {
		String name = "RED" + type.onName(this);
		if (type.level != 0) name = "WHITE[" + type.level + "] " + name;

		return name;
	}
	
	protected void tick()
	{
        if (!main.isValid() || main.isDead()) {
            destroy();
        }

		if (director != null) {
            if (director.isDead() || !director.isValid() || !getLocation().getChunk().isLoaded()) {
                destroy();
            }
            else {
                main.teleport(director.getLocation(), TeleportCause.PLUGIN);
                EntityUtils.applyPotionEffect(director, PotionEffectType.INVISIBILITY, 200, 0, false);
                EntityUtils.applyPotionEffect(director, PotionEffectType.FIRE_RESISTANCE, 200, 0, false);
            }
		}

        if (displayName == null && (lastDamager != null || health < 0.99) && !dead) {
			Location d = main.getLocation().add(0, main.getHeight() + 0.4, 0);

			displayName = EntityUtils.spawnHologram(d, -1);
            if (displayHealth == null) {
                displayHealth = EntityUtils.spawnHologram(d.clone().subtract(0, 0.25, 0), -1);
            }
            if (displayStatuses == null) {
                displayStatuses = EntityUtils.spawnHologram(d.clone().subtract(0, 0.5, 0), -1);
            }
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

        if (target == null || target.dead) {
			target = findTarget();
			if (target == null)  {
				type.onTarget(this, null);

				LivingEntity applyTo = director == null ? (LivingEntity)main : director;
				EntityUtils.applyPotionEffect(applyTo, PotionEffectType.SLOWNESS, 19, 50, false);
                if (director == null) {
                    ((Mob) getMain()).setTarget(null);
                }
                else {
                    director.setTarget(null);
                }
			}
            else {
                if (director == null) {
                    ((Mob) getMain()).setTarget(target.getTargetable());
                }
                else {
                    director.setTarget(target.getTargetable());
                }
            }
		}
		else {
            if (!target.isTargetable(this)) {
                target = null;
            }
            else {
                if (director == null) {
                    ((Mob) getMain()).setTarget(target.getTargetable());
                }
                else {
                    director.setTarget(target.getTargetable());
                }
                if (target instanceof GamePlayer) {
                    type.onTarget(this, (GamePlayer) target);
                }
            }
		}
	}

	@Override
	public LivingEntity getTargetable() {
		return (LivingEntity) getMain();
	}

	protected GameEntity findTarget() {
		//GameEntity en;
        List<GameEnemy> enemies = EntityUtils.getNearbyEnemies(getLocation(), 14);
		enemies.removeIf((e) -> !(e instanceof GameSummon));
		if (!enemies.isEmpty()) return enemies.get(0);
        else {
            for (var player : PlayerManager.getOnlinePlayers()) {
                if (player.getLocation().distance(getLocation()) > player.stats.getStat(Stat.VISIBILITY)) {
                    continue;
                }
                if (player.targeted.size() >= player.getMaxTargets() && !type.ignoreTargetLimit) {
                    continue;
                }
                player.targeted.add(this);
                return player;
            }
            return null;
        }
	}

	
	public void setLocation(Location l)
	{
		main.teleport(l);
		if (director != null) director.teleport(l);
	}
	
	
	public void doTick()
	{
		type.onTick(this);
		tick();
	}
	
	public final void register()
	{
		if (getMain() == null) return;

		List<Entity> all = new ArrayList<>(getParts());
		all.add(getMain());

		super.register(uuid);
		for (Entity e : all)
		{
			e.getPersistentDataContainer().set(keyEnemy, PersistentDataType.STRING, uuid.toString());
		}
	}
	
	@Override
	public Location getLocation()
	{
		return director == null ? main.getLocation() : director.getLocation();
	}
	
	public void dropItems(GamePlayer killer)
	{
		if (killer == null) return;

        killer.IncrementKill(type.id);
		
		for (var a : killer.abilities) a.ability.onKill(a, this);
        for (var eh : killer.GetEventHandlers()) eh.OnKill(killer, this, killer.area);
		
		for (ItemStack it : type.loot.generate(killer)) killer.giveItem(it);
		
		killer.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8, 1.4);
		killer.lastXpReward = killer.gainXp(LevelUtils.getEnemyBaseXpReward(type.level));
		
		killer.lastCoinReward = killer.gainCoins(this);
		
		killer.rewardTick = 30;
	}


    @Deprecated
	public void damage(int damage)
	{
		DamageInfo info = new DamageInfo();
		info.damage = damage;
		info.type = DamageType.HOLY;
		
		damage(info);
	}

    @Deprecated
	public void damage(int damage, DamageType type)
	{
		DamageInfo info = new DamageInfo();
		info.damage = damage;
		info.type = type;
		
		damage(info);
	}

	@Override
    @Deprecated
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

                target = info.attacker;
                info.attacker.targeted.add(this);
			}

		}
		
		DamageInfo modified = new DamageInfo();
		modified.damage = (int)Math.round(info.damage * type.armour.getModifier(info.type));
		modified.damage = Math.max(0, modified.damage);
		modified.type = info.type;
		modified.attacker = info.attacker;
		modified.defender = this;
		type.onDamaged(this, modified);
		
		health -= (double)(modified.damage)/type.getMaxHealth();
		
		health = Math.max(0, health);

        var damageHologram = EntityUtils.spawnTextHologram(
                main.getLocation().add(RandomUtils.getDouble(-0.7F, 0.7F),
                        RandomUtils.getDouble(0.4F, 0.8F),
                        RandomUtils.getDouble(-0.7F, 0.7F)),
                30
        );
        if (damageHologram != null) {
            damageHologram.text(text(modified.damage));
        }
		
		Entity main = getMain();
		if (main instanceof LivingEntity)
		{
			((LivingEntity)main).playHurtAnimation(0);
		}

		if (getHealth() == 0) destroy();
        else if (lastDamager != null && !info.indirect) {
            lastDamager.playSound(Sound.ENTITY_PLAYER_HURT, 0.7, 0.9);
        }
		
	}

}
