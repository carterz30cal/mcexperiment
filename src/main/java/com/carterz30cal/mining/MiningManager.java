package com.carterz30cal.mining;

import com.carterz30cal.entities.player.GamePlayer;
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

/**
 * @author carterz30cal
 * @version 3
 * @since 1.0.0
 */
public class MiningManager {
    private static final MiningManager instance;

    static {
        instance = new MiningManager();
        String[] areaFiles = {
                "waterway/mining", "necropolis/mining"
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
    private final Map<Location, Long> currentlyMining = new HashMap<>();
    private final Map<Location, Material> originalBlock = new HashMap<>();
    private final List<BukkitRunnable> runnables = new ArrayList<>();

    public static void onDisable() {
        for (var runnable : instance.runnables) {
            runnable.run();
        }
    }

    /**
     * Attempt to mine a block in the world using our custom mining system.
     * @param player who is trying to mine a <code>Block</code>?
     * @param location where is the <code>Block</code> being mined?
     * @since 1.0.0
     */
    public static void attemptMine(GamePlayer player, Location location) {
        OreType ore = instance.ores.getOrDefault(location.getBlock().getType(), null);
        if (ore == null) {
            return;
        }
        if (!ore.area.equals(player.area)) {
            return;
        }
        if (ore.powerRequired > player.getStat(Stat.BREAKING_POWER)) {
            return;
        }

        long progress = instance.currentlyMining.getOrDefault(location, 0L) + player.getStat(Stat.MINING_SPEED);
        if (progress > ore.hardness) {
            player.playSound(location.getBlock().getBlockData().getSoundGroup().getBreakSound(), 1, 1);

            int blocks = Math.toIntExact(player.getStat(Stat.CLEARING) / 100) + 1;
            int remaining = Math.toIntExact(player.getStat(Stat.CLEARING) - ((blocks - 1) * 100L));
            if (remaining >= RandomUtils.getRandom(1, 100)) blocks++;
            int r = 0;
            List<Location> mining = new ArrayList<>();
            while (blocks > 0 && r < 5) {
                for (int x = -r; x <= r && blocks > 0; x++) {
                    for (int z = -r; z <= r && blocks > 0; z++) {
                        for (int y = 0; y <= r && blocks > 0; y++) {
                            var check = location.clone().add(x, y, z);
                            var checkOre = instance.ores.getOrDefault(check.getBlock().getType(), null);
                            if (checkOre == ore) {
                                mining.add(check);
                                blocks--;
                            }
                        }
                    }
                }
                r++;
            }

            for (var miningBlock : mining) {
                int conversion = RandomUtils.getRandom(1, 1000);
                if (conversion <= ore.conversionChance) {
                    miningBlock.getBlock().setType(ore.convertsInto);
                    instance.originalBlock.put(miningBlock.getBlock().getLocation(), ore.blockType);
                    BukkitRunnable runnable =
                            new BukkitRunnable() {

                                @Override
                                public void run() {
                                    miningBlock.getBlock().setType(ore.blockType);
                                }
                            };
                    instance.runnables.add(runnable);
                }
                else {
                    miningBlock.getBlock().setType(ore.minesInto);
                    BukkitRunnable runnable =
                            new BukkitRunnable() {

                                @Override
                                public void run() {
                                    if (instance.originalBlock.containsKey(miningBlock.getBlock().getLocation())) {
                                        miningBlock.getBlock().setType(instance.originalBlock.get(miningBlock.getBlock().getLocation()));
                                    }
                                    else {
                                        miningBlock.getBlock().setType(ore.blockType);
                                    }
                                }
                            };
                    runnable.runTaskLater(Dungeons.instance, ore.regenTime);
                    instance.runnables.add(runnable);
                }
            }



            long dropMultiplier = player.stats.stat(Stat.MINING_FORTUNE) / 100;
            long chanceForExtra = player.stats.stat(Stat.MINING_FORTUNE) - (dropMultiplier * 100);
            if (RandomUtils.getRandom(1, 100) <= chanceForExtra) {
                dropMultiplier++;
            }

            // grant regular drops
            long dropAmount = RandomUtils.getRandom(ore.lowerBound, ore.upperBound)
                    * (dropMultiplier + 1) * mining.size();
            ItemStack dropItem = ItemFactory.build(ore.item, (int) dropAmount);
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
        int loci = location.getBlockX() + location.getBlockY() * 2 + location.getBlockZ() * 3;
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
        packet.getIntegers().write(0, loci);
        packet.getBlockPositionModifier().write(0, new BlockPosition(location.toVector()));
        int iprogress = (int) Math.min(Math.round(progress * 10), 9);
        if (progress == -1) {
            iprogress = -1;
        }
        packet.getIntegers().write(1, iprogress);

        protocolManager.sendServerPacket(player.player, packet);
    }


}
