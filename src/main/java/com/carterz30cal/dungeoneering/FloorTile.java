package com.carterz30cal.dungeoneering;

public enum FloorTile {
	WALL (false),
	CORRIDOR,
	ROOM,
	KEY_ROOM,
	KEY,
	DOOR,
	TREASURE,
	VOID (false),
	VOID_CORRIDOR,
	STARTING_ROOM,
	FLOOD_FILLED (false);
	private boolean passable;
	private FloorTile() {
		passable = true;
	}
	
	private FloorTile(boolean p) {
		passable = p;
	}
	
	public boolean isPassable() {
		return passable;
	}
	
	public boolean isSpawnable() {
		return this == CORRIDOR || this == ROOM || this == VOID_CORRIDOR;
	}
}
