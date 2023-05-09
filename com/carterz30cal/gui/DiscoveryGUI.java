package com.carterz30cal.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.Collection;
import com.carterz30cal.items.DiscoveryManager;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.Recipe;
import com.carterz30cal.utils.StringUtils;

public class DiscoveryGUI extends AbstractGUI {
	public int page = 1;
	public String selected;
	public DiscoveryGUI(GamePlayer owner) {
		super(owner);
		
		inventory = new GooeyInventory("Discoveries", 6);
		inventory.initUsingTemplate(GooeyTemplate.SHOPPY);
		
		selected = null;
		
		update();
	}
	
	public void update()
	{
		if (selected == null)
		{
			List<Collection> discoveries = DiscoveryManager.getAll();
			
			int select = (page - 1) * 28;
			for (int i = 0; i < 28; i++)
			{
				int o = select + i;
				int x = i % 7 + 1;
				int y = i / 7 + 1;
				
				if (o < discoveries.size())
				{
					Collection discovery = discoveries.get(o);
					int level = owner.getDiscoveryLevel(discovery);
					
					ItemStack disp;
					if (level == 0 && owner.discoveries.getOrDefault(discovery.id, 0) == 0)
					{
						disp = GooeyInventory.produceElement("BARRIER", "REDDiscovery locked!");
					}
					else
					{
						boolean maxed = level == discovery.tiers.size();
						
						String colour = maxed ? "GREEN" : level == 0 ? "RED" : "YELLOW";
						disp = ItemFactory.buildCustom(discovery.displayItem, colour + discovery.name + " WHITE[" + colour + level + "WHITE]", null);
						ItemMeta meta = disp.getItemMeta();
						
						List<String> lore = StringUtils.colourList(discovery.description);
						lore.add("");
						if (!maxed)
						{
							double progress = (double)owner.discoveries.getOrDefault(discovery.id, 0) / discovery.tiers.get(level);
							
							lore.add("GRAYNext tier rewards:");
							for (String recipe : discovery.recipes.getOrDefault(level + 1, new ArrayList<>())) 
							{
								Recipe r = ItemFactory.recipes.get(recipe);
								
								String n = r.customName != null ? r.customName : ItemFactory.getItemTypeName(r.item);
								lore.add("DARK_GRAY- " + n + " DARK_GRAY[Recipe]");
							}
							if (discovery.xpRewards.get(level) != 0) lore.add("DARK_GRAY- AQUA+" + discovery.xpRewards.get(level) + "XP");
							lore.add("");
							lore.add(StringUtils.progressBar(
									progress, 35, 
									ChatColor.GREEN, ChatColor.DARK_GRAY) + " GREEN" + 
									StringUtils.asPercent(progress) + " WHITE[" + colour + 
									owner.discoveries.getOrDefault(discovery.id, 0) + "WHITE/GREEN" + 
									discovery.tiers.get(level) + "WHITE]");
						}
						else
						{
							lore.add("GOLDMAXED");
						}
						
						meta.setLore(StringUtils.colourList(lore));
						disp.setItemMeta(meta);
					}
					
					inventory.setSlot(disp, calc(x, y));
				}
				else inventory.setSlot(null, calc(x, y));
			}
		}
		
		
		inventory.update();
	}

}
