package com.carterz30cal.areas.bosses;

import com.carterz30cal.areas.spawners.AbstractEnemySpawner;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.enemies.core.EnemyBuilder;
import com.carterz30cal.entities.enemies.core.EnemyManager;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.enemies.directors.behaviour.BossRoomTargetingBehaviour;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.RandomUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.megavex.scoreboardlibrary.api.sidebar.component.LineDrawable;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles area bosses, and their sign-up processes, as well as more involved abilities that
 * cannot or should not be attached to an entity directly.<br>
 * Provides methods for dealing with sign-up, teleports and deaths.
 *
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class AbstractAreaBoss extends AbstractEnemySpawner {
    /**
     * Our set of registered players. These should be targeted by effects
     * and also granted loot upon victory. This field is <code>private</code> as derivative classes
     * should not touch this field directly and instead use the appropriate methods to handle the data.
     * @since 1.0.0
     */
    private final Set<GamePlayer> registeredPlayers = new HashSet<>();
    /**
     * What abilities are on cooldown?
     * Ticked down in <code>tick()</code>.
     * Accessed only through appropriate methods
     * @since 1.0.0
     */
    private final Map<String, Integer> cooldowns = new HashMap<>();
    /**
     * What phase of the boss fight are we in?<br>
     * -1 should be <b>inactive</b><br>
     * 0 should be reserved for <b>preparing</b><br>
     * Negative numbers should never be used for active phases
     * of the boss fight.
     * @since 1.0.0
     */
    protected int phase = -1;
    /**
     * Internal audience for sending messages to. Automatically populated using
     * <code>register()</code> and <code>deregister()</code>.
     * @since 1.0.0
     */
    private Audience fightingAudience = Audience.empty();

    /**
     * Our list of owned entities. These should be guaranteed to disappear when the fight has ended.
     * All enemies present in the ownedEnemies array should also be present here.
     *
     * @since 1.0.0
     */
    private final List<GameEntity> ownedEntities = new ArrayList<>();
    /**
     * Our array of weighted drops. Modified using <code>drops()</code>.<br>
     * We can determine what drops to give a player using the <code>drop()</code> method,
     * which can also specify the number of drops to give to the player.
     * @since 1.0.0
     */
    private WeightedDrop[] drops;
    /**
     * A subset of the owned entities list, containing only enemies.
     * These should not persist beyond the lifetime of the fight.
     */
    private final Map<String, GameEnemy> enemies = new HashMap<>();
    /**
     * Boss bar displayed for the <code>fightingAudience</code>. Should only be created
     * after all players have registered.
     *
     * @since 1.0.0
     */
    private BossBar bossBar;

    /**
     * Called after all players who want to participate have signed up, basically
     * actually starts the boss fight. <br>Usually will involve a warp into the boss arena.
     * @since 1.0.0
     */
    public abstract void start();

    /**
     * Should reset the fight to an inactive state, with no players registered and any
     * lingering effects/abilities removed. <br>
     * Does not signify that loot should be dropped, this should
     * be handled by the <code>loot()</code> method.
     * @implNote This should not be called if the fight is not at least in the
     * preparation stage, or that will result in undefined behaviour.
     * phase should be set to <b>-1</b> when using this method.
     * @since 1.0.0
     */
    public abstract void end();

    /**
     * Handles general boss mechanics, that need to be ran on a tick-by-tick basis.
     * This will normally determine phase change, when to end the fight, other
     * fairly important functions.
     * @implNote this method is only called when <code>phase >= 0</code>.
     * should generally call <code>super.tick()</code> as this will handle cooldowns.
     * @since 1.0.0
     */
    public void tick() {
        if (registeredPlayers.isEmpty()) {
            end();
        }
        cooldowns.replaceAll((_, v) -> v - 1);
        cooldowns.values().removeIf(v -> v <= 0);
    }

    /**
     * Register a player's interest in taking part. This may consume an item.
     *
     * @param player who is registering their interest?
     * @throws IllegalArgumentException if this player is already registered
     * @implNote should always be overwritten with initialization logic, as the first player
     * to register should be registering from an inactive state (-1)
     * @since 1.0.0
     */
    public void register(GamePlayer player) {
        if (registeredPlayers.contains(player)) {
            throw new IllegalStateException("Player is already registered for this boss fight!");
        }
        else {
            registeredPlayers.add(player);
            fightingAudience = Audience.audience(fightingAudience, player.player);
        }
    }

    /**
     * Remove a player from the register, say if they die or if they back out
     * from the fight.
     *
     * @param player who are we deregistering from the fight?
     * @throws IllegalArgumentException if the player was not registered to begin with.
     * @since 1.0.0
     */
    public void deregister(GamePlayer player) {
        if (!registeredPlayers.contains(player)) {
            throw new IllegalStateException("Player is not registered for this boss fight!");
        }
        else {
            removeBossBar(player);
            registeredPlayers.remove(player);
            List<Audience> remaining = new ArrayList<>();
            for (GamePlayer p : registeredPlayers) {
                remaining.add(p.player);
            }
            fightingAudience = Audience.audience(remaining);
        }
    }

    /**
     * Get the register of <code>GamePlayer</code>s.
     *
     * @return A <code>Set</code> of <code>GamePlayer</code>s.
     * @since 1.0.0
     */
    public Set<GamePlayer> registered() {
        return registeredPlayers;
    }

    /**
     * Clears the register
     *
     * @since 1.0.0
     */
    public void clearRegistered() {
        registeredPlayers.clear();
    }

    /**
     * @param player who are we asking about?
     * @return whether the player is registered for the fight
     * @since 1.0.0
     */
    public boolean isRegistered(GamePlayer player) {
        return registeredPlayers.contains(player);
    }

    /**
     * Hook that allows behaviour to happen when a player leaves the fight.
     * General behaviour will remove the player from the register and
     * stop them receiving loot.
     *
     * @param player which player has left the fight?
     * @param reason why did they leave the fight?
     * @implNote must guarantee that the player will not be targeted
     * by effects that occur within the boss arena.<br>
     * should remove players from receiving the <code>loot()</code> benefits.
     * @since 1.0.0
     */
    public void onLeftFight(@NotNull GamePlayer player, @NotNull LeftFightReason reason) {

    }

    /**
     * allows for custom scoreboard elements
     *
     * @param player what player is this scoreboard for?
     * @param board  where we want to draw to.
     * @since 1.0.0
     */
    public void scoreboard(@NotNull GamePlayer player, @NotNull LineDrawable board) {

    }

    /**
     * @return the phase of the fight that we're currently in.
     * @since 1.0.0
     */
    public int phase() {
        return phase;
    }

    /**
     * Check if an ability is on cooldown.
     *
     * @param ability what are we checking?
     * @return <code>true</code> if the ability is on cooldown, <code>false</code> otherwise.
     * @since 1.0.0
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isOnCooldown(@NotNull String ability) {
        return cooldown(ability) > 0;
    }

    /**
     * Put this ability on cooldown, for <code>cooldown</code> ticks.
     *
     * @param ability  string name of the ability
     * @param cooldown how long, in ticks, should this ability be on cooldown?
     * @since 1.0.0
     */
    public void cooldown(@NotNull String ability, @Range(from = 0, to = Integer.MAX_VALUE) int cooldown) {
        cooldowns.put(ability, cooldown);
    }

    /**
     * Check the ticks remaining of any cooldown on this boss
     *
     * @param ability string name of the ability we're checking
     * @return the ticks left on this cooldown
     * @since 1.0.0
     */
    public @Range(from = 0, to = Integer.MAX_VALUE) int cooldown(@NotNull String ability) {
        var tick = cooldowns.getOrDefault(ability, 0);
        tick = Math.max(tick, 0);
        return tick;
    }

    /**
     * Sets the drops that this boss can give to each player upon victory.
     * Runs through the array and throws an exception if any drops are invalid.
     *
     * @param drops our array of new weighted drops.
     * @throws IllegalArgumentException if any <code>weight < 1</code>.
     * @since 1.0.0
     */
    public void drops(@NotNull WeightedDrop... drops) {
        for (var drop : drops) {
            if (drop.weight < 1) {
                throw new IllegalArgumentException("Weight must be at least 1 for: " + drop.data);
            }
        }
        this.drops = drops;
    }

    /**
     * Gets a list of <code>ItemStack</code>s for a given <code>GamePlayer</code>
     *
     * @param subject  who is this for? may adjust weights depending on Luck stat
     * @param quantity how many <code>ItemStack</code>s do we need?
     * @return a list of <code>ItemStack</code>s, which can be given to the <code>subject</code>
     * @since 1.0.0
     */
    public @NotNull List<ItemStack> drops(@Nullable GamePlayer subject, @Range(from = 0, to = Integer.MAX_VALUE) int quantity) {
        List<ItemStack> drops = new ArrayList<>();
        int total = 0;
        for (var drop : this.drops) total += drop.weight;
        while (quantity > 0) {
            int pick = RandomUtils.getRandom(1, total);
            for (var drop : this.drops) {
                if (drop.weight >= pick) {
                    drops.add(ItemFactory.buildItemFromString(drop.data, subject));
                    break;
                }
                else {
                    pick -= drop.weight;
                }
            }
            quantity--;
        }
        return drops;
    }

    /**
     * Spawns a <code>GameEnemy</code> using an <code>EnemyBuilder</code> object.
     *
     * @param builder  enemy definition to use to spawn with.
     * @param position where do we want to spawn this enemy?
     * @return the entity object of this new spawned enemy.
     * @see EnemyBuilder
     * @see GameEnemy
     * @since 1.0.0
     */
    public GameEnemy spawn(
            @NotNull EnemyBuilder builder,
            @NotNull Location position) {
        var entity = builder.build(position);
        entity.register();
        ownedEntities.add(entity);
        enemies.put(entity.getUUID().toString(), entity);
        return entity;
    }

    /**
     * Spawns a <code>GameEnemy</code> using an <code>EnemyBuilder</code> object.
     *
     * @param builder  enemy definition to use to spawn with.
     * @param position where do we want to spawn this enemy?
     * @return the entity object of this new spawned enemy.
     * @see EnemyBuilder
     * @see GameEnemy
     * @since 1.0.0
     */
    public GameEnemy spawn(
            @NotNull String builder,
            @NotNull Location position) {
        var entity = EnemyManager.spawn(builder, position);
        ownedEntities.add(entity);
        enemies.put(entity.getUUID().toString(), entity);
        return entity;
    }

    /**
     * Spawns a <code>GameEnemy</code> using an <code>EnemyBuilder</code> object.
     *
     * @param id       the enemy identifier
     * @param builder  enemy definition to use to spawn with.
     * @param position where do we want to spawn this enemy?
     * @return the entity object of this new spawned enemy.
     * @see EnemyBuilder
     * @see GameEnemy
     * @since 1.0.0
     */
    public GameEnemy spawn(
            @NotNull String id,
            @NotNull EnemyBuilder builder,
            @NotNull Location position) {
        var entity = builder.build(position);
        entity.register();
        ownedEntities.add(entity);
        enemies.put(id, entity);
        return entity;
    }

    /**
     * Clones the builder, adjusts the targeting behaviour and returns the copy
     * of the builder. Typically used to change the targeting behaviour to something
     * like <code>BossRoomTargetingBehaviour</code>, which has a wider scope than
     * <code>SimpleTargetingBehaviour</code> does.
     *
     * @param existing the old builder
     * @return the copied builder
     */
    public EnemyBuilder adjustBuilderTargetingBehaviour(EnemyBuilder existing) {
        var cloned = new EnemyBuilder(existing);
        cloned.getDirectorBuilder().setTargetingBehaviour(new BossRoomTargetingBehaviour(this));
        return cloned;
    }

    /**
     * Gets an enemy using an id.
     *
     * @param id enemy identifier
     * @return <code>GameEnemy</code>, if it exists.
     * @since 1.0.0
     */
    public @Nullable GameEnemy get(
            @NotNull String id) {
        return enemies.getOrDefault(id, null);
    }

    /**
     * Clears the enemies from the boss fight.
     *
     * @implNote Must <code>remove()</code> every en
     * @since 1.0.0
     */
    public void removeEnemies() {
        for (var enemy : enemies.values()) {
            enemy.remove();
        }
        enemies.clear();
    }

    /**
     * Returns a list of enemies registered with this boss.
     *
     * @return the list of registered enemies
     * @since 1.0.0
     */
    public List<GameEnemy> getEnemies() {
        return enemies.values().stream().toList();
    }

    /**
     * @return the number of enemies active, including presumably the main boss.
     * @since 1.0.0
     */
    public int getAliveEnemyCount() {
        AtomicInteger count = new AtomicInteger();
        enemies.values().forEach((e) -> {
            if (!e.dead) {
                count.getAndIncrement();
            }
        });
        return count.get();
    }

    /**
     * Create and show a boss bar to the current audience for this boss fight.
     *
     * @param name    message on the boss bar, in MiniMessage format
     * @param colour  colour of the boss bar
     * @param overlay notches or no notches, basically
     * @see MiniMessage
     * @since 1.0.0
     */
    public void createBar(@NotNull String name, @NotNull BossBar.Color colour, @NotNull BossBar.Overlay overlay) {
        bossBar = BossBar.bossBar(MiniMessage.miniMessage().deserialize(name), 1, colour, overlay);
        fightingAudience.showBossBar(bossBar);
    }

    /**
     * Adjusts the boss bar. Silently fails if the bar is null.
     *
     * @param name     message on the boss bar, in MiniMessage format
     * @param progress how full is the bar (0-1)?
     * @since 1.0.0
     */
    public void bossBar(@NotNull String name, double progress) {
        if (bossBar == null) {
            return;
        }
        bossBar.name(MiniMessage.miniMessage().deserialize(name));
        bossBar.progress((float) progress);
    }

    /**
     * Adjusts the boss bar. Silently fails if the bar is null.
     *
     * @param progress how full is the bar (0-1)?
     * @since 1.0.0
     */
    public void bossBar(double progress) {
        if (bossBar == null) {
            return;
        }
        bossBar.progress((float) progress);
    }

    /**
     * Removes the boss bar for all players on the register
     *
     * @since 1.0.0
     */
    public void removeBossBar() {
        fightingAudience.hideBossBar(bossBar);
        bossBar = null;
    }

    /**
     * Removes the boss bar for a specific player
     *
     * @param player who are we removing the boss bar from?
     * @since 1.0.0
     */
    public void removeBossBar(GamePlayer player) {
        bossBar.removeViewer(player.player);
    }

    /**
     * Sends a message to every player on the register.
     *
     * @param message a string message in MiniMessage format
     * @param delay   how long, in ticks, before the message is sent.
     * @see MiniMessage
     * @see Audience
     * @since 1.0.0
     */
    public void message(@NotNull String message, int delay) {
        var component = MiniMessage.miniMessage().deserialize(message);
        if (delay == 0) {
            fightingAudience.sendMessage(component);
        }
        else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    fightingAudience.sendMessage(component);
                }
            }.runTaskLater(Dungeons.instance, delay);
        }
    }

    /**
     * Sends a message to every player on the register instantly.
     * @param message a string message in MiniMessage format
     * @see MiniMessage
     * @see Audience
     * @since 1.0.0
     * @implNote overloads <code>message(message, delay)</code> with params <code>(message, 0)</code>
     */
    public void message(@NotNull String message) {
        message(message, 0);
    }

    /**
     * Indicates why the player has left the fight.
     * Generally won't affect the hook much, it's just in case.
     * The main one to check here will probably be <code>LOGOUT</code>, as its slightly
     * more complex due to the fact that the <code>GamePlayer</code> will be saved/saving.
     *
     * @author carterz30cal
     * @version 1
     * @since 1.0.0
     */
    public enum LeftFightReason {
        DEATH,
        TELEPORTED,
        WALKED_OUT,
        LOGOUT
    }
}
