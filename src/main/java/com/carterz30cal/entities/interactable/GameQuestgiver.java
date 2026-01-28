package com.carterz30cal.entities.interactable;

import com.carterz30cal.areas2.quests.Questgivers;
import com.carterz30cal.areas2.quests.Quests;
import com.carterz30cal.areas2.quests.rewards.QuestReward;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.main.Dungeons;
import org.bukkit.scheduler.BukkitRunnable;

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

            new BukkitRunnable() {

                @Override
                public void run() {
                    QuestReward reward = save.sectionSave.GetSection().GetQuestReward();
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
                delay += 30;
            }
            interactingPlayer.questTick = delay + 10;
        }

        super.Interact(interactingPlayer);
    }
}
