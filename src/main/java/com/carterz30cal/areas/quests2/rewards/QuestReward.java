package com.carterz30cal.areas.quests2.rewards;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.abilities.implementation.Ability;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound.Source;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.InvocationTargetException;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class QuestReward implements Ability {
    public static QuestReward blank = new QuestReward();
    protected long xp;
    protected long coins;

    public QuestReward(ConfigurationSection config) {
        xp = config.getLong("xp", 0L);
        coins = config.getLong("coins", 0L);
    }

    private QuestReward() {
        xp = 0;
        coins = 0;
    }

    public static QuestReward create(ConfigurationSection config) {
        try {
            return (QuestReward) Class.forName("com.carterz30cal.areas.quests2.rewards." + config.getString("class")).getConstructor(ConfigurationSection.class).newInstance(config);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void completion(GamePlayer player) {
        if (xp > 0) {
            player.sendMessage("<dark_grey>- - <aqua>+" + xp + "XP");
            player.gainXp(xp);
        }
        if (coins > 0) {
            player.sendMessage("<dark_grey>- - <gold>+" + coins + " coins");
            player.gainCoins(coins);
        }
        player.play(Key.key("block.note_block.chime"), Source.BLOCK, 0.4, 0.6, 4);
        player.play(Key.key("block.note_block.chime"), Source.BLOCK, 0.45, 0.7, 5);
        player.play(Key.key("block.note_block.chime"), Source.BLOCK, 0.45, 0.75, 6);
        player.play(Key.key("block.note_block.chime"), Source.BLOCK, 0.6, 0.8, 9);
    }

    public long xp() {
        return xp;
    }
}
