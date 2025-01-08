package com.carterz30cal.events;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.gui.MenuGUI;
import com.carterz30cal.items.Item;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.quests.Quest;

public class ListenerPlayerInteract implements Listener {
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		GamePlayer p = (GamePlayer)GameEntity.get(e.getPlayer());
		
		if (p.player.getGameMode() == GameMode.CREATIVE) return;
		else e.setCancelled(true);
		
		if (e.getItem() != null && p.player.getInventory().getHeldItemSlot() == 8)
		{
			p.openGui(new MenuGUI(p));
		}
		else
		{
			Item item = ItemFactory.getItem(e.getItem());
			Action act = e.getAction();
			if (act == Action.RIGHT_CLICK_AIR || act == Action.RIGHT_CLICK_BLOCK) 
			{
				p.allowInteract = !p.allowInteract;
				if (p.allowInteract) for (ItemAbility a : p.abilities) a.onRightClick();
				
				if (item != null && item.material == Material.FISHING_ROD) e.setCancelled(false);
				if (act == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.BARREL) {
					e.setCancelled(false);
				}
			}
			else if (act == Action.LEFT_CLICK_AIR || act == Action.LEFT_CLICK_BLOCK)
			{
				/*
				if (item != null && item.type == ItemType.BOW && p.bowTick < 1)
				{
					p.bowTick = 4;
					if (p.getQuiverCount() > 0)
					{
						Arrow arrow = p.player.launchProjectile(Arrow.class);
						arrow.getPersistentDataContainer().set(GameEnemy.keyEnemy, PersistentDataType.STRING, p.player.getUniqueId().toString());
						arrow.getPersistentDataContainer().set(GameEnemy.keyArrowType, PersistentDataType.STRING, p.useArrow());
					}
					else p.sendMessage("REDYou are out of arrows!");
				}
				for (ItemAbility a : p.abilities) a.onLeftClick();
				*/
			}
			
			
			
		}
	}
	
	@EventHandler
	public void onPlayerAnim(PlayerAnimationEvent e) {
		
		GamePlayer p = (GamePlayer)GameEntity.get(e.getPlayer());
		Item item = ItemFactory.getItem(p.getMainItem());
		if (e.getAnimationType() == PlayerAnimationType.ARM_SWING) {
			if (item != null && item.type == ItemType.BOW && p.bowTick < 1)
			{
				p.bowTick = 4;
				if (p.getQuiverCount() > 0)
				{
					Arrow arrow = p.player.launchProjectile(Arrow.class);
					arrow.getPersistentDataContainer().set(GameEnemy.keyEnemy, PersistentDataType.STRING, p.player.getUniqueId().toString());
					arrow.getPersistentDataContainer().set(GameEnemy.keyArrowType, PersistentDataType.STRING, p.useArrow());
				}
				else p.sendMessage("REDYou are out of arrows!");
			}
			for (ItemAbility a : p.abilities) a.onLeftClick();
		}
	}
	
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) 
	{
		GamePlayer p = (GamePlayer)GameEntity.get(e.getPlayer());
		Quest q = Quest.get(e.getRightClicked());
		
		
		if (p != null && q != null && p.questTick == 0)
		{
			if (p.completedQuests.contains(q.id)) return;
			if (p.quests.containsKey(q.id)) q.duringQuest(p);
			else q.startQuest(p);
		}
	}
}
