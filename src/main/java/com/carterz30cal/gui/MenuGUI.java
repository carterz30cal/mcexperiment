package com.carterz30cal.gui;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.items.Item;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.ItemTypeUse;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatDisplayType;
import com.carterz30cal.utils.LevelUtils;
import com.carterz30cal.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MenuGUI extends AbstractGUI 
{
	public final int ANVIL_POS = calc(7, 1);
	public final int FORGE_POS = calc(1, 1);
	public final int LEVEL_POS = calc(3, 1);
	public final int DISCOVERY_POS = calc(4, 1);
	public final int QUIVER_POS = calc(5, 1);
	public final int TALIS_POS = calc(6, 1);
	public final int SACK_POS = calc(2, 1);
	public final int BACKPACK_POS = calc(5, 2);
	public final int PET_POS = calc(3, 2);
    public final int QUEST_POS = calc(4, 2);

	
	public final int LINES = 6;
	
	public MenuGUI(GamePlayer owner)
	{
		super(owner);
		
		inventory = new GooeyInventory("Dungeoneer", LINES);
		inventory.initUsingTemplate(GooeyTemplate.PANED);
		
		update();
	}
	
	public void update() {
		ItemStack player = ItemFactory.ripPlayerSkull(owner);
		
		String statDisplay = StringUtils.progressBar(owner.getLevelProgress(), 40, ChatColor.AQUA, ChatColor.DARK_GRAY);
		statDisplay += " AQUA" + ((int)(owner.getLevelProgress() * 1000) / 10) + "% to Level " + (owner.getLevel() + 1) + ";";
		statDisplay += "DARK_GRAY" + owner.xp + "/" + LevelUtils.getXpForLevel(owner.getLevel() + 1) + ";";
		for (Stat stat : owner.stats.getStats())
		{
			if (stat.display == StatDisplayType.NO_DISPLAY || stat.display == StatDisplayType.NO_DISPLAY_IN_PLAYER_STATS) continue;
			statDisplay += ";" + stat.colour + stat.name + ": WHITE" + owner.stats.getDisplayed(stat);
		}
		if (owner.player.isOp()) statDisplay += ";;YELLOWClick to open the secret admin menu!";
		
		player = ItemFactory.buildCustom(player, owner.player.getPlayerListName(), statDisplay);
		inventory.setSlot(player, calc(4, 0));
		
		inventory.setSlot(GooeyInventory.produceElement("ANVIL", "LIGHT_PURPLEMagic Anvil"), ANVIL_POS);
		inventory.setSlot(GooeyInventory.produceElement("FURNACE", "AQUAThe Item Forge"), FORGE_POS);
		inventory.setSlot(GooeyInventory.produceElement("EXPERIENCE_BOTTLE", "DARK_PURPLELevels"), LEVEL_POS);
		inventory.setSlot(GooeyInventory.produceElement("HOPPER", "BLUEDiscoveries"), DISCOVERY_POS);

		inventory.setSlot(GooeyInventory.produceElement("LEATHER", "AQUABackpack"), BACKPACK_POS);
		
		// quiver
		int arrowCount = 0;
		for (String a : owner.quiver.keySet()) arrowCount += owner.quiver.get(a);
		inventory.setSlot(ItemFactory.buildCustom("ARROW", "WHITEQuiver", "GRAYHolding WHITE" + arrowCount + "GRAY arrows."), QUIVER_POS);
		
		//if (owner.talismans.size() == 0) inventory.setSlot(ItemFactory.buildCustom("MINECART", "GOLDTalisman Bag", "REDCurrently holding no talismans, go find some!"), TALIS_POS);
		//else if (owner.talismans.size() == 1) inventory.setSlot(ItemFactory.buildCustom("MINECART", "GOLDTalisman Bag", "GRAYHolding WHITE1GRAY talisman."), TALIS_POS);
		//else inventory.setSlot(ItemFactory.buildCustom("MINECART", "GOLDTalisman Bag", "GRAYHolding WHITE" + owner.talismans.size() + "GRAY talismans."), TALIS_POS);
        inventory.setSlot(ItemFactory.buildCustom("LEAD", "REDBestiary"), TALIS_POS);
		
		if (owner.getLevel() < 2) {
			inventory.setSlot(GooeyInventory.produceElement("RED_STAINED_GLASS_PANE", "REDLocked for now!"), SACK_POS);
		}
		else {
			inventory.setSlot(ItemFactory.buildCustom("CHEST", "AQUAIngredient Sack", "GRAYContains GOLD" +
                            owner.getSackSpaceUsed() + "WHITE/GOLD" + owner.getSackSize() + "GRAY items."),
					SACK_POS);
		}
		
		for (int i = 0; i < 5; i++) {
			int pos = calc(2 + i, 4);
			
			if (i >= owner.talismans.size()) inventory.setSlot(ItemFactory.buildCustom("RED_STAINED_GLASS_PANE", "REDEmpty Talisman Slot", "GRAYYou may place a talisman here!"), pos);
			else {
				String tali = owner.talismans.get(i);
				inventory.setSlot(ItemFactory.build(tali), pos);
			}
		}

		inventory.setSlot(ItemFactory.buildCustom("BONE", "GOLDPets", "Active Pet: AHHH"), PET_POS);

        inventory.setSlot(ItemFactory.buildCustom("WRITTEN_BOOK", "GOLDQuests"), QUEST_POS);
		
		inventory.update();
	}

	
	public boolean allowClick(int clickPos, ItemStack clicked)
	{
		if (clickPos == ANVIL_POS) owner.openGui(new AnvilGUI(owner));
		else if (clickPos == FORGE_POS) owner.openGui(new ForgeGUI(owner));
		else if (clickPos == LEVEL_POS) owner.openGui(new LevelGUI(owner));
		else if (clickPos == DISCOVERY_POS) owner.openGui(new DiscoveryGUI(owner));
		else if (clickPos == calc(4, 0) && owner.player.isOp()) owner.openGui(new AdminItemGUI(owner));
		else if (clickPos == QUIVER_POS) owner.openGui(new QuiverGUI(owner));
		else if (clickPos == BACKPACK_POS) owner.openGui(new BackpackGUI(owner));
		else if (clickPos == PET_POS) owner.openGui(new PetsGUI(owner));
        else if (clickPos == QUEST_POS) {
            owner.openGui(new QuestGUI(owner));
        }
        else if (clickPos == TALIS_POS) {
            owner.openGui(new BestiaryGUI(owner));
        }
        else if (owner.getLevel() > 1 && clickPos == SACK_POS) owner.openGui(new SackGUI(owner));
        else if (clickPos >= LINES * 9) {
            Item cli = ItemFactory.getItem(clicked);
            if (owner.talismans.size() >= 5) owner.sendMessage("REDYou have no free accessory slots!");
            else if (cli == null) return false;
            else if (cli.type.use != ItemTypeUse.TALISMAN) {
                owner.sendMessage("REDOnly talismans may go in a talisman slot!");
            }
            else {
                List<String> taliTags = new ArrayList<>();
                for (String talisman : owner.talismans) {
                    taliTags.addAll(ItemFactory.getItem(talisman).tags);
                }

                List<String> check = new ArrayList<>(cli.tags);
                check.removeIf((t) -> !taliTags.contains(t));

                if (check.size() != 0 || owner.talismans.contains(cli.id)) {
                    owner.sendMessage("REDYou already have an incompatible talisman equipped!");
                }
                else {
                    owner.talismans.add(ItemFactory.getItem(clicked).id);
                    clicked.setAmount(clicked.getAmount() - 1);
                    owner.playSound(Sound.BLOCK_DISPENSER_DISPENSE, 0.7, 1);
                }
            }
            update();
        }
        else if (clickPos >= calc(2, 4) && clickPos <= calc(6, 4)) {
            int pos = clickPos - calc(2, 4);

            if (pos >= owner.talismans.size()) return false;

            String tali = owner.talismans.get(pos);
            owner.talismans.remove(tali);

            ItemStack click = clicked.clone();
            ItemFactory.update(click, owner);
            owner.giveItem(click);
            owner.playSound(Sound.BLOCK_DISPENSER_DISPENSE, 0.7, 1);

            update();
        }
		return false;
	}
}
