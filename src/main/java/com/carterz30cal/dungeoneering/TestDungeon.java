package com.carterz30cal.dungeoneering;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.main.Dungeons;
import org.bukkit.scheduler.BukkitRunnable;

public class TestDungeon extends AbstractDungeon {

	public TestDungeon(GamePlayer player) {
		super(player);
		
		DungeonFloor floor = new DungeonFloor();
		floor.materials = new FloorMaterials();
		
		floor.init(this);
		
		new BukkitRunnable() {
			boolean teleported = false;
			@Override
			public void run() {
				if (!teleported && floor.generated) {
					teleported = true;
					player.player.teleport(floor.spawn);
				}

				floor.tick(player);
			}
			
		}.runTaskTimer(Dungeons.instance, 1, 1);
	}

}
