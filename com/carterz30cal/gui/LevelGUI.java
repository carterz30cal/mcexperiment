package com.carterz30cal.gui;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.Recipe;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.utils.LevelUtils;

public class LevelGUI extends AbstractGUI
{
	public boolean[][] pattern = {
			{true,  false, false, false, false},
			{true,   true,  true,  true,  true},
			{false, false, false, false, true},
			{true,   true,  true,  true,  true}}; 
	public int[] pInc = {1, 5, 1, 5};
	public int offset;
	public LevelGUI(GamePlayer owner) {
		super(owner);
		
		inventory = new GooeyInventory("Levels", 6);
		
		int level = 0;
		while (level < owner.getLevel())
		{
			level += pInc[offset % pattern.length];
			offset++;
		}
		if (offset > 1) offset -= 2;
		
		update();
	}
	
	private void update()
	{
		int level = 0;
		for (int c = 0; c < offset + 9; c++)
		{
			if (c - offset < 0) level += pInc[c % pattern.length];
			else
			{
				if (c % pattern.length == 3)
				{
					for (int r = 4; r >= 0; r--)
					{
						int pos = r * 9 + (c - offset);
						if (pattern[c % pattern.length][r])
						{
							level++;
							inventory.setSlot(getLevelPane(level), pos);
						}
						else inventory.setSlot(GooeyInventory.produceElement("BLACK_STAINED_GLASS_PANE", " "), pos);
					}
				}
				else
				{
					for (int r = 0; r < 5; r++)
					{
						int pos = r * 9 + (c - offset);
						if (pattern[c % pattern.length][r])
						{
							level++;
							inventory.setSlot(getLevelPane(level), pos);
						}
						else inventory.setSlot(GooeyInventory.produceElement("BLACK_STAINED_GLASS_PANE", " "), pos);
					}
				}
				
				
				inventory.setSlot(GooeyInventory.produceElement("WHITE_STAINED_GLASS_PANE", " "), calc(c - offset, 5));
			}
		}
		
		if (offset > 0) inventory.setSlot(GooeyInventory.produceElement("ARROW", "REDGo Back"), calc(0, 5));
		inventory.setSlot(GooeyInventory.produceElement("ARROW", "GREENNext"), calc(8, 5));
		
		inventory.update();
	}
	
	private ItemStack getLevelPane(int level)
	{
		if (level > LevelUtils.LEVEL_MAX) return GooeyInventory.produceElement("BLACK_STAINED_GLASS_PANE", " ");
		
		boolean hasLevel = level <= owner.getLevel();
		boolean nextLevel = level - 1 == owner.getLevel();
		String name = hasLevel ? "GREEN" : (nextLevel) ? "YELLOW" : "RED";
		String lore = hasLevel ? "GRAYYou have unlocked the following perks:;" : "GRAYReaching this level will;GRAYunlock the following perks:;";
		String mat = hasLevel ? "LIME_STAINED_GLASS_PANE" : (nextLevel) ? "YELLOW_STAINED_GLASS_PANE" : "RED_STAINED_GLASS_PANE";
		
		
		if (level % 5 == 0)
		{
			lore += " BLUE+1 " + Stat.POWER.getReverse() + ";";
			lore += " GREEN+2 " + Stat.DEFENCE.getReverse() + ";";
		}
		else lore += " RED+8 " + Stat.HEALTH.getReverse() + ";";
		if (level > 1) {
			if (level % 8 == 0) lore += " WHITE+2000 Sack Space;";
			else lore += " WHITE+500 Sack Space;";
		}
		if (level == 5) lore += " WHITE+1 Forge Slot;";
		
		int totXp = LevelUtils.getXpForLevel(level);
		
		if (hasLevel)
		{
			int recipes =  ItemFactory.levelRecipes.getOrDefault(level, new ArrayList<>()).size();
			if (recipes > 0) lore += "GRAYand the following recipes:;";
			for (Recipe r : ItemFactory.levelRecipes.getOrDefault(level, new ArrayList<>()))
			{
				if (r.hideInLevelMenu) continue;
				String n = r.customName != null ? r.customName : ItemFactory.getItemTypeName(r.item);
				lore += "DARK_GRAY- GRAY" + n + ";";
				//lore += "DARK_GRAY+GRAY" + n + " GOLDRecipe;";
			}
			
			if (level == LevelUtils.LEVEL_MAX) 
			{
				mat = "GOLD_BLOCK";
				lore += ";GOLDMax level achieved, congrats!";
			}
		}
		else
		{
			if (level == LevelUtils.LEVEL_MAX) mat = "COAL_BLOCK";
			
			int recipes = ItemFactory.levelRecipes.getOrDefault(level, new ArrayList<>()).size();
			if (recipes == 1) lore += "GOLD +1 Recipe;";
			else if (recipes > 1) lore += "GOLD +" + recipes + " Recipes;";
			
			if (nextLevel)
			{
				int gotXp = owner.xp;
				
				lore += ";AQUA" + gotXp + " / " + totXp + "XP BLUE(AQUA" + (int)(owner.getLevelProgress() * 1000)/10 + "%BLUE)";
			}
			else lore += ";AQUA" + totXp + "XP BLUErequired.";
		}
		
		
		return ItemFactory.buildCustom(mat, name + "Level " + level, lore);
	}
	
	public boolean allowClick(int clickPos, ItemStack clicked)
	{
		if (clickPos == calc(0, 5) && offset > 0) offset--;
		else if (clickPos == calc(8, 5)) offset++;
		
		update();
		return false;
	}

}
