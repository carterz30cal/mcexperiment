package com.carterz30cal.areas.quests2.extensions;

import com.carterz30cal.areas.quests2.Quest;
import com.carterz30cal.areas.quests2.QuestData;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.RandomUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public abstract class QuestWithDialog extends Quest {
    protected final String voice;
    protected final String prefix;
    protected List<String> beginning;
    protected List<String> chatter;
    protected List<String> ending;

    public QuestWithDialog(QuestData data, GamePlayer owner, ConfigurationSection specifics) {
        super(data, owner, specifics);
        beginning = specifics.getStringList("dialog.beginning");
        chatter = specifics.getStringList("dialog.chatter");
        ending = specifics.getStringList("dialog.ending");
        voice = specifics.getString("dialog.voice", "<red>null");
        prefix = "<white><<gold>" + voice + "</gold>>:</white> ";
    }

    @Override
    public void start() {
        super.start();
        int delay = 0;
        for (var s : beginning) {
            owner.getOwner().sendMessage(prefix + s, delay);
            delay += 50;
        }
        owner.getOwner().questTick = delay + 10;
    }

    @Override
    public void progress() {
        super.progress();
        if (finished()) {
            int delay = 0;
            for (var s : ending) {
                owner.getOwner().sendMessage(prefix + s, delay);
                delay += 50;
            }
            owner.getOwner().questTick = delay + 10;
            final var that = this;
            new BukkitRunnable() {
                public void run() {
                    owner.getOwner().questing.finish(that);
                }
            }.runTaskLater(Dungeons.instance, delay);
        }
        else {
            owner.getOwner().sendMessage(prefix + RandomUtils.getChoice(chatter));
        }
    }
}
