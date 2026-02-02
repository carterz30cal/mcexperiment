package com.carterz30cal.gui;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.*;
import com.carterz30cal.items.abilities2.Abilities;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnvilGUI extends AbstractGUI 
{
	public boolean locked;
	public ItemReqs requirements;
	public AnvilGUI(GamePlayer owner)
	{
		super(owner);
		
		inventory = new GooeyInventory("Anvil", 6);
		inventory.initUsingTemplate(GooeyTemplate.PANED_DARK);
		
		locked = true;
		
		inventory.setSlot(null, calc(1, 4));
		inventory.setSlot(null, calc(7, 4));
		inventory.setSlot(null, calc(4, 1));
		update(false);
		inventory.update();
	}
	
	private void update(boolean positive)
	{
		String ty = positive ? "BLUE_STAINED_GLASS_PANE" : "RED_STAINED_GLASS_PANE";
		
		locked = positive;
		inventory.setSlot(null, calc(4, 1));
		for (int i = 2; i < 7; i++) inventory.setSlot(GooeyInventory.produceElement(ty, " "), calc(i, 4));
		for (int i = 2; i < 5; i++) inventory.setSlot(GooeyInventory.produceElement(ty, " "), calc(4, i));
	}
	
	public void onClose()
	{
		if (inventory.getSlot(calc(7, 4)) != null) owner.giveItem(inventory.getSlot(calc(7, 4)));
		if (inventory.getSlot(calc(1, 4)) != null) owner.giveItem(inventory.getSlot(calc(1, 4)));
	}
	
	public boolean allowClick(int clickPos, ItemStack clicked)
	{
		if ((clickPos == calc(1, 4) || clickPos == calc(7, 4)) && clicked != null)
		{
			owner.giveItem(clicked);
			inventory.setSlot(null, clickPos);
			owner.playSound(Sound.BLOCK_DISPENSER_DISPENSE, 0.7, 1);
			
			update(false);
			inventory.update();
		}
		else if (clickPos == calc(4, 4) && clicked != null && clicked.getType() == Material.ANVIL)
		{
			if (requirements.areRequirementsMet(owner))
			{
				requirements.execute(owner);
				
				owner.giveItem(inventory.getSlot(calc(4, 1)));
				owner.playSound(Sound.BLOCK_ANVIL_USE, 0.9, 1);
				
				inventory.setSlot(null, calc(1, 4));
				inventory.setSlot(null, calc(4, 1));
				inventory.setSlot(null, calc(7, 4));
				
				update(false);
				inventory.update();
			}
			else owner.sendMessage("REDYou do not meet the requirements!");
		}
		else if (clickPos >= 54 && clicked != null)
		{
			Item click = ItemFactory.getItem(clicked);
			if (click == null) return false;
			ItemStack one = clicked.clone();
			one.setAmount(1);
			if ((click.type == ItemType.ENCHANTMENT || click.type == ItemType.ATTUNER) && inventory.getSlot(calc(7, 4)) == null)
			{
				inventory.setSlot(one, calc(7, 4));
				clicked.setAmount(clicked.getAmount() - 1);
				owner.playSound(Sound.BLOCK_DISPENSER_DISPENSE, 0.7, 1);
			}
			else if (click.type != ItemType.INGREDIENT && inventory.getSlot(calc(1, 4)) == null)
			{
				inventory.setSlot(one, calc(1, 4));
				clicked.setAmount(clicked.getAmount() - 1);
				owner.playSound(Sound.BLOCK_DISPENSER_DISPENSE, 0.7, 1);
			}
			else return false;
			
			boolean usable = inventory.getSlot(calc(7, 4)) != null && inventory.getSlot(calc(1, 4)) != null;
			if (usable)
			{
				requirements = new ItemReqs();
				
				ItemStack applying = inventory.getSlot(calc(1, 4));
				Item apply = ItemFactory.getItem(applying);
				
				ItemStack book = inventory.getSlot(calc(7, 4));
				
				ItemStack product = applying.clone();
				
				var applyingEnchants = ItemFactory.getItemEnchants(applying);
				var bookEnchants = ItemFactory.getItemEnchants(book);
				
				
				boolean changeMade = false;
				boolean appliable = true;
				
				for (var enchant : bookEnchants)
				{
					if (enchant.ability.getApplicableTypes().contains(apply.type)) continue;
					
					appliable = false;
					break;
				}
				//if (ItemFactory.sumEnchantPower(applyingEnchants) + ItemFactory.sumEnchantPower(bookEnchants) > apply.stats.getStat(Stat.ENCHANT_POWER)) appliable = false;
				
				// check if any enchants actually get applied or increased
				if (appliable || apply.type == ItemType.ENCHANTMENT)
				{
					Map<Abilities, Integer> enchantmentTypes = new HashMap<>();
					for (var e : applyingEnchants) enchantmentTypes.put(e.ability.source, e.level);
					
					boolean modification = false;
					for (var be : bookEnchants)
					{
						Abilities source = be.ability.source;
						if (!enchantmentTypes.containsKey(source) || enchantmentTypes.get(source) < be.level)
						{
							enchantmentTypes.put(source, be.level);
							requirements.addRequirement(be.ability.getCatalystRequirements(be, be.level));
							modification = true;
						}
						else if (enchantmentTypes.get(source) == be.level && be.level < be.ability.getMaximumLevel())
						{
							enchantmentTypes.put(source, be.level + 1);
							requirements.addRequirement(be.ability.getCatalystRequirements(be, be.level + 1));
							modification = true;
						}
					}
					
					changeMade = modification;
					if (!modification)
					{
						//ItemFactory.setItemData(product, ItemFactory.getItemData(applying));
					}
					else
					{
						ItemStack preprod = product.clone();
						String ench = ItemFactory.flattenEnchMap(enchantmentTypes);
						ItemFactory.addItemData(product, "enchants", ench);
						
						if (ItemFactory.sumEnchantPower(ItemFactory.getItemEnchants(product)) > apply.stats.getStat(Stat.ENCHANT_POWER) && apply.type != ItemType.ENCHANTMENT)
						{
							changeMade = false;
							product = preprod;
						}
					}
					
				}
				
				appliable = false;
				Item bookItem = ItemFactory.getItem(book);
				if (bookItem != null && bookItem.type == ItemType.ATTUNER && apply.type.use == ItemTypeUse.WIELDABLE)
				{
					List<ItemAttuner> attuners = ItemFactory.getAttuners(applying);
					if (attuners.size() < 5)
					{
						ItemStack preprod = product.clone();
						String flatAttuners = ItemFactory.getItemData(product).get("attuners");
						flatAttuners += "," + bookItem.id;
						
						ItemFactory.addItemData(preprod, "attuners", flatAttuners);
						changeMade = true;
						product = preprod;
					}
				}
				
				
				// final production thing after all checks.
				if (changeMade)
				{
					ItemFactory.update(product, null);
					
					StringBuilder lore = new StringBuilder("GRAYYou need the following in order to;GRAYcombine these two items together!;;GRAYRequires:");
					
					if (requirements.coins != 0) lore.append(";").append(StringUtils.getPrettyCoins(requirements.coins));
					else if (requirements.reqs.isEmpty()) lore = new StringBuilder("GREENThis interaction has no requirements!");
					for (String item : requirements.getItems())
					{
						lore.append(";").append(ItemFactory.getItemTypeName(item)).append(" DARK_GRAYx").append(requirements.getAmount(item));
					}
					
					ItemStack anvil = ItemFactory.buildCustom("ANVIL", "GREENClick!", lore.toString());
					
					update(true);
					inventory.setSlot(product, calc(4, 1));
					inventory.setSlot(anvil, calc(4, 4));
					inventory.update();
				}
				else update(false);
			}
			else update(false);
			
			
			inventory.update();
		}
		return false;
	}
	
	

}
