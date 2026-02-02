package com.carterz30cal.areas;

import com.carterz30cal.entities.EnemyManager;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.mining.Mineable;
import com.carterz30cal.utils.BlockUtils;
import com.carterz30cal.utils.BlockUtils.BlockStructure;
import com.carterz30cal.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class ForagingAreaWaterway extends AbstractArea 
{
	public final Location c1 = new Location(Dungeons.w, -131, 77, 26);
	public final Location c2 = new Location(Dungeons.w, -142, 87, 50);
	
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

		int minX = Math.min(c1.getBlockX(), c2.getBlockX());
		int maxX = Math.max(c1.getBlockX(), c2.getBlockX());
		int minY = Math.min(c1.getBlockY(), c2.getBlockY());
		int maxY = Math.max(c1.getBlockY(), c2.getBlockY());
		int minZ = Math.min(c1.getBlockZ(), c2.getBlockZ());
		int maxZ = Math.max(c1.getBlockZ(), c2.getBlockZ());

		for (int x = minX; x <= maxX; x++)
		{
			for (int z = minZ; z <= maxZ; z++)
			{
				for (int y = minY; y <= maxY; y++) {
					Block b = Dungeons.w.getBlockAt(x, y, z);
					if (b.getType() != Material.AIR) continue;

					int randb = RandomUtils.getRandom(1, 201);
					if (randb <= 50) {

					}
					else if (randb <= 185) {
						Mineable.create(x, y, z, Material.OAK_LEAVES, 110, "leaf_mush", 1);
					}
					else if (randb <= 198) {
						Mineable.create(x, y, z, Material.OAK_WOOD, 200, "leaf_mush", 2);
					}
				}
				
				
			}
		}
		
	}
	
	public void onMine(GamePlayer miner, Location loc)
	{
		int rand = RandomUtils.getRandom(5, 300);
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
