package com.carterz30cal.gui;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.*;
import com.carterz30cal.items.discoveries.Collection;
import com.carterz30cal.items.discoveries.DiscoveryManager;
import com.carterz30cal.items.recipes.Recipe;
import com.carterz30cal.items.recipes.RecipeCategory;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.StringUtils;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.kyori.adventure.text.Component.text;

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
			else if (i % 9 > 6) {
                inventory.setSlot(getForgingItem(f), i);
                f++;
            }
            else {
                inventory.setSlot(ItemFactory.customItem("WHITE_STAINED_GLASS_PANE",
                                " "),
                        i);
            }
		}
		
		if (category != ItemFactory.baseCategory) 
		{
			inventory.setSlot(createCategoryDisplay(category.id, false), calc(3, 0));
            inventory.setSlot(ItemFactory.customItem("ARROW", "<red>Back</red>"), calc(3, 5));
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
        if (page > 1) {
            inventory.setSlot(ItemFactory.customItem("ARROW",
                            "<green>Page " + (page - 1) + "</green>"),
                    calc(1, 5));
        }
		if (count > page * 20)
		{
            inventory.setSlot(ItemFactory.customItem("ARROW",
                            "<green>Page " + (page + 1) + "</green>"),
                    calc(5, 5));
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
        boolean inUse = f >= owner.forge.size();
        if (inUse)
		{
            if (f < owner.getForgeSlots()) {
                return ItemFactory.customItem("ORANGE_STAINED_GLASS_PANE", "<#cc5500>Slot available!</#cc5500>");
            }
            else {
                return ItemFactory.customItem("RED_STAINED_GLASS_PANE", "<red>Slot locked!</red>");
            }
		}
		else 
		{
			ItemStack p = owner.forge.get(f).produce();
            if (!p.hasItemMeta()) {
                return p;
            }
            p.editMeta(meta -> {
                var lore = meta.lore();
                assert lore != null;
                lore.add(text().build());
                lore.add(text().content("Time Remaining:").color(NamedTextColor.WHITE).append(
                        text(StringUtils.getPrettyTime(owner.forge.get(f).finished))
                ).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE).build());
                meta.lore(lore);
            });
			
			return p;
		}
	}
	
	public ItemStack createRecipeDisplay(String recipe)
	{
		Recipe rec = ItemFactory.recipes.get(recipe);
		
		long pLevel = owner.getLevel();
		
		List<String> requirements = new ArrayList<>();
        if (rec.levelRequirement > pLevel) {
            requirements.add("<grey>This recipe unlocks at <white>Level " + rec.levelRequirement + "</white>.</grey>");
        }
		if (rec.discoveryReq != null)
		{
			Collection col = DiscoveryManager.get(rec.discoveryReq);
			if (owner.getDiscoveryLevel(col) < rec.discoveryReqLevel + 1)
			{
                requirements.add("<grey>This recipe unlocks at <white>" + col.name + " " + (rec.discoveryReqLevel + 1) + "</white>.</grey>");
			}
		}

        if (!requirements.isEmpty())
		{
            return ItemFactory.customItem("RED_STAINED_GLASS_PANE",
                    "<red>Recipe locked!</red>", requirements);
		}
		
		ItemStack base = ItemFactory.build(rec.item, rec.amount);
        if (base == null) {
            Dungeons.instance.getLogger().severe("ForgeGUI: could not find item: " + rec.item);
            return ItemFactory.customItem("BARRIER",
                    "<dark_red>An error has occurred, please try again later.</dark_red>", requirements);
        }
		ItemFactory.setItemData(base, "enchants:" + rec.enchants);
        ItemFactory.update(base, owner.getItemContext());
		ItemFactory.makeInvalid(base);

        List<String> lore = new ArrayList<>();
		
		lore.add("");
        lore.add(rec.time == 0 ? "<white>Time: <green>Instant</green></white>" : "<white>Time:" + StringUtils.getPrettyTime(rec.time));
        lore.add("<white><b>Requirements: </b></white>");
        if (rec.coinCost != 0) {
            lore.add("<dark_grey>-</dark_grey> <gold>" +
                    (rec.coinCost == 1 ? "1 coin" : rec.coinCost + " coins") + "</gold>");
        }
        for (String recipeIngredient : rec.items.keySet())
		{
            var ingredient = ItemFactory.getItem(recipeIngredient);
            var rarityColour = ingredient.rarity.textColor.asHexString() + ">";

			StringBuilder builder = new StringBuilder();
            builder.append("<dark_grey>- <");
            builder.append(rarityColour);
            builder.append(ingredient.name);
            builder.append("</");
            builder.append(rarityColour);
            builder.append(" <dark_grey>x");
            builder.append(rec.items.get(recipeIngredient));
            if (ingredient.type == ItemType.INGREDIENT) {
                int sack = owner.sack.getOrDefault(recipeIngredient, 0);
                int total = sack;
                for (var checking : owner.player.getInventory().getContents()) {
                    var check = ItemFactory.getItem(checking);
                    if (check == null || !check.id.equals(recipeIngredient)) {
                        continue;
                    }
                    total += checking.getAmount();
                }
                builder.append(" [You have ");
                if (total >= rec.items.get(recipeIngredient)) {
                    if (sack == total) {
                        builder.append("<green>");
                        builder.append(sack);
                        builder.append("</green> in your sack]");
                    }
                    else {
                        builder.append("<green>");
                        builder.append(total);
                        builder.append("</green> in total, <green>");
                        builder.append(sack);
                        builder.append("</green> are in your sack]");
                    }
                }
                else {
                    if (total == 0) {
                        builder.append("<red>none</red>]</dark_grey>");
                    }
                    else if (sack == total) {
                        builder.append("<red>");
                        builder.append(sack);
                        builder.append("</red> in your sack]");
                    }
                    else {
                        builder.append("<red>");
                        builder.append(total);
                        builder.append("</red> in total, <red>");
                        builder.append(sack);
                        builder.append("</red> are in your sack]");
                    }
                }
			}
			lore.add(builder.toString());
		}

        base.editMeta(meta -> {
            var listed = meta.lore();
            for (var line : lore) {
                var component = MiniMessage.miniMessage().deserialize(line).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
                assert listed != null;
                listed.add(component);
            }

            if (rec.amount > 1) {
                meta.displayName(text(rec.amount + "x ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE).append(Objects.requireNonNull(meta.displayName())));
            }
            meta.lore(listed);
        });
		
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

        List<String> listed = new ArrayList<>();
        String title = "<grey>Unlocked Recipes: " +
                (unlocked == 0 ? "<red>" : (unlocked == categoryRecipes.size() ? "<green>" : "<yellow>")) +
                unlocked + "<grey>/</grey><green>" + categoryRecipes.size();
        listed.add(title);
        if (!cat.description.isEmpty()) {
            listed.add("");
            listed.addAll(cat.description);
        }
        if (clickPrompt) {
            listed.add("");
            listed.add("<yellow>Click to view recipes!</yellow>");
        }

		if (unlocked == 0) return null;
        return ItemFactory.customItem(cat.icon,
                cat.name,
                listed);
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
                        owner.sendMessage("<red>You haven't unlocked this recipe yet!</red>");
						owner.playSound(Sound.ENTITY_CREEPER_HURT, 0.4, 0.9);
					}
					else if (owner.isForgeFull() && recipe.time != 0)
					{
                        owner.sendMessage("<red>You have no free forge slots available!</red>");
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
                            if (recipe.enchants == null && !data.isEmpty()) {
                                item.data = data;
                            }
							owner.scheduleForgeItem(item);
						}
						else 
						{
                            owner.sendMessage("<red>Cannot forge, requirements not met!</red>");
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
