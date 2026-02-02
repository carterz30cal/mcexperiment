package com.carterz30cal.areas2.quests.requirements;

import com.carterz30cal.areas2.bosses.waterway.AreaBossWaterwaySeraph;
import com.carterz30cal.entities.player.GamePlayer;

public class SeraphNotActiveRequirement implements QuestRequirement {
    @Override
    public boolean HasMetRequirements(GamePlayer player) {
        return !AreaBossWaterwaySeraph.IsPlayerCurrentlyParticipating(player);
    }
}
