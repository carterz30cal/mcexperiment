package com.carterz30cal.areas.quests2.implementations.waterway;

import com.carterz30cal.areas.bosses.waterway.AreaBossWaterwaySeraph;
import com.carterz30cal.areas.quests2.QuestData;
import com.carterz30cal.areas.quests2.implementations.KillTagQuest;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.enemies.core.EnemyBuilder;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.entities.player.summons.GameSummon;
import com.carterz30cal.items.abilities.implementation.AbilityWithTick;
import com.carterz30cal.items.abilities.implementation.ContextWithAbility;
import com.carterz30cal.main.Dungeons;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 * This just adds tam as a summon to the seraph fight.
 *
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class TamSeraphQuest extends KillTagQuest implements AbilityWithTick {
    private final String id;
    private GameSummon tam;
    private boolean retreated = false;

    public TamSeraphQuest(QuestData data, GamePlayer owner, ConfigurationSection specifics) {
        super(data, owner, specifics);

        id = specifics.getString("entity");
    }

    @Override
    protected String display() {
        if (tam == null) {
            return "Fight the Waterway Seraph!";
        }
        else {
            return "Defeat the Waterway Seraph!";
        }
    }

    @Override
    public void tick(ContextWithAbility<? extends GameEntity> context, int tick) {
        if (!(context.getOwner() instanceof GamePlayer player)) {
            return;
        }
        if (!AreaBossWaterwaySeraph.instance.isRegistered(player) || AreaBossWaterwaySeraph.instance.phase() < 0) {
            if (tam != null) {
                tam.remove();
                tam = null;
            }
            return;
        }

        if (tam == null) {
            tam = GameSummon.spawnExact(player, new Location(Dungeons.w, 116, 88, 167.5, -90, 0), EnemyBuilder.getBuilder(id));
            tam.usesMana = false;
        }
        else if (!tam.isAlive() && !retreated) {
            player.sendMessage("<grey>Tam has retreated from the fight!");
            retreated = true;
        }
    }
}
