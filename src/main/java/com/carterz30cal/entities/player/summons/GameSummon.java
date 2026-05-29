package com.carterz30cal.entities.player.summons;

import com.carterz30cal.entities.enemies.core.EnemyBuilder;
import com.carterz30cal.entities.enemies.core.EnemyData;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.enemies.directors.EnemyDirector;
import com.carterz30cal.entities.enemies.directors.EnemyDirectorBuilder;
import com.carterz30cal.entities.enemies.directors.behaviour.SummonTargetingBehaviour;
import com.carterz30cal.entities.enemies.representation.EnemyRepresentationBuilder;
import com.carterz30cal.entities.enemies.representation.EnemyRepresentationData;
import com.carterz30cal.entities.health.EntityHealthSystem;
import com.carterz30cal.entities.health.EntityHealthSystemBuilder;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.utils.ParticleUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;

import static net.kyori.adventure.text.Component.text;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class GameSummon extends GameEnemy {

    private GamePlayer owner;
    private int useManaTick = 0;
    private static final EnemyRepresentationBuilder representationBuilder;

    static {
        var data = new EnemyRepresentationData();
        data.type = EntityType.ZOMBIE;
        data.offset = new Vector(0, 0, 0);
        data.scale = 0.9D;
        data.equipment = new HashMap<>();
        data.equipment.put(EquipmentSlot.CHEST, "CHAINMAIL_CHESTPLATE");
        representationBuilder = new EnemyRepresentationBuilder();
        representationBuilder.add(data);
    }

    public GameSummon(EntityHealthSystem healthSystem, EnemyDirector director, EnemyData data) {
        super(representationBuilder.build(director.getLocation()), healthSystem, director, data);
        typeId = uuid.toString();
        representation.register(this);
        register(uuid);
    }

    public GamePlayer getOwner() {
        return this.owner;
    }

    public static GameSummon spawn(GamePlayer owner, Location where, GameEnemy dead) {
        var healthBuilder = new EntityHealthSystemBuilder().setMaxHealth(dead.getHealthSystem().getMaxHealth());
        var director = new EnemyDirectorBuilder().setSpeed(2).setTargetingBehaviour(new SummonTargetingBehaviour(owner));
        var summon = new GameSummon(healthBuilder.build(), director.build(where), dead.getEnemyData());
        summon.owner = owner;
        return summon;
    }

    public static GameSummon spawn(GamePlayer owner, Location where, EnemyBuilder builder) {
        var healthBuilder = builder.getHealthSystemBuilder();
        var director = new EnemyDirectorBuilder().setSpeed(2).setTargetingBehaviour(new SummonTargetingBehaviour(owner));
        var summon = new GameSummon(healthBuilder.build(), director.build(where), builder.getEnemyData());
        summon.owner = owner;
        return summon;
    }

    @Override
    public void tick() {
        super.tick();

        useManaTick++;
        if (useManaTick % 40 == 0 && !owner.useMana((int) enemyData.level)) {
            var packet = new DamagePacket();
            packet.defender = this;
            long damage = 2 + (healthSystem.getMaxHealth() / 8);
            packet.addDamage(
                    com.carterz30cal.entities.health.damage.DamageType.SUFFOCATION,
                    damage);
            damage(packet);
        }
        representation.applyPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        ParticleUtils.spawn(getLocation().add(0, 1, 0), new Particle.DustOptions(Color.GRAY, 0.5F), 0.6);

        if (enemyDirector.getTarget() == owner.player && owner.player.getLocation().distance(getLocation()) < 5) {
            enemyDirector.setTarget(null);
        }
    }

    @Override
    public void dropLoot() {

    }

    @Override
    protected Component getName() {
        return super.getName().append(text(" Soul", NamedTextColor.AQUA));
    }
}
