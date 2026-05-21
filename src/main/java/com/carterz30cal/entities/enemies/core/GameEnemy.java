package com.carterz30cal.entities.enemies.core;

import com.carterz30cal.areas.AbstractGameArea;
import com.carterz30cal.entities.AbstractEnemyType;
import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.DamageType;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.damage.StatusEffects;
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
import com.carterz30cal.utils.LevelUtils;
import com.carterz30cal.utils.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.ItemStack;
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

    @Deprecated
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

    public void tick() {
        enemyDirector.tick(this);
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

    @Override
    public DamagePacket getBlankDamagePacket() {
        var packet = new DamagePacket();
        packet.aggressor = this;
        for (var damage : enemyData.damages.entrySet()) {
            packet.addDamage(damage.getKey(), damage.getValue());
        }
        return packet;
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
            var coins = getCoinValue(attacker);
            attacker.coins += coins;
            attacker.lastCoinReward = coins;
            attacker.rewardTick = 30;
        }
    }

	public final boolean hasTag(String tag)
	{
		return type.tags.contains(tag);
	}

    public void kill() {
        destroy();
    }
	
	protected final void deregister()
	{
		deregister(uuid);
	}

    @Deprecated
	public final void deregisterEnemy()
	{
		deregister();
	}

    @Deprecated
	public void doTick()
	{

	}

	public final void register()
	{
        enemyDirector.register(this);
        representation.register(this);

		super.register(uuid);
	}
	
	@Override
	public Location getLocation()
	{
        return enemyDirector.getLocation();
	}
	
    @Deprecated
	public void setLocation(Location l)
	{
		main.teleport(l);
		if (director != null) director.teleport(l);
	}

    @Deprecated
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

    }

    @Deprecated
	public void damage(int damage, DamageType type)
	{
	}

	@Override
    @Deprecated
	public void damage(DamageInfo info) {

    }

}
