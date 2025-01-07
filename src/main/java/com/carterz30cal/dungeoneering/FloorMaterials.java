package com.carterz30cal.dungeoneering;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.utils.BlockUtils;

public class FloorMaterials 
{
	public List<Material> wall;
	public List<Material> floor;
	public List<Material> lights;
	public List<Material> keyAltar;
	public List<Material> bridges;
	
	public List<ItemStack> loot = new ArrayList<>();
	public List<String> enemies = new ArrayList<>();
	
	public FloorMaterials() {
		wall = BlockUtils.createMatArray(Material.STONE, Material.COBBLESTONE);
		floor = BlockUtils.createMatArray(wall, Material.MOSSY_COBBLESTONE, Material.ANDESITE);
		lights = BlockUtils.createMatArray(Material.GLOWSTONE);
		keyAltar = BlockUtils.createMatArray(Material.OXIDIZED_COPPER);
		bridges = BlockUtils.createMatArray(Material.SPRUCE_PLANKS, Material.DARK_OAK_PLANKS);
		
		addLoot("dungeon_dust");
		addEnemy("waterway_crypt_dweller");
	}
	
	public void addLoot(String item) {
		ItemStack gen = ItemFactory.build(item);
		loot.add(gen);
	}
	
	public void addEnemy(String enemy) {
		enemies.add(enemy);
	}
}
