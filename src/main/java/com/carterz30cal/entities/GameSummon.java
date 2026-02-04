package com.carterz30cal.entities;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.utils.EntityUtils;
import com.carterz30cal.utils.ParticleUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class GameSummon extends GameEnemy {

    private GamePlayer owner;
    private int useManaTick = 0;

    public GameSummon(Location spawn, AbstractEnemyType type) {
        super(spawn, type);
    }

    @Override
    public int getHealth()
    {
        return (int)(health * type.getMaxHealth() * 0.5);
    }

    @Override
    public void setHealth(int value)
    {
        double prog = (double)value / ((double)type.getMaxHealth() * 0.5);

        if (prog > 1) prog = 1;

        if (prog <= 0) kill();

        health = prog;
    }

    public GamePlayer getOwner() {
        return this.owner;
    }

    @Override
    public void doTick()
    {
        useManaTick++;
        if (useManaTick % 40 == 0 && !owner.useMana(type.level)) damage(Math.max(type.getMaxHealth() / 6, 2), DamageType.WITHER);

        if (main instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) main;
            EntityUtils.applyPotionEffect(livingEntity, PotionEffectType.INVISIBILITY, 20, 1, false);

            ParticleUtils.spawn(getLocation().add(0, type.displayHeight / 2, 0), new Particle.DustOptions(Color.GRAY, 0.5F), 0.6);
        }

        tick();
    }

    @Override
    protected GameEntity findTarget() {
        //GameEntity en;
        List<GameEnemy> enemies = EntityUtils.getNearbyEnemies(getLocation(), 10);
        enemies.removeIf((e) -> (e instanceof GameSummon));
        if (!enemies.isEmpty()) return enemies.get(0);
            //else if (owner.getLocation().distance(getLocation()) > 5) return owner;
        else return null;
    }

    @Override
    protected String getName() {
        String name = "BLUE" + type.onName(this) + " Soul";
        if (type.level != 0) name = "WHITE[" + type.level + "] " + name;

        return name;
    }

    @Override
    public void dropItems(GamePlayer killer)
    {

    }

    public static GameSummon SpawnSummonFromEnemy(GamePlayer owner, GameEnemy dead) {
        return SpawnSummonFromEnemy(owner, dead.getLocation(), dead.type);
    }

    public static GameSummon SpawnSummonFromEnemy(GamePlayer owner, Location location, AbstractEnemyType dead) {
        GameSummon summon = new GameSummon(location, dead);
        summon.main = EntityUtils.spawnPart(EntityType.ZOMBIE, location);
        EntityUtils.setArmourPiece((Mob)summon.main, EquipmentSlot.HEAD, new ItemStack(Material.CHAINMAIL_HELMET));
        EntityUtils.setArmourPiece((Mob)summon.main, EquipmentSlot.CHEST, new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        if (dead.level > 15) {
            EntityUtils.setArmourPiece((Mob) summon.main, EquipmentSlot.LEGS, new ItemStack(Material.CHAINMAIL_LEGGINGS));
        }
        if (dead.level > 10) {
            EntityUtils.setArmourPiece((Mob) summon.main, EquipmentSlot.FEET, new ItemStack(Material.CHAINMAIL_BOOTS));
        }
        summon.owner = owner;
        summon.SetSpeed(2);

        summon.register();
        return summon;
    }


}
