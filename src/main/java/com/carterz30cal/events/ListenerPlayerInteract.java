package com.carterz30cal.events;

import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.entities.interactable.GameEntityInteractable;
import com.carterz30cal.gui.LootboxGUI;
import com.carterz30cal.gui.MenuGUI;
import com.carterz30cal.items.Item;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.ItemLootbox;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.mining.MiningManager;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

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
                if (item instanceof ItemLootbox && e.getItem().getAmount() > 0) {
					ItemLootbox lootbox = (ItemLootbox)item;

					p.openGui(new LootboxGUI(p, lootbox));

					e.setCancelled(true);
					e.getItem().setAmount(e.getItem().getAmount() - 1);
				}
				p.allowInteract = !p.allowInteract;
				if (p.allowInteract) for (var a : p.abilities) a.ability.onRightClick(a);
                if (act == Action.RIGHT_CLICK_BLOCK && p.area != null) {
                    assert e.getClickedBlock() != null;
                    p.area.getArea().OnRightClick(p, e.getClickedBlock().getLocation());
                }
				
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
            if (item != null) {
                if (item.type == ItemType.BOW) {
                    if (p.bowTick < 1) {
                        p.bowTick = 4;
                        if (p.getQuiverCount() > 0) {
                            Arrow arrow = p.player.launchProjectile(Arrow.class);
                            arrow.getPersistentDataContainer().set(GameEnemy.keyEnemy, PersistentDataType.STRING, p.player.getUniqueId().toString());
                            arrow.getPersistentDataContainer().set(GameEnemy.keyArrowType, PersistentDataType.STRING, p.useArrow());
                        }
                        else {
                            p.sendMessage("REDYou are out of arrows!");
                        }
                    }
                }
                else if (item.type == ItemType.PICKAXE) {
                    if (p.player.getGameMode() == GameMode.SURVIVAL) {
                        Block attemptingToMine = p.player.getTargetBlockExact(5, FluidCollisionMode.NEVER);
                        if (attemptingToMine != null) {
                            MiningManager.attemptMine(p, attemptingToMine.getLocation());
                        }
                    }
                }
            }
			for (var a : p.abilities) a.ability.onLeftClick(a);
		}
	}
	
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) 
	{
		GamePlayer p = (GamePlayer)GameEntity.get(e.getPlayer());
        if (p != null && p.questTick == 0) {
            GameEntityInteractable interactable = GameEntityInteractable.GetEntity(e.getRightClicked());
            if (interactable != null) {
                p.questTick = 4;
                interactable.Interact(p);
            }
        }
	}
}
