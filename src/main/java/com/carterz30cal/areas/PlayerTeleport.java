package com.carterz30cal.areas;

import com.carterz30cal.areas.quests.Questgivers;
import com.carterz30cal.areas.quests.requirements.PreviousQuestRequirement;
import com.carterz30cal.areas.quests.requirements.QuestRequirement;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.main.Dungeons;
import org.bukkit.Location;

public enum PlayerTeleport {
    WATERWAY_SPAWN("waterway", new Location(Dungeons.w, 0.5, 65, 0.5)),
    WATERWAY_LEAF_CAVE("leaf", new Location(Dungeons.w, -55, 84, 92, -170, 0), new PreviousQuestRequirement(Questgivers.SCARED_SAM_SPOT1)),
    WATERWAY_TOP("andyapple", new Location(Dungeons.w, 23, 125, 97.5, 90, 0), new PreviousQuestRequirement(Questgivers.ANDY_APPLE)),
    WATERWAY_PONDS("wwponds", new Location(Dungeons.w, -124, 78, 6)),
    WATERWAY_SERAPH("seraph", new Location(Dungeons.w, 47, 96, 167.5, -90, 20)),
    NECROPOLIS_SPAWN("necropolis", new Location(Dungeons.w, 55.5, 69, 334.5, 12, 0));
    private final String commandShorthand;
    private final Location location;
    private final QuestRequirement requirement;

    PlayerTeleport(String commandShorthand, Location location, QuestRequirement requirement) {
        this.commandShorthand = commandShorthand;
        this.location = location;
        this.requirement = requirement;
    }

    PlayerTeleport(String commandShorthand, Location location) {
        this.commandShorthand = commandShorthand;
        this.location = location;
        this.requirement = null;
    }

    public static PlayerTeleport GetTeleport(String shorthand) {
        for (var t : PlayerTeleport.values()) {
            if (t.GetCommandShorthand().equals(shorthand)) {
                return t;
            }
        }
        return null;
    }

    public String GetCommandShorthand() {
        return commandShorthand;
    }

    public Location GetLocation() {
        return location;
    }

    public boolean HasRequirements(GamePlayer player) {
        if (requirement == null) {
            return true;
        }
        else {
            return requirement.HasMetRequirements(player);
        }
    }
}
