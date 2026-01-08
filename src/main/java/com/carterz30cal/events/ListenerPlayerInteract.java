package com.carterz30cal.events;

import com.carterz30cal.entities.Shop;
import com.carterz30cal.gui.LootboxGUI;
import com.carterz30cal.gui.ShopGUI;
import com.carterz30cal.items.*;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.gui.MenuGUI;

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
				if (item != null && item instanceof ItemLootbox && e.getItem().getAmount() > 0) {
					ItemLootbox lootbox = (ItemLootbox)item;

					p.openGui(new LootboxGUI(p, lootbox));

					e.setCancelled(true);
					e.getItem().setAmount(e.getItem().getAmount() - 1);
				}
				p.allowInteract = !p.allowInteract;
				if (p.allowInteract) for (var a : p.abilities) a.ability.onRightClick(a);
				
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
			for (var a : p.abilities) a.ability.onLeftClick(a);
		}
	}
	
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) 
	{
		GamePlayer p = (GamePlayer)GameEntity.get(e.getPlayer());
		//Quest q = Quest.get(e.getRightClicked());
		Shop shop = Shop.getShop(e.getRightClicked());

		if (p != null && p.questTick == 0) {
			/*if (q != null) {
				if (p.completedQuests.contains(q.id)) return;
				//if (p.quests.containsKey(q.id)) q.duringQuest(p);
				else q.startQuest(p);
			/*} else*/ if (shop != null) {
				p.openGui(new ShopGUI(p, shop));
			}
		}
		

	}
}
