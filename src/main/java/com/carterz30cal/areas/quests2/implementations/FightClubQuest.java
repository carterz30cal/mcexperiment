package com.carterz30cal.areas.quests2.implementations;

import com.carterz30cal.areas.quests2.QuestData;
import com.carterz30cal.areas.quests2.extensions.FailableQuest;
import com.carterz30cal.areas.quests2.extensions.QuestWithDialog;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.enemies.core.EnemyManager;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.health.damage.handlers.DamageableEntity;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.abilities.implementation.AbilityWithKillEffect;
import com.carterz30cal.items.abilities.implementation.ContextWithAbility;
import com.carterz30cal.main.Dungeons;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class FightClubQuest extends QuestWithDialog implements FailableQuest, AbilityWithKillEffect {
    protected GameEnemy enemy;
    protected boolean killedByPlayer;

    public FightClubQuest(QuestData data, GamePlayer owner, ConfigurationSection specifics) {
        super(data, owner, specifics);

        var spl = specifics.getString("location", "0,0,0,0,0").split(",");
        var yaw = spl.length < 4 ? 0 : Float.parseFloat(spl[3]);
        var pitch = spl.length < 5 ? 0 : Float.parseFloat(spl[4]);
        var location = new Location(Dungeons.w, Double.parseDouble(spl[0]), Double.parseDouble(spl[1]), Double.parseDouble(spl[2]), yaw, pitch);
        enemy = EnemyManager.spawn(specifics.getString("enemy"), location);
        enemy.persistent = true;
    }

    @Override
    public boolean finished() {
        return killedByPlayer;
    }

    @Override
    public void progress() {
        if (!finished()) {
            owner.getOwner().questing.remove(this);
            fail();
        }
        super.progress();
    }

    @Override
    protected String display() {
        return !finished() ? "Win the fight!" : "Return to " + voice + "!";
    }

    @Override
    public void fail() {
        if (enemy != null) {
            enemy.remove();
        }
    }

    @Override
    public void save(ConfigurationSection section) {

    }

    @Override
    public void load(ConfigurationSection section) {

    }

    @Override
    public void killEffect(ContextWithAbility<? extends GameEntity> context, DamageableEntity killed) {
        if (killed.equals(enemy)) {
            killedByPlayer = true;
        }
    }
}
