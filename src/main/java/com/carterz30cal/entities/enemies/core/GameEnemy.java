package com.carterz30cal.entities.enemies.core;

import com.carterz30cal.areas.AbstractGameArea;
import com.carterz30cal.entities.AbstractEnemyType;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.TagHavingEntity;
import com.carterz30cal.entities.damage.StatusEffects;
import com.carterz30cal.entities.enemies.abilities.EnemyAbility;
import com.carterz30cal.entities.enemies.abilities.EnemyAbilityContext;
import com.carterz30cal.entities.enemies.directors.EnemyDirector;
import com.carterz30cal.entities.enemies.representation.EnemyInformationDisplay;
import com.carterz30cal.entities.enemies.representation.EnemyRepresentation;
import com.carterz30cal.entities.health.EntityHealthSystem;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.health.damage.handlers.AggressiveEntity;
import com.carterz30cal.entities.health.damage.handlers.DamageableEntity;
import com.carterz30cal.entities.health.status.StatusEffect;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.abilities2.implementation.AbilityWithKillEffect;
import com.carterz30cal.items.abilities2.implementation.ContextWithAbility;
import com.carterz30cal.items.abilities2.implementation.RegisterableAbility;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.stats.Stat;
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
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.kyori.adventure.text.Component.text;

/**
 * @author carterz30cal
 * @version 4
 * @since 1.0.0
 */
@SuppressWarnings("UnnecessaryUnicodeEscape")
public class GameEnemy extends GameEntity implements AggressiveEntity, DamageableEntity, TagHavingEntity
{
	public static NamespacedKey keyEnemy = new NamespacedKey(Dungeons.instance, "keyEnemy");

    private final BukkitRunnable ticker;
    @Deprecated
	public AbstractEnemyType type;
    @Deprecated
	public Entity main;
    @Deprecated
	public List<Entity> parts = new ArrayList<>();
    @Deprecated
    public Mob director;
    @Deprecated
	public GameEntity target;
    @Deprecated
	public GamePlayer lastDamager;
    @Deprecated
	public StatusEffects statuses;
    @Deprecated
	public StatusEffects resistances;
    @Deprecated
	public Map<String, Object> data = new HashMap<>();
    protected EnemyRepresentation representation;
    protected EntityHealthSystem healthSystem;
    protected EnemyDirector enemyDirector;
    protected EnemyData enemyData;
    protected EnemyInformationDisplay enemyInformationDisplay;
    protected final List<EnemyAbilityContext> abilities = new ArrayList<>();
    @Deprecated
	protected ArmorStand display;
    public AbstractGameArea spawnedArea;
    protected String typeId;

    public GameEnemy(EnemyRepresentation representation, EntityHealthSystem healthSystem, EnemyDirector director, EnemyData data, String typeId) {
        this.representation = representation;
        this.healthSystem = healthSystem;
        this.enemyDirector = director;
        this.enemyData = data;
        this.enemyInformationDisplay = new EnemyInformationDisplay(this);
        this.typeId = typeId;
        this.uuid = UUID.randomUUID();

        this.ticker = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        };
        this.ticker.runTaskTimer(Dungeons.instance, 0, 1);
    }

    public GameEnemy(EnemyRepresentation representation, EntityHealthSystem healthSystem, EnemyDirector director, EnemyData data) {
        this.representation = representation;
        this.healthSystem = healthSystem;
        this.enemyDirector = director;
        this.enemyData = data;
        this.enemyInformationDisplay = new EnemyInformationDisplay(this);
        this.uuid = UUID.randomUUID();

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

    public void setAbilities(List<EnemyAbility> ability) {
        for (var a : ability) {
            var context = new EnemyAbilityContext(a, this);
            abilities.add(context);
            if (a instanceof RegisterableAbility registerableAbility) {
                registerableAbility.register(context);
            }
        }
    }

    @Override
    public void damage(@NotNull DamagePacket damagePacket) {
        if (healthSystem.damage(damagePacket)) {
            if (damagePacket.aggressor instanceof GamePlayer player) {
                player.playSound(enemyData.hurtSound, 0.5, 1);
                enemyDirector.knockback(1, damagePacket.aggressor.getLocation());
            }
            if (healthSystem.isDead()) {
                kill();
            }

            representation.damage();
        }
    }

    @Override
    public void heal(long amount) {
        healthSystem.heal(amount);
    }

    @Override
    public List<? extends ContextWithAbility<? extends GameEntity>> getDefensiveDamageModifiers() {
        return abilities;
    }

    @Override
    public boolean isImmune(StatusEffect effect) {
        return false;
    }

    @Override
    public boolean isAlive() {
        return !dead;
    }

    /**
     *
     * @param by what is attempting to attack us
     * @return false if the victim is currently invulnerable, true otherwise
     */
    @Override
    public boolean isDamageable(AggressiveEntity by) {
        return true;
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
    public boolean isTargetable(AggressiveEntity by) {
        return enemyDirector.isTargetable(by);
    }

    @Override
    public List<? extends ContextWithAbility<? extends GameEntity>> getAggressiveDamageModifiers() {
        return List.of();
    }

    /**
     * This just handles any post-attack events we want the entity to work with.
     * e.g. for projectiles this will destroy the projectile, for players this will
     * trigger attack cooldowns.
     */
    @Override
    public void attack() {
        representation.swing();
    }

    public void tick() {
        if (!enemyDirector.getTargetableEntity().isValid()) {
            remove();
            return;
        }
        enemyDirector.tick(this);
        representation.tick(enemyDirector.getLocation());
        healthSystem.tick();

        int i = 0;
        enemyInformationDisplay.reset();
        enemyInformationDisplay.setLine(0, getName());
        if (enemyData.alwaysDisplayHealth || !healthSystem.isAtMaxHealth()) {
            enemyInformationDisplay.setLine(1, text(healthSystem.getHealth() + "\u2665", NamedTextColor.RED));
            i++;
        }
        for (var status : StatusEffect.values()) {
            var value = healthSystem.getBuildup(status);
            if (value < 1) {
                continue;
            }
            i++;
            enemyInformationDisplay.setLine(i, text().append(
                    text(status.symbol + " " + status.shortName + " ", status.textColour),
                    StringUtils.progressBar(4, healthSystem.getBuildupPercentage(status), status.textColour, NamedTextColor.DARK_GRAY)
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
		dead = true;

        enemyDirector.remove();
        representation.kill();
        enemyInformationDisplay.remove(false);
        var lastAttacker = healthSystem.getLastAttacker();
        if (lastAttacker instanceof GamePlayer player) {
            player.attackTick = 0;
        }

        if (spawnedArea != null && !healthSystem.getPlayerAttackers().isEmpty()) {
            spawnedArea.OnKill(this);
        }
        dropLoot();

        for (var a : abilities) {
            if (a.getAbility() instanceof RegisterableAbility registerable) {
                registerable.unregister(a);
            }
        }

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
        dead = true;

        enemyDirector.remove();
        representation.remove();
        enemyInformationDisplay.remove(true);

        ticker.cancel();
        deregister();
    }

    public void dropLoot() {
        for (var ability : abilities) {
            if (ability.getAbility() instanceof AbilityWithKillEffect kill) {
                kill.killEffect(ability, this);
            }
        }
        for (var attacker : healthSystem.getPlayerAttackers()) {
            var builder = EnemyBuilder.getBuilder(typeId);
            assert builder != null;
            if (!builder.isTemporaryBuilder()) {
                attacker.incrementKill(typeId);
            }
            for (var a : attacker.abilities) {
                if (a.ability instanceof AbilityWithKillEffect killEffect) {
                    killEffect.killEffect(a, this);
                }
            }

            for (var eh : attacker.GetEventHandlers())
                eh.OnKill(attacker, this, attacker.area);
            if (enemyData.lootTable != null) {
                for (ItemStack it : enemyData.lootTable.generate(attacker)) {
                    attacker.giveItem(it);
                }
            }

            attacker.playSound(enemyData.deathSound, 0.6, 1);
            attacker.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1.35);
            var coins = getCoinValue(attacker);
            attacker.coins += coins;
            attacker.lastCoinReward = coins;
            attacker.rewardTick = 30;
        }
    }

    /**
     * @deprecated in favour of tag() from TagHavingEntity interface
     */
    @Deprecated
	public final boolean hasTag(String tag)
	{
        return enemyData.tags.contains(tag);
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

    public EntityHealthSystem getHealthSystem() {
        return healthSystem;
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

    @Override
    public boolean tag(@Nullable String tag) {
        return enemyData.tag(tag);
    }
}
