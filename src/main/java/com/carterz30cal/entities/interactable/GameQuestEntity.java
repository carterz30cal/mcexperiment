package com.carterz30cal.entities.interactable;

import com.carterz30cal.areas.quests2.Quest;
import com.carterz30cal.areas.quests2.QuestData;
import com.carterz30cal.entities.enemies.core.EnemyBuilder;
import com.carterz30cal.entities.enemies.representation.EnemyRepresentationBuilder;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.main.Dungeons;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class GameQuestEntity extends GameEntityInteractable {
    protected QuestData data;
    protected String name;

    public GameQuestEntity(EnemyRepresentationBuilder builder, Location location, String name) {
        super(builder, location);
        this.name = name;

        title("<gold>" + this.name);
        subtitle("<gold><b>QUEST");
    }

    public static GameQuestEntity create(QuestData data, ConfigurationSection specifics) {
        var spl = specifics.getString("location", "0,0,0,0,0").split(",");
        var yaw = spl.length < 4 ? 0 : Float.parseFloat(spl[3]);
        var pitch = spl.length < 5 ? 0 : Float.parseFloat(spl[4]);
        var location = new Location(Dungeons.w, Double.parseDouble(spl[0]), Double.parseDouble(spl[1]), Double.parseDouble(spl[2]), yaw, pitch);
        var representation = EnemyBuilder.getBuilder(specifics.getString("representation")).getRepresentationBuilder();
        var entity = new GameQuestEntity(representation, location, specifics.getString("name"));
        entity.data = data;
        entity.focusRadius = specifics.getDouble("focus-radius", 6);
        return entity;
    }

    @Override
    protected boolean isVisible(GamePlayer viewer) {
        return !viewer.questing.finished(data.id) && data.requirement.satisfied(viewer);
    }

    @Override
    public void interact(GamePlayer interactingPlayer) {
        super.interact(interactingPlayer);

        var quest = interactingPlayer.questing.get(data.id);
        if (quest != null) {
            interactingPlayer.questing.select(data.id);
            quest.progress();
        }
        else {
            var starting = Quest.create(data, interactingPlayer);
            interactingPlayer.questing.start(starting);
        }
    }
}
