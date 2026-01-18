package com.carterz30cal.mining;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.utils.FileUtils;
import com.carterz30cal.utils.RandomUtils;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class MiningManager {
    private static final MiningManager instance;

    static {
        instance = new MiningManager();
        String[] areaFiles = {
                "waterway2/waterway_mining"
        };
        for (String file : areaFiles) {
            FileConfiguration c = FileUtils.getData(file);
            assert c != null;
            for (String p : c.getKeys(false)) {

                OreType ore = new OreType(Objects.requireNonNull(c.getConfigurationSection(p)));
                instance.ores.put(ore.blockType, ore);
            }
        }
    }

    private final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    private final Map<Material, OreType> ores = new HashMap<>();
    private final Map<Location, Integer> currentlyMining = new HashMap<>();
    private final Map<Location, Material> originalBlock = new HashMap<>();
    private List<BukkitRunnable> runnables = new ArrayList<>();

    public static void onDisable() {
        for (var runnable : instance.runnables) {
            runnable.run();
        }
    }

    public static void attemptMine(GamePlayer player, Location location) {
        OreType ore = instance.ores.getOrDefault(location.getBlock().getType(), null);
        if (ore == null) {
            return;
        }
        if (ore.powerRequired > player.stats.getStat(Stat.BREAKING_POWER)) {
            return;
        }

        int progress = instance.currentlyMining.getOrDefault(location, 0) + player.stats.getStat(Stat.MINING_SPEED);
        if (progress > ore.hardness) {
            // mine block

            player.playSound(location.getBlock().getBlockData().getSoundGroup().getBreakSound(), 1, 1);

            int conversion = RandomUtils.getRandom(1, 1000);
            if (conversion <= ore.conversionChance) {
                location.getBlock().setType(ore.convertsInto);
                instance.originalBlock.put(location.getBlock().getLocation(), ore.blockType);
                BukkitRunnable runnable =
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                location.getBlock().setType(ore.blockType);
                            }
                        };
                instance.runnables.add(runnable);
            }
            else {
                location.getBlock().setType(ore.minesInto);
                BukkitRunnable runnable =
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                if (instance.originalBlock.containsKey(location.getBlock().getLocation())) {
                                    location.getBlock().setType(instance.originalBlock.get(location.getBlock().getLocation()));
                                    instance.originalBlock.remove(location.getBlock().getLocation());
                                }
                                else {
                                    location.getBlock().setType(ore.blockType);
                                }
                            }
                        };
                runnable.runTaskLater(Dungeons.instance, ore.regenTime);
                instance.runnables.add(runnable);
            }

            int dropMultiplier = player.stats.getStat(Stat.MINING_FORTUNE) / 100;
            int chanceForExtra = player.stats.getStat(Stat.MINING_FORTUNE) - (dropMultiplier * 100);
            if (RandomUtils.getRandom(1, 100) <= chanceForExtra) {
                dropMultiplier++;
            }

            // grant regular drops
            int dropAmount = RandomUtils.getRandom(ore.lowerBound, ore.upperBound) * (dropMultiplier + 1);
            ItemStack dropItem = ItemFactory.build(ore.item, dropAmount);
            player.giveItem(dropItem, true);
            instance.currentlyMining.remove(location);
            instance.sendBlockDamage(player, location, -1);
        }
        else {
            instance.sendBlockDamage(player, location, progress / (double) ore.hardness);
            instance.currentlyMining.put(location, progress);
        }
    }

    private void sendBlockDamage(GamePlayer player, Location location, double progress) {
        int locid = location.getBlockX() + location.getBlockY() * 2 + location.getBlockZ() * 3;
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
        packet.getIntegers().write(0, locid);
        packet.getBlockPositionModifier().write(0, new BlockPosition(location.toVector()));
        int iprogress = (int) Math.min(Math.round(progress * 10), 9);
        if (progress == -1) {
            iprogress = -1;
        }
        packet.getIntegers().write(1, iprogress);

        protocolManager.sendServerPacket(player.player, packet);
    }


}
