package com.carterz30cal.areas;

import com.carterz30cal.areas.bosses.necropolis.AreaMinibossNecropolisHydra;
import com.carterz30cal.areas.quests.Questgivers;
import com.carterz30cal.areas.quests.requirements.HydraBossInactiveRequirement;
import com.carterz30cal.areas.quests.requirements.PreviousQuestRequirement;
import com.carterz30cal.areas.quests.requirements.QuestRequirement;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.main.Dungeons;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public enum PlayerTeleport {
    WATERWAY_SPAWN("waterway", new Location(Dungeons.w, 0.5, 65, 0.5)),
    WATERWAY_LEAF_CAVE("leaf", new Location(Dungeons.w, -55, 84, 92, -170, 0), Areas.WATERWAY, new PreviousQuestRequirement(Questgivers.SCARED_SAM_SPOT1)),
    WATERWAY_TOP("andyapple", new Location(Dungeons.w, 23, 125, 97.5, 90, 0), Areas.WATERWAY, new PreviousQuestRequirement(Questgivers.ANDY_APPLE)),
    WATERWAY_PONDS("wwponds", new Location(Dungeons.w, -124, 78, 6), Areas.WATERWAY),
    WATERWAY_SERAPH("seraph", new Location(Dungeons.w, 47, 96, 167.5, -90, 20), Areas.WATERWAY),
    NECROPOLIS_SPAWN("necropolis", new Location(Dungeons.w, 55.5, 69, 334.5, 12, 0)),
    NECROPOLIS_HYDRA("hydra", AreaMinibossNecropolisHydra.ALTAR_LOCATION.clone().add(1.5, 0, 0), Areas.NECROPOLIS, new HydraBossInactiveRequirement()),
    NECROPOLIS_CRYPTS("crypts", new Location(Dungeons.w, -1, 68, 391, 90, 0), Areas.NECROPOLIS);
    private final String commandShorthand;
    private final Location location;
    private final QuestRequirement requirement;
    @Nullable
    private final Areas area;

    /**
     * @param commandShorthand shorthand
     * @param location         world location
     * @since 1.0.0 [1]
     */
    PlayerTeleport(String commandShorthand, Location location) {
        this.commandShorthand = commandShorthand;
        this.location = location;
        this.requirement = null;
        this.area = null;
    }

    /**
     * @param commandShorthand shorthand
     * @param location         world location
     * @param area             the area to check
     * @since 1.0.0 [2]
     */
    PlayerTeleport(String commandShorthand, Location location, @Nullable Areas area) {
        this.commandShorthand = commandShorthand;
        this.location = location;
        this.requirement = null;
        this.area = area;
    }

    /**
     * @param commandShorthand shorthand
     * @param location         world location
     * @param area             the area to check
     * @param requirement      any requirements?
     * @since 1.0.0 [2]
     */
    PlayerTeleport(String commandShorthand, Location location, @Nullable Areas area, @Nullable QuestRequirement requirement) {
        this.commandShorthand = commandShorthand;
        this.location = location;
        this.requirement = requirement;
        this.area = area;
    }

    public static @Nullable PlayerTeleport teleport(String shorthand) {
        for (var t : PlayerTeleport.values()) {
            if (t.shorthand().equals(shorthand)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Get the shorthand to be used in the teleport command
     * @return the shorthand code
     * @since 1.0.0 [1]
     */
    public String shorthand() {
        return commandShorthand;
    }

    /**
     *
     * @return where this point is, in the game world
     * @since 1.0.0 [1]
     */
    public Location location() {
        return location.clone();
    }

    /**
     * Does a <code>GamePlayer</code> meet the requirements to warp to this point?
     *
     * @param player the player we're testing
     * @return <code>true</code>, if the player should be able to warp here, otherwise <code>false</code>.
     * @since 1.0.0 [1]
     */
    public boolean requirements(GamePlayer player) {
        if (requirement == null) {
            return true;
        }
        else {
            return requirement.hasMetRequirements(player);
        }
    }

    /**
     * Should this warp be visible in autocomplete?
     *
     * @param player who wants to teleport... forever?
     * @return whether this warp should be visible or not
     * @since 1.0.0 [2]
     */
    public boolean visible(GamePlayer player) {
        if (area == null || player.area == null) {
            return requirements(player);
        }
        else {
            return requirements(player) && player.area.equals(area);
        }
    }
}
