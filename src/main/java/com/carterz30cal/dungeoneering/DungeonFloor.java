package com.carterz30cal.dungeoneering;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.BlockUtils;
import com.carterz30cal.utils.BlockUtils.BlockStructure;
import com.carterz30cal.utils.RandomUtils;

public class DungeonFloor 
{
	public int roomCount = 0;
	public int tunnelCount = 0;
	public FloorTile[][] open;
	public boolean[][] allowed;
	public double[][] difficulty;
	
	public BlockStructure floor;
	
	public final int START_AREA_SIZE = 5;
	public final int KEY_ROOM_SIZE = 5;
	public final int MAP_SIZE = 45;
	
	public final int DUNGEON_BASE_HEIGHT = 150;
	public final int DUNGEON_HEIGHT = 6;
	public final int DUNGEON_WALL_HEIGHT = 5;
	public final int TREASURE_COUNT = 30;
	public final int THREADS = 120;
	
	public FloorMaterials materials;
	
	public List<Integer> treasureNodes = new ArrayList<>();
	public List<DungeonSpawnLocation> enemies = new ArrayList<>();
	public Map<FloorTile, Material> temp = new HashMap<>();
	
	
	public boolean generated;
	
	public Location spawn;
	
	public void init(AbstractDungeon dungeon) {
		open = new FloorTile[MAP_SIZE * 3][MAP_SIZE * 3];
		allowed = new boolean[MAP_SIZE * 3][MAP_SIZE * 3];
		for (int l = 0; l < MAP_SIZE * 3; l++) {
			Arrays.fill(allowed[l], true);
		}
		difficulty = new double[MAP_SIZE * 3][MAP_SIZE * 3];
		roomCount = 0;
		
		floor = BlockUtils.createStructure(UUID.randomUUID().toString());
		
		temp.put(null, Material.RED_CONCRETE);
		temp.put(FloorTile.WALL, Material.BLACK_CONCRETE);
		temp.put(FloorTile.CORRIDOR, Material.GREEN_CONCRETE);
		temp.put(FloorTile.DOOR, Material.BLUE_CONCRETE);
		temp.put(FloorTile.ROOM, Material.YELLOW_CONCRETE);
		temp.put(FloorTile.VOID, Material.AIR);
		temp.put(FloorTile.VOID_CORRIDOR, Material.OAK_PLANKS);
		temp.put(FloorTile.FLOOD_FILLED, Material.PURPLE_CONCRETE);
		temp.put(FloorTile.STARTING_ROOM, Material.CYAN_CONCRETE);
		temp.put(FloorTile.KEY_ROOM, Material.EMERALD_BLOCK);
		
		materials = new FloorMaterials();
		
		generateMaze();
		generateDifficultyMap();
		generateRooms();
		generateVoids();
		placeStartRoom();
		placeKeyRoom();
		generateTreasure();
		postProcessFloor();
		//debugGenerate();
		generate();
	}
	
	public void tick(GamePlayer player) {
		if (!generated) return;
		
		for (DungeonSpawnLocation l : enemies) {
			if (player.getDistance(l.location) < 7) l.spawn();
		}
		enemies.removeIf(a -> a.spawned);
	}
	
	public void generate() {
		final int THREAD_SIZE = (MAP_SIZE * MAP_SIZE * 9) / THREADS;
		
		for (int i = 0; i <= THREADS; i++) {
			final int w = i;
			new BukkitRunnable() {
				final int start = THREAD_SIZE * w;
				@Override
				public void run() {
					for (int j = 0; j < THREAD_SIZE; j++) {
						int x = (start + j) / (MAP_SIZE * 3);
						int y = (start + j) % (MAP_SIZE * 3);
						try {
							generateSlice(x, y);
						}
						catch (ArrayIndexOutOfBoundsException e) {
							
						}
					}
					
				}
				
			}.runTaskLater(Dungeons.instance, i);
		}
		
		new BukkitRunnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				generated = true;
			}
			
		}.runTaskLaterAsynchronously(Dungeons.instance, THREADS + 1);
	}
	
	
	public void generateSlice(int x, int y) {
		FloorTile here = open[x][y];
		if (here == null) here = FloorTile.WALL;
		
		final int CEILING = DUNGEON_BASE_HEIGHT + DUNGEON_HEIGHT + DUNGEON_WALL_HEIGHT - 1;
		
		switch (here) {
		case WALL:
			for (int z = 0; z < DUNGEON_HEIGHT + DUNGEON_WALL_HEIGHT - 1; z++) floor.set(x, DUNGEON_BASE_HEIGHT + z, y, RandomUtils.getChoice(materials.wall));
			break;
		case VOID:
			floor.set(x, DUNGEON_BASE_HEIGHT, y, Material.BEDROCK);
			floor.set(x, DUNGEON_BASE_HEIGHT + 1, y, Material.LAVA);
			break;
		case VOID_CORRIDOR:
			floor.set(x, DUNGEON_BASE_HEIGHT, y, Material.BEDROCK);
			floor.set(x, DUNGEON_BASE_HEIGHT + 1, y, Material.LAVA);
			floor.set(x, DUNGEON_BASE_HEIGHT + DUNGEON_HEIGHT - 1, y, RandomUtils.getChoice(materials.bridges));
			break;
		case CORRIDOR:
			for (int z = 0; z < DUNGEON_HEIGHT; z++) floor.set(x, DUNGEON_BASE_HEIGHT + z, y, RandomUtils.getChoice(materials.floor));
			break;
		case KEY_ROOM:
		case ROOM:
		case STARTING_ROOM:
			for (int z = 0; z < DUNGEON_HEIGHT; z++) floor.set(x, DUNGEON_BASE_HEIGHT + z, y, RandomUtils.getChoice(materials.floor));
			break;
		case KEY:
			for (int z = 0; z < DUNGEON_HEIGHT; z++) floor.set(x, DUNGEON_BASE_HEIGHT + z, y, RandomUtils.getChoice(materials.floor));
			floor.set(x, DUNGEON_BASE_HEIGHT + DUNGEON_HEIGHT, y, Material.EMERALD_BLOCK);
			break;
		case TREASURE:
			for (int z = 0; z < DUNGEON_HEIGHT - 1; z++) floor.set(x, DUNGEON_BASE_HEIGHT + z, y, RandomUtils.getChoice(materials.floor));
			floor.set(x, DUNGEON_BASE_HEIGHT + DUNGEON_HEIGHT - 1, y, Material.GOLD_BLOCK);
			floor.set(x, DUNGEON_BASE_HEIGHT + DUNGEON_HEIGHT, y, Material.BARREL);
			
			Block b = Dungeons.w.getBlockAt(x, DUNGEON_BASE_HEIGHT + DUNGEON_HEIGHT, y);
			Barrel barrel = (Barrel) b.getState();
			
			int loots = RandomUtils.getRandom(4, 7);
			for (int l = 0; l < loots; l++) barrel.getInventory().addItem(RandomUtils.getChoice(materials.loot));
			break;
		default:
			floor.set(x, DUNGEON_BASE_HEIGHT, y, Material.RED_WOOL);
		}
		
		if (x % 6 == 0 && y % 6 == 0) floor.set(x, CEILING, y, RandomUtils.getChoice(materials.lights));
		else floor.set(x, CEILING, y, RandomUtils.getChoice(materials.wall));
	}
	
	public void debugGenerate() {
		for (int x = 0; x < MAP_SIZE * 3; x++) {
			final int fx = x;
			new BukkitRunnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					for (int y = 0; y < MAP_SIZE; y++) {
						floor.set(fx, 150, y, temp.get(open[fx][y]));
						genDebugDiffTile(fx, y);
					}
				}
				
			}.runTaskLater(Dungeons.instance, x * 3);
			new BukkitRunnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					for (int y = MAP_SIZE; y < MAP_SIZE * 2; y++) {
						floor.set(fx, 150, y, temp.get(open[fx][y]));
						genDebugDiffTile(fx, y);
					}
				}
				
			}.runTaskLater(Dungeons.instance, x * 3 + 1);
			new BukkitRunnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					for (int y = MAP_SIZE * 2; y < MAP_SIZE * 3; y++) {
						floor.set(fx, 150, y, temp.get(open[fx][y]));
						genDebugDiffTile(fx, y);
					}
				}
				
			}.runTaskLater(Dungeons.instance, x * 3 + 2);
		}
	}
	
	private void genDebugDiffTile(int x, int y) {
		if (difficulty[x][y] == 0) floor.set(x, 160, y, Material.BLUE_STAINED_GLASS);
		else if (difficulty[x][y] < 0.333333) floor.set(x, 160, y, Material.GREEN_STAINED_GLASS);
		else if (difficulty[x][y] < 0.66666666) floor.set(x, 160, y, Material.YELLOW_STAINED_GLASS);
		else if (difficulty[x][y] < 1.2) floor.set(x, 160, y, Material.RED_STAINED_GLASS);
		else floor.set(x, 160, y, Material.WHITE_STAINED_GLASS);
	}
	
	private void generateDifficultyMap() {
		final int CENTER = (MAP_SIZE * 3) / 2;
		for (int x = 0; x < MAP_SIZE * 3; x++) {
			for (int y = 0; y < MAP_SIZE * 3; y++) {
				double dist1 = Math.sqrt(Math.pow(CENTER - x, 2) + Math.pow(CENTER - y, 2));
				double dist2 = Math.abs(CENTER - x) + Math.abs(CENTER - y);
				
				double dist = (dist1 + dist2) / 2;
				
				if (dist1 <= START_AREA_SIZE * 2.8) difficulty[x][y] = 0;
				else difficulty[x][y] = Math.pow((dist / (MAP_SIZE * 2.25D)), 1.1);
			}
		}
	}
	
	private int getAround(int x, int y, FloorTile var) {
		int count = 0;
		if (open[x - 1][y - 1] == var) count++;
		if (open[x - 1][y - 0] == var) count++;
		if (open[x - 1][y + 1] == var) count++;
		if (open[x - 0][y - 1] == var) count++;
		if (open[x - 0][y + 1] == var) count++;
		if (open[x + 1][y - 1] == var) count++;
		if (open[x + 1][y - 0] == var) count++;
		if (open[x + 1][y + 1] == var) count++;
		
		return count;
	}
	
	private void generateMaze() 
	{
		boolean[][] lovelace = new boolean[MAP_SIZE][MAP_SIZE];
		
		Deque<Integer> nodes = new ArrayDeque<>();
		int cx = MAP_SIZE / 2;
		int cy = MAP_SIZE / 2;
		int current = convXY(cx, cy);
		
		nodes.addFirst(current);
		
		while (nodes.size() > 0) {
			Integer[] choices = {1,2,3,4};
			
			
			int c = 0;
			while (c < 4) {
				choices = RandomUtils.shuffle(choices);
				int dx = 0;
				int dy = 0;
				
				int ch = choices[c % 4];
				if (ch == 1) dx = 1;
				else if (ch == 2) dx = -1;
				else if (ch == 3) dy = 1;
				else dy = -1;
				
				if (canContinue(lovelace, cx + dx, cy + dy)) {
					lovelace[cx + dx][cy + dy] = true;
					
					cx += dx;
					cy += dy;
					current = convXY(cx, cy);
					nodes.addFirst(current);
				}
				else c++;
			}
			
			int nc = nodes.removeFirst();
			if (c == 4) treasureNodes.add(nc);
			cx = nc / MAP_SIZE;
			cy = nc % MAP_SIZE;
		}
		
		for (int x = 1; x < MAP_SIZE; x++) {
			for (int y = 1; y < MAP_SIZE; y++) {
				int mx = x * 3;
				int my = y * 3;
				FloorTile val = lovelace[x][y] ? FloorTile.CORRIDOR : FloorTile.WALL;
				open[mx - 1][my - 1] = val;
				open[mx - 1][my    ] = val;
				open[mx - 1][my + 1] = val;
				open[mx    ][my - 1] = val;
				open[mx    ][my    ] = val;
				open[mx    ][my + 1] = val;
				open[mx + 1][my - 1] = val;
				open[mx + 1][my    ] = val;
				open[mx + 1][my + 1] = val;
			}
		}
	}
	
	private void generateTreasure() {
		int treasures = TREASURE_COUNT;
		
		while (treasures > 0 && treasureNodes.size() > 0) {
			int pos = RandomUtils.getChoice(treasureNodes);
			int px = (pos / MAP_SIZE) * 3;
			int py = (pos % MAP_SIZE) * 3;
			
			int count = 0;
			if (open[px + 2][py] == FloorTile.WALL) count++;
			if (open[px - 2][py] == FloorTile.WALL) count++;
			if (open[px][py + 2] == FloorTile.WALL) count++;
			if (open[px][py - 2] == FloorTile.WALL) count++;
			
			if (open[px][py] == FloorTile.CORRIDOR && count == 3) {
				open[px][py] = FloorTile.TREASURE;
				treasures--;
			}
		}
	}
	
	
	private void postProcessFloor() {
		int CENTER = (MAP_SIZE * 3) / 2;
		for (int x = CENTER - START_AREA_SIZE - 5; x < CENTER + START_AREA_SIZE + 5; x++) {
			for (int y = CENTER - START_AREA_SIZE - 5; y < CENTER + START_AREA_SIZE + 5; y++) {
				difficulty[x][y] = 0;
				
			}
		}
		
		for (int x = 0; x < MAP_SIZE * 3; x++) {
			for (int y = 0; y < MAP_SIZE * 3; y++) {
				if (open[x][y] == FloorTile.VOID_CORRIDOR && getAround(x, y, FloorTile.VOID_CORRIDOR) < 3) open[x][y] = FloorTile.WALL; 
				else if (open[x][y] == FloorTile.CORRIDOR) {
					int cora = getAround(x, y, FloorTile.CORRIDOR);
					if (cora < 2) open[x][y] = FloorTile.WALL;
				}
				
				if (open[x][y] != null && open[x][y].isSpawnable()) {
					int chance = (int)Math.floor(difficulty[x][y] * 1000);
					int random = RandomUtils.getRandom(1, 10000);
					
					if (chance >= random) {
						enemies.add(new DungeonSpawnLocation(new Location(Dungeons.w, x, DUNGEON_BASE_HEIGHT + DUNGEON_HEIGHT, y), materials));
					}
				}
			}
		}
		
		spawn = new Location(Dungeons.w, CENTER + 0.5, DUNGEON_BASE_HEIGHT + DUNGEON_HEIGHT, CENTER + 0.5);
	}
	
	private void generateRooms() {
		int rooms = 12;
		
		int ROOM_SIZE_MIN = 8;
		int ROOM_SIZE_MAX = 22;
		
		while (rooms > 0) {
			int size = RandomUtils.getRandom(ROOM_SIZE_MIN, ROOM_SIZE_MAX);
			
			int cx = RandomUtils.getRandom(2, MAP_SIZE * 3 - size - 2);
			int cy = RandomUtils.getRandom(2, MAP_SIZE * 3 - size - 2);
			
			double roomDifficulty = 0;
			boolean acceptable = true;
			for (int x = cx; x <= cx + size; x++) {
				for (int y = cy; y <= cy + size; y++) {
					if (!allowed[x][y]) {
						acceptable = false;
						break;
					}
					
					roomDifficulty += difficulty[x][y];
				}
			}
			if (!acceptable) break;
			
			roomDifficulty = (roomDifficulty / (size * size)) * 1.25;
			for (int x = cx; x <= cx + size; x++) {
				for (int y = cy; y <= cy + size; y++) {
					open[x][y] = FloorTile.ROOM;
					difficulty[x][y] = roomDifficulty;
				}
			}
			
			for (int x = cx - 1; x <= cx + size + 2; x++) {
				for (int y = cy - 1; y <= cy + size + 2; y++) {
					allowed[x][y] = false;
				}
			}
			
			rooms--;
		}
	}
	
	private void generateVoids() {
		int voids = 5;
		
		int VOID_SIZE_MIN = 7;
		int VOID_SIZE_MAX = 17;
		while (voids > 0) {
			int sx = RandomUtils.getRandom(VOID_SIZE_MIN, VOID_SIZE_MAX);
			int sy = RandomUtils.getRandom(VOID_SIZE_MIN, VOID_SIZE_MAX);
			
			int cx = RandomUtils.getRandom(1, MAP_SIZE * 3 - sx - 1);
			int cy = RandomUtils.getRandom(1, MAP_SIZE * 3 - sy - 1);
			
			for (int x = cx; x <= cx + sx; x++) {
				for (int y = cy; y <= cy + sy; y++) {
					if (open[x][y] == FloorTile.WALL) open[x][y] = FloorTile.VOID;
					else if (open[x][y] == FloorTile.CORRIDOR) open[x][y] = FloorTile.VOID_CORRIDOR;
				}
			}
			voids--;
		}
		
	}
	
	private void placeStartRoom() {
		int STARTING_ROOM_SIZE = START_AREA_SIZE;
		
		int CENTER = (MAP_SIZE * 3) / 2;
		
		for (int x = CENTER - STARTING_ROOM_SIZE - 1; x <= CENTER + STARTING_ROOM_SIZE + 1; x++) {
			for (int y = CENTER - STARTING_ROOM_SIZE - 1; y <= CENTER + STARTING_ROOM_SIZE + 1; y++) {
				if (open[x][y] == FloorTile.ROOM || open[x][y] == FloorTile.VOID) open[x][y] = FloorTile.WALL;
				else if (open[x][y] == FloorTile.VOID_CORRIDOR) open[x][y] = FloorTile.CORRIDOR;
			}
		}
		for (int x = CENTER - STARTING_ROOM_SIZE; x <= CENTER + STARTING_ROOM_SIZE; x++) {
			for (int y = CENTER - STARTING_ROOM_SIZE; y <= CENTER + STARTING_ROOM_SIZE; y++) {
				open[x][y] = FloorTile.STARTING_ROOM;
			}
		}
	}
	
	private void placeKeyRoom() {
		int rx, ry = 0;
		while (true) {
			rx = RandomUtils.getRandom(3, MAP_SIZE * 3 - (KEY_ROOM_SIZE + 1));
			rx /= 3;
			rx *= 3;
			rx -= 1;
			ry = RandomUtils.getRandom(3, MAP_SIZE * 3 - (KEY_ROOM_SIZE + 1));
			ry /= 3;
			ry *= 3;
			ry -= 1;
			
			if (difficulty[rx][ry] < 0.5) continue;
			boolean allow = true;
			boolean foundCorridor = false;
			for (int x = rx; x < rx + KEY_ROOM_SIZE; x++) {
				for (int y = ry; y < ry + KEY_ROOM_SIZE; y++) {
					if (open[x][y] == FloorTile.CORRIDOR) foundCorridor = true;
					if (open[x][y] != FloorTile.ROOM) continue;

					allow = false;
					break;
				}
			}
			int corridorCorners = 0;
			if (open[rx][ry] == FloorTile.CORRIDOR) corridorCorners++;
			if (open[rx + (KEY_ROOM_SIZE - 1)][ry] == FloorTile.CORRIDOR) corridorCorners++;
			if (open[rx][ry + (KEY_ROOM_SIZE - 1)] == FloorTile.CORRIDOR) corridorCorners++;
			if (open[rx + (KEY_ROOM_SIZE - 1)][ry + (KEY_ROOM_SIZE - 1)] == FloorTile.CORRIDOR) corridorCorners++;
			
			
			if (allow && foundCorridor && corridorCorners == 1) break;
		}
		
		for (int x = rx; x < rx + KEY_ROOM_SIZE; x++) {
			for (int y = ry; y < ry + KEY_ROOM_SIZE; y++) {
				open[x][y] = FloorTile.KEY_ROOM;
			}
		}
		
		int cx = rx + (KEY_ROOM_SIZE / 2);
		int cy = ry + (KEY_ROOM_SIZE / 2);
		//System.out.println("x: " + cx + ", y: " + cy);
		open[cx][cy] = FloorTile.KEY;
	}
	
	private boolean canContinue(boolean[][] map, int x, int y) 
	{
		if (x < 1 || x > MAP_SIZE - 2 || y < 1 || y > MAP_SIZE - 2) return false;
		
		int neigh = 0;
		if (map[x + 1][y]) neigh++;
		if (map[x][y]) neigh++;
		if (map[x - 1][y]) neigh++;
		if (map[x + 1][y - 1]) neigh++;
		if (map[x][y - 1]) neigh++;
		if (map[x - 1][y - 1]) neigh++;
		if (map[x + 1][y + 1]) neigh++;
		if (map[x][y + 1]) neigh++;
		if (map[x - 1][y + 1]) neigh++;
		
		return neigh < 3;
	}
	private int convXY(int x, int y) {
		return x * MAP_SIZE + y;
	}
	
}
