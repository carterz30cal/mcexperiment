package com.carterz30cal.gui;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.recipes.Recipe;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.utils.LevelUtils;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

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
                        if (pattern[c % pattern.length][r]) {
                            level++;
                            inventory.setSlot(getLevelPane(level), pos);
                        }
                        else {
                            inventory.setSlot(
                                    ItemFactory.customItem("BLACK_STAINED_GLASS_PANE", " "),
                                    pos
                            );
                        }
					}
				}
				else
				{
					for (int r = 0; r < 5; r++)
					{
						int pos = r * 9 + (c - offset);
                        if (pattern[c % pattern.length][r]) {
                            level++;
                            inventory.setSlot(getLevelPane(level), pos);
                        }
                        else {
                            inventory.setSlot(
                                    ItemFactory.customItem("BLACK_STAINED_GLASS_PANE", " "),
                                    pos
                            );
                        }
					}
				}

                inventory.setSlot(
                        ItemFactory.customItem("WHITE_STAINED_GLASS_PANE", " "),
                        calc(c - offset, 5)
                );
			}
		}
        if (offset > 0) {
            inventory.setSlot(
                    ItemFactory.customItem("ARROW", "<red>Back</red>"),
                    calc(0, 5)
            );
        }
        inventory.setSlot(
                ItemFactory.customItem("ARROW", "<green>Next</green>"),
                calc(8, 5)
        );
		
		inventory.update();
	}
	
	private ItemStack getLevelPane(int level)
	{
        if (level > LevelUtils.LEVEL_MAX) {
            return ItemFactory.customItem("BLACK_STAINED_GLASS_PANE", " ");
        }
		
		boolean hasLevel = level <= owner.getLevel();
		boolean nextLevel = level - 1 == owner.getLevel();

        var paneName = text()
                .color(hasLevel ? NamedTextColor.GREEN
                        : (nextLevel ? NamedTextColor.YELLOW : NamedTextColor.RED))
                .content("Level " + level);
        List<TextComponent.Builder> loreList = new ArrayList<>();

		String name = hasLevel ? "GREEN" : (nextLevel) ? "YELLOW" : "RED";
		String lore = hasLevel ? "GRAYYou have unlocked the following perks:;" : "GRAYReaching this level will;GRAYunlock the following perks:;";
        String mat = hasLevel ? "LIME_STAINED_GLASS_PANE"
                : (nextLevel) ? "YELLOW_STAINED_GLASS_PANE" : "RED_STAINED_GLASS_PANE";
		
		
		if (level % 5 == 0)
		{
            loreList.add(
                    text().content(" +1 ").append(Stat.POWER.getReversed()).color(NamedTextColor.BLUE)
            );
            loreList.add(
                    text().content(" +2 ").append(Stat.DEFENCE.getReversed()).color(NamedTextColor.GREEN)
            );
		}
        else {
            loreList.add(
                    text().content(" +8 ").append(Stat.HEALTH.getReversed()).color(NamedTextColor.RED)
            );
        }
		if (level > 1) {
            if (level % 8 == 0) {
                loreList.add(
                        text().content(" +5000 Sack Capacity").color(NamedTextColor.WHITE)
                );
            }
            else {
                loreList.add(
                        text().content(" +1000 Sack Capacity").color(NamedTextColor.WHITE)
                );
            }
		}
        if (level == 5) {
            loreList.add(
                    text().content(" +2 Forge Slots").color(NamedTextColor.WHITE)
            );
        }
		
		long totXp = LevelUtils.getXpForLevel(level);
		
		if (hasLevel)
		{
            loreList.addFirst(
                    text().content("You have unlocked the following perks:").color(NamedTextColor.WHITE)
            );
            int recipes = ItemFactory.levelRecipes.getOrDefault(level, new ArrayList<>()).size();
            if (recipes > 0) {
                loreList.add(
                        text().content("and the following recipes:").color(NamedTextColor.WHITE)
                );
            }
			for (Recipe r : ItemFactory.levelRecipes.getOrDefault(level, new ArrayList<>()))
			{
				if (r.hideInLevelMenu) continue;
                if (r.customName != null) {
                    loreList.add(
                            text().content("- ").color(NamedTextColor.DARK_GRAY).append(MiniMessage.miniMessage().deserialize(r.customName))
                    );
                }
                else {
                    loreList.add(
                            text().content("- ").color(NamedTextColor.DARK_GRAY).append(
                                    ItemFactory.getItemNameBuilder(r.item)
                            )
                    );
                }
			}
			
			if (level == LevelUtils.LEVEL_MAX) 
			{
				mat = "GOLD_BLOCK";
                loreList.add(
                        text()
                );
                loreList.add(
                        text().content("Max level achieved, congrats!").color(NamedTextColor.GOLD)
                );
			}
		}
		else
		{
            loreList.addFirst(
                    text().content("Reaching this level will unlock the following perks:").color(NamedTextColor.WHITE)
            );
			if (level == LevelUtils.LEVEL_MAX) mat = "COAL_BLOCK";
			
			int recipes = ItemFactory.levelRecipes.getOrDefault(level, new ArrayList<>()).size();
            loreList.add(
                    text()
                            .content(recipes == 1 ?
                                    " +1 recipe" : " +" + recipes + " recipes"
                            ).color(NamedTextColor.GOLD)
            );
			
			if (nextLevel)
			{
				long gotXp = owner.xp;
                loreList.add(
                        text().append(
                                MiniMessage.miniMessage().deserialize(
                                        "<aqua>"
                                                + gotXp
                                                + " / "
                                                + totXp
                                                + "XP <blue>(</blue>"
                                                + (int) (owner.getLevelProgress() * 1000) / 10
                                                + "%<blue>)</blue></aqua>"
                                )
                        )
                );
			}
            else {
                loreList.add(
                        text().append(
                                text(totXp + "XP required").color(NamedTextColor.AQUA)
                        )
                );
            }
		}

        return ItemFactory.customItem(mat, paneName, loreList);
	}
	
	public boolean allowClick(int clickPos, ItemStack clicked)
	{
		if (clickPos == calc(0, 5) && offset > 0) offset--;
		else if (clickPos == calc(8, 5)) offset++;
		
		update();
		return false;
	}

}
