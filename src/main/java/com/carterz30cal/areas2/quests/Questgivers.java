package com.carterz30cal.areas2.quests;

import com.carterz30cal.areas2.quests.requirements.PreviousQuestRequirement;
import com.carterz30cal.areas2.quests.requirements.QuestRequirement;
import com.carterz30cal.areas2.quests.rewards.ItemQuestReward;
import com.carterz30cal.areas2.quests.rewards.QuestReward;
import com.carterz30cal.areas2.quests.sections.QuestSectionBringItem;
import com.carterz30cal.areas2.quests.sections.QuestSectionBringItemCustomDescription;
import com.carterz30cal.areas2.quests.sections.QuestSectionKill;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.entities.interactable.GameQuestgiver;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.StringDescription;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public enum Questgivers {
    TUTORIAL_TAM_SPOT1(
            "Tutorial Tam", EntityType.IRON_GOLEM, new Location(Dungeons.w, -14.7, 64, 12.7),
            null,
            new QuestSectionKill(
                    new StringDescription("Hi there! I'm Tam!", "Welcome to Dungeons!", "To start off, kill some nearby lunatics!"),
                    new StringDescription("Yay! Well done!", "You'll notice you got your first level!", "Talk to me again if you want to know how to forge a weapon!"),
                    new StringDescription("Come back after killing those Lunatics!"), new QuestReward(10),
                    "LUNATIC", "Lunatics", 5),
            new QuestSectionKill(
                    new StringDescription("Welcome back!",
                            "To forge a Flimsy Sword, you'll want to click on the emerald in your hotbar.",
                            "From there you should see a furnace, this is your forge!",
                            "Within here, there are various categories! Navigate to the weapons category in waterway!",
                            "Forge the Flimsy Sword, then kill some more lunatics.",
                            "Report back when you're done!"
                    ),
                    new StringDescription("Nice work!",
                            "By now you should have realised that collecting Weird Flesh adds to your discoveries!",
                            "These grant you XP, and unique forge recipes.",
                            "Find me deeper in BLUEWaterwayWHITE and I'll teach you how to fight a Titan!"),
                    new StringDescription(
                            "Craft a Flimsy Sword and whack some lunatics!", "You can probably already GOLDupgradeWHITE your Flimsy Sword.",
                            "Kill them dead!"),
                    new QuestReward(10), "LUNATIC", "Lunatics", 25
            )
    ),
    TUTORIAL_TAM_SPOT2(
            "Tutorial Tam", EntityType.IRON_GOLEM, new Location(Dungeons.w, -94.5, 65, 6.5),
            new PreviousQuestRequirement(TUTORIAL_TAM_SPOT1),
            new QuestSectionKill(
                    new StringDescription("Hi!",
                            "The Seraph summons a Titan when enough Lunatics are killed in the area.",
                            "Titans are fierce opponents, so gear up before you face one!"),
                    new StringDescription("You managed it! Nice work!", "Have these extra lootboxes I found!"),
                    new StringDescription("Kill lunatics to summon a Titan nearby!"),
                    new ItemQuestReward(10, "titan_lootbox_1", 3),
                    "TITAN",
                    "LIGHT_PURPLETitan",
                    1
            )
    ),

    TUTORIAL_FISHING(
            "Fisherman Freddy", "fish_barrel", new Location(Dungeons.w, 36.5, 81, -5.5, -35, 0),
            new PreviousQuestRequirement(TUTORIAL_TAM_SPOT1),
            new QuestSectionBringItem(
                    new StringDescription(
                            "Hey! Freddy's the name, fishing's the game!",
                            "I have a spare fishing rod... if you want it?",
                            "Bring me 10 weird flesh if you do."),
                    new StringDescription("Thanks! Here you go!"),
                    new StringDescription("I'd like 10 weird flesh for my fishing rod."),
                    new ItemQuestReward(5, "flimsy_fishing_rod", 1),
                    "weird_flesh", 10
            )
    ),
    CREEPY_JIM_SPOT1(
            "Jim", EntityType.HUSK, new Location(Dungeons.w, -60.8, 76, 22.15),
            new PreviousQuestRequirement(TUTORIAL_TAM_SPOT1),
            new QuestSectionBringItem(
                    new StringDescription(".....", "NEED", "CHILLI", "POWDER"),
                    new StringDescription("THANKS"),
                    new StringDescription("CHILLI POWDER..."),
                    new ItemQuestReward(5, "jims_boots"), "chilli_powder", 1
            ),
            new QuestSectionBringItem(
                    new StringDescription(".....", "MORE", "CHILLI", "POWDER"),
                    new StringDescription("TAKE", "SWORD", "GOODBYE"),
                    new StringDescription("MORE CHILLI"),
                    new ItemQuestReward(5, "jims_sword"), "chilli_powder", 4
            )
    ),

    SCARED_SAM_SPOT1(
            "Sam", "green_slime", new Location(Dungeons.w, -53.5, 84, 96.5, 90, 0),
            null,
            new QuestSectionBringItemCustomDescription(
                    new StringDescription("I hate it here...", "I miss my home..", "Why can't I go back?"),
                    new StringDescription("You're the best!", "I'm Sam, but you can call me Sam!", "Maybe I'll see you around?"),
                    new StringDescription("I always feel better after food.", "I feel like I'm missing something..", "I hate cod.", "Have you heard much about the Seraph?"),
                    new StringDescription("Find something to cheer Sam up."),
                    new QuestReward(25),
                    "sams_apple", 1
            )
    ),

    ANDY_APPLE(
            "Andy Apple", "fish_tank", new Location(Dungeons.w, 24, 125, 99),
            new PreviousQuestRequirement(CREEPY_JIM_SPOT1),
            new QuestSectionKill(
                    new StringDescription("I see you've met Jim..", "He's quite the chatty fella!",
                            "I happen to be a collector of fine apples.", "If you'd like one... get rid of some nuisances for me.",
                            "Have they done anything wrong?", "No, not really."
                    ),
                    new StringDescription("Fine, here's an apple I suppose."),
                    new StringDescription(
                            "Don't come back until you've cleared those loons.",
                            "Get them away from my tree!", "Honestly, I just want a quiet moment! REDGo away!"),
                    new ItemQuestReward(10, "sams_apple"),
                    "LUNATIC_SKY", "Sky Lunatics", 40
            )
    );
    private final String name;
    private final EntityType entityType;
    private final String skullProfileId;
    private final Location location;
    private final List<QuestSection> quests;
    private final QuestRequirement requirement;

    //TODO ADD REWARDS & TALLY UP XP REWARDS

    Questgivers(String name, EntityType entityType, Location location, @Nullable QuestRequirement requirement, QuestSection... quests) {
        this.name = name;
        this.entityType = entityType;
        this.location = location;
        this.skullProfileId = null;
        this.quests = Arrays.asList(quests);
        this.quests.forEach(s -> s.questgiver = this);
        this.requirement = requirement;

        new GameQuestgiver(this);
    }

    Questgivers(String name, String skullProfileId, Location location, @Nullable QuestRequirement requirement, QuestSection... quests) {
        this.name = name;
        this.entityType = EntityType.MANNEQUIN;
        this.location = location;
        this.skullProfileId = skullProfileId;
        this.quests = Arrays.asList(quests);
        this.quests.forEach(s -> s.questgiver = this);
        this.requirement = requirement;

        new GameQuestgiver(this);
    }

    @Override
    public String toString() {
        return name;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Location getLocation() {
        return location;
    }

    public String getSkullProfileId() {
        return skullProfileId;
    }

    public int getQuestCount() {
        return quests.size();
    }

    public QuestSection getQuest(int questIndex) {
        return quests.get(questIndex);
    }

    public void RegisterQuests(Quests quest) {
        quests.forEach(s -> s.parent = quest);
    }

    public boolean HasMetRequirements(GamePlayer player) {
        if (requirement == null) {
            return true;
        }
        else {
            return requirement.HasMetRequirements(player);
        }
    }

    public Quests GetParent() {
        if (quests.isEmpty()) {
            return null;
        }
        else {
            return quests.get(0).parent;
        }
    }
}
