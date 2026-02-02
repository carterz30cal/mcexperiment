package com.carterz30cal.areas2.bosses.waterway;

import com.carterz30cal.areas2.PlayerTeleport;
import com.carterz30cal.areas2.bosses.AbstractAreaBoss;
import com.carterz30cal.entities.*;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.Item;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.mining.MiningManager;
import com.carterz30cal.utils.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class AreaBossWaterwaySeraph extends AbstractAreaBoss {
    private final static List<WeightedDrop> drops = new ArrayList<>();
    private final Set<GamePlayer> registeredPlayers;
    private final Location altarLocation = new Location(Dungeons.w, 61, 88, 167);
    private final Box doorBox = new Box(64, 89, 167).Expand(0, 1, 1);
    private static AreaBossWaterwaySeraph instance;
    private final Location seraphSpawnLocation = new Location(Dungeons.w, 86.5, 88, 167.5, 90, 18);

    static {
        drops.add(new WeightedDrop("enchanted_book£1£enchants:ENCHANT_LUCK-1~", 60));
        drops.add(new WeightedDrop("waterway_sac£6", 40));
        drops.add(new WeightedDrop("gold_leaf£96", 40));
        drops.add(new WeightedDrop("seraph_sword£1", 5));
        drops.add(new WeightedDrop("enchanted_book£1£enchants:ENCHANT_CONCENTRATION-1~", 25));
        drops.add(new WeightedDrop("enchanted_book£1£enchants:ENCHANT_SHARPNESS-3~", 20));
        drops.add(new WeightedDrop("waterway_seraph_key£5", 5));
        drops.add(new WeightedDrop("seraphs_eye£3", 5));
        drops.add(new WeightedDrop("eye_of_spider£2", 10));
        drops.add(new WeightedDrop("clear_glass_helmet£1", 5));
        drops.add(new WeightedDrop("seraphs_pyjamas£1", 5));
        drops.add(new WeightedDrop("seraph_ooze", 2));
    }

    public final int BOMB_DAMAGE = 230;
    public final int FLAME_TRAP_TICK_DAMAGE = 50;
    private final Box bounds = new Box(90, 83, 177, 65, 93, 158);

    private ArmorStand hologramAltar;
    private boolean started;
    private boolean dead;
    private int animTick;
    private SeraphFightVariation fightVariation;
    private GameEnemy boss;
    private final Location seraphEyeItemLocation = new Location(Dungeons.w, 81.5, 84, 167.5);
    private final Box trapBox = new Box(82, 84, 175, 74, 84, 159);
    private final List<GameEnemy> importantSummons = new ArrayList<>();
    private int totalSpawns = 0;
    private final List<GameEnemy> summons = new ArrayList<>();
    private int phase = 1;


    public AreaBossWaterwaySeraph() {
        this.registeredPlayers = new HashSet<>();
        instance = this;
        started = false;
        closeDoor();
    }

    public static int GetParticipatingCount() {
        return instance.registeredPlayers.size();
    }

    public static boolean IsPlayerCurrentlyParticipating(GamePlayer player) {
        return instance.active && instance.started && instance.registeredPlayers.contains(player);
    }

    @Override
    public void tick() {
        super.tick();

        if (hologramAltar == null || !hologramAltar.isValid()) {
            if (hologramAltar != null) {
                hologramAltar.remove();
            }
            hologramAltar = EntityUtils.spawnHologram(altarLocation.clone().add(0.5, 1.3, 0.5), -1);
        }
        if (started) {
            hologramAltar.setCustomName(StringUtils.colourString("REDBoss alive!"));
        }
        else {
            hologramAltar.setCustomName(StringUtils.colourString("LIGHT_PURPLESeraph Altar"));
        }

        if (!active) {
            return;
        }

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
            if (registeredPlayers.isEmpty()) {
                endFight();
            }
            else {
                regularFightTick();
            }
        }
    }

    private void regularFightTick() {
        damageNonRegisteredPlayers();
        animTick++;
        if (animTick == 60 && phase == 1) {
            spawnMainBoss();
        }
        else if (animTick > 60) {
            doTraps();
            if (animTick % (100 - (20 * phase)) == 0) {
                spawnVariationMobs();
            }
            summons.removeIf(e -> e.dead);
            importantSummons.removeIf(e -> e.dead);

            if (phase == 1) {
                if (importantSummons.isEmpty()) {
                    phase = 2;
                    boss.SetSpeed(1.5);
                    boss.setImmune(false);
                    sendMessage("DARK_REDNO! WHITETime for me to join the fight...", 1);
                }
            }
            else if (phase == 2) {
                if (boss.dead) {
                    animTick = 0;
                    phase = -1;
                    for (var summon : summons) summon.remove();
                    sendMessage("No...", 10);
                    sendMessage("I can hear the abyss call me..", 40);
                    sendMessage("I'll be back.", 80);
                    for (var p : registeredPlayers) p.sendMessage("DARK_GRAYITALICThe seraph fades to mist..", 100);
                }
            }
            else if (phase == -1) {
                if (animTick == 100) {
                    endFight();
                    int pool = 0;
                    for (var item : drops) pool += item.weight;
                    for (var p : registeredPlayers) {
                        GameFloatingItem.spawn(seraphEyeItemLocation.clone(), ItemFactory.build("seraphs_eye"), p);
                        int pick = RandomUtils.getRandom(1, pool);
                        for (var item : drops) {
                            if (item.weight >= pick) {
                                GameFloatingItem.spawn(seraphEyeItemLocation.clone().subtract(0, 0, 2), ItemFactory.BuildItemFromString(item.data), p);
                                break;
                            }
                            else {
                                pick -= item.weight;
                            }
                        }
                        pick = RandomUtils.getRandom(1, pool);
                        for (var item : drops) {
                            if (item.weight >= pick) {
                                GameFloatingItem.spawn(seraphEyeItemLocation.clone().add(0, 0, 2), ItemFactory.BuildItemFromString(item.data), p);
                                break;
                            }
                            else {
                                pick -= item.weight;
                            }
                        }
                    }
                }
            }
        }
    }

    private void spawnMainBoss() {
        boss = EnemyManager.spawn("waterway_seraph", seraphSpawnLocation);
        boss.setImmune(true);
        boss.SetSpeed(0);

        importantSummons.add(EnemyManager.spawn(fightVariation.spawnMid, new Location(Dungeons.w, 85.5, 84, 163.5, 90, 0)));
        //85.5, 84, 171.5
        importantSummons.add(EnemyManager.spawn(fightVariation.spawnMid, new Location(Dungeons.w, 85.5, 84, 171.5, 90, 0)));
    }

    private void doFlameTrap() {
        if (phase < 2) {
            return;
        }

        if (animTick % 140 == 3) {
            int i = 1;
            while (i-- > 0) {
                final Location flame = trapBox.GetRandomMobLocation().subtract(-0.5, 1, -0.5);
                MiningManager.SetBlock(flame, Material.ORANGE_WOOL);
                new BukkitRunnable() {
                    int tick = 0;

                    @Override
                    public void run() {
                        tick++;
                        if (tick % 5 == 2 && tick < 20) {
                            Dungeons.w.playSound(flame, Sound.BLOCK_NOTE_BLOCK_SNARE, 1.1f, 1.4f);
                        }
                        if (dead) {
                            cancel();
                            MiningManager.UnsetBlock(flame);
                        }
                        else if (tick < 20) {
                            ParticleUtils.spawn(flame.clone().add(0, 1.1, 0), Particle.SMALL_FLAME, 0.4, 5);
                        }
                        else if (tick == 20) {
                            MiningManager.SetBlock(flame, Material.RED_STAINED_GLASS);
                        }
                        else if (tick == 20 * 10) {
                            cancel();
                            MiningManager.UnsetBlock(flame);
                        }
                        else {
                            ParticleUtils.spawn(flame.clone().add(0, 1.1 + ((tick % 8)), 0), Particle.SMALL_FLAME, 0.3 + (0.125 * (tick % 8)), 20 - (tick % 8));

                            List<GameEntity> targets = new ArrayList<>();
                            targets.addAll(registeredPlayers);
                            targets.addAll(summons);
                            targets.addAll(importantSummons);
                            targets.add(boss);
                            for (var p : targets) {
                                Location flat = p.getLocation();
                                flat.setY(flame.getY());
                                if (flat.distance(flame) < 1.1 && tick % 4 == 0) {
                                    DamageInfo info = new DamageInfo();
                                    info.damage = FLAME_TRAP_TICK_DAMAGE;
                                    info.type = DamageType.FIRE;
                                    info.defender = p;
                                    info.attacker = null;
                                    info.indirect = true;
                                    p.damage(info);
                                }
                            }
                        }
                    }
                }.runTaskTimer(Dungeons.instance, 1, 1);
            }
        }
    }

    private void doTraps() {
        doFlameTrap();
        if (animTick % 300 == 25) {
            int i = 2;

            while (i-- > 0) {
                Location bomb = trapBox.GetRandomMobLocation().subtract(-0.5, 1, -0.5);
                for (int x = -2; x < 3; x++)
                    for (int z = -2; z < 3; z++)
                        MiningManager.SetBlock(bomb.clone().add(x, 0, z), Material.ORANGE_WOOL);
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        for (int x = -2; x < 3; x++)
                            for (int z = -2; z < 3; z++)
                                MiningManager.SetBlock(bomb.clone().add(x, 0, z), Material.RED_WOOL);
                    }
                }.runTaskLater(Dungeons.instance, 40);
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        for (var registered : registeredPlayers) {
                            if (registered.getLocation().distance(bomb) < 3.25) {
                                registered.damage(BOMB_DAMAGE);
                            }
                        }
                        List<GameEntity> targets = new ArrayList<>();
                        targets.addAll(summons);
                        targets.addAll(importantSummons);
                        targets.add(boss);
                        for (var summon : targets) {
                            if (summon.getLocation().distance(bomb) < 3.75) {
                                DamageInfo info = new DamageInfo();
                                info.damage = BOMB_DAMAGE * 5;
                                info.type = DamageType.FIRE;
                                info.defender = summon;
                                info.attacker = null;
                                info.indirect = true;
                                summon.damage(info);
                            }
                        }
                        if (RandomUtils.getRandom(0, 8) == 0) {
                            sendMessage("DARK_REDKaboom!", 10);
                        }
                        Dungeons.w.createExplosion(bomb, 3F, false, false, boss.getMain());
                        for (int x = -2; x < 3; x++)
                            for (int z = -2; z < 3; z++) MiningManager.UnsetBlock(bomb.clone().add(x, 0, z));
                    }
                }.runTaskLater(Dungeons.instance, 60);
            }
        }
    }

    @Override
    public void OnTeleport(GamePlayer player, PlayerTeleport teleport) {
        super.OnTeleport(player, teleport);

        registeredPlayers.remove(player);
    }

    @Override
    public void OnRightClick(GamePlayer player, Location clicked) {
        super.OnRightClick(player, clicked);

        Item holding = ItemFactory.getItem(player.getMainItem());
        if (started) {
            return;
        }
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
        else if (boss != null) {
            list.add("RED" + boss.getHealth() + "\u2665");
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

    private void sendMessage(String message, int delay) {
        registeredPlayers.forEach(player -> player.sendMessage("WHITE<BLUEWater SeraphWHITE> " + message, delay));
    }

    private void startFight() {
        closeDoor();
        animTick = 0;
        totalSpawns = 0;
        phase = 1;
        fightVariation = RandomUtils.getChoice(SeraphFightVariation.values());
        sendMessage("Welcome, adventurers..", 20);
        sendMessage(fightVariation.message, 50);
    }

    private void endFight() {
        dead = true;
        openDoor();
        if (boss != null && !boss.dead) {
            boss.remove();
        }
        summons.forEach(GameEnemy::remove);
        summons.clear();
        importantSummons.forEach(GameEnemy::remove);
        importantSummons.clear();
    }

    private void spawnVariationMobs() {
        //if (totalSpawns > 20) return;
        int count;
        if (animTick == 80) {
            count = 4;
        }
        else {
            count = 2;
        }
        for (int i = 0; i < count; i++) {
            if (summons.size() > 10) {
                break;
            }
            summons.add(EnemyManager.spawn("water_seraph_fanatic", trapBox.GetRandomMobLocation()));
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
        FANATICS("Please give a warm welcome to my loyal supporters!", "water_seraph_golem");
        private final String message;
        private final String spawnMid;

        SeraphFightVariation(String message, String spawnMid) {
            this.message = message;
            this.spawnMid = spawnMid;
        }
    }

    private static class WeightedDrop {
        public int weight;
        public String data;

        public WeightedDrop(String data, int weight) {
            this.weight = weight;
            this.data = data;
        }
    }
}
