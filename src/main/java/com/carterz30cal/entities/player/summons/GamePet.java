package com.carterz30cal.entities.player.summons;

import com.carterz30cal.entities.enemies.core.EnemyData;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.enemies.directors.EnemyDirector;
import com.carterz30cal.entities.enemies.directors.EnemyDirectorBuilder;
import com.carterz30cal.entities.enemies.directors.behaviour.PetTargetingBehaviour;
import com.carterz30cal.entities.enemies.representation.EnemyRepresentation;
import com.carterz30cal.entities.enemies.representation.EnemyRepresentationBuilder;
import com.carterz30cal.entities.enemies.representation.ItemDisplayRepresentationData;
import com.carterz30cal.entities.health.EntityHealthSystem;
import com.carterz30cal.entities.health.EntityHealthSystemBuilder;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.health.damage.handlers.AggressiveEntity;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static net.kyori.adventure.text.Component.text;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class GamePet extends GameEnemy {
    private static final EntityHealthSystemBuilder hs = new EntityHealthSystemBuilder().setMaxHealth(1000);
    protected final GamePlayer owner;
    private final String petType;

    public GamePet(GamePlayer owner, EnemyRepresentation rep, EnemyDirector director, EntityHealthSystem healthSystem, String petType, EnemyData data) {
        super(rep, healthSystem, director, data);
        this.owner = owner;
        this.petType = petType;
        representation.register(this);
        enemyDirector.register(this);
        register(uuid);
    }

    public static GamePet spawn(GamePlayer owner, Location location, String pet) {
        var director = new EnemyDirectorBuilder().setSummon(true).setEntityType(EntityType.ZOMBIE).setSpeed(1.8).setTargetingBehaviour(new PetTargetingBehaviour(owner));
        var rep = new EnemyRepresentationBuilder();
        var it = new ItemDisplayRepresentationData();
        it.offset = new Vector(0, 0.4, 0);
        it.scale = 1;
        it.equipment = new HashMap<>();
        it.itemInformation = pet + "£1";
        var data = new EnemyData();
        data.level = 0;
        data.mmName = owner.player.getName() + "'s " + ItemFactory.getItem(pet).name;
        data.name = MiniMessage.miniMessage().deserialize(data.mmName);
        rep.add(it);
        return new GamePet(owner, rep.build(location), director.build(location), hs.build(), pet, data);
    }

    @Override
    public boolean isTargetable(AggressiveEntity by) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (owner.activePet == null || !owner.activePet.equals(petType)) {
            remove();
        }
        if (owner.distance(getLocation()) > 20) {
            teleport(owner.getLocation());
        }
    }

    @Override
    public void dropLoot() {

    }

    protected Component getName() {
        return text().append(
                enemyData.name
        ).build();
    }

    @Override
    public void damage(@NotNull DamagePacket damagePacket) {

    }
}
