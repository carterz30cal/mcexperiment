package com.carterz30cal.gui;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.*;
import com.carterz30cal.items.abilities.Abilities;
import com.carterz30cal.items.abilities.implementation.GameAbstractEnchant;
import com.carterz30cal.items.types.ItemAttuner;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
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
        for (int i = 2; i < 7; i++) inventory.setSlot(ItemFactory.customItem(ty, " "), calc(i, 4));
        for (int i = 2; i < 5; i++) inventory.setSlot(ItemFactory.customItem(ty, " "), calc(4, i));
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
                boolean valid = true;
				
				for (var enchant : bookEnchants)
				{
                    if (!(enchant.ability instanceof GameAbstractEnchant enchantment)) {
                        continue;
                    }
                    if (enchantment.getValidTypes().contains(apply.type)) {
                        continue;
                    }

                    valid = false;
					break;
				}
				
				// check if any enchants actually get applied or increased
                if (valid || apply.type == ItemType.ENCHANTMENT)
				{
					Map<Abilities, Integer> enchantmentTypes = new HashMap<>();
                    for (var e : applyingEnchants) {
                        if (!(e.ability instanceof GameAbstractEnchant enchantment)) {
                            continue;
                        }
                        enchantmentTypes.put(enchantment.source, e.level);
                    }
					
					boolean modification = false;
					for (var be : bookEnchants)
					{
                        if (!(be.ability instanceof GameAbstractEnchant enchant)) {
                            continue;
                        }
                        Abilities source = enchant.source;
						if (!enchantmentTypes.containsKey(source) || enchantmentTypes.get(source) < be.level)
						{
							enchantmentTypes.put(source, be.level);
                            requirements.addRequirement(enchant.getCatalystRequirements(be, be.level));
							modification = true;
						}
                        else if (enchantmentTypes.get(source) == be.level && be.level < enchant.getMaximumLevel())
						{
							enchantmentTypes.put(source, be.level + 1);
                            requirements.addRequirement(enchant.getCatalystRequirements(be, be.level + 1));
							modification = true;
						}
					}
					
					changeMade = modification;
                    if (modification)
					{
						ItemStack preprod = product.clone();
                        String enchants = ItemFactory.flattenEnchMap(enchantmentTypes);
                        ItemFactory.addItemData(product, "enchants", enchants);

                        if (ItemFactory.sumEnchantPower(ItemFactory.getItemEnchants(product)) > apply.stats.stat(Stat.ENCHANT_POWER) && apply.type != ItemType.ENCHANTMENT)
						{
							changeMade = false;
							product = preprod;
						}
					}
					
				}

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

                    List<String> list = new ArrayList<>();
                    if (requirements.coins == 0 && requirements.reqs.isEmpty()) {
                        list.add("<grey>This interaction has no requirements!");
                    }
                    else {
                        list.add("<grey>To combine these items, you</grey>");
                        list.add("<grey>will need the following:</grey>");
                        list.add("");
                        list.add("<grey>Requires:</grey>");
                        if (requirements.coins > 0) {
                            list.add("<gold>" + StringUtils.addCommas(requirements.coins) +
                                    (requirements.coins == 1 ? " coin" : " coins") + "</gold>");
                        }
                        for (var ingredient : requirements.getItems()) {
                            var item = ItemFactory.getItem(ingredient);
                            var colour = item.rarity.textColor.asHexString() + ">";
                            list.add("<dark_grey>- <" + colour + item.name + "</" + colour + " x" + requirements.getAmount(ingredient) + "</dark_grey>");
                        }
                    }

                    var anvil = ItemFactory.customItem("ANVIL", "<green>Click!</green>", list);
					
					update(true);
					inventory.setSlot(product, calc(4, 1));
					inventory.setSlot(anvil, calc(4, 4));
				}
				else update(false);
			}
			else update(false);

            inventory.update();
		}
		return false;
	}
	
	

}
