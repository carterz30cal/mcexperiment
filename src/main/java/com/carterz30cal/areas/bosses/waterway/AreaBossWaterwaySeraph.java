package com.carterz30cal.areas.bosses.waterway;

import com.carterz30cal.areas.bosses.AbstractAreaBoss;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.health.damage.DamageType;
import com.carterz30cal.entities.player.GamePlayer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * Singleton boss class for the final fight of the Waterway area.
 *
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public final class AreaBossWaterwaySeraph extends AbstractAreaBoss {
    private final static String BOSS_MESSAGE_PREFIX = "<dark_grey>[<#7335db>Water Seraph</#7335db>]: </dark_grey>";
    private final static double DAMAGING_WATER_HEIGHT = 66.8D;
    private final static long DAMAGING_WATER_DAMAGE = 40;
    private final static Vector INVERT_Y_VECTOR = new Vector(1, 0, 1);
    private final static Vector BOUNCE_VECTOR = new Vector(0, 2.65, 0);
    public static AreaBossWaterwaySeraph instance = new AreaBossWaterwaySeraph();

    @Override
    public void register(GamePlayer player) {
        if (phase > 0) {
            throw new IllegalStateException("Fight is already in progress, cannot register!");
        }
        super.register(player);
        player.sendMessage("<grey>You have registered for the <#7335db>Water Seraph</#7335db> fight! <em>It will start shortly</em>.", 5);
        if (!isOnCooldown("PREPARED")) {
            phase = 0;
        }
        cooldown("PREPARED", 200);
    }

    @Override
    public void start() {
        phase = 1;
    }

    @Override
    public void end() {
        phase = -1;
    }

    @Override
    public void tick() {
        super.tick();
        switch (phase) {
            case 0 -> preparingPhase();
            case 1 -> phase1();
            case 2 -> phase2();
        }

        // BOUNCY WATER W/ DAMAGE
        for (var player : registered()) {
            var cooldownId = player.getUUID().toString() + "_WATER_BOUNCE";
            if (player.getLocation().getY() <= DAMAGING_WATER_HEIGHT && !isOnCooldown(cooldownId)) {
                var velocity = player.player.getVelocity().multiply(INVERT_Y_VECTOR).add(BOUNCE_VECTOR);
                player.player.setVelocity(velocity);
                cooldown(cooldownId, 10);
                if (phase > 0) {
                    var packet = new DamagePacket(player, null, DAMAGING_WATER_DAMAGE, DamageType.FROST);
                    player.damage(packet);
                }
            }
        }
    }

    @Override
    public void onLeftFight(@NotNull GamePlayer player, @NotNull LeftFightReason reason) {
        super.onLeftFight(player, reason);

        deregister(player);
    }

    /**
     * This is phase <code>0</code>, whilst we're waiting for all players who want to register, to do so.<br>
     * We'll display a <code>TextDisplay</code> entity, with the time until the boss fight starts.
     *
     * @since 1.0.0
     */
    private void preparingPhase() {
        if (!isOnCooldown("PREPARED")) {
            start();
        }
    }

    /**
     * Initial messages, adds cooldown for Seraph to spawn
     */
    private void phase1() {
        message(BOSS_MESSAGE_PREFIX + "Welcome...");
        message(BOSS_MESSAGE_PREFIX + "to my Experiment Lab!", 10);
        cooldown("PHASE_2_TRANSITION", 100);
        phase = 2;
    }

    private void phase2() {
        if (!isOnCooldown("PHASE_2_TRANSITION")) {
            phase = 3;
        }
    }

    /*
    static {
        drops.add(new WeightedDrop("enchanted_book£1£enchants:ENCHANT_LUCK-1~", 60));
        drops.add(new WeightedDrop("waterway_sac£6", 40));
        drops.add(new WeightedDrop("gold_leaf£96", 40));
        drops.add(new WeightedDrop("seraph_sword£1", 5));
        drops.add(new WeightedDrop("enchanted_book£1£enchants:ENCHANT_CONCENTRATION-1~", 25));
        drops.add(new WeightedDrop("enchanted_book£1£enchants:ENCHANT_SHARPNESS-3~", 20));
        drops.add(new WeightedDrop("waterway_seraph_key£5", 5));
        drops.add(new WeightedDrop("seraphs_eye£3", 5));
        drops.add(new WeightedDrop("eye_of_spider£2", 10));
        drops.add(new WeightedDrop("clear_glass_helmet£1", 5));
        drops.add(new WeightedDrop("seraphs_pyjamas£1", 5));
        drops.add(new WeightedDrop("seraph_ooze", 2));
        drops.add(new WeightedDrop("seraphs_guide_to_summoning_spirits", 2));
    }
*/
}
