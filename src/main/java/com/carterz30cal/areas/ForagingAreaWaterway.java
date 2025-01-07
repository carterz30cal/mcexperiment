package com.carterz30cal.areas;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.carterz30cal.entities.EnemyManager;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.mining.Mineable;
import com.carterz30cal.utils.BlockUtils;
import com.carterz30cal.utils.BlockUtils.BlockStructure;
import com.carterz30cal.utils.RandomUtils;

public class ForagingAreaWaterway extends AbstractArea 
{
	public final Location c1 = new Location(Dungeons.w, -232, 68, -52);
	public final Location c2 = new Location(Dungeons.w, -195, 68, -106);
	
	public int tick = 0;
	
	private BlockStructure ground;
	//private BlockStructure forest;
	
	private String[] rng = {
			"woodchip-8","woodchip-4","woodchip-2","leaf_ball-1","leaf_mush-140","leaf_mush-72","leaf_mush-24","arrow-33","ghost_powder-1"
	};
	
	public ForagingAreaWaterway() {
		super();
		
		ground = BlockUtils.createStructure("foraging_waterway_ground");
		//forest = BlockUtils.createStructure("foraging_waterway_forest");
		for (int x = c1.getBlockX(); x <= c2.getBlockX(); x++)
		{
			for (int z = c2.getBlockZ(); z <= c1.getBlockZ(); z++) 
			{
				Block b = Dungeons.w.getBlockAt(x, 68, z);
				if (b.getType() == Material.GRAY_CONCRETE) {
					int rand = RandomUtils.getRandom(1, 5);
					Material m = rand < 3 ? Material.PODZOL : (rand < 5 ? Material.BROWN_CONCRETE_POWDER : Material.BROWN_WOOL);
					ground.fill(x, 69, z, m);
					
					for (int y = 70; y < 80; y++) Dungeons.w.getBlockAt(x, y, z).setType(Material.AIR);
					
					int randb = RandomUtils.getRandom(1, 200);
					if (randb <= 60) continue;
					else if (randb <= 184) {
						Mineable.create(x, 70, z, Material.OAK_LEAVES, 110, "leaf_mush", 2);
					}
					else if (randb <= 199) {
						Mineable.create(x, 70, z, Material.OAK_WOOD, 800, "woodchip", 1);
					}
					else {
						String[] r = RandomUtils.getChoice(rng).split("-");
						int am = Integer.parseInt(r[1]);
						Mineable.create(x, 70, z, Material.CHEST, 2200, r[0], am, false);
						continue;
					}
					
					
					for (int y = 71; y < 78; y++) {
						int leaf = RandomUtils.getRandom(1, 100);
						if (leaf <= 25) continue;
						else if (leaf <= 27) Mineable.create(x, 71, z, Material.OAK_WOOD, 800, "woodchip", 1);
						else Mineable.create(x, y, z, Material.OAK_LEAVES, 110, "leaf_mush", 2);
					}
					
				}
				
				
			}
		}
		
	}
	
	public void onMine(GamePlayer miner, Location loc)
	{
		int rand = RandomUtils.getRandom(1, 300);
		if (rand == 1) {
			if (miner.getLevel() >= 9) EnemyManager.spawn("lurker_2", loc);
			else EnemyManager.spawn("lurker_1", loc);
		}
	}
	
	public void onEnd()
	{
		ground.wipe();
	}
}
