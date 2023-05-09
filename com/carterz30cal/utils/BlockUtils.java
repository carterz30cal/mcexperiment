package com.carterz30cal.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Leaves;

import com.carterz30cal.main.Dungeons;

public class BlockUtils
{
	public static Map<String, BlockStructure> structures = new HashMap<>();
	public static Map<Block, Material> original = new HashMap<>();
	public static Map<Block, BlockData> origData = new HashMap<>();
	
	public static BlockStructure createStructure(String id)
	{
		return new BlockStructure(id);
	}
	
	/*
	 * THIS METHOD PROVIDES A SAFE METHOD OF CHANGING BLOCKS
	 * THAT GUARANTEES THEY STAY IN THE SAME STATE INDEFINITELY.
	 * E.G. LEAVES SET HERE WILL NOT DECAY.
	 * 
	 * 
	 */
	public static void setBlock(Location l, Material t) {
		Block b = Dungeons.w.getBlockAt(l);
		b.setType(t);
		BlockData d = b.getBlockData();
		if (d instanceof Leaves)
		{
			Leaves leaves = (Leaves)d;
			leaves.setPersistent(true);
		}
		
		b.setBlockData(d);
	}
	
	public static BlockStructure getStructure(String id)
	{
		return structures.getOrDefault(id, null);
	}
	
	public static List<Material> createMatArray(Material... mats) {
		List<Material> materials = new ArrayList<>();
		materials.addAll(Arrays.asList(mats));
		return materials;
	}
	public static List<Material> createMatArray(List<Material> existing, Material... mats) {
		List<Material> materials = new ArrayList<>();
		materials.addAll(existing);
		materials.addAll(Arrays.asList(mats));
		return materials;
	}
	
	public static Location getWithin(Location c1, Location c2) {
		double x1 = Math.min(c1.getX(), c2.getX());
		double x2 = Math.max(c1.getX(), c2.getX());
		double y1 = Math.min(c1.getY(), c2.getY());
		double y2 = Math.max(c1.getY(), c2.getY());
		double z1 = Math.min(c1.getZ(), c2.getZ());
		double z2 = Math.max(c1.getZ(), c2.getZ());
		
		return new Location(c1.getWorld(), 
				RandomUtils.getDouble(x1, x2),
				RandomUtils.getDouble(y1, y2),
				RandomUtils.getDouble(z1, z2));
	}
	
	
	public static void removeStructures()
	{
		for (BlockStructure s : structures.values()) s.clear();
		
		for (Block b : original.keySet()) {
			b.setType(original.get(b));
			try {
				b.setBlockData(origData.getOrDefault(b, null));
			}
			catch (Exception e) {
				
			}
		}
	}
	
	public static class BlockStructure 
	{
		private String id;
		private List<BlockMemory> blocks = new ArrayList<>();
		
		public BlockStructure(String id)
		{
			this.id = id;
			structures.put(this.id, this);
		}
		
		public Material fill(int x, int y, int z, Material m)
		{
			Block b = Dungeons.w.getBlockAt(x, y, z);
			if (b.getType() == Material.AIR)
			{
				BlockMemory memory = new BlockMemory();
				memory.block = b;
				memory.previous = b.getType();
				
				b.setType(m);
				blocks.add(memory);
				
				original.putIfAbsent(b, Material.AIR);
				
				return Material.AIR;
			}
			else return b.getType();
		}
		
		public void set(int x, int y, int z, Material m)
		{
			Block b = Dungeons.w.getBlockAt(x, y, z);
			
			Material orig = original.getOrDefault(b, b.getType());
			
			
			BlockMemory memory = new BlockMemory();
			memory.block = b;
			memory.previous = orig;
			
			original.putIfAbsent(b, orig);
			origData.putIfAbsent(b, b.getBlockData());
			
			b.setType(m);
			blocks.add(memory);
		}
		
		public void setIf(int x, int y, int z, Material m, Material onlyIf)
		{
			Block b = Dungeons.w.getBlockAt(x, y, z);
			
			if (b.getType() != onlyIf) return;
			
			Material orig = original.getOrDefault(b, b.getType());
			
			
			BlockMemory memory = new BlockMemory();
			memory.block = b;
			memory.previous = orig;
			
			original.putIfAbsent(b, orig);
			origData.putIfAbsent(b, b.getBlockData());
			
			b.setType(m);
			blocks.add(memory);
		}
		
		public void setIfNot(int x, int y, int z, Material m, Material onlyIfNot)
		{
			Block b = Dungeons.w.getBlockAt(x, y, z);
			
			if (b.getType() == onlyIfNot) return;
			
			Material orig = original.getOrDefault(b, b.getType());
			
			
			BlockMemory memory = new BlockMemory();
			memory.block = b;
			memory.previous = orig;
			
			original.putIfAbsent(b, orig);
			origData.putIfAbsent(b, b.getBlockData());
			
			b.setType(m);
			blocks.add(memory);
		}
		
		public void set(int x, int y, int z, Material m, Material previous)
		{
			Block b = Dungeons.w.getBlockAt(x, y, z);
			
			BlockMemory memory = new BlockMemory();
			memory.block = b;
			memory.previous = previous;
			
			b.setType(m);
			blocks.add(memory);
		}
		
		public void setAll(Material m)
		{
			for (BlockMemory block : blocks) block.block.setType(m);
		}
		
		public void revert()
		{
			for (BlockMemory block : blocks) block.block.setType(block.previous);
		}
		
		
		public void clear()
		{
			for (BlockMemory block : blocks) block.block.setType(block.previous);
			blocks.clear();
		}
		
		public void wipe()
		{
			for (BlockMemory block : blocks) block.block.setType(block.previous);
			blocks.clear();
			
			structures.remove(id);
		}
		
		private class BlockMemory 
		{
			public Block block;
			public Material previous;
		}
	}
}
