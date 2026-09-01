package com.carterz30cal.areas.quests;

import com.carterz30cal.areas.quests.requirements.PreviousQuestRequirement;
import com.carterz30cal.areas.quests.requirements.QuestRequirement;
import com.carterz30cal.areas.quests.rewards.ItemQuestReward;
import com.carterz30cal.areas.quests.rewards.QuestReward;
import com.carterz30cal.areas.quests.sections.QuestSectionBringItem;
import com.carterz30cal.areas.quests.sections.QuestSectionBringItemCustomDescription;
import com.carterz30cal.areas.quests.sections.QuestSectionFinishOtherSection;
import com.carterz30cal.areas.quests.sections.QuestSectionKill;
import com.carterz30cal.entities.enemies.representation.EnemyRepresentationBuilder;
import com.carterz30cal.entities.enemies.representation.EnemyRepresentationData;
import com.carterz30cal.entities.interactable.GameQuestgiver;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.StringDescription;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

/**
 * @author carterz30cal
 * @version 4
 * @since 1.0.0
 */
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
                            "Find me deeper within <blue>Waterway</blue> and I'll teach you how to fight a Titan!"),
                    new StringDescription(
                            "Craft a Flimsy Sword and whack some lunatics!", "You can probably already <gold>upgrade</gold> your Flimsy Sword.",
                            "Kill them dead!"),
                    new QuestReward(10), "LUNATIC", "Lunatics", 25
            )
    ),
    TUTORIAL_TAM_SPOT2(
            "Tutorial Tam", EntityType.IRON_GOLEM, new Location(Dungeons.w, -82.9, 65, -14.3, -36, 2),
            new PreviousQuestRequirement(TUTORIAL_TAM_SPOT1),
            new QuestSectionKill(
                    new StringDescription("Hi!",
                            "The Seraph summons a Titan when enough Lunatics are killed in the area.",
                            "Titans are fierce opponents, so gear up before you face one!"),
                    new StringDescription("You managed it! Nice work!", "Have these extra lootboxes I found!"),
                    new StringDescription("Kill lunatics to summon a Titan nearby!"),
                    new ItemQuestReward(10, "titan_lootbox_1", 3),
                    "TITAN",
                    "<light_purple>Titan",
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
            ),
            new QuestSectionBringItem(
                    new StringDescription("Say, I'm pretty hungry,,", "You wouldn't happen to have any cod?", "Please?"),
                    new StringDescription("You're too kind.", "Take some Fishing Berries for your troubles."),
                    new StringDescription("Please, bring me some waterway cod."),
                    new ItemQuestReward(5, "fishing_berries", 2),
                    "waterway_cod", 50
            )
    ),

    CREEPY_JIM_SPOT1(
            "Jim", EntityType.HUSK, new Location(Dungeons.w, -60.8, 76, 22.15),
            new PreviousQuestRequirement(TUTORIAL_TAM_SPOT1),
            new QuestSectionBringItem(
                    new StringDescription(".....", "NEED", "CHILLI", "POWDER", "WARRIORS HAVE. TAKE FROM THEM."),
                    new StringDescription("THANKS"),
                    new StringDescription("CHILLI POWDER...", "WARRIORS HAVE POWDER. WANT POWDER."),
                    new ItemQuestReward(5, "jims_boots"), "chilli_powder", 1
            ),
            new QuestSectionBringItem(
                    new StringDescription(".....", "MORE", "CHILLI", "POWDER"),
                    new StringDescription("TAKE", "SWORD", "AND, NOTE.", "GOODBYE"),
                    new StringDescription("MORE CHILLI"),
                    new ItemQuestReward(5, "jims_sword", "waterway_note_1"), "chilli_powder", 4
            )
    ),

    FISHERMAN_FREDDY_2(
            "Fisherman Freddy", "fish_barrel", new Location(Dungeons.w, -107.5, 78, 22.5, 127, 5),
            new PreviousQuestRequirement(CREEPY_JIM_SPOT1),
            new QuestSectionKill(
                    new StringDescription("Hey again!", "Have you done any more fishing recently?", "Have you found any uncommon mobs yet?",
                            "Rarer fish can be made more common by increasing your fishing power!", "Anyways, can you get rid of some sea slimes?",
                            "Their bodies can be turned into a pretty decent industrial lubricant and I've ran out."),
                    new StringDescription("Thank you!", "Please take this enchanted book, which can be made from green slime!", "Also, take this note that I found lying around a bit further up!"),
                    new StringDescription("Can you kill some sea slimes for me?", "They won't spawn from common bobbers.", "Sea slimes won't spawn from common bobbers."),
                    new ItemQuestReward(5, "enchanted_book£1£enchants:ENCHANT_SLIMED-1", "waterway_note_2"),
                    "SEA_SLIME",
                    "Sea Slimes",
                    5
            )
    ),

    SCARED_SAM_SPOT1(
            "Sam", "green_slime", new Location(Dungeons.w, -53.5, 84, 96.5, 90, 0),
            null,
            new QuestSectionBringItemCustomDescription(
                    new StringDescription("I hate it here...", "I miss my home..", "Why can't I go back?"),
                    new StringDescription("You're the best!", "I'm Sam, but you can call me Sam!", "Maybe I'll see you around?"),
                    new StringDescription("I always feel better after food.", "I feel like I'm missing something..", "I hate cod.", "Have you heard much about the Seraph?"),
                    new StringDescription("<white>Find something to cheer Sam up.</white>"),
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
                            "Get them away from my tree!", "Honestly, I just want a quiet moment! <red><em>Go away!</em></red>"),
                    new ItemQuestReward(10, "sams_apple"),
                    "LUNATIC_SKY", "Sky Lunatics", 20
            )
    ),

    MORGK(
            "Morgk", EntityType.BOGGED, new Location(Dungeons.w, -87.5, 67, 24.5, -130, 4),
            new PreviousQuestRequirement(CREEPY_JIM_SPOT1),
            new QuestSectionBringItem(
                    new StringDescription("I'd be careful going up the mountain!", "It houses the Seraph's temple!",
                            "I've heard at the top there are some rather fiesty lunatics", "They might have Waterway Sacs?",
                            "If you find any, I'd love to take one off your hands"),
                    new StringDescription("Thanks!", "I hear these make great weapon upgrades!", "I hope you didn't need to upgrade your weapon.", "Take this powerful enchantment with you."),
                    new StringDescription("I'd really like a Waterway Sac, if you happen across any.", "Have you seen Sam lately?", "Have you seen Jim around?"),
                    new ItemQuestReward(10, "enchanted_book£1£enchants:ENCHANT_CONCENTRATION-2", 1),
                    "waterway_sac", 1
            ),
            new QuestSectionFinishOtherSection(
                    new PreviousQuestRequirement(SCARED_SAM_SPOT1),
                    new StringDescription("Have you met Sam?", "They're a good friend, and I think they're homesick.", "Could you help them out?"),
                    new StringDescription("Can you help Sam out?", "Sam really likes apples!"),
                    new StringDescription("You're the best.", "I found this note when I went and spoke to Sam!"),
                    new ItemQuestReward(5, "waterway_note_3")
            )
    ),

    WATERWAY_CHARLOTTE_1(
            "Charlotte", EntityType.CAVE_SPIDER, new Location(Dungeons.w, -40.5, 64, -24.5, 9, 4),
            new PreviousQuestRequirement(TUTORIAL_TAM_SPOT2),
            new QuestSectionKill(
                    new StringDescription(
                            "I heard that in the cave behind me, you can find <red>Lava Lunatics</red>.",
                            "They sometimes drop Waterway Sacs!", "If you kill a few, maybe you'll get one?"
                    ),
                    new StringDescription(
                            "Did you get one?", "Here's one I found earlier!"
                    ),
                    new StringDescription(
                            "Head into that cave below!", "You can warp out of the cave using /warp"
                    ),
                    new ItemQuestReward(10, "waterway_sac"),
                    "LUNATIC_LAVA", "Lava Lunatics", 20
            )
    ),

    WATERWAY_MAIN(
            "Necropolis Knight Jaylo", EntityType.MANNEQUIN, new Location(Dungeons.w, -87.8, 68, 37.7, -158, 0),
            null,
            new QuestSectionFinishOtherSection(
                    new PreviousQuestRequirement(CREEPY_JIM_SPOT1),
                    new StringDescription("I'm looking for my sister-in-arms.",
                            "She came to Waterway a little while ago, and I haven't been able to meet with her.",
                            "If you see her, or find anyone mentioning Katy, please let me know."),
                    new StringDescription("Please help me find her!"),
                    new StringDescription("You found a note?", "Oh, so she's gone off investigating?", "Well, at least we know she's around here somewhere.", "Take these attuners, as payment."),
                    new ItemQuestReward(20, "weak_silk_mass£3")
            ),
            new QuestSectionBringItem(
                    new StringDescription("If I'm going to venture upwards, towards the temple, I'm going to need to enchant my gear", "Can you help me get some catalysts?"),
                    new StringDescription("Thank you very much!", "These are just what I need!", "I'm putting Sharpness 5 on my Piranha Dagger!", "If you find any more information, I'll be further up the mountain."),
                    new StringDescription("I think 2 combination catalyst shards will do it?"),
                    new QuestReward(20),
                    "combination_catalyst_shard",
                    2
            )
    ),

    WATERWAY_MAIN_2(
            "Necropolis Knight Jaylo", EntityType.MANNEQUIN, new Location(Dungeons.w, -15.78, 96, 140.7, 145, 0),
            new PreviousQuestRequirement(WATERWAY_MAIN),
            new QuestSectionFinishOtherSection(
                    new PreviousQuestRequirement(FISHERMAN_FREDDY_2),
                    new StringDescription("I'm definitely getting worried.", "I've searched everywhere except inside the temple.", "It looks dangerous in there!"),
                    new StringDescription("I'm wondering if there are any more notes lying around?", "Surely Katy would have documented her findings...", "Why is the temple so scary to me?"),
                    new StringDescription("Sea slimes?", "Whatever could the Seraph want with those...",
                            "I don't think that's enough information to conclude anything.",
                            "Keep searching, I'm going to poke inside the temple."),
                    new ItemQuestReward(10, "enchanted_book£1£enchants:ENCHANT_SHARPNESS-4", "enchanted_book£1£enchants:ENCHANT_HEALTHY-3")
            ),
            new QuestSectionFinishOtherSection(
                    new PreviousQuestRequirement(MORGK),
                    new StringDescription("I've had a little poke around. I don't like what I saw...", "The chance that Katy was captured only grows and grows."),
                    new StringDescription("I really need more information on what is going on here.", "Have you spoke to Morgk recently?"),
                    new StringDescription("Okay, that does it.", "I need to explore the temple properly."),
                    new QuestReward(10)
            )
    ),

    NECROPOLIS_YONNA(
            "Yonna", EntityType.BREEZE, new Location(Dungeons.w, 39.3, 69.06, 383.4, 123, 0),
            new PreviousQuestRequirement(CREEPY_JIM_SPOT1),
            new QuestSectionBringItem(
                    new StringDescription("Hello, I'm Yonna!", "It's very nice to meet you.",
                            "Say, you wouldn't happen to have any sand lying about?", "12 should do it!"),
                    new StringDescription("This is very helpful, thank you!", "Now I can begin my brewing career!",
                            "Hey, while you're here why don't you take this note I found on the floor by this brewing stand?"),
                    new StringDescription("I could really do with 12 sand."),
                    new ItemQuestReward(15, "necropolis_note_1", 1),
                    "sand",
                    12
            )
    );
    private final String name;
    private final Location location;
    private final List<QuestSection> quests;
    private final QuestRequirement requirement;
    private final EnemyRepresentationBuilder representationBuilder;

    Questgivers(String name, EntityType entityType, Location location, QuestRequirement requirement, QuestSection... quests) {
        this.name = name;
        this.location = location;
        this.quests = Arrays.asList(quests);
        this.quests.forEach(s -> s.questgiver = this);
        this.requirement = requirement;
        this.representationBuilder = new EnemyRepresentationBuilder();
        var data = new EnemyRepresentationData();
        data.offset = new Vector();
        data.type = entityType;
        data.scale = 1;
        representationBuilder.add(data);

        new GameQuestgiver(this);
    }

    Questgivers(String name, String skullProfileId, Location location, QuestRequirement requirement, QuestSection... quests) {
        this.name = name;
        this.location = location;
        this.quests = Arrays.asList(quests);
        this.quests.forEach(s -> s.questgiver = this);
        this.requirement = requirement;
        this.representationBuilder = new EnemyRepresentationBuilder();
        var data = new EnemyRepresentationData();
        data.offset = new Vector();
        data.type = EntityType.MANNEQUIN;
        data.scale = 1;
        representationBuilder.add(data);

        new GameQuestgiver(this);
    }

    @Override
    public String toString() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public int getQuestCount() {
        return quests.size();
    }

    public QuestRequirement getRequirement() {
        return requirement;
    }

    public EnemyRepresentationBuilder getRepresentationBuilder() {
        return representationBuilder;
    }

    public QuestSection getQuest(int questIndex) {
        if (quests.isEmpty()) {
            return null;
        }
        return quests.get(questIndex);
    }

    public void registerQuests(Quests quest) {
        quests.forEach(s -> s.parent = quest);
    }

    public Quests getParent() {
        if (quests.isEmpty()) {
            return null;
        }
        else {
            return quests.getFirst().parent;
        }
    }
}
