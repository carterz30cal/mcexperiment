package com.carterz30cal.gui;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.abilities2.implementation.PlayerAbilityContext;
import com.carterz30cal.skills.SkillSoulType;
import com.carterz30cal.skills.Skills;
import com.carterz30cal.utils.StringUtils;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class SkillTreeGUI extends AbstractGUI {
    private final Skills[] skills = new Skills[54];
    private int page = 1;

    public SkillTreeGUI(GamePlayer owner) {
        super(owner);

        inventory = new GooeyInventory("Skill Tree", 6);
        refresh();
    }

    private void refresh() {
        inventory.initUsingTemplate(GooeyTemplate.EMPTY);
        for (int y = 0; y < 6; y++) {
            inventory.setSlot(ItemFactory.customItem("WHITE_STAINED_GLASS_PANE", ""), calc(0, y));
            inventory.setSlot(ItemFactory.customItem("WHITE_STAINED_GLASS_PANE", ""), calc(8, y));
        }
        inventory.setSlot(ItemFactory.customItem("ARROW", "<green>Up a page!"), calc(8, 1));
        if (page > 1) {
            inventory.setSlot(ItemFactory.customItem("ARROW", "<green>Down a page!"), calc(8, 4));
        }

        var info = getInfo();
        inventory.setSlot(ItemFactory.customItem("REDSTONE_TORCH", "<red>Information!", info), calc(0, 2));
        inventory.setSlot(ItemFactory.customItem("NETHERITE_BLOCK", "<red>Reset your tree!"), calc(0, 3));

        for (var skill : Skills.values()) {
            if (skill.position.page() != page) {
                continue;
            }
            var cal = calc(skill.position.x(), skill.position.y());
            inventory.setSlot(getSkillInfo(skill), cal);
            skills[cal] = skill;
        }
        inventory.update();
    }

    @Override
    public boolean allowRightClick(int clickPos, ItemStack current) {
        if (clickPos == calc(0, 3)) {
            owner.skillTree.resetTree();
        }
        refresh();
        return false;
    }

    @Override
    public boolean allowLeftClick(int clickPos, ItemStack current) {
        if (clickPos > 53) {
            return false;
        }
        else if (clickPos == calc(0, 3)) {
            owner.sendMessage("<red>Right click to reset your tree!");
        }
        else if (clickPos == calc(8, 1)) {
            page++;
        }
        else if (clickPos == calc(8, 4) && page > 1) {
            page--;
        }
        else if (skills[clickPos] != null) {
            var skill = skills[clickPos];
            if (owner.skillTree.canLevel(skill)) {
                owner.skillTree.addSkillLevel(skill);
                owner.playSound(Sound.BLOCK_DISPENSER_DISPENSE, 0.5, 1);
            }
            else {
                owner.sendMessage("<red>Cannot level skill!");
            }
        }
        refresh();
        return false;
    }

    private ItemStack getSkillInfo(Skills skill) {
        var tree = owner.skillTree;
        var level = tree.getSkillLevel(skill);
        var soulReq = skill.getSkill().getSoulsNeedForLevel(level + 1);
        var soulType = skill.getSkill().soulType;
        var mockContext = new PlayerAbilityContext(skill.getSkill());
        var unlockable = skill.hasParent(tree.getSkills()) && owner.skillTree.getRemainingTokens() > 0;
        mockContext.level = Math.max(1, level);
        mockContext.owner = owner;

        var material = (level == 0) ? (!unlockable ? "COAL" : "REDSTONE") : ((level == skill.getSkill().maxLevel) ? "DIAMOND" : "EMERALD");
        var lore = new ArrayList<String>();
        lore.add("<dark_grey>Level " + level + "/" + skill.getSkill().maxLevel + "</dark_grey>");
        lore.add("");
        lore.addAll(skill.getSkill().miniMessageDescription(mockContext));
        lore.add("");

        if (level == 0) {
            if (!unlockable) {
                if (owner.skillTree.getRemainingTokens() > 0) {
                    lore.add("<red>You haven't reached this node on the tree yet!</red>");
                }
                else {
                    lore.add("<red>You don't have any more tokens!</red>");
                }
            }
            else {
                lore.add("<green>Use a skill point to unlock this skill!");
            }
        }
        else if (level == skill.getSkill().maxLevel) {
            lore.removeLast();
        }
        else {
            if (tree.canLevel(skill)) {
                lore.add("<green>Spend " + soulReq + " " + soulType.getName() + " souls to level up!");
            }
            else {
                lore.add("<red>You need " + soulReq + " " + soulType.getName() + " souls to level this up!");
            }
        }

        var nameColour = level == skill.getSkill().maxLevel ? "<aqua>" : (level > 0 ? "<green>" : "<red>");

        return ItemFactory.customItem(material, nameColour + skill.getSkill().name(mockContext), lore);
    }

    private @NotNull List<String> getInfo() {
        var info = new ArrayList<String>();
        info.add("<grey>Your skill tree is the home for all");
        info.add("<grey>of your powerful stat enhancements, as");
        info.add("<grey>well as unique abilities!");
        info.add("<grey>You gain one skill token per player level");
        info.add("<grey>which allows you to unlock a new skill, then");
        info.add("<grey>you may level up these skills using <blue>Souls");
        info.add("<grey>which can be found in various flavours in differing");
        info.add("<grey>areas. Right click the slot below this text");
        info.add("<grey>if you must reset your tree!");
        info.add("");
        info.add("<grey>You have <green>" + owner.skillTree.getRemainingTokens() + "</green> unused skill tokens!");
        info.add("<grey>Souls: ");
        for (var s : SkillSoulType.values()) {
            info.add(s.getName() + "<dark_grey> - <grey>" + StringUtils.addCommas(owner.skillTree.getSoulCount(s)));
        }
        return info;
    }
}
