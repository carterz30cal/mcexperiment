package com.carterz30cal.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Slime;
import org.bukkit.persistence.PersistentDataType;

import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.EntityUtils;
import com.carterz30cal.utils.StringUtils;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class GameOwnable extends GameEntity {
	public List<GamePlayer> owners = new ArrayList<>();
	public Entity main;
	public ArmorStand display;
	public String name;
	public UUID u;
	
	private List<Entity> clones = new ArrayList<>();
	private EntityType type;
	private Location location;
	
	@Override
	public int getHealth() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		main.remove();
		display.remove();
	}

	@Override
	public void damage(DamageInfo info) {
		// TODO Auto-generated method stub

	}

	@Override
	public Location getLocation() {
		// TODO Auto-generated method stub
		return main.getLocation();
	}
	
	public void refreshVisibility() {
		// BLANK SLATE FOR ALL PLAYERS // 
		main.remove();
		display.remove();
		
		main = generateMain(location, type);
		display = generateDisplay(name, location);
		
		// REMOVE THE ENTITIES FROM PLAYERS WITHOUT PERMS // 
		PacketContainer packet = Dungeons.proto.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
		List<Integer> ids = new ArrayList<>();
		ids.add(main.getEntityId());
		ids.add(display.getEntityId());
		
		//packet.getIntegers().write(0, 1);
		try {
			packet.getIntLists().write(0, ids);
			
			for (GamePlayer p : PlayerManager.players.values()) if (!owners.contains(p)) Dungeons.proto.sendServerPacket(p.player, packet);
		}
		catch (Exception e) {
			
		}
		
		
		
		/*
		PacketContainer respawn1 = Dungeons.proto.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
		PacketContainer respawn2 = Dungeons.proto.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
		respawn1.getIntegers().write(0, main.getEntityId());
		respawn1.getIntegers().write(1, (int)main.getType().getTypeId());
		respawn2.getIntegers().write(0, display.getEntityId());
		respawn2.getIntegers().write(1, 2);
		
		for (GamePlayer p : owners) {
			Dungeons.proto.sendServerPacket(p.player, respawn1);
			Dungeons.proto.sendServerPacket(p.player, respawn2);
		}
		*/
	}
	
	public void addOwner(GamePlayer p, boolean refresh)
	{
		owners.add(p);
		if (refresh) refreshVisibility();
	}
	
	private Entity generateMain(Location location, EntityType type) {
		Entity m = EntityUtils.spawnPart(type, location);
		if (m instanceof Mob)
		{
			Mob mmain = (Mob)m;
			mmain.setRemoveWhenFarAway(false);
			mmain.setAI(false);
			mmain.setSilent(true);
		}
		
		if (m instanceof Slime)
		{
			Slime myHomeSlime = (Slime)m;
			myHomeSlime.setSize(3);
		}
		
		m.getPersistentDataContainer().set(GameEnemy.keyEnemy, PersistentDataType.STRING, u.toString());
		
		return m;
	}
	
	private ArmorStand generateDisplay(String name, Location location) {
		ArmorStand display = EntityUtils.spawnHologram(location.clone().add(0, 1.9, 0), 0);
		display.setCustomName(ChatColor.GOLD + StringUtils.colourString(name));
		
		display.getPersistentDataContainer().set(GameEnemy.keyEnemy, PersistentDataType.STRING, u.toString());
		
		return display;
	}
	
	public GameOwnable(String name, Location location, EntityType type) {
		this.name = name;
		this.location = location;
		this.type = type;
		
		Entity m = EntityUtils.spawnPart(type, location);
		if (m instanceof Mob)
		{
			Mob mmain = (Mob)m;
			mmain.setRemoveWhenFarAway(false);
			mmain.setAI(false);
			mmain.setSilent(true);
		}
		
		if (m instanceof Slime)
		{
			Slime myHomeSlime = (Slime)m;
			myHomeSlime.setSize(3);
		}
		
		main = m;
		u = main.getUniqueId();
		display = generateDisplay(name, location);
		
		
		register();
	}
	
	public final void semireg() {
		for (Entity e : clones)
		{
			e.getPersistentDataContainer().set(GameEnemy.keyEnemy, PersistentDataType.STRING, u.toString());
		}
	}
	
	public final void register()
	{
		if (main == null) return;
		
		UUID uuid = main.getUniqueId();
		u = uuid;
		
		List<Entity> all = new ArrayList<>();
		if (display != null) all.add(display);
		
		super.register(u);
		for (Entity e : all)
		{
			e.getPersistentDataContainer().set(GameEnemy.keyEnemy, PersistentDataType.STRING, u.toString());
		}
		
		refreshVisibility();
	}

}
