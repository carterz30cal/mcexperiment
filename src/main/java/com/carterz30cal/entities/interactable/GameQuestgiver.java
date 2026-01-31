package com.carterz30cal.entities.interactable;

import com.carterz30cal.areas2.quests.Questgivers;
import com.carterz30cal.areas2.quests.Quests;
import com.carterz30cal.areas2.quests.rewards.QuestReward;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.main.Dungeons;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class GameQuestgiver extends GameEntityInteractable {
    protected Questgivers backing;

    public GameQuestgiver(Questgivers backing) {
        super(backing.getEntityType(), backing.getLocation(), backing.toString(), "GOLDBOLDQuest");
        this.skullProfileId = backing.getSkullProfileId();
        this.backing = backing;
    }

    @Override
    protected boolean HasMetRequirements(GamePlayer interactingPlayer) {
        Quests quest = backing.GetParent();
        if (quest == null) {
            return true;
        }
        else if (quest.HasCompletedQuestgiver(interactingPlayer, backing)) {
            return false;
        }
        else {
            return backing.HasMetRequirements(interactingPlayer);
        }
    }

    @Override
    public void Interact(GamePlayer interactingPlayer) {
        Quests quest = backing.GetParent();
        if (quest == null) {
            return;
        }

        Quests.QuestSave save = interactingPlayer.GetQuestSave(quest);
        if (save.sectionSave == null) {
            quest.FixSave(save, interactingPlayer);
        }
        if (save.sectionSave.GetSection().questgiver != backing) {
            return;
        }

        String title = "WHITE<YELLOW" + backing.toString() + "WHITE> ";
        save.sectionSave.Interact();
        if (save.sectionSave.IsFinished()) {
            int delay = 0;
            for (var l : save.sectionSave.GetSection().GetEndDialogue().GetList()) {
                interactingPlayer.sendMessage(title + l, delay);
                delay += 30;
            }

            List<String> questComplete = new ArrayList<>();
            questComplete.add("AQUA- - - - GOLDBOLDQuest complete! AQUA- - - -");
            QuestReward reward = save.sectionSave.GetSection().GetQuestReward();
            if (reward != null) {
                questComplete.addAll(reward.GetRewardDescription());
            }
            interactingPlayer.playSound(Sound.BLOCK_NOTE_BLOCK_CHIME, 0.4, 0.6, delay + 4);
            interactingPlayer.playSound(Sound.BLOCK_NOTE_BLOCK_CHIME, 0.45, 0.7, delay + 5);
            interactingPlayer.playSound(Sound.BLOCK_NOTE_BLOCK_CHIME, 0.45, 0.75, delay + 6);
            interactingPlayer.playSound(Sound.BLOCK_NOTE_BLOCK_CHIME, 0.6, 0.8, delay + 9);

            interactingPlayer.sendChunkMessage(questComplete, delay + 5);

            new BukkitRunnable() {

                @Override
                public void run() {

                    if (reward != null) {
                        interactingPlayer.lastXpReward = interactingPlayer.gainXp(reward.GetXP());
                        interactingPlayer.rewardTick = 30;
                        reward.GrantOneTimeRewards(interactingPlayer);
                    }
                    quest.MoveSave(interactingPlayer);
                }
            }.runTaskLater(Dungeons.instance, delay + 5);
            interactingPlayer.questTick = delay + 10;
        }
        else {
            int delay = 0;
            for (var l : save.sectionSave.Talk().GetList()) {
                interactingPlayer.sendMessage(title + l, delay);
                interactingPlayer.playSound(Sound.ENTITY_VILLAGER_AMBIENT, 0.3, 0.8, delay);
                delay += 30;
            }
            interactingPlayer.questTick = delay + 10;
        }

        super.Interact(interactingPlayer);
    }
}
