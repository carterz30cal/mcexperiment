package com.carterz30cal.gui;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.Item;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.ItemTypeUse;
import com.carterz30cal.stats.StatDisplayType;
import com.carterz30cal.utils.LevelUtils;
import com.carterz30cal.utils.StringUtils;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.kyori.adventure.text.Component.text;

public class MenuGUI extends AbstractGUI 
{
	public final int ANVIL_POS = calc(7, 1);
	public final int FORGE_POS = calc(1, 1);
	public final int LEVEL_POS = calc(3, 1);
	public final int DISCOVERY_POS = calc(4, 1);
    public final int QUIVER_POS = calc(6, 2);
    public final int BESTIARY_POS = calc(6, 1);
	public final int SACK_POS = calc(2, 1);
	public final int BACKPACK_POS = calc(5, 2);
	public final int PET_POS = calc(3, 2);
    public final int QUEST_POS = calc(4, 2);
    public final int WARDROBE_POS = calc(5, 1);

	
	public final int LINES = 6;
	
	public MenuGUI(GamePlayer owner)
	{
		super(owner);
		
		inventory = new GooeyInventory("Dungeoneer", LINES);
		inventory.initUsingTemplate(GooeyTemplate.PANED);
		
		update();
	}

    /**
     * @return an ItemStack with all the player's information, including XP progress and stats
     * @since 1.0.0
     */
    private ItemStack playerHead() {
        var lore = new ArrayList<TextComponent.Builder>();
        lore.add(
                StringUtils.progressBar(40, owner.getLevelProgress(), NamedTextColor.AQUA, NamedTextColor.DARK_GRAY)
                        .append(
                                text(((int) (owner.getLevelProgress() * 1000) / 10) +
                                        "% to Level " +
                                        (owner.getLevel() + 1))
                                        .color(NamedTextColor.AQUA)
                        )
        );
        lore.add(
                text().content(owner.xp + "/" + LevelUtils.getXpForLevel(owner.getLevel() + 1)).color(NamedTextColor.DARK_GRAY)
        );

        for (var stat : owner.stats.getStats()) {
            if (stat.display == StatDisplayType.NO_DISPLAY || stat.display == StatDisplayType.NO_DISPLAY_IN_PLAYER_STATS) {
                continue;
            }
            lore.add(
                    text().append(text().content(stat.name + ": ").color(stat.textColour))
                            .append(text().content(" " + owner.stats.getDisplayed(stat)).color(NamedTextColor.WHITE))
            );
        }
        if (owner.player.isOp()) {
            lore.add(text());
            lore.add(text().content("Click to open the super-secret admin menu!").color(NamedTextColor.YELLOW));
        }

        return ItemFactory.customItem(
                ItemFactory.ripPlayerSkull(owner),
                text().append(owner.player.playerListName()),
                lore
        );
    }

	
	public void update() {
        inventory.setSlot(playerHead(), calc(4, 0));

        inventory.setSlot(ItemFactory.customItem("ANVIL", "<light_purple>Magic Anvil</light_purple>"), ANVIL_POS);
        inventory.setSlot(ItemFactory.customItem("FURNACE", "<aqua>The Item Forge</aqua>"), FORGE_POS);
        inventory.setSlot(ItemFactory.customItem("EXPERIENCE_BOTTLE", "<dark_purple>Levels</dark_purple>"), LEVEL_POS);
        inventory.setSlot(ItemFactory.customItem("HOPPER", "<blue>Discoveries</blue>"), DISCOVERY_POS);

        inventory.setSlot(ItemFactory.customItem("LEATHER", "<aqua>Backpack</aqua>"), BACKPACK_POS);
        inventory.setSlot(ItemFactory.customItem("gold_leaf_chestplate", "<yellow>Wardrobe</yellow>"), WARDROBE_POS);

		int arrowCount = 0;
		for (String a : owner.quiver.keySet()) arrowCount += owner.quiver.get(a);
        inventory.setSlot(
                ItemFactory.customItem("ARROW",
                        "<white>Quiver</white>",
                        "<grey>Holding " + StringUtils.addCommas(arrowCount) + " arrows.</grey>"),
                QUIVER_POS);
		
		//if (owner.talismans.size() == 0) inventory.setSlot(ItemFactory.buildCustom("MINECART", "GOLDTalisman Bag", "REDCurrently holding no talismans, go find some!"), TALIS_POS);
		//else if (owner.talismans.size() == 1) inventory.setSlot(ItemFactory.buildCustom("MINECART", "GOLDTalisman Bag", "GRAYHolding WHITE1GRAY talisman."), TALIS_POS);
		//else inventory.setSlot(ItemFactory.buildCustom("MINECART", "GOLDTalisman Bag", "GRAYHolding WHITE" + owner.talismans.size() + "GRAY talismans."), TALIS_POS);
        inventory.setSlot(ItemFactory.customItem("LEAD", "<red>Bestiary</red>"), BESTIARY_POS);
		
		if (owner.getLevel() < 2) {
            inventory.setSlot(ItemFactory.customItem("RED_STAINED_GLASS_PANE", "<red>Locked for now!</red>"), SACK_POS);
		}
		else {
            inventory.setSlot(
                    ItemFactory.customItem("CHEST",
                            "<aqua>Ingredient Sack</aqua>",
                            "<grey>Contains <gold>" + owner.getSackSpaceUsed() + "<dark_grey>/</dark_grey>" + owner.getSackSize() + "</gold> items.</grey>"),
					SACK_POS);
		}
		
		for (int i = 0; i < 5; i++) {
			int pos = calc(2 + i, 4);

            if (i >= owner.talismans.size()) {
                inventory.setSlot(
                        ItemFactory.customItem("RED_STAINED_GLASS_PANE",
                                "<red>Empty Talisman Slot</red>",
                                "<grey>Click a talisman in your inventory to populate this spot!</grey>"), pos);
            }
            else {
                String tali = owner.talismans.get(i);
                inventory.setSlot(ItemFactory.build(tali), pos);
            }
		}

        List<String> petsLore = new ArrayList<>();
        if (!owner.pets.isEmpty() || owner.activePet != null) {
            int petCount = owner.pets.size();
            if (owner.activePet != null) {
                petCount++;
            }
            if (petCount == 1) {
                petsLore.add("<grey>You have 1 pet</grey>");
            }
            else {
                petsLore.add("<grey>You have " + petCount + " pets</grey>");
            }
        }
        else {
            petsLore.add("<grey>You have no pets!</grey>");
        }
        if (owner.activePet != null) {
            petsLore.add("<grey>Your active pet is " + Objects.requireNonNull(ItemFactory.getItem(owner.activePet)).name + "</grey>");
        }
        else {
            petsLore.add("<grey>You have <red>no</red> active pet!</grey>");
        }

        inventory.setSlot(
                ItemFactory.customItem("BONE",
                        "<gold>Pets</gold>",
                        petsLore),
                PET_POS);

        inventory.setSlot(ItemFactory.customItem("WRITTEN_BOOK", "<gold>Quests</gold>"), QUEST_POS);
		
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
        else if (clickPos == WARDROBE_POS) {
            owner.openGui(new WardrobeGUI(owner));
        }
        else if (clickPos == QUEST_POS) {
            owner.openGui(new QuestGUI(owner));
        }
        else if (clickPos == BESTIARY_POS) {
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

                if (!check.isEmpty() || owner.talismans.contains(cli.id)) {
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
            ItemFactory.update(click, owner.getItemContext());
            owner.giveItem(click);
            owner.playSound(Sound.BLOCK_DISPENSER_DISPENSE, 0.7, 1);

            update();
        }
		return false;
	}
}
