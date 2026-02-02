package com.carterz30cal.entities;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.Item;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.EntityUtils;
import com.carterz30cal.utils.MathsUtils;
import com.carterz30cal.utils.RandomUtils;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class GameFloatingItem extends GameEntity {
	public boolean collected;
	
	private ArmorStand display;
	private ItemStack reward;
	private GamePlayer owner;
	
	private BukkitRunnable ticker;
	
	
	
	@Override
	public int getHealth() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		deregister(display.getUniqueId());
		display.remove();
	}

	@Override
	public void damage(DamageInfo info) {
		// TODO Auto-generated method stub

	}

	@Override
	public Location getLocation() {
		// TODO Auto-generated method stub
		return display.getLocation();
	}

	
	public static GameFloatingItem spawn(Location loc, ItemStack reward, GamePlayer owner)
	{
		GameFloatingItem item = new GameFloatingItem();
		
		ArmorStand a = EntityUtils.spawnHologram(loc.add(0, 0.5, 0), -1);
        if (a == null) {
            return null;
        }
		a.setSmall(false);
		a.setMarker(false);
		
		String name = reward.getItemMeta().getDisplayName();
		if (reward.getAmount() > 1) name = "DARK_GRAY" + reward.getAmount() + "x " + name;
		
		EntityUtils.setEntityName(a, name);
		item.display = a;
		item.reward = reward;
		item.owner = owner;
		
		Item i = ItemFactory.getItem(reward);
		if (i == null) a.getEquipment().setItemInMainHand(reward);
		else if (i.type == ItemType.HELMET) a.getEquipment().setHelmet(reward);
		else if (i.type == ItemType.CHESTPLATE) a.getEquipment().setChestplate(reward);
		else if (i.type == ItemType.LEGGINGS) a.getEquipment().setLeggings(reward);
		else if (i.type == ItemType.BOOTS) a.getEquipment().setBoots(reward);
		else a.getEquipment().setItemInMainHand(reward);
		
		PacketContainer packet = Dungeons.proto.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
		List<Integer> ids = new ArrayList<>();
		ids.add(item.display.getEntityId());
		
		//packet.getIntegers().write(0, 1);
		try {
			packet.getIntLists().write(0, ids);
			
			for (GamePlayer p : PlayerManager.players.values()) if (p != owner) Dungeons.proto.sendServerPacket(p.player, packet);
		} catch (Exception ignored) {
			
		}
		
		
		item.ticker = new BukkitRunnable()
				{
					double angle = RandomUtils.getDouble(0, 360);
					@Override
					public void run() {
						angle += 2D;
						
						Location rot = item.display.getLocation();
						rot.setDirection(new Vector(MathsUtils.getCircleX(angle), Math.PI / 2, MathsUtils.getCircleZ(angle)));
						item.display.teleport(rot);
						
						double dist = item.owner.getLocation().distance(item.getLocation());
						if (dist < 1.5 || dist > 20)
						{
							owner.giveItem(item.reward);
							owner.playSound(Sound.ENTITY_ITEM_PICKUP, 1.8, 1);
							
							item.collected = true;
							item.remove();
							cancel();
						}
					}
			
				};
		item.ticker.runTaskTimer(Dungeons.instance, 0, 1);
		item.register(a.getUniqueId());
		return item;
	}
	
}
