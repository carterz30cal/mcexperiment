package com.carterz30cal.areas.quests2.implementations;

import com.carterz30cal.areas.quests2.QuestData;
import com.carterz30cal.areas.quests2.extensions.QuestWithDialog;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.TagHavingEntity;
import com.carterz30cal.entities.health.damage.handlers.DamageableEntity;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.abilities.implementation.AbilityWithKillEffect;
import com.carterz30cal.items.abilities.implementation.ContextWithAbility;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class KillTagQuest extends QuestWithDialog implements AbilityWithKillEffect {
    protected final int count;
    protected final String tag;
    protected final String prettyName;
    protected int kills;

    public KillTagQuest(QuestData data, GamePlayer owner, ConfigurationSection specifics) {
        super(data, owner, specifics);

        count = specifics.getInt("count", 1);
        tag = specifics.getString("tag");
        prettyName = specifics.getString("pretty-name", tag);
    }

    @Override
    public boolean finished() {
        return kills >= count;
    }

    @Override
    protected String display() {
        var colour = kills == 0 ? "red>" : "yellow>";
        if (finished()) {
            return "Return to " + voice + "!";
        }
        else {
            return "Kill <" + colour + kills + "</" + colour + "<grey>/</grey>" + count + " " + prettyName + ".";
        }
    }

    @Override
    public void save(ConfigurationSection section) {
        section.set("kills", kills);
    }

    @Override
    public void load(ConfigurationSection section) {
        kills = section.getInt("kills", 0);
    }

    @Override
    public void killEffect(ContextWithAbility<? extends GameEntity> ignoredContext, DamageableEntity killed) {
        if (killed instanceof TagHavingEntity tagged) {
            if (tagged.tag(tag)) {
                kills++;
            }
        }
    }
}
