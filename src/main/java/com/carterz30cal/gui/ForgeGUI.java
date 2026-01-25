package com.carterz30cal.gui;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.*;
import com.carterz30cal.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ForgeGUI extends AbstractGUI 
{
	public RecipeCategory category;
	public int page = 1;
	private boolean allowNextPage = false;
	
	public String[] categories;
	public String[] recipes;
	
	
	private int refresh;
	
	public ForgeGUI(GamePlayer owner) {
		super(owner);
		
		inventory = new GooeyInventory("Forge", 6);
		
		
		
		category = ItemFactory.baseCategory;
		update();
		inventory.update();
	}
	
	public void moveTo(String n)
	{
		category = ItemFactory.categories.get(n);
		page = 1;
		
		update();
	}
	
	public void update()
	{
		int f = 0;
		for (int i = 0; i < 54; i++)
		{
			if (i / 9 != 0 && i / 9 != 5 && i % 9 != 0 && i % 9 < 6) inventory.setSlot(null, i);
			else if (i % 9 > 6) 
			{
				inventory.setSlot(getForgingItem(f), i);
				f++;
			}
			else inventory.setSlot(GooeyInventory.produceElement("WHITE_STAINED_GLASS_PANE", " "), i);
		}
		
		if (category != ItemFactory.baseCategory) 
		{
			inventory.setSlot(createCategoryDisplay(category.id, false), calc(3, 0));
			inventory.setSlot(GooeyInventory.produceElement("ARROW", "REDBack"), calc(3, 5));
		}
		
		
		categories = new String[54];
		recipes = new String[54];
		
		int i = 0;
		int g = 0;
		for (int sc = (page - 1) * 20; sc < category.subcategories.size() && i < 20; sc++)
		{
			ItemStack cat = createCategoryDisplay(category.subcategories.get(sc), true);
			if (cat == null) 
			{
				g++;
				continue;
			}
			inventory.setSlot(cat, within(i));
			categories[within(i)] = category.subcategories.get(sc);
			
			i++;
		}
		
		int o = page > 1 ? category.subcategories.size() : 0;
		if (page == 1) g = 0;
		
		for (int r = (page - 1) * 20; r < category.recipes.size() + o - g && i < 20; r++)
		{
			inventory.setSlot(createRecipeDisplay(category.recipes.get(r + g - o)), within(i));
			recipes[within(i)] = category.recipes.get(r + g - o);
			
			i++;
		}
		
		int count = category.recipes.size() + category.subcategories.size() - g;
		if (page > 1) inventory.setSlot(GooeyInventory.produceElement("ARROW", "GREENPage " + (page-1)), calc(1, 5));
		if (count > page * 20)
		{
			inventory.setSlot(GooeyInventory.produceElement("ARROW", "GREENPage " + (page+1)), calc(5, 5));
			allowNextPage = true;
		}
		else allowNextPage = false;
		
		inventory.update();
	}
	
	private int within(int i)
	{
		int x = (i % 5) + 1;
		int y = (i / 5) + 1;
		return (y * 9) + x;
	}
	
	
	public void onTick()
	{
		refresh++;
		if (refresh % 20 == 0) update();
	}
	
	public ItemStack getForgingItem(int f)
	{
		boolean summin = f >= owner.forge.size();
		if (summin)
		{
			if (f < owner.getForgeSlots()) return GooeyInventory.produceElement("ORANGE_STAINED_GLASS_PANE", "GOLDSlot available!");
			else return GooeyInventory.produceElement("RED_STAINED_GLASS_PANE", "REDSlot locked!");
		}
		else 
		{
			ItemStack p = owner.forge.get(f).produce();
			ItemMeta m = p.getItemMeta();
			List<String> lore = m.getLore();
			
			lore.add("");
			lore.add("WHITETime Remaining: " + StringUtils.getPrettyTime(owner.forge.get(f).finished));
			
			m.setLore(StringUtils.colourList(lore));
			p.setItemMeta(m);
			
			return p;
		}
	}
	
	public ItemStack createRecipeDisplay(String recipe)
	{
		Recipe rec = ItemFactory.recipes.get(recipe);
		
		long pLevel = owner.getLevel();
		
		List<String> requirements = new ArrayList<>();
		if (rec.levelRequirement > pLevel) requirements.add("GRAYThis recipe unlocks at WHITELevel " + rec.levelRequirement + "GRAY.");
		if (rec.discoveryReq != null)
		{
			Collection col = DiscoveryManager.get(rec.discoveryReq);
			if (owner.getDiscoveryLevel(col) < rec.discoveryReqLevel + 1)
			{
				requirements.add("GRAYThis recipe unlocks at WHITE" + col.name + " " + (rec.discoveryReqLevel + 1) + "GRAY.");
			}
		}
		
		if (requirements.size() != 0) 
		{
            ItemStack r = ItemFactory.buildCustom("RED_STAINED_GLASS_PANE", "REDRecipe locked!");
			ItemMeta m = r.getItemMeta();
			
			m.setLore(StringUtils.colourList(requirements));
			r.setItemMeta(m);
			return r;
		}
		
		ItemStack base = ItemFactory.build(rec.item, rec.amount);
		ItemFactory.setItemData(base, "enchants:" + rec.enchants);
		ItemFactory.update(base, null);
		ItemFactory.makeInvalid(base);
		
		ItemMeta meta = base.getItemMeta();
		List<String> lore = meta.getLore();
		if (lore == null) lore = new ArrayList<>();
		
		lore.add("");
		lore.add(rec.time == 0 ? "WHITETime: GREENInstant" : "WHITETime:WHITE" + StringUtils.getPrettyTime(rec.time));
		lore.add("WHITEBOLDRequirements: ");
		if (rec.coinCost != 0) lore.add("DARK_PURPLE- " + (rec.coinCost == 1 ? "GOLD1 Coin" : "GOLD" + rec.coinCost + " Coins"));
		for (String item : rec.items.keySet())
		{
			int amountInSack = owner.sack.getOrDefault(item, 0);

			StringBuilder builder = new StringBuilder();
			builder.append("- ");
			builder.append(ItemFactory.getItemTypeName(item));
			builder.append(" DARK_GRAYx");
			builder.append(rec.items.get(item));
			if (ItemFactory.getItem(item).type == ItemType.INGREDIENT) {
				builder.append("  [Sack has x");
				if (amountInSack >= rec.items.get(item)) builder.append("GREEN");
				else builder.append("RED");

				builder.append(amountInSack);
				builder.append("DARK_GRAY]");
			}

			lore.add(builder.toString());
		}
		
		if (rec.amount > 1) meta.setDisplayName(meta.getDisplayName() + " " + ChatColor.DARK_GRAY + "x" + rec.amount);
 		meta.setLore(StringUtils.colourList(lore));
		base.setItemMeta(meta);
		
		return base;
	}
	
	public ItemStack createCategoryDisplay(String category, boolean clickPrompt)
	{
		RecipeCategory cat = ItemFactory.categories.get(category);
		
		List<Recipe> categoryRecipes = cat.getRecipes(true);
		long pLevel = owner.getLevel();
		int unlocked = 0;
		for (Recipe recipe : categoryRecipes)
		{
			boolean collectionUnlocked = true;
			if (recipe.discoveryReq != null)
			{
				Collection col = DiscoveryManager.get(recipe.discoveryReq);
				if (owner.getDiscoveryLevel(col) < recipe.discoveryReqLevel + 1)
				{
					collectionUnlocked = false;
				}
			}
			
			if (recipe.levelRequirement <= pLevel && collectionUnlocked) unlocked++;
		}
		
		String colour = unlocked == 0 ? "RED" : (unlocked == categoryRecipes.size() ? "GREEN" : "YELLOW");
		String lore = "GRAYUnlocked Recipes: " + colour + unlocked + "WHITE/GREEN" + categoryRecipes.size();
		for (String d : cat.description) lore += ";GRAY" + d;
		if (clickPrompt) lore += ";;YELLOWClick to view recipes!";
		
		if (unlocked == 0) return null;
		return ItemFactory.buildCustom(cat.icon, cat.name, lore);
	}
	
	public boolean allowClick(int clickPos, ItemStack clicked)
	{
		if (clickPos >= 54) return false;
		
		if (clickPos == calc(3, 5) && category != ItemFactory.baseCategory) moveTo(category.parent);
		else if (clickPos == calc(1, 5) && page > 1) 
		{
			page--;
			update();
		}
		else if (clickPos == calc(5, 5) && allowNextPage)
		{
			page++;
			update();
		}
		else 
		{
			String clickCat = categories[clickPos];
			if (clickCat != null) moveTo(clickCat);
			else
			{
				String clickRecipe = recipes[clickPos];
				
				if (clickRecipe != null)
				{
					Recipe recipe = ItemFactory.recipes.get(clickRecipe);
					
					boolean collectionUnlocked = true;
					if (recipe.discoveryReq != null)
					{
						Collection col = DiscoveryManager.get(recipe.discoveryReq);
						if (owner.getDiscoveryLevel(col) < recipe.discoveryReqLevel + 1)
						{
							collectionUnlocked = false;
						}
					}
					
					
					if (recipe.levelRequirement > owner.getLevel() || !collectionUnlocked)
					{
						owner.sendMessage("REDYou haven't unlocked this recipe yet!");
						owner.playSound(Sound.ENTITY_CREEPER_HURT, 0.4, 0.9);
					}
					else if (owner.isForgeFull() && recipe.time != 0)
					{
						owner.sendMessage("REDYou have no free forge slots available!");
						owner.playSound(Sound.ENTITY_CREEPER_HURT, 0.4, 0.9);
					}
					else
					{
						ItemReqs requirements = new ItemReqs();
						requirements.coins = recipe.coinCost;
						for (String i : recipe.items.keySet()) requirements.addRequirement(new ItemReq(i, recipe.items.get(i)));
						
						if (requirements.areRequirementsMet(owner))
						{
							String data = requirements.grabDataFromRequirements(owner);
							requirements.execute(owner);
							
							owner.playSound(Sound.BLOCK_ANVIL_USE, 0.9, 1.1);
							
							ForgingItem item = new ForgingItem(recipe);
							if (recipe.enchants == null && !data.equals("")) item.data = data;
							owner.scheduleForgeItem(item);
						}
						else 
						{
							//for (String k : requirements.reqs.keySet()) System.out.println(k);
							owner.sendMessage("REDCannot forge, requirements not met.");
							owner.playSound(Sound.ENTITY_CREEPER_HURT, 0.8, 0.6);
						}
					}
					
					
					
				}
				
			}
			update();
		}
		
		return false;
	}

}
