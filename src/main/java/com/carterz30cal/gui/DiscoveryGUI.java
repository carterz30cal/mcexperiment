package com.carterz30cal.gui;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.Collection;
import com.carterz30cal.items.DiscoveryManager;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.Recipe;
import com.carterz30cal.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class DiscoveryGUI extends AbstractGUI {
	public int page = 1;
	public String selected;
	public String[] selectable;

	public final Integer[][] positions = {
			{},
			{4},
			{3,5},
			{3,4,5},
			{2,3,5,6},
			{2,3,4,5,6},
			{1,2,3,5,6,7},
			{1,2,3,4,5,6,7}
	};

	public DiscoveryGUI(GamePlayer owner) {
		super(owner);
		
		inventory = new GooeyInventory("Discoveries", 6);
		inventory.initUsingTemplate(GooeyTemplate.SHOPPY);
		
		selected = null;
		selectable = new String[54];
		
		update();
	}

	public boolean allowClick(int clickPos, ItemStack clicked)
	{
		if (selected == null) {
			selected = selectable[clickPos];
			update();
		}
		else if (clickPos == calc(4, 5)){
			selected = null;
			update();
		}
		return false;
	}
	
	public void update()
	{
		inventory.initUsingTemplate(GooeyTemplate.PANED_DARK);
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
					if (level == 0 && owner.discoveries.getOrDefault(discovery.id, 0L) == 0)
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
							double progress = (double)owner.discoveries.getOrDefault(discovery.id, 0L) / discovery.tiers.get(level);
							
							lore.add("GRAYNext tier rewards:");
							getCollectionLevelDetails(discovery, level + 1, lore);
							lore.add(StringUtils.progressBar(
									progress, 35, 
									ChatColor.GREEN, ChatColor.DARK_GRAY) + " GREEN" + 
									StringUtils.asPercent(progress) + " WHITE[" + colour + 
									owner.discoveries.getOrDefault(discovery.id, 0L) + "WHITE/GREEN" +
									discovery.tiers.get(level) + "WHITE]");
                        }
						else
						{
							lore.add("GOLDMAXED");
                        }
                        lore.add("");
                        lore.add("GRAYClick to view tiers!");

                        assert meta != null;
                        meta.setLore(StringUtils.colourList(lore));
						disp.setItemMeta(meta);
					}

					selectable[calc(x,y)] = discovery.id;
					inventory.setSlot(disp, calc(x, y));
				}
				else {
					selectable[calc(x,y)] = null;
					inventory.setSlot(null, calc(x, y));
				}
			}
		}
		else {
			Collection discovery = DiscoveryManager.get(selected);
			int level = owner.getDiscoveryLevel(discovery);

			for (int i = 0; i < discovery.getMaxTier(); i++) {
				int x = i % 7;
				int y = i / 7;
				int d = Math.min(discovery.getMaxTier() - (7 * y), 7);
				int lvl = i + 1;

				String colour = lvl <= level ? "GREEN" : lvl == level + 1 ? "YELLOW" : "RED";
				ItemStack display = ItemFactory.buildCustom(colour + "_STAINED_GLASS_PANE", colour + discovery.name + " " + lvl, null);
				List<String> lore = new ArrayList<>();
				double progress = (double)owner.discoveries.getOrDefault(discovery.id, 0L) / discovery.tiers.get(lvl - 1);
				lore.add("GRAYITALIC" + discovery.tierDescription.get(lvl - 1));
				lore.add("");
				lore.add("GRAYRewards");
				getCollectionLevelDetails(discovery, lvl, lore);
				if (lvl <= level) {
					lore.add(StringUtils.progressBar(1, 35, ChatColor.GREEN, ChatColor.DARK_GRAY) + " GREEN100% WHITE[" + colour +
							owner.discoveries.getOrDefault(discovery.id, 0L) + "WHITE/GREEN" +
							discovery.tiers.get(lvl - 1) + "WHITE]");
				}
				else lore.add("GRAY" + StringUtils.progressBar(progress, 35, ChatColor.GREEN, ChatColor.DARK_GRAY) + " GREEN" + StringUtils.asPercent(progress) + " WHITE[" + colour +
						owner.discoveries.getOrDefault(discovery.id, 0L) + "WHITE/GREEN" +
						discovery.tiers.get(lvl - 1) + "WHITE]");

				ItemMeta meta = display.getItemMeta();
                assert meta != null;
                meta.setLore(StringUtils.colourList(lore));
				display.setItemMeta(meta);

				inventory.setSlot(display, calc(positions[d][x], y + 2));
			}

            inventory.setSlot(ItemFactory.buildCustom(discovery.displayItem,
                            "GREEN" + discovery.name + " " + level,
                            "GRAYYou have collected GREEN"
                                    +
                                    StringUtils.commaify(owner.discoveries.getOrDefault(discovery.id, 0L))
                                    +
                                    "GRAY items."),
					calc(4, 0));
			inventory.setSlot(ItemFactory.buildCustom("ARROW", "GREENBack", null), calc(4,5));
		}
		
		inventory.update();
	}

	private void getCollectionLevelDetails(Collection discovery, int level, List<String> lore) {
		for (String recipe : discovery.recipes.getOrDefault(level - 1, new ArrayList<>()))
		{
			Recipe r = ItemFactory.recipes.get(recipe);

			String n = r.customName != null ? r.customName : ItemFactory.getItemTypeName(r.item);
			lore.add("DARK_GRAY- " + n + " DARK_GRAY[Recipe]");
		}
		if (discovery.xpRewards.get(level - 1) != 0) lore.add("DARK_GRAY- AQUA+" + discovery.xpRewards.get(level - 1) + "XP");
		lore.add("");
	}

}
