package com.carterz30cal.areas.quests2;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.entities.player.interfaces.PlayerSavable;
import com.carterz30cal.items.abilities.implementation.AbilityWithScoreboard;
import com.carterz30cal.items.abilities.implementation.PlayerAbilityContext;
import com.carterz30cal.utils.StringUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public abstract class Quest implements AbilityWithScoreboard, PlayerSavable {
    protected QuestData questData;
    protected PlayerAbilityContext owner;

    public Quest(QuestData data, GamePlayer owner, ConfigurationSection ignoredSpecifics) {
        this.questData = data;
        this.owner = new PlayerAbilityContext(owner, this);
    }

    public static Quest create(QuestData data, GamePlayer owner) {
        try {
            return data.questClass.getConstructor(QuestData.class, GamePlayer.class, ConfigurationSection.class).newInstance(data, owner, data.specifics);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected String name() {
        return questData.name;
    }

    public void start() {

    }

    public void progress() {

    }

    public abstract boolean finished();

    /**
     * Get the contents of this quest's scoreboard details
     *
     * @return a <code>String</code> with what should be displayed on the scoreboard, which will be auto-wrapped to fit.
     * @since 1.0.0 [1]
     */
    protected abstract String display();

    @Override
    public List<String> scoreboard(PlayerAbilityContext context) {
        var list = new ArrayList<String>();
        list.add("<gold><b>QUEST</b></gold>");
        list.add("<gold>" + name());
        list.addAll(StringUtils.wrapText(display(), 20));
        return list;
    }

    /**
     * Get the underlying player context
     *
     * @return the player ability context
     * @since 1.0.0 [1]
     */
    public PlayerAbilityContext context() {
        return owner;
    }

    /**
     * @since 1.0.0 [1]
     */
    public String id() {
        return questData.id;
    }
}
