package com.carterz30cal.mining;

import com.carterz30cal.areas.AbstractArea;
import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.utils.BlockUtils;
import com.carterz30cal.utils.RandomUtils;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class Mineable
{
	public static Map<String, Mineable> mining = new HashMap<>();
	public final Location location;
	
	public int hardness;
	public int maxHardness;
	public String drop;
	public int dropAmount;
	private int id;
	public final boolean multiply;
	
	
	private final Vector[] offsets = {
			new Vector(-1, -1, -1),
			new Vector( 0, -1, -1),
			new Vector(-1,  0, -1),
			new Vector( 0,  0, -1),
			new Vector(-1, -1,  0),
			new Vector( 0, -1,  0),
			new Vector(-1,  0,  0),
			//new Vector( 0,  0,  0),
			new Vector( 1,  0,  0),
			new Vector( 0,  1,  0),
			new Vector( 1,  1,  0),
			new Vector( 0,  0,  1),
			new Vector( 1,  0,  1),
			new Vector( 1,  1,  1),
	};
	
	public Mineable(Location l, Material m, int h, String d, int a, boolean mu)
	{
		location = l;
		hardness = h;
		maxHardness = h;
		drop = d;
		dropAmount = a;
		id = RandomUtils.getRandom(1, 2147000000);
		multiply = mu;
		
		BlockUtils.setBlock(l, m);
		mining.put(getId(location), this);
	}
	
	public void damage(GamePlayer player) {
		hardness -= player.stats.getStat(Stat.MINING_SPEED);
		
		int val = 9 - (int)Math.round(9 * ((double)hardness/maxHardness));
		doBreakAnim(val);
		
		if (hardness <= 0) mine(player);
	}
	
	public void doBreakAnim(int val) {
		try {
			PacketContainer packet = Dungeons.proto.createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
			packet.getIntegers().write(0, id);
			packet.getBlockPositionModifier().write(0, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
			packet.getIntegers().write(1, val);
			
			for (GamePlayer p : PlayerManager.players.values()) Dungeons.proto.sendServerPacket(p.player, packet);
		}
		catch (Exception e) {
			
		}
		
	}

	public void destroy() {
		Dungeons.w.getBlockAt(location).setType(Material.AIR);
	}
	
	
	public void mine(GamePlayer player) {
		mine(player, player.stats.getStat(Stat.CLEARING));
	}
	
	public void mine(GamePlayer player, int clearing) {
		Dungeons.w.getBlockAt(location).setType(Material.AIR);
		mining.remove(getId(location));
		
		int multi = (player.stats.getStat(Stat.PICKING) / 100) + 1;
		multi += (RandomUtils.getRandomEx(0, 100) <= player.stats.getStat(Stat.PICKING) % 100) ? 1 : 0;
		
		int am = multiply ? (dropAmount * multi) : dropAmount;
		
		player.giveItem(ItemFactory.build(drop, am));
		player.playSound(Sound.BLOCK_WOOL_BREAK, 1, 1.2);
		
		doBreakAnim(0);
		
		for (AbstractArea a : AbstractArea.areas.values()) 
		{
			a.onMine(player, location);
		}
		
		if (clearing > 0) {
			
			
			Vector[] shuffled = RandomUtils.shuffle(offsets);
			for (Vector v : shuffled) {
				Location lo = location.clone().add(v);
				
				if (!mining.containsKey(getId(lo))) continue;
				
				Mineable mi = get(lo);
				mi.mine(player, clearing - 1);
				break;
			}
		}
	}
	
	
	public static Mineable create(int x, int y, int z, Material m, int hardness, String drop, int amount) {
		return create(x, y, z, m, hardness, drop, amount, true);
	}
	public static Mineable create(int x, int y, int z, Material m, int hardness, String drop, int amount, boolean multiply) {
		return new Mineable(new Location(Dungeons.w,x,y,z), m, hardness, drop, amount, multiply);
	}
	
	public static Mineable get(Block b) {
		if (b == null) return null;
		String id = getId(b.getLocation());
		
		return mining.getOrDefault(id, null);
	}
	
	public static Mineable get(Location l) {
		String id = getId(l);
		
		return mining.getOrDefault(id, null);
	}
	
	public static String getId(Location l) {
		String s = l.getBlockX() + "/" + l.getBlockY() + "/" + l.getBlockZ();
		return s;
	}

	public static void removeAll() {
		for (Mineable block : mining.values()) {
			block.destroy();
		}
	}
	
	
}
