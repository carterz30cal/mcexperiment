package com.carterz30cal.areas.quests.requirements;

import com.carterz30cal.areas.bosses.waterway.AreaBossWaterwaySeraph;
import com.carterz30cal.entities.player.GamePlayer;

public class SeraphNotActiveRequirement implements QuestRequirement {
    @Override
    public boolean HasMetRequirements(GamePlayer player) {
        return !AreaBossWaterwaySeraph.IsPlayerCurrentlyParticipating(player);
    }
}
