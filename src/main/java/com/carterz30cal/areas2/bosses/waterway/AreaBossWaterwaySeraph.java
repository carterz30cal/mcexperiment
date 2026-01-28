package com.carterz30cal.areas2.bosses.waterway;

import com.carterz30cal.areas2.bosses.AbstractAreaBoss;
import com.carterz30cal.entities.EnemyManager;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.Item;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.mining.MiningManager;
import com.carterz30cal.utils.Box;
import com.carterz30cal.utils.EntityUtils;
import com.carterz30cal.utils.RandomUtils;
import com.carterz30cal.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class AreaBossWaterwaySeraph extends AbstractAreaBoss {
    private final Set<GamePlayer> registeredPlayers;
    private final Location altarLocation = new Location(Dungeons.w, 61, 88, 167);
    private final Box doorBox = new Box(64, 89, 167).Expand(0, 1, 1);
    private final Box bounds = new Box(91, 83, 177, 65, 93, 157);
    private final Location seraphSpawnLocation = new Location(Dungeons.w, 86.5, 88, 167.5, 90, 18);


    private ArmorStand hologramAltar;
    private boolean started;
    private boolean dead;
    private int animTick;
    private SeraphFightVariation fightVariation;
    private GameEnemy boss;
    private List<GameEnemy> summons = new ArrayList<>();
    private int totalSpawns = 0;


    public AreaBossWaterwaySeraph() {
        this.registeredPlayers = new HashSet<>();
        started = false;
        closeDoor();
    }

    @Override
    public void tick() {
        super.tick();

        if (hologramAltar == null || !hologramAltar.isValid()) {
            hologramAltar = EntityUtils.spawnHologram(altarLocation.clone().add(0.5, 1.3, 0.5), -1);
        }
        if (started) {
            hologramAltar.setCustomName(StringUtils.colourString("REDBoss alive!"));
        }
        else {
            hologramAltar.setCustomName(StringUtils.colourString("LIGHT_PURPLESeraph Altar"));
        }

        if (active) {
            if (!started) {
                started = true;
                for (var registered : registeredPlayers) {
                    if (!bounds.IsWithin(registered.getLocation())) {
                        started = false;
                    }
                }
                if (started) {
                    startFight();
                }
            }
            else if (dead) {
                if (bounds.Expand(1).GetPlayersWithin().isEmpty()) {
                    closeDoor();
                    active = false;
                    started = false;
                    dead = false;
                    registeredPlayers.clear();
                }
            }
            else {
                if ((animTick > 60 && boss.dead) || registeredPlayers.isEmpty()) {
                    endFight();
                }
                else {
                    damageNonRegisteredPlayers();
                    animTick++;
                    if (animTick == 60) {
                        boss = EnemyManager.spawn("waterway_seraph", seraphSpawnLocation);
                    }
                    else if (animTick > 60) {
                        if (animTick % 80 == 0) {
                            spawnVariationMobs();
                        }
                        if (animTick % 30 == 15) {
                            for (var ad : summons) {
                                if (ad.dead) {
                                    continue;
                                }
                                boss.setHealth(boss.getHealth() + 250);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void OnRightClick(GamePlayer player, Location clicked) {
        super.OnRightClick(player, clicked);

        Item holding = ItemFactory.getItem(player.getMainItem());
        if (holding != null && holding.id.equals("waterway_seraph_key") && clicked.equals(altarLocation)) {
            if (registeredPlayers.contains(player)) {
                player.sendMessage("REDYou're already signed up!");
            }
            else {
                if (!active) {
                    active = true;
                    openDoor();
                }
                player.sendMessage("GOLDSigned up!");
                player.playSound(Sound.BLOCK_NOTE_BLOCK_CHIME, 0.7, 1);
                player.getMainItem().setAmount(player.getMainItem().getAmount() - 1);
                registeredPlayers.add(player);
            }
        }
    }

    @Override
    public void OnPlayerDeath(GamePlayer player) {
        registeredPlayers.remove(player);
        super.OnPlayerDeath(player);
    }

    @Override
    public String GetName(GamePlayer player) {
        return "BLUEWater Seraph";
    }

    @Override
    public List<String> GetScoreboard(GamePlayer player) {
        var list = super.GetScoreboard(player);
        if (!started) {
            list.add("REDEnter the boss room!");
        }
        else if (dead) {
            list.add("GOLDCollect your loot, then");
            list.add("GOLDleave the boss room!");
        }

        return list;
    }

    @Override
    public boolean IsPlayerInvolved(GamePlayer player) {
        return registeredPlayers.contains(player);
    }


    private void damageNonRegisteredPlayers() {
        for (var player : bounds.GetPlayersWithin()) {
            if (!registeredPlayers.contains(player)) {
                player.damage(player.getHealth() / 5);
            }
        }
    }

    private void startFight() {
        closeDoor();
        animTick = 0;
        totalSpawns = 0;
        fightVariation = RandomUtils.getChoice(SeraphFightVariation.values());
        registeredPlayers.forEach(player -> player.sendMessage("BLUEWater Seraph: WHITEHello adventurers..", 20));
        registeredPlayers.forEach(player -> player.sendMessage("BLUEWater Seraph: WHITE" + fightVariation.message, 30));
    }

    private void endFight() {
        dead = true;
        openDoor();
        if (boss != null && !boss.dead) {
            boss.remove();
        }
        summons.forEach(GameEnemy::remove);
        summons.clear();
    }

    private void spawnVariationMobs() {
        //if (totalSpawns > 20) return;
        int count;
        if (animTick == 80) {
            count = 6;
        }
        else {
            count = 2;
        }
        for (int i = 0; i < count; i++) {
            if (summons.size() > fightVariation.summonCap) {
                break;
            }
            summons.add(EnemyManager.spawn(fightVariation.spawnMid, bounds.GetRandomMobLocation()));
            totalSpawns++;
        }
    }


    private void openDoor() {
        doorBox.GetWithin().forEach(b -> MiningManager.SetBlock(b, Material.AIR));
    }

    private void closeDoor() {
        doorBox.GetWithin().forEach(b -> MiningManager.SetBlock(b, Material.BROWN_STAINED_GLASS));
    }

    private enum SeraphFightVariation {
        FANATICS("Please give a warm welcome to my loyal supporters!", "water_seraph_fanatic", 20);
        private final String message;
        private final String spawnMid;
        private final int summonCap;

        SeraphFightVariation(String message, String spawnMid, int summonCap) {
            this.message = message;
            this.spawnMid = spawnMid;
            this.summonCap = summonCap;
        }
    }
}
