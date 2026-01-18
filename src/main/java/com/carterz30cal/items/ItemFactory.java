package com.carterz30cal.items;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.entities.Shop;
import com.carterz30cal.entities.damage.StatusEffect;
import com.carterz30cal.entities.damage.StatusEffects;
import com.carterz30cal.gui.GooeyInventory;
import com.carterz30cal.items.abilities2.Abilities;
import com.carterz30cal.items.abilities2.implementation.GameAbility;
import com.carterz30cal.items.sets.ItemSet;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatDisplayType;
import com.carterz30cal.utils.FileUtils;
import com.carterz30cal.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;

public class ItemFactory
{
	public static ItemFactory instance;
	public static ItemStack menuItem;
	public static RecipeCategory baseCategory = new RecipeCategory();
	public static Map<String, RecipeCategory> categories = new HashMap<>();
	public static Map<String, Recipe> recipes = new HashMap<>();
	public static Map<Integer, List<Recipe>> levelRecipes = new HashMap<>();

	public static String[] files = {
			"waterway2/items/ingredients", "waterway2/items/weapons/swords","waterway2/items/weapons/bows",
            "waterway2/items/weapons/attuners", "waterway2/items/pickaxes/pickaxes",
            "waterway2/items/fishing_rods/rods",
			"waterway2/items/talismans/utility","waterway2/items/talismans/offensive",
			"waterway2/items/lootboxes",
			"waterway2/items/armours/uncommon_armours","waterway2/items/armours/rare_armours",
			"waterway2/items/armours/very_rare_armours",
			"waterway2/items/armours/sets/uncommon_sets","waterway2/items/armours/sets/rare_sets",
			"waterway2/items/armours/sets/very_rare_sets",
			"waterway2/items/pet_items"
	};
	
	
	public static NamespacedKey kItem = new NamespacedKey(Dungeons.instance, "item");
	public static NamespacedKey kData = new NamespacedKey(Dungeons.instance, "data");
	public static NamespacedKey kUUID = new NamespacedKey(Dungeons.instance, "uuid");
	public static String[] categoryFiles = {
            "waterway2/recipes/categories"
	};
	public static String[] recipeFiles = {
            "waterway2/recipes/swords", "waterway2/recipes/bows", "waterway2/recipes/ingredients",
            "waterway2/recipes/armours/uncommon_armours", "waterway2/recipes/armours/rare_armours",
            "waterway2/recipes/armours/very_rare_armours",
            "waterway2/recipes/attuners_offensive",
            "waterway2/recipes/enchantments/sharpness",
            "waterway2/recipes/talismans", "waterway2/recipes/harvesters", "waterway2/recipes/fishing_rods", "waterway2/recipes/pickaxes"
	};
    public static String[] skullFiles = {
            "waterway2/skulls"
    };

	public static String[] shopFiles = {
			"waterway2/items/shops"
	};
    private static Map<String, PlayerProfile> skullProfiles = new HashMap<>();
	
	private static List<String> itemList;
	
	public static Map<String, Item> items = new HashMap<>();
	
	public ItemFactory()
	{
		instance = this;

		menuItem = GooeyInventory.produceElement("EMERALD", "GOLDMenu DARK_GRAY(Click)");
		itemList = new ArrayList<>();
		for (String file : files)
		{
			FileConfiguration c = FileUtils.getData(file);
			for (String p : c.getKeys(false))
			{
				ConfigurationSection i = c.getConfigurationSection(p);
				
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

	public static void update(ItemStack i, GamePlayer player)
	{
		if (i == null) return;
		Item item = getItem(i);
		if (item == null) return;
        if (i.getItemMeta() instanceof SkullMeta && item.skullProfileId != null) {
            setSkullTexture(i, item.skullProfileId);
        }
		ItemMeta meta = i.getItemMeta();


        List<String> lore = new ArrayList<>();

		var enchantments = getItemEnchants(i);
		StatContainer stats = item.stats.clone();

		int maxEnchantPower = getItemMaxEnchantPower(i, player);
		int usedEnchantPower = Math.max(0, sumEnchantPower(enchantments));

		StringBuilder name = new StringBuilder(item.rarity.colour + item.name);


		String descriptor = "DARK_GRAY" + item.rarity.name + " " + item.type.toString();
		if (item.rarity == ItemRarity.MYSTERIOUS) descriptor = "DARK_GRAYMysterious Item";

		if (maxEnchantPower > 0)
		{
			if (usedEnchantPower > maxEnchantPower) descriptor = descriptor + " [DARK_REDOVERMAXDARK_GRAY]";
			else if (usedEnchantPower == maxEnchantPower) descriptor = descriptor + " [AQUAMAXDARK_GRAY]";
			else descriptor = descriptor + " [AQUA" + usedEnchantPower + "DARK_GRAY/AQUA" + maxEnchantPower + "DARK_GRAY]";
		}
		else if (item.type == ItemType.ENCHANTMENT)
		{
			descriptor = descriptor + " [AQUA" + usedEnchantPower + "DARK_GRAY]";
		}
		if (item.value > 0 && isItemBaseModel(i)) {
			descriptor = descriptor + " DARK_GREEN$" + item.value + "DARK_GRAY";
		}
		lore.add(descriptor);
		lore.add("");

		List<ItemAttuner> attuners = getAttuners(i);
		if (!attuners.isEmpty()) name.append(" DARK_GRAY[");
		for (ItemAttuner attuner : attuners)
		{
			name.append(attuner.plus);
			attuner.stats.pushIntoContainer(stats);
		}
		if (!attuners.isEmpty()) name.append("DARK_GRAY]");

		if (item.type != ItemType.ENCHANTMENT)
		{
			var all = getItemAbilities(i, player);
			for (var e : all)
			{
				e.ability.onItemStats(e, stats);
			}
			stats.executeOperations();
			for (var e : all)
			{
				e.ability.onItemStatsLate(e, stats);
			}
			stats.executeOperations();
		}

		for (Stat stat : stats.getStats())
		{
			if (stat.display == StatDisplayType.NO_DISPLAY) continue;
			lore.add(stat.colour + stat.name + ": WHITE" + stats.getDisplayed(stat));
		}

		if (!stats.statuses.isEmpty()) {
			lore.add("");
			lore.add("GOLDApplies: ");
			for (StatusEffect effect : stats.statuses.effects.keySet()) {
				int value = stats.statuses.effects.get(effect);
				if (value == 0) continue;
				lore.add(" " + effect.colour + value + " " + effect.name + " " + effect.symbol);
			}
		}

		if (meta instanceof LeatherArmorMeta)
		{
			LeatherArmorMeta leather = (LeatherArmorMeta)meta;

			leather.setColor(Color.fromRGB(item.r, item.g, item.b));
		}


		if (enchantments.size() > 4)
		{
			if (!lore.get(lore.size() - 1).isEmpty()) lore.add("");

			StringBuilder l = new StringBuilder();
			int in = 0;
			for (var enchant : enchantments)
			{
				in++;
				if (in < 3)
				{
					l.append(getColouredEnchantmentName(enchant)).append(", ");
				}
				else
				{
					lore.add(l + getColouredEnchantmentName(enchant));
					l = new StringBuilder();
					in = 0;
				}
			}
			if (in != 0) lore.add(l.substring(0, l.length() - 2));
		}
		else if (!enchantments.isEmpty())
		{
			if (!lore.get(lore.size() - 1).isEmpty()) lore.add("");
			for (var enchant : enchantments)
			{
				lore.add(getColouredEnchantmentName(enchant));
				for (String d : enchant.description()) lore.add(" " + d);
				if (item.type == ItemType.ENCHANTMENT) {
					StringBuilder appl = new StringBuilder();
					appl.append("DARK_GRAY Used with: ");
					for (ItemType ty : enchant.ability.getApplicableTypes()) {
						appl.append(ty.toString());
						if (appl.charAt(appl.length() - 1) != 's') appl.append("s");
						appl.append(", ");
					}
					lore.add(appl.substring(0, appl.length() - 2));
				}
			}
		}

		if (!lore.get(lore.size() - 1).isEmpty()) lore.add("");
		var abilities = getItemAbilities(item);
		if (item instanceof ItemPet) {
			ItemPet pet = (ItemPet) item;
			if (pet.activeAbility != null) {
				abilities.add(0, pet.activeAbility.getContext(player, item.rarity.ordinal()));
			}
		}
		for (var context : abilities)
		{
			lore.add(context.name());
			for (String d : context.description()) lore.add(" " + d);
		}

		if (!lore.get(lore.size() - 1).isEmpty()) lore.add("");
		for (String d : item.description) lore.add("GRAY" + d);
		if (item.type == ItemType.ATTUNER)
		{
			lore.add("GRAYYou may apply up to five attuners");
			lore.add("GRAYon any wieldable item.");
		}

		if (player != null) {
			if (!item.set.equals("null") && player.hasSet(item.set)) {
				ItemSet set = (ItemSet) getItem(item.set);
				lore.add("GOLDBOLDSET BONUS: " + set.name);
				for (String d : set.description) lore.add(" GRAY" + d);
				for (Stat stat : set.stats.getStats())
				{
					if (stat.display == StatDisplayType.NO_DISPLAY) continue;
					lore.add(" " + stat.colour + stat.name + ": WHITE" + set.stats.getDisplayed(stat));
				}
				for (var context : getItemAbilities(set))
				{
					context.owner = player;
					//lore.add(" " + ability.name());
					for (String d : context.description()) lore.add(" " + d);
				}
			}

			if (player.getLevel() < item.stats.getStat(Stat.LEVEL_REQUIREMENT)) {
				lore.add("RED\u00D7 Requires Level " + item.stats.getStat(Stat.LEVEL_REQUIREMENT));
			}
		}


		while (lore.get(lore.size() - 1).isEmpty()) lore.remove(lore.size() - 1);
		meta.setLore(StringUtils.colourList(lore));
		meta.setDisplayName(StringUtils.colourString(name.toString()));

		if (item.glow)
		{
			meta.addEnchant(Enchantment.UNBREAKING, 1, false);
		}
		else meta.removeEnchant(Enchantment.UNBREAKING);

		i.setItemMeta(meta);
	}

	public static ItemStack ripPlayerSkull(GamePlayer player)
	{
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);

		if (!Bukkit.getServer().getOnlineMode()) return skull;

		SkullMeta meta = (SkullMeta)skull.getItemMeta();
        if (meta == null) {
            return skull;
        }
        meta.addItemFlags(ItemFlag.HIDE_PROFILE);

		try
		{
			meta.setOwnerProfile(player.player.getPlayerProfile());
		}
		catch (Exception e)
		{

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

    @SuppressWarnings("UnstableApiUsage")
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
            meta.removeItemFlags(ItemFlag.HIDE_LORE);
            meta.setUnbreakable(true);
        }

		stack.setItemMeta(meta);
		if (item != null) update(stack, null);
		
		return stack;
	}
	
	public static ItemStack buildBook(String enchants)
	{
		ItemStack book = build("enchanted_book");
		
		ItemFactory.addItemData(book, "enchants", enchants);
		ItemFactory.update(book, null);
		
		return book;
	}
	
	public static ItemStack setItemEnchAttunersShortcut(ItemStack item, String enchants, String attuners)
	{
		ItemFactory.addItemData(item, "enchants", enchants);
		if (attuners != null) ItemFactory.addItemData(item, "attuners", attuners);
		ItemFactory.update(item, null);
		
		return item;
	}
	
	public static boolean isItemBaseModel(ItemStack item) {
        return getItemData(item).isEmpty();
	}
	
	public static String getItemName(ItemStack item)
	{
		if (item == null || !item.hasItemMeta()) return "null";
		assert item.getItemMeta() != null;
		return item.getItemMeta().getDisplayName();
	}

	public static String getColouredEnchantmentName(GameAbility.AbilityContext context) {
		int maxLevel = context.ability.getMaximumLevel();
		StringBuilder name = new StringBuilder();
		if (context.level < maxLevel) {
			name.append("DARK_PURPLE");
		}
		else if (context.level == maxLevel) {
			name.append("BLUE");
		}
		else name.append("GOLD");
		name.append(context.name());
		name.append(" ");
		name.append(context.level);
		return name.toString();
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
	
	public static int getItemMaxEnchantPower(ItemStack i, GamePlayer player)
	{
		Item item = getItem(i);
		if (item == null) return 0;
		
		int power = item.stats.getStat(Stat.ENCHANT_POWER);
		
		return power;
	}
	
	/*
	 * item data format
	 * 
	 * enchants:power-1,strength-2,MIDAS_TOUCH-5~reforge:incredible
	 * 
	 */
	
	public static int sumEnchantPower(List<GameAbility.AbilityContext> enchants)
	{
		int power = 0;
		for (var e : enchants) power += e.getEnchantPower();
		
		return power;
	}
	
	
	/*
	 * this function doesn't return incompatible enchantments.
	 * 
	 * 
	 */
	public static List<GameAbility.AbilityContext> getItemEnchants(ItemStack i)
	{
		return getItemEnchants(i, null);
	}
	
	public static List<GameAbility.AbilityContext> getItemEnchants(ItemStack i, GamePlayer owner)
	{
		List<GameAbility.AbilityContext> enchants = new ArrayList<>();
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
			GameAbility.AbilityContext context = ability.getContext(owner, level);

            if (context.ability.getApplicableTypes().contains(item.type) || item.type == ItemType.ENCHANTMENT) {
                enchants.add(context);
            }
        }
		
		return enchants;
	}
	
	
	
	public static List<GameAbility.AbilityContext> getItemAbilities(Item item)
	{
		return getItemAbilities(item, null);
	}
	public static List<GameAbility.AbilityContext> getItemAbilities(Item item, GamePlayer p)
	{
		if (item == null) return new ArrayList<>();
		
		List<GameAbility.AbilityContext> abilities = new ArrayList<>();
		for (var a : item.abilities)
		{
			abilities.add(a.getContext(p, item.rarity.ordinal()));
		}
		
		return abilities;
	}

	public static List<GameAbility.AbilityContext> getItemAbilities(ItemStack item)
	{
		return getItemAbilities(item, null);
	}
	public static List<GameAbility.AbilityContext> getItemAbilities(ItemStack item, GamePlayer p)
	{
		if (item == null) return new ArrayList<>();
		Item i = getItem(item);

		List<GameAbility.AbilityContext> abilities = new ArrayList<>();
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
	
	public static ItemStack buildCustom(String base, String name, String lore)
	{
		ItemStack item = build(base);
		return buildCustom(item, name, lore);
	}
	
	public static ItemStack buildCustom(ItemStack item, String name, String lore)
	{
		if (item == null) return null;
		makeInvalid(item);

		ItemMeta meta = item.getItemMeta();
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
	
    public static void setSkullTexture(ItemStack skull, String profileId) {
        if (skull == null || !skull.hasItemMeta()) {
            return;
        }
        ItemMeta meta = skull.getItemMeta();
        if (meta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) meta;
            PlayerProfile profile = skullProfiles.get(profileId);
            //System.out.println(profile.getTextures().getSkin());
            skullMeta.setOwnerProfile(profile);
            skull.setItemMeta(skullMeta);
        }
    }
	
	public static String getItemTypeName(String type)
	{
		Item item = getItem(type);
		if (item == null) return "REDnull";
		else return item.rarity.colour + item.name;
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
		
		String pressedData = "";
		for (Entry<String, String> d : data.entrySet())
		{
			pressedData = pressedData + d.getKey() + ":" + d.getValue() + "~";
		}
		
		ItemMeta meta = item.getItemMeta();
		meta.getPersistentDataContainer().set(kData, PersistentDataType.STRING, pressedData);
		
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
        PlayerProfile profile = Dungeons.instance.getServer().createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();

        try {
            textures.setSkin(new URL(url));
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
		
		String dataPart = item.getItemMeta().getPersistentDataContainer().getOrDefault(kData, PersistentDataType.STRING, "");
		return dataPart;
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
			item.rarity = ItemRarity.valueOf(i.getString("rarity", "COMMON"));
			item.stats = new StatContainer();
			item.glow = i.getBoolean("glow", false);
			item.description = i.getStringList("description");
			item.value = i.getLong("value");
			item.tags = i.getStringList("tags");
			item.set = i.getString("set", "null");
			if (item.description == null) item.description = new ArrayList<>();

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
                for (String stat : s.getKeys(false))
				{
					item.stats.setStat(Stat.valueOf(stat.toUpperCase()), StringUtils.getIntFromStringConf(s, stat, in));
				}
			}

			itemList.add(item.id);
			items.put(item.id, item);
		}


	}

	private void generateRegular(String p, ConfigurationSection i)
	{
		Item item;
		ItemType type = ItemType.valueOf(i.getString("type", "INGREDIENT"));
		int lootboxItemCount = -1;
		switch (type) {
			case ATTUNER:
				item = new ItemAttuner();
				break;
			case LOOTBOX:
				item = new ItemLootbox();
				((ItemLootbox) item).table = new ItemLootTable(i);
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
					System.out.println("[DUNGEONS] Failed to generate ability: " + i.getString("active-ability"));
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

		item.glow = i.getBoolean("glow", false);
		item.description = i.getStringList("description");
		item.discovery = i.getString("discovery", null);
		item.discoveryProgress = i.getInt("discovery-progress", 0);
		item.set = i.getString("set", "null");
        item.skullProfileId = i.getString("skull-profile-id", null);

		if (item.description == null) item.description = new ArrayList<>();
		if (lootboxItemCount > 0) {
			//item.description.add("");
			item.description.add("GRAYThis lootbox can contain up");
			item.description.add("GRAYto GOLD" + lootboxItemCount + "GRAY unique items!");
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

		if (item instanceof ItemAttuner)
		{
			ItemAttuner attuner = (ItemAttuner)item;
			attuner.plus = i.getString("attuner-icon");
		}


		if (i.contains("stats"))
		{
			ConfigurationSection s = i.getConfigurationSection("stats");
			for (String stat : s.getKeys(false))
			{
				item.stats.setStat(Stat.valueOf(stat.toUpperCase()), s.getInt(stat));
			}
		}

		if (i.contains("statuses")) {
			ConfigurationSection k = i.getConfigurationSection("statuses");
			item.stats.statuses = new StatusEffects(k);
		}

		if (!items.containsKey(item.id)) itemList.add(item.id);
		items.put(item.id, item);
	}
	
}
