package com.carterz30cal.gui;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.discoveries.Collection;
import com.carterz30cal.items.discoveries.DiscoveryManager;
import com.carterz30cal.items.recipes.Recipe;
import com.carterz30cal.utils.StringUtils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
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
        if (clickPos >= 54) {
            return false;
        }
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

                    ItemStack display;
					if (level == 0 && owner.discoveries.getOrDefault(discovery.id, 0L) == 0)
					{
                        display = ItemFactory.customItem("BARRIER", "<red>Discovery locked!</red>");
					}
					else
					{
						boolean maxed = level == discovery.tiers.size();
                        String colour = maxed ? "<green>" : level == 0 ? "<red>" : "<yellow>";

                        List<String> lore = new ArrayList<>(discovery.description);
						lore.add("");
						if (!maxed)
						{
							double progress = (double)owner.discoveries.getOrDefault(discovery.id, 0L) / discovery.tiers.get(level);

                            lore.add("<grey>Next tier rewards:");
							getCollectionLevelDetails(discovery, level + 1, lore);
                            lore.add(StringUtils.stringProgressBar(40, progress,
                                    NamedTextColor.GREEN,
                                    NamedTextColor.DARK_GRAY)
                                    + " <green>" +
                                    StringUtils.asPercent(progress) + " <white>[" + colour +
                                    owner.discoveries.getOrDefault(discovery.id, 0L) + "</" + colour.substring(1) + "/<green>" +
                                    discovery.tiers.get(level) + "</green>]");
                        }
						else
						{
                            lore.add("<gold>MAXED");
                        }
                        lore.add("");
                        lore.add("<grey>Click to view tiers!");

                        display = ItemFactory.customItem(discovery.displayItem, colour + discovery.name + " <white>[</white>" + level + "<white>]</white>", lore);
					}

					selectable[calc(x,y)] = discovery.id;
                    inventory.setSlot(display, calc(x, y));
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

                String pane;
                String colour;
                if (lvl <= level) {
                    pane = "GREEN_STAINED_GLASS_PANE";
                    colour = "green>";
                }
                else if (lvl == level + 1) {
                    pane = "YELLOW_STAINED_GLASS_PANE";
                    colour = "yellow>";
                }
                else {
                    pane = "RED_STAINED_GLASS_PANE";
                    colour = "red>";
                }


                double progress = (double) owner.discoveries.getOrDefault(discovery.id, 0L) / discovery.tiers.get(lvl - 1);

                List<String> lore = new ArrayList<>();
                lore.add("<grey><em>" + discovery.tierDescription.get(lvl - 1));
				lore.add("");
                lore.add("<grey>Rewards");
				getCollectionLevelDetails(discovery, lvl, lore);
				if (lvl <= level) {
                    lore.add(StringUtils.stringProgressBar(40, 1,
                            NamedTextColor.GREEN,
                            NamedTextColor.DARK_GRAY) +
                            " <green>100%</green> <white>[<green>" +
                            owner.discoveries.getOrDefault(discovery.id, 0L) +
                            "</green>/<green>" + discovery.tiers.get(lvl - 1) + "</green>]");
				}
                else {
                    lore.add(StringUtils.stringProgressBar(40, progress,
                            NamedTextColor.GREEN,
                            NamedTextColor.DARK_GRAY) +
                            " <green>" + StringUtils.asPercent(progress) + "</green> <white>[<" + colour +
                            owner.discoveries.getOrDefault(discovery.id, 0L) + "</" + colour +
                            "/<green>" + discovery.tiers.get(lvl - 1) + "</green>]");
                }

                var display = ItemFactory.customItem(pane, "<" + colour + discovery.name + " " + lvl, lore);
				inventory.setSlot(display, calc(positions[d][x], y + 2));
			}

            inventory.setSlot(ItemFactory.customItem(discovery.displayItem,
                            "<green>" + discovery.name + " " + level,
                            "<grey>You have collected <green>" +
                                    StringUtils.addCommas(owner.discoveries.getOrDefault(discovery.id, 0L)) +
                                    "</green> items."),
					calc(4, 0));
            inventory.setSlot(ItemFactory.customItem("ARROW", "<green>Back"), calc(4, 5));
		}
		
		inventory.update();
	}

	private void getCollectionLevelDetails(Collection discovery, int level, List<String> lore) {
		for (String recipe : discovery.recipes.getOrDefault(level - 1, new ArrayList<>()))
		{
			Recipe r = ItemFactory.recipes.get(recipe);
            var item = ItemFactory.getItem(r.item);

            var colour = "<" + item.rarity.textColor.asHexString() + ">";
            String n = r.customName != null ? r.customName : item.name;
            lore.add("<dark_grey>- " + colour + n + " <dark_grey>[Recipe]");
		}
        if (discovery.xpRewards.get(level - 1) != 0) {
            lore.add("<dark_grey>- <aqua>+" + discovery.xpRewards.get(level - 1) + "XP");
        }
		lore.add("");
	}

}
