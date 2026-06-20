package com.carterz30cal.items;

import com.carterz30cal.entities.Shop;
import com.carterz30cal.entities.damage.StatusEffects;
import com.carterz30cal.entities.health.status.StatusEffect;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.abilities.Abilities;
import com.carterz30cal.items.abilities.implementation.AbilityWithStats;
import com.carterz30cal.items.abilities.implementation.GameAbstractEnchant;
import com.carterz30cal.items.abilities.implementation.PlayerAbilityContext;
import com.carterz30cal.items.recipes.Recipe;
import com.carterz30cal.items.recipes.RecipeCategory;
import com.carterz30cal.items.sets.ItemSet;
import com.carterz30cal.items.trims.TrimMaterialWrapper;
import com.carterz30cal.items.trims.TrimPatternWrapper;
import com.carterz30cal.items.types.ItemAttuner;
import com.carterz30cal.items.types.ItemLootbox;
import com.carterz30cal.items.types.ItemPet;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatDisplayType;
import com.carterz30cal.utils.FileUtils;
import com.carterz30cal.utils.StringUtils;
import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.*;
import java.util.Map.Entry;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

/**
 * @author carterz30cal
 * @version 4
 * @since 1.0.0
 */
public class ItemFactory
{
	public static ItemFactory instance;
	public static ItemStack menuItem;
	public static RecipeCategory baseCategory = new RecipeCategory();
	public static Map<String, RecipeCategory> categories = new HashMap<>();
	public static Map<String, Recipe> recipes = new HashMap<>();
	public static Map<Integer, List<Recipe>> levelRecipes = new HashMap<>();

	public static String[] files = {
            "waterway/items/ingredients", "waterway/items/weapons/swords", "waterway/items/weapons/bows",
            "waterway/items/weapons/attuners", "waterway/items/pickaxes/pickaxes",
            "waterway/items/fishing_rods/rods",
            "waterway/items/talismans/utility", "waterway/items/talismans/offensive",
            "waterway/items/lootboxes",
            "waterway/items/armours/uncommon_armours", "waterway/items/armours/rare_armours",
            "waterway/items/armours/very_rare_armours", "waterway/items/armours/epic_armours",
            "waterway/items/armours/sets/uncommon_sets", "waterway/items/armours/sets/rare_sets",
            "waterway/items/armours/sets/very_rare_sets",
            "waterway/items/pet_items", "waterway/items/quest_items",
            "necropolis/items/weapons/common_swords",
            "necropolis/items/pets/common_pets",
            "necropolis/items/ingredients"
	};
	
	
	public static NamespacedKey kItem = new NamespacedKey(Dungeons.instance, "item");
	public static NamespacedKey kData = new NamespacedKey(Dungeons.instance, "data");
	public static NamespacedKey kUUID = new NamespacedKey(Dungeons.instance, "uuid");
    private static final Map<String, PlayerProfile> skullProfiles = new HashMap<>();
	public static String[] recipeFiles = {
            "waterway/recipes/swords", "waterway/recipes/bows", "waterway/recipes/ingredients",
            "waterway/recipes/armours/uncommon_armours", "waterway/recipes/armours/rare_armours",
            "waterway/recipes/armours/very_rare_armours",
            "waterway/recipes/attuners_offensive",
            "waterway/recipes/enchantments/sharpness",
            "waterway/recipes/talismans", "waterway/recipes/fishing_rods", "waterway/recipes/pickaxes"
	};
    public static String[] skullFiles = {
            "waterway/skulls"
    };

	public static String[] shopFiles = {
            "waterway/items/shops", "necropolis/shops"
	};
	public static String[] categoryFiles = {
            "waterway/recipes/categories", "necropolis/recipes/categories"
	};
	
	private static List<String> itemList;
	
	public static Map<String, Item> items = new HashMap<>();
	
	public ItemFactory()
	{
		instance = this;

        menuItem = ItemFactory.customItem("EMERALD", "<gold>Menu</gold> <dark_grey>(Click!)</dark_grey>");
		itemList = new ArrayList<>();
		for (String file : files)
		{
			FileConfiguration c = FileUtils.getData(file);
            if (c == null) {
                continue;
            }
            for (String p : c.getKeys(false))
			{
				ConfigurationSection i = c.getConfigurationSection(p);
                if (i == null) {
                    continue;
                }
				
				String temp = p.split("-")[0];
				switch (temp)
				{
				case "armour":
					generateArmour(p.split("-")[1], i);
					break;
				default:
					generateRegular(temp, i);
				}
			}
		}
		
		
		categories.put("base", baseCategory);
		List<RecipeCategory> cat = new ArrayList<>();
		for (String cFile : categoryFiles)
		{
			FileConfiguration c = FileUtils.getData(cFile);
			for (String p : c.getKeys(false))
			{
				ConfigurationSection i = c.getConfigurationSection(p);
				
				cat.add(new RecipeCategory(i));
			}
		}
		
		for (RecipeCategory c : cat) 
		{
			if (c == baseCategory) continue;
			categories.get(c.parent).subcategories.add(c.id);
		}
		
		for (String rFile : recipeFiles)
		{
			FileConfiguration c = FileUtils.getData(rFile);
			for (String p : c.getKeys(false))
			{
				ConfigurationSection i = c.getConfigurationSection(p);
				
				new Recipe(i, true);
			}
		}

		for (String sFile : shopFiles) {
			FileConfiguration c = FileUtils.getData(sFile);
			for (String p : c.getKeys(false))
			{
				ConfigurationSection i = c.getConfigurationSection(p);

				new Shop(i);
			}
		}

        for (String skullFile : skullFiles) {
            FileConfiguration c = FileUtils.getData(skullFile);
            for (String p : c.getKeys(false)) {
                ConfigurationSection i = c.getConfigurationSection(p);
                generateSkullProfile(p, i.getString("skull-url"));
            }
        }
	}


    public static void update(
            @NotNull ItemStack stack
    ) {
        update(stack, (FactoryBuildContext) null);
    }

    /**
     * @param stack   ItemStack that we want to update
     * @param context Provides GamePlayer and additional context such as desired verbosity and colourblindness settings.
     * @since 1.0.0
     */
    @SuppressWarnings("UnnecessaryUnicodeEscape")
    public static void update(
            @NotNull ItemStack stack,
            @Nullable FactoryBuildContext context) {
        Item item = getItem(stack);
        if (item == null) {
            return;
        }
        if (context == null) {
            context = FactoryBuildContext.NULL;
        }
        if (stack.getItemMeta() instanceof SkullMeta && item.skullProfileId != null) {
            setSkullTexture(stack, item.skullProfileId);
        }
        var meta = stack.getItemMeta();
        var itemNameB = text().decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        var descriptor = text().decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        List<Component> lore = new ArrayList<>();

        itemNameB.append(text(item.name, item.rarity.textColor));

        var enchantments = getItemEnchants(stack);
        StatContainer stats = item.stats.clone();
        int maxEnchantPower = getItemMaxEnchantPower(stack, context.player);
        int usedEnchantPower = Math.max(0, sumEnchantPower(enchantments));

        if (item.rarity == ItemRarity.MYSTERIOUS) {
            descriptor.append(text("Mysterious Item", DARK_GRAY));
        }
        else {
            descriptor.append(text(item.rarity.name).append(text(" ")).append(text(item.type.toString())).color(DARK_GRAY));
        }
        if (maxEnchantPower > 0) {
            if (usedEnchantPower > maxEnchantPower) {
                descriptor.append(text(" [", DARK_GRAY)).append(text("OVERMAX", DARK_RED));
            }
            else if (usedEnchantPower == maxEnchantPower) {
                descriptor.append(text(" [", DARK_GRAY)).append(text("MAX", AQUA));
            }
            else {
                descriptor.append(text(" [", DARK_GRAY))
                        .append(text(usedEnchantPower, AQUA))
                        .append(text("/", DARK_GRAY))
                        .append(text(maxEnchantPower, AQUA));
            }
            descriptor.append(text("]", DARK_GRAY));
        }
        else if (item.type == ItemType.ENCHANTMENT) {
            descriptor
                    .append(text(" [").color(DARK_GRAY))
                    .append(text(usedEnchantPower).color(AQUA))
                    .append(text("]").color(DARK_GRAY));
        }
        if (item.value > 0 && isItemBaseModel(stack)) {
            descriptor.append(text(" $" + item.value).color(NamedTextColor.DARK_GREEN));
        }

        List<ItemAttuner> attuners = getAttuners(stack);
        if (!attuners.isEmpty()) {
            itemNameB.append(text(" [", DARK_GRAY));
            for (ItemAttuner attuner : attuners) {
                itemNameB.append(text(attuner.plus, attuner.colour));
                attuner.stats.pushIntoContainer(stats);
            }
            itemNameB.append(text("]", DARK_GRAY));
        }

        if (item.type != ItemType.ENCHANTMENT) {
            var all = getItemAbilities(stack, context.player);
            for (var e : all) {
                if (e.ability instanceof AbilityWithStats abilityWithStats) {
                    abilityWithStats.modifyStats(e, stats, AbilityWithStats.Situation.ITEM);
                }
            }
            stats.execute();
        }

        record Section(List<TextComponent.Builder> section) {
        }
        ;
        List<Section> sections = new ArrayList<>();
        Section statSection = new Section(new ArrayList<>());
        {
            boolean showStats = false;
            for (Stat stat : stats.getStats()) {
                if (stat.display == StatDisplayType.NO_DISPLAY) {
                    continue;
                }
                showStats = true;
                statSection.section.add(text().append(text(stat.name + ": ", stat.textColour)).append(text(stats.getDisplayed(stat), NamedTextColor.WHITE)));
            }

            Section statusSection = new Section(new ArrayList<>());
            {
                if (!stats.statuses.isEmpty()) {
                    var l = text().append(text("Applies: ", BLUE));
                    var vstat = stats.getStatuses();

                    int r = vstat.size();
                    for (StatusEffect effect : vstat) {
                        r--;
                        int value = stats.statuses.effects.get(effect);
                        l.append(text(value + effect.symbol + " " + effect.name, effect.textColour));
                        if (r > 0) {
                            l.append(text(", ", DARK_GRAY));
                        }
                    }
                    statusSection.section.add(l);
                }
            }

            if (showStats) {
                sections.add(statSection);
            }
            if (!stats.statuses.isEmpty()) {
                sections.add(statusSection);
            }
        }

        if (!enchantments.isEmpty()) {
            Section enchantSection = new Section(new ArrayList<>());
            {
                if (enchantments.size() > 4) {
                    var l = text();
                    int in = 0;
                    int i = 0;
                    for (var enchant : enchantments) {
                        in++;
                        i++;
                        l.append(text(enchant.name() + " " + enchant.level, enchant.colour()));
                        if (in < 3) {
                            if (i != enchantments.size()) {
                                l.append(text(", ", DARK_GRAY));
                            }
                        }
                        else {
                            enchantSection.section.add(l);
                            l = text();
                            in = 0;
                        }
                    }
                    if (in != 0) {
                        enchantSection.section.add(l);
                    }
                }
                else {
                    for (var enchant : enchantments) {
                        var l = text().append(text(enchant.name() + " " + enchant.level, enchant.colour()));
                        var desc = enchant.componentDescription();
                        enchantSection.section.add(l);
                        if (!desc.isEmpty()) {
                            for (var d : desc) {
                                enchantSection.section.add(text().append(text(" ")).append(d));
                            }
                        }
                        if (item.type == ItemType.ENCHANTMENT) {
                            if (enchant.ability instanceof GameAbstractEnchant enchAbility) {
                                var validTypes = enchAbility.getValidTypes();
                                if (!validTypes.isEmpty()) {
                                    var k = text();
                                    k.append(text(" Used with: ", DARK_GRAY));
                                    int i = 0;
                                    for (ItemType ty : validTypes) {
                                        i++;
                                        k.append(text(ty.toPlural(), DARK_GRAY));
                                        if (i != validTypes.size()) {
                                            k.append(text(", ", DARK_GRAY));
                                        }
                                    }
                                    enchantSection.section.add(k);
                                }

                            }

                        }
                    }
                }
            }
            sections.add(enchantSection);
        }

        var abilities = getItemAbilities(item);
        Section specificSection = new Section(new ArrayList<>());
        boolean drawSpecificSection = false;
        {
            if (item instanceof ItemPet pet) {
                if (pet.activeAbility != null) {
                    abilities.addFirst(pet.activeAbility.getContext(context.player, item.rarity.ordinal()));
                }
            }
            else if (item.type == ItemType.ATTUNER) {
                drawSpecificSection = true;
                specificSection.section.add(text().append(text("You may apply up to five attuners onto").color(GRAY)));
                specificSection.section.add(text().append(text("any wieldable item using the anvil menu.").color(GRAY)));
            }
        }
        if (drawSpecificSection) {
            sections.add(specificSection);
        }

        Section abilitySection = new Section(new ArrayList<>());
        for (var ability : abilities) {
            abilitySection.section.add(text().append(text(ability.name(), ability.colour())));
            for (var d : ability.componentDescription()) abilitySection.section.add(text().append(text(" ")).append(d));
        }
        if (!abilities.isEmpty()) {
            sections.add(abilitySection);
        }

        if (context.player != null) {
            Section setSection = new Section(new ArrayList<>());
            if (!item.set.equals("null") && context.player.hasSet(item.set)) {
                ItemSet set = (ItemSet) getItem(item.set);
                setSection.section.add(text().append(text("SET BONUS: " + set.name, GOLD).decorate(TextDecoration.BOLD)));
                for (var d : set.lore) {
                    var miniMessage = MiniMessage.miniMessage().deserialize(" <dark_grey>" + d + "</dark_grey>");
                    setSection.section.add(text().append(miniMessage));
                }

                for (Stat stat : set.stats.getStats()) {
                    if (stat.display == StatDisplayType.NO_DISPLAY) {
                        continue;
                    }
                    var l = text();
                    l.append(text(" " + stat.name + ": ", stat.textColour));
                    l.append(text(set.stats.getDisplayed(stat), WHITE));
                    setSection.section.add(l);
                }
                for (var ability : getItemAbilities(set)) {
                    ability.owner = context.player;
                    for (var d : ability.componentDescription())
                        setSection.section.add(text().append(text(" ", DARK_GRAY).append(d)));
                }
            }


            if (context.player.getLevel() < item.stats.stat(Stat.LEVEL_REQUIREMENT)) {
                setSection.section.add(text().append(text("\u00D7 Requires Level " + item.stats.stat(Stat.LEVEL_REQUIREMENT), RED)));
            }
            if (!setSection.section.isEmpty()) {
                sections.add(setSection);
            }
        }

        if (item.lore != null && !item.lore.isEmpty()) {
            Section loreSection = new Section(new ArrayList<>());
            for (var l : item.lore) {
                var miniMessage = MiniMessage.miniMessage().deserialize("<dark_grey>" + l + "</dark_grey>");
                loreSection.section.add(text().decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE).append(miniMessage));
            }
            if (!loreSection.section.isEmpty()) {
                sections.add(loreSection);
            }
        }

        if (!sections.isEmpty()) {
            lore.add(text(""));
            for (var section : sections) {
                for (var sec : section.section) {
                    lore.add(sec.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE).build());
                }
                lore.add(text(""));
            }
            lore.removeLast();
        }

        lore.addFirst(descriptor.build());
        meta.customName(itemNameB.build());
        meta.lore(lore);

        if (meta instanceof LeatherArmorMeta leather) {
            leather.setColor(Color.fromRGB(item.r, item.g, item.b));
        }
        if (meta instanceof ArmorMeta armor) {
            if (item.trimMaterial != TrimMaterialWrapper.NULL && item.trimPattern != TrimPatternWrapper.NULL) {
                ArmorTrim trim = new ArmorTrim(item.trimMaterial.GetTrimMaterial(), item.trimPattern.GetTrimPattern());
                armor.setTrim(trim);
            }
        }
        if (item.glow) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, false);
        }
        else {
            meta.removeEnchant(Enchantment.UNBREAKING);
        }
        stack.setItemMeta(meta);
    }

	public static ItemStack ripPlayerSkull(GamePlayer player)
	{
        var skull = ItemStack.of(Material.PLAYER_HEAD);

		if (!Bukkit.getServer().getOnlineMode()) return skull;

		SkullMeta meta = (SkullMeta)skull.getItemMeta();
        if (meta == null) {
            return skull;
        }

		try
		{
            meta.setPlayerProfile(player.player.getPlayerProfile());
		} catch (Exception ignored) {
        }

		skull.setItemMeta(meta);

		return skull;
	}
	
	public static ItemStack build(Item i)
	{
		return build(i.id, 1);
	}
	public static ItemStack build(String i)
	{
		return build(i, 1);
	}

    public static ItemStack build(String i, int amount)
	{
		Item item = items.get(i);
		ItemStack stack;
		if (item == null) 
		{
			try
			{
				stack = new ItemStack(Material.valueOf(i.toUpperCase()), amount);
			}
			catch (Exception e)
			{
				return null;
			}
		}
		else stack = new ItemStack(item.material, amount);
		
		ItemMeta meta = stack.getItemMeta();

        if (meta != null) {
            if (item != null)
            {
                meta.getPersistentDataContainer().set(kItem, PersistentDataType.STRING, item.id);
                if (item.type != ItemType.INGREDIENT && item.type.use != ItemTypeUse.CONSUMABLE) meta.getPersistentDataContainer().set(kUUID, PersistentDataType.STRING, UUID.randomUUID().toString());

            }
            meta.getPersistentDataContainer().set(kData, PersistentDataType.STRING, "");
            meta.addItemFlags(ItemFlag.values());
            //meta.removeItemFlags(ItemFlag.HIDE_LORE);
            meta.setUnbreakable(true);
        }

		stack.setItemMeta(meta);
        if (item != null) {
            update(stack, (FactoryBuildContext) null);
        }
		
		return stack;
	}
	
	public static boolean isItemBaseModel(ItemStack item) {
        return getItemData(item).isEmpty();
	}
	
	public static Item getItem(ItemStack item)
	{
		if (item == null || !item.hasItemMeta()) return null;
		return items.getOrDefault(item.getItemMeta().getPersistentDataContainer().getOrDefault(kItem, PersistentDataType.STRING, ""), null);
	}
	public static Item getItem(String item)
	{
		if (item == null) return null;
		return items.getOrDefault(item, null);
	}

    @Deprecated
	public static int getItemMaxEnchantPower(ItemStack i, GamePlayer player)
	{
		Item item = getItem(i);
		if (item == null) return 0;

        return item.stats.getStat(Stat.ENCHANT_POWER);
	}
	
	/*
	 * item data format
	 * 
	 * enchants:power-1,strength-2,MIDAS_TOUCH-5~reforge:incredible
	 * 
	 */

    public static int sumEnchantPower(List<PlayerAbilityContext> enchants)
	{
		int power = 0;
        for (var e : enchants) {
            if (e.ability instanceof GameAbstractEnchant enchant) {
                power += (int) enchant.getEnchantPower(e);
            }
        }
		
		return power;
	}


    public static List<PlayerAbilityContext> getItemEnchants(ItemStack i)
	{
		return getItemEnchants(i, null);
	}

    /**
     *
     * @param i     the ItemStack we're checking
     * @param owner the player that we're checking on.
     * @return all compatible enchantments on i
     * @since 1.0.0
     */
    public static List<PlayerAbilityContext> getItemEnchants(ItemStack i, GamePlayer owner) {
        List<PlayerAbilityContext> enchants = new ArrayList<>();
		String[] strEnch = getItemData(i).getOrDefault("enchants", "").split(",");
		Item item = getItem(i);
		if (item == null) return new ArrayList<>();
		
		for (String enchantment : strEnch)
		{
			String[] split = enchantment.split("-");
			if (split.length == 1) continue;
			
			int level = Integer.parseInt(split[1]);
			Abilities ability;
			try {
				ability = Abilities.valueOf(split[0]);
			} catch (IllegalArgumentException e) {
				continue;
            }
            PlayerAbilityContext context = ability.getContext(owner, level);
            if (item.type == ItemType.ENCHANTMENT) {
                enchants.add(context);
            }
            else if (context.ability instanceof GameAbstractEnchant enchant && enchant.getValidTypes().contains(item.type)) {
                enchants.add(context);
            }
        }
		
		return enchants;
	}


    public static List<PlayerAbilityContext> getItemAbilities(Item item)
	{
		return getItemAbilities(item, null);
    }

    public static List<PlayerAbilityContext> getItemAbilities(Item item, GamePlayer p)
	{
		if (item == null) return new ArrayList<>();

        List<PlayerAbilityContext> abilities = new ArrayList<>();
		for (var a : item.abilities)
		{
			abilities.add(a.getContext(p, item.rarity.ordinal()));
		}
		
		return abilities;
    }

    public static List<PlayerAbilityContext> getItemAbilities(ItemStack item, GamePlayer p)
	{
		if (item == null) return new ArrayList<>();
		Item i = getItem(item);

        List<PlayerAbilityContext> abilities = new ArrayList<>();
		for (var a : i.abilities)
		{
			abilities.add(a.getContext(p, i.rarity.ordinal()));
		}
		abilities.addAll(getItemEnchants(item, p));

		return abilities;
	}
	
	
	
	public static void makeInvalid(ItemStack item)
	{
		if (item == null || !item.hasItemMeta()) return;
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return;
		meta.getPersistentDataContainer().remove(kItem);
		meta.getPersistentDataContainer().set(kData, PersistentDataType.STRING, "");
		meta.getPersistentDataContainer().remove(kUUID);
		
		item.setItemMeta(meta);
	}

    @Deprecated
    public static ItemStack buildCustom(String base, String name) {
        return customItem(base, name);
    }

    public static ItemStack customItem(String base, String name) {
        ItemStack stack = build(base);
        return customItem(stack, name, new ArrayList<>());
    }

    public static ItemStack customItem(String base, String name, NamedTextColor colour) {
        ItemStack stack = build(base);
        return customItem(stack, text().content(name).color(colour));
    }

    public static ItemStack customItem(String base, TextComponent.Builder name, TextComponent.Builder... lore) {
        ItemStack stack = build(base);
        return customItem(stack, name, lore);
    }

    public static ItemStack customItem(String base, TextComponent.Builder name, List<TextComponent.Builder> lore) {
        ItemStack stack = build(base);
        return customItem(stack, name, lore);
    }

    public static ItemStack customItem(String base, String name, String lore) {
        ItemStack stack = build(base);
        List<String> loreList = new ArrayList<>();
        loreList.add(lore);
        return customItem(stack, name, loreList);
    }

    public static ItemStack customItem(String base, String name, List<String> lore) {
        ItemStack stack = build(base);
        return customItem(stack, name, lore);
    }


    /**
     *
     * @deprecated in favour of customItem() or more complex buildCustom() calls.
     */
    @Deprecated
	public static ItemStack buildCustom(String base, String name, String lore)
	{
		ItemStack item = build(base);
		return buildCustom(item, name, lore);
    }

    /**
     *
     * @deprecated in favour of customItem() or more complex buildCustom() calls.
     */
    @Deprecated
    public static ItemStack buildCustom(String base, String name, @NotNull List<String> lore) {
        ItemStack item = build(base);
        return buildCustom(item, name, lore);
    }

    /**
     *
     * @deprecated in favour of customItem() or more complex buildCustom() calls.
     */
    @Deprecated
	public static ItemStack buildCustom(ItemStack item, String name, String lore)
	{
		if (item == null) return null;
		makeInvalid(item);

		ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(StringUtils.colourString(name));
		
		if (lore != null)
		{
			List<String> loreList = new ArrayList<>();
			for (String l : lore.split(";"))
			{
				loreList.add(StringUtils.colourString(l));
			}
			
			meta.setLore(loreList);
		}
		
		item.setItemMeta(meta);
		
		return item;
    }

    /**
     *
     * @deprecated in favour of customItem() or more complex buildCustom() calls.
     */
    @Deprecated
    public static ItemStack buildCustom(ItemStack item, String name, List<String> lore) {
        if (item == null) {
            return null;
        }
        makeInvalid(item);

        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(StringUtils.colourString(name));

        if (lore != null) {
            meta.setLore(StringUtils.colourList(lore));
        }


        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack buildCustom(
            @NotNull String stack,
            String name,
            @Nullable NamedTextColor colour,
            @Nullable TextComponent.Builder... lore) {
        ItemStack item = build(stack);
        if (item == null) {
            return null;
        }
        return buildCustom(item, name, colour == null ? null : TextColor.color(colour), lore);
    }

    public static ItemStack buildCustom(
            @NotNull ItemStack stack,
            String name,
            @Nullable NamedTextColor colour,
            @Nullable TextComponent.Builder... lore) {
        return buildCustom(stack, name, colour == null ? null : TextColor.color(colour), lore);
    }

    public static ItemStack buildCustom(
            @NotNull ItemStack stack,
            String name,
            @Nullable NamedTextColor colour,
            @Nullable NamedTextColor loreColour,
            @Nullable TextComponent.Builder lore) {
        return buildCustom(stack, name, colour == null ? null : TextColor.color(colour), lore == null ? null : lore.color(loreColour));
    }

    /**
     *
     * @param stack  The template ItemStack that we want to turn into a display item.
     * @param name   Whatever you want the ItemStack's custom name to be, in plaintext.
     * @param colour Either null, which will use the Item default, or a specified TextColor.
     * @param lore   List of Builders that determines the item lore. If null it sets the lore to empty.
     * @return ItemStack that has been made 'invalid' (isn't recognized by the game as a custom item) with specified name and lore.
     * @implNote This isn't safe to run on players' items, make sure you clone the ItemStack first.
     * @since 1.0.0
     */
    @NotNull
    public static ItemStack buildCustom(
            @NotNull ItemStack stack,
            String name,
            @Nullable TextColor colour,
            @Nullable TextComponent.Builder... lore) {
        if (colour == null) {
            Item item = getItem(stack);
            if (item == null) {
                colour = TextColor.color(0x0);
            }
            else {
                colour = getItem(stack).rarity.textColor;
            }
        }
        final TextColor nameColour = colour;
        return customItem(stack, text().content(name).color(nameColour), lore);
    }

    /**
     *
     * @param stack The template ItemStack that we want to turn into a display item.
     * @param name  Whatever you want the ItemStack's custom name to be, in plaintext.
     * @param lore  List of Builders that determines the item lore. If null it sets the lore to empty.
     * @return ItemStack that has been made 'invalid' (isn't recognized by the game as a custom item) with specified name and lore.
     * @implNote This isn't safe to run on players' items, make sure you clone the ItemStack first.
     * @since 1.0.0
     */
    public static ItemStack customItem(
            @NotNull ItemStack stack,
            @Nullable TextComponent.Builder name,
            @Nullable TextComponent.Builder... lore
    ) {
        if (lore == null) {
            return customItem(stack, name, new ArrayList<>());
        }
        else {
            return customItem(stack, name, List.of(lore));
        }
    }

    /**
     *
     * @param stack The template ItemStack that we want to turn into a display item.
     * @param name  Whatever you want the ItemStack's custom name to be, in plaintext.
     * @param lore  List of Builders that determines the item lore. If null it sets the lore to empty.
     * @return ItemStack that has been made 'invalid' (isn't recognized by the game as a custom item) with specified name and lore.
     * @implNote This isn't safe to run on players' items, make sure you clone the ItemStack first.
     * @since 1.0.0
     */
    public static ItemStack customItem(
            @NotNull ItemStack stack,
            @Nullable TextComponent.Builder name,
            @Nullable List<TextComponent.Builder> lore
    ) {
        makeInvalid(stack);
        stack.editMeta(meta -> {
            if (name != null) {
                meta.customName(name.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE).build());
            }
            var llist = new ArrayList<Component>();
            if (lore != null) {
                for (var l : lore) {
                    if (l == null) {
                        continue;
                    }
                    llist.add(l.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE).build());
                }
            }
            meta.lore(llist);
        });
        return stack;
    }


    /**
     * MiniMessage format customItem generator.
     *
     * @param stack The template ItemStack that we want to turn into a display item.
     * @param name  Whatever you want the ItemStack's custom name to be, in MiniMessage format.
     * @param lore  List of Strings in MiniMessage format.
     * @return ItemStack that has been made 'invalid' (isn't recognized by the game as a custom item) with specified name and lore.
     * @implNote This isn't safe to run on players' items, make sure you clone the ItemStack first.
     * @since 1.0.0
     */
    public static ItemStack customItem(
            @NotNull ItemStack stack,
            @Nullable String name,
            @Nullable List<String> lore
    ) {
        makeInvalid(stack);
        stack.editMeta(meta -> {
            if (name != null) {
                var miniName = MiniMessage.miniMessage().deserialize(name).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
                meta.customName(miniName);
            }
            var llist = new ArrayList<Component>();
            if (lore != null) {
                for (var l : lore) {
                    if (l == null) {
                        continue;
                    }
                    var miniL = MiniMessage.miniMessage().deserialize(l);
                    llist.add(miniL.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                }
            }
            meta.lore(llist);
        });
        return stack;
    }
	
    public static void setSkullTexture(ItemStack skull, String profileId) {
        if (skull == null || !skull.hasItemMeta()) {
            return;
        }
        ItemMeta meta = skull.getItemMeta();
        if (meta instanceof SkullMeta skullMeta) {
            var profile = skullProfiles.get(profileId);
            skullMeta.setPlayerProfile(profile);
            skull.setItemMeta(skullMeta);
        }
    }

    public static PlayerProfile getSkullProfile(String profileId) {
        return skullProfiles.get(profileId);
    }

    /**
     * @deprecated in favour of getItemNameBuilder()
     */
    @Deprecated
	public static String getItemTypeName(String type)
	{
		Item item = getItem(type);
		if (item == null) return "REDnull";
		else return item.rarity.colour + item.name;
    }

    public static TextComponent.Builder getItemNameBuilder(String type) {
        Item item = getItem(type);
        if (item == null) {
            return text().content("null").color(RED);
        }
        else {
            return text().content(item.name).color(item.rarity.textColor);
        }
    }
	
	public static String flattenEnchMap(Map<Abilities, Integer> map)
	{
		StringBuilder flat = new StringBuilder();
		for (var entry : map.entrySet())
		{
			flat.append(entry.getKey().name()).append("-").append(entry.getValue()).append(",");
		}
		return flat.toString();
	}
	
	
	public static List<ItemAttuner> getAttuners(ItemStack item)
	{
		Map<String, String> data = getItemData(item);
		String[] attuners = data.getOrDefault("attuners", "").split(",");
		
		List<ItemAttuner> attunerList = new ArrayList<>();
		for (String s : attuners)
		{
			ItemAttuner a = (ItemAttuner)getItem(s);
			if (a != null && a.type == ItemType.ATTUNER) attunerList.add(a);
		}
		
		return attunerList;
	}
	
	public static void addItemData(ItemStack item, String key, String data)
	{
		if (item == null || !item.hasItemMeta()) return;
		
		Map<String, String> map = getItemData(item);
		map.put(key, data);
		setItemData(item, map);
	}
	
	public static void setItemData(ItemStack item, Map<String, String> data)
	{
		if (item == null || !item.hasItemMeta()) return;

        StringBuilder pressedData = new StringBuilder();
		for (Entry<String, String> d : data.entrySet()) {
            pressedData.append(d.getKey()).append(":").append(d.getValue()).append("~");
		}
		
		ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(kData, PersistentDataType.STRING, pressedData.toString());
		
		item.setItemMeta(meta);
	}
	
	public static void setItemData(ItemStack item, String data)
	{
		if (item == null || !item.hasItemMeta()) return;
		
		ItemMeta meta = item.getItemMeta();
		meta.getPersistentDataContainer().set(kData, PersistentDataType.STRING, data);
		
		item.setItemMeta(meta);
	}
	
	public static Map<String, String> getItemData(ItemStack item)
	{
		if (item == null || !item.hasItemMeta()) return new HashMap<>();
		
		Map<String, String> data = new HashMap<>();
		
		String[] dataPart = item.getItemMeta().getPersistentDataContainer().getOrDefault(kData, PersistentDataType.STRING, "").split("~");
		for (String spl : dataPart)
		{
			String[] spl2 = spl.split(":");
			if (spl2.length == 1) continue;
			data.put(spl2[0], spl2[1]);
		}
		return data;
	}

    public static void generateSkullProfile(String id, String url) {
        PlayerProfile profile = Dungeons.instance.getServer().createProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();

        try {
            textures.setSkin(URI.create(url).toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        profile.setTextures(textures);

        profile.update().thenAcceptAsync(updatedProfile -> {
            skullProfiles.put(id, updatedProfile);
        }, runnable -> Bukkit.getScheduler().runTask(Dungeons.instance, runnable));
    }

	public static String getFlatItemData(ItemStack item)
	{
		if (item == null || !item.hasItemMeta()) return "";

        return item.getItemMeta().getPersistentDataContainer().getOrDefault(kData, PersistentDataType.STRING, "");
	}
	
	public static String getItemList(int pos)
	{
		return itemList.get(pos);
	}

	private void generateArmour(String p, ConfigurationSection i)
	{
		ItemType[] types = {
				ItemType.HELMET,
				ItemType.CHESTPLATE,
				ItemType.LEGGINGS,
				ItemType.BOOTS
		};

		for (int in = 0; in <= 3; in++)
		{
			Item item;
			ItemType type = types[in];

			item = new Item();

			item.name = i.getStringList("name").get(in);
			//item.name = StringUtils.getStrFromStringConf(i, "name", in);
			item.id = p + "_" + type.name().toLowerCase();
			item.type = type;
			item.material = Material.valueOf(i.getString("material") + "_" +  type.name());
            item.skullProfileId = i.getString("skull-profile-id");
            if (type == ItemType.HELMET && item.skullProfileId != null) {
                item.material = Material.PLAYER_HEAD;
            }

            item.trimPattern = TrimPatternWrapper.valueOf(i.getString("trim-pattern", "NULL"));
            item.trimMaterial = TrimMaterialWrapper.valueOf(i.getString("trim-material", "NULL"));

			item.rarity = ItemRarity.valueOf(i.getString("rarity", "COMMON"));
			item.stats = new StatContainer();
			item.glow = i.getBoolean("glow", false);
            item.lore = i.getStringList("lore");
			item.value = i.getLong("value");
			item.tags = i.getStringList("tags");
			item.set = i.getString("set", "null");
            if (item.lore == null) {
                item.lore = new ArrayList<>();
            }

			var abilities = i.getStringList("abilities");
			item.abilities = new ArrayList<>();
			for (var ability : abilities) {
				item.abilities.add(Abilities.valueOf(ability));
			}

			if (i.contains("colour"))
			{
				item.r = StringUtils.getIntFromStringConf(i, "colour.r", in);
				item.g = StringUtils.getIntFromStringConf(i, "colour.g", in);
				item.b = StringUtils.getIntFromStringConf(i, "colour.b", in);
			}


			if (i.contains("stats"))
			{
				ConfigurationSection s = i.getConfigurationSection("stats");
                assert s != null;
                for (String stat : s.getKeys(false)) {
                    item.stats.setStat(Stat.valueOf(stat.toUpperCase()), StringUtils.getLongFromStringConf(s, stat, in));
				}
			}

			itemList.add(item.id);
			items.put(item.id, item);
		}


	}

    public static ItemStack buildItemFromString(String string) {
        return buildItemFromString(string, null);
    }

    public static ItemStack buildItemFromString(String string, GamePlayer owner) {
        if (string == null) {
            return null;
        }
        String[] spl = string.split("£");
        ItemStack item = ItemFactory.build(spl[0]);
        if (item == null) {
            return null;
        }
        else {
            switch (spl.length) {
                case 1:
                    break;
                case 2:
                    item.setAmount(Integer.parseInt(spl[1]));
                    break;
                default:
                    item.setAmount(Integer.parseInt(spl[1]));
                    ItemFactory.setItemData(item, spl[2]);
                    break;
            }
            ItemFactory.update(item, owner == null ? null : owner.getItemContext());
            return item;
        }
    }

    public static String buildStringFromItem(ItemStack item) {
        Item itemType = getItem(item);
        if (itemType == null) {
            return null;
        }
        else {
            String data = ItemFactory.getFlatItemData(item);
            if (data.isEmpty()) {
                return itemType.id + "£" + item.getAmount();
            }
            else {
                return itemType.id + "£" + item.getAmount() + "£" + data;
            }
        }
    }

	private void generateRegular(String p, ConfigurationSection i)
	{
		Item item;
		ItemType type = ItemType.valueOf(i.getString("type", "INGREDIENT"));
		int lootboxItemCount = -1;
		switch (type) {
			case ATTUNER:
                var attuner = new ItemAttuner();
                item = attuner;
                attuner.plus = i.getString("attuner-icon");
                if (i.isString("attuner-colour")) {
                    assert i.getString("attuner-colour") != null;
                    NamedTextColor ntc = NAMES.value(Objects.requireNonNull(i.getString("attuner-colour")).toLowerCase());
                    if (ntc != null) {
                        attuner.colour = TextColor.color(ntc);
                    }
                    else {
                        Dungeons.instance.getLogger().warning("ItemFactory: couldn't find NamedTextColour at " + p + ".attuner-colour");
                        attuner.colour = TextColor.color(0x0);
                    }
                }
                else if (i.isConfigurationSection("attuner-colour")) {
                    int r, g, b;
                    r = i.getInt("attuner-colour.r");
                    g = i.getInt("attuner-colour.g");
                    b = i.getInt("attuner-colour.b");
                    attuner.colour = TextColor.color(r, g, b);
                }

				break;
			case LOOTBOX:
				item = new ItemLootbox();
                ((ItemLootbox) item).table = new ItemLootTable(i.getConfigurationSection("drops"));
				lootboxItemCount = ((ItemLootbox) item).table.getDropCount();
				break;
			case VIRTUAL_SET:
				ItemSet set = new ItemSet();
				set.requireCount = i.getInt("requireCount", 4);
				item = set;
				break;
			case PET:
				ItemPet pet = new ItemPet();
				pet.petLine = i.getString("pet-line");
				try {
					pet.activeAbility = Abilities.valueOf(i.getString("active-ability"));
				} catch (IllegalArgumentException e) {
                    Dungeons.instance.getLogger().severe("ItemFactory: couldn't find " + i.getString("active-ability") + " ability.");
				}

				item = pet ;
				break;
			default:
				item = new Item();
				break;
		}

		item.name = i.getString("name", "null");
		item.id = p;
		item.type = type;
		item.material = Material.valueOf(i.getString("material", "BARRIER"));
		item.rarity = ItemRarity.valueOf(i.getString("rarity", "COMMON"));
		item.stats = new StatContainer();
		item.value = i.getLong("value");

        item.trimPattern = TrimPatternWrapper.valueOf(i.getString("trim-pattern", "NULL"));
        item.trimMaterial = TrimMaterialWrapper.valueOf(i.getString("trim-material", "NULL"));

		item.glow = i.getBoolean("glow", false);
        item.lore = i.getStringList("lore");
		item.discovery = i.getString("discovery", null);
		item.discoveryProgress = i.getInt("discovery-progress", 0);
		item.set = i.getString("set", "null");
        item.skullProfileId = i.getString("skull-profile-id", null);

        if (item.lore == null) {
            item.lore = new ArrayList<>();
        }
		if (lootboxItemCount > 0) {
			//item.description.add("");
            item.lore.add("<grey>This lookbox can contain up to <gold>" + lootboxItemCount + "</gold> different");
            item.lore.add("<grey>drops, all of which are added automatically to</grey>");
            item.lore.add("<grey>either your inventory or your sack upon opening!</grey>");
		}
		var abilities = i.getStringList("abilities");
		item.abilities = new ArrayList<>();
		for (var ability : abilities) {
			try {
				item.abilities.add(Abilities.valueOf(ability));
			}
			catch (IllegalArgumentException e) {
				System.out.println("[DUNGEONS] Failed to generate ability: " + ability);
			}
		}
		item.tags = i.getStringList("tags");

        if (i.contains("colour"))
		{
			item.r = i.getInt("colour.r", 0);
			item.g = i.getInt("colour.g", 0);
			item.b = i.getInt("colour.b", 0);
		}


		if (i.contains("stats"))
		{
			ConfigurationSection s = i.getConfigurationSection("stats");
            assert s != null;
            for (String stat : s.getKeys(false)) {
                item.stats.setStat(Stat.valueOf(stat.toUpperCase()), s.getLong(stat));
			}
		}

		if (i.contains("statuses")) {
			ConfigurationSection k = i.getConfigurationSection("statuses");
            assert k != null;
            item.stats.statuses = new StatusEffects(k);
		}

		if (!items.containsKey(item.id)) itemList.add(item.id);
		items.put(item.id, item);
    }

    public static final class FactoryBuildContext {
        public static final FactoryBuildContext NULL = new FactoryBuildContext();
        public final GamePlayer player;
        public DescriptionVerbosity verbosity;

        private FactoryBuildContext() {
            this.player = null;
            this.verbosity = DescriptionVerbosity.VERBOSE;
        }

        public FactoryBuildContext(GamePlayer player) {
            this.player = player;
            this.verbosity = DescriptionVerbosity.VERBOSE;
        }

        @Override
        public String toString() {
            return "FactoryBuildContext[" +
                    "player=" + player + ']';
        }

        public enum DescriptionVerbosity {
            VERBOSE,
            COMPACT
        }
    }

}
