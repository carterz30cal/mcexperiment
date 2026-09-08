package com.carterz30cal.entities.player;

import com.carterz30cal.areas.AreaManager;
import com.carterz30cal.areas.Areas;
import com.carterz30cal.areas.PlayerTeleport;
import com.carterz30cal.areas.quests.Quests;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.health.EntityHealthSystem;
import com.carterz30cal.entities.health.damage.AttackType;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.health.damage.DamageType;
import com.carterz30cal.entities.health.damage.handlers.AggressiveEntity;
import com.carterz30cal.entities.health.damage.handlers.DamageableEntity;
import com.carterz30cal.entities.health.status.StatusEffect;
import com.carterz30cal.entities.player.summons.GamePet;
import com.carterz30cal.events.GameEventHandler;
import com.carterz30cal.fishing.FishingArea;
import com.carterz30cal.gui.AbstractGUI;
import com.carterz30cal.items.*;
import com.carterz30cal.items.abilities.implementation.*;
import com.carterz30cal.items.discoveries.Collection;
import com.carterz30cal.items.discoveries.DiscoveryManager;
import com.carterz30cal.items.recipes.Recipe;
import com.carterz30cal.items.sets.ItemSet;
import com.carterz30cal.items.types.ItemAttuner;
import com.carterz30cal.items.types.ItemPet;
import com.carterz30cal.items.types.ItemPotion;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.mining.Mineable;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;
import com.carterz30cal.stats.operations.AddStatOperation;
import com.carterz30cal.utils.EntityUtils;
import com.carterz30cal.utils.LevelUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.*;

import static net.kyori.adventure.text.Component.text;

/**
 * @author carterz30cal
 * @version 4
 * @since 1.0.0
 */
@SuppressWarnings("UnnecessaryUnicodeEscape")
public class GamePlayer extends GameEntity implements DamageableEntity, AggressiveEntity
{
	public Player player;
	public StatContainer stats;
	public StatContainer lastStats;
	public AbstractGUI gui;
    public Areas area;

	public Map<String, Integer> sets = new HashMap<>();
	
	public long coins;
	
	public long level;
    private final Map<Quests, Quests.QuestSave> quests = new EnumMap<>(Quests.class);
	public long xp;
	
	public List<ForgingItem> forge = new ArrayList<>();

    public List<PlayerAbilityContext> abilities;
    private boolean cachedLevel = false;
    private Quests selectedQuest;
    private final Map<UUID, GameEventHandler> eventHandlers = new HashMap<>();

	public List<String> talismans = new ArrayList<>();
	public List<String> pets = new ArrayList<>();
	public String activePet;
	
	public String dungeonId;
	
	public FishingArea.FishingBobber bobber;
	
	public boolean flagIgnoreInvClose;
	
	private double mana;
	
	private int regenTick;
	
	public int attackTick;
	public int bowTick;
	public int questTick;
    private int areaCheckTick;
    private int invulnerabilityTick;
    public Map<String, Long> kills = new HashMap<>();
	
	public long lastXpReward;
    public long lastCoinReward;
    public int abilityTick;
	public int rewardTick;
	
	public boolean mining;
	public boolean allowInteract;

    public PlayerWardrobe wardrobe = new PlayerWardrobe(this);
    public PlayerSkillTree skillTree = new PlayerSkillTree(this);
    public GamePet pet;
    public PlayerItemProducer factory;
    public EntityHealthSystem healthSystem;
	
	public Map<String, Long> discoveries = new HashMap<>();
	public Map<String, Integer> quiver = new HashMap<>();
    public Map<String, Long> sack = new HashMap<>();

	public Map<Integer, String> backpack = new HashMap<>();
	
	public List<GameEnemy> targeted = new ArrayList<>();

    private PlayerScoreboard playerScoreboard;


    public void register(UUID uuid)
	{
		player.getPersistentDataContainer().set(GameEnemy.keyEnemy, PersistentDataType.STRING, uuid.toString());
		
		mana = 1;
        this.uuid = uuid;
		
		entities.put(uuid, this);

        playerScoreboard = new PlayerScoreboard(this);
	}
	
	public void tick()
	{
		for (ForgingItem item : forge)
		{
			if (LocalDateTime.now().isAfter(item.finished))
			{
				if (player.getInventory().firstEmpty() == -1) {
					if (!item.haveNotified) {
                        sendMessage(
                                "<red>Your " + ItemFactory.getItem(item.item).name + " is done, but you don't have enough spare room in your inventory.</red>"
                        );
						item.haveNotified = true;
					}
				} else {
                    var rep = ItemFactory.getItem(item.item);
                    sendMessage(
                            "<green>Your " + rep.name + " is done! Find it in your " +
                                    (rep.type == ItemType.INGREDIENT ? "Ingredient Sack!" : "Inventory!") +
                                    "</green>"
                    );
					giveItem(item.produce(), false);
					item.isDone = true;
				}
			}
		}
		forge.removeIf((f) -> f.isDone);
		
		
		if (mining) {
			Block b = player.getTargetBlockExact(5);
			Mineable m = Mineable.get(b);
			EntityUtils.applyPotionEffect(player, PotionEffectType.MINING_FATIGUE, 5, 4, false);
			if (m != null) m.damage(this);
		}

        var miningFatigue = player.getPotionEffect(PotionEffectType.MINING_FATIGUE);
        if (miningFatigue == null) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, PotionEffect.INFINITE_DURATION, 2, false, false, false));
        }
        var haste = player.getPotionEffect(PotionEffectType.HASTE);
        if (haste == null) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, PotionEffect.INFINITE_DURATION, 0, false, false, false));
        }
        Objects.requireNonNull(player.getAttribute(Attribute.ATTACK_SPEED)).setBaseValue(10);


		player.removePotionEffect(PotionEffectType.DARKNESS);
		
		
		abilities = new ArrayList<>();
		lastStats = stats;
		stats = new StatContainer();
		
		stats.scheduleOperation(Stat.HEALTH, StatOperationType.ADD, 100);
		stats.scheduleOperation(Stat.HEALTH, StatOperationType.CAP_MIN, 10);
		stats.scheduleOperation(Stat.VITALITY, StatOperationType.ADD, 5);
		stats.scheduleOperation(Stat.VITALITY, StatOperationType.CAP_MIN, 0);
		stats.scheduleOperation(Stat.VISIBILITY, StatOperationType.ADD, 16);
		stats.scheduleOperation(Stat.VISIBILITY, StatOperationType.CAP_MIN, 1);
		stats.scheduleOperation(Stat.VISIBILITY, StatOperationType.CAP_MAX, 24);
        stats.scheduleOperation(Stat.FOCUS, StatOperationType.ADD, 1);
        stats.scheduleOperation(Stat.FOCUS, StatOperationType.CAP_MIN, 0);
        stats.scheduleOperation(Stat.INVULNERABILITY_TICKS, StatOperationType.ADD, 4);
        stats.scheduleOperation(Stat.WARDROBE_SLOTS, StatOperationType.ADD, PlayerWardrobe.DEFAULT_SLOT_COUNT);

		stats.scheduleOperation(Stat.POWER, StatOperationType.CAP_MIN, 0);
		stats.scheduleOperation(Stat.MIGHT, StatOperationType.CAP_MIN, 0);
		stats.scheduleOperation(Stat.STRENGTH, StatOperationType.CAP_MIN, 0);
		
		// level stats!
		
		long levelTens = level / 5;
		
		stats.scheduleOperation(Stat.HEALTH, StatOperationType.ADD, (level - levelTens) * 8);
		stats.scheduleOperation(Stat.DEFENCE, StatOperationType.ADD, levelTens * 2);
		stats.scheduleOperation(Stat.POWER, StatOperationType.ADD, levelTens);

        stats.scheduleOperation(Stat.SACK_SPACE, StatOperationType.ADD, Math.max(0, level - 1) * 1000);
        stats.scheduleOperation(Stat.SACK_SPACE, StatOperationType.ADD, (level >> 3) * 4000);
		
		
		List<ItemStack> items = new ArrayList<>();
		sets.clear();
		for (ItemStack armour : player.getInventory().getArmorContents())
		{
			Item item = ItemFactory.getItem(armour);
			if (item == null || item.type.use != ItemTypeUse.WEARABLE) continue;
			
			items.add(armour);
			if (item.set != null) sets.put(item.set, sets.getOrDefault(item.set, 0) + 1);
		}
		
		ItemStack main = player.getInventory().getItemInMainHand();
		Item mainItem = ItemFactory.getItem(main);
		ItemStack off = player.getInventory().getItemInOffHand();
		Item offItem = ItemFactory.getItem(off);
		
		if (mainItem != null)
		{
            if (mainItem.type.use == ItemTypeUse.WIELDABLE || mainItem.type.use == ItemTypeUse.WIELDABLE_CONSUMABLE)
                items.add(main);
            else {
                ItemFactory.update(main, getItemContext());
            }
		}
		if (offItem != null)
		{
            if (offItem.type.use == ItemTypeUse.OFFHAND) items.add(off);
            else {
                ItemFactory.update(off, getItemContext());
            }
		}

		var updated = new ArrayList<String>();
		for (String talisman : talismans) {
			var item = ItemFactory.buildItemFromString(talisman);
			var check = ItemFactory.getItem(item);
			if (check instanceof ItemPotion) {
				var duration = ItemFactory.getPotionDuration(item);
                if (duration >= 1) {
                    ItemFactory.setPotionDuration(item, duration - 1);
                    updated.add(ItemFactory.buildStringFromItem(item));
                    items.add(item);
                }
				else sendMessage("<red>One of your potions has just expired!");
            }
			else {
				items.add(item);
				updated.add(talisman);
			}
		}
		talismans = updated;
		for (String pet : pets) items.add(ItemFactory.build(pet));
		if (activePet != null) {
			ItemPet itemActivePet = (ItemPet) ItemFactory.getItem(activePet);
			if (itemActivePet != null && itemActivePet.activeAbility != null) {
				abilities.add(itemActivePet.activeAbility.getContext(this, itemActivePet.rarity.ordinal()));
				items.add(ItemFactory.build(activePet));
			}
            if (player.getGameMode() == GameMode.CREATIVE) {
                if (pet != null) {
                    pet.remove();
                }
            }
            else if (pet == null || pet.dead) {
                pet = GamePet.spawn(this, getLocation(), activePet);
            }
		}

		for (String s : sets.keySet()) {
			if (hasSet(s)) items.add(ItemFactory.build(s));
		}

		for (ItemStack item : items)
		{
            ItemFactory.update(item, getItemContext());
			Item i = ItemFactory.getItem(item);
            if (i == null || i.stats.stat(Stat.LEVEL_REQUIREMENT) > getLevel()) {
                continue;
            }
			StatContainer itemStats = i.stats.clone();
			for (ItemAttuner attuner : ItemFactory.getAttuners(item)) attuner.stats.pushIntoContainer(itemStats);
			//List<ItemEnchant> enchants = ItemFactory.getItemEnchants(item);
			
			var iAbilities = ItemFactory.getItemAbilities(item, this);
            for (var e : iAbilities) {
                if (e.ability instanceof AbilityWithStats is) {
                    is.modifyStats(e, itemStats, AbilityWithStats.Situation.ITEM);
                }
            }
            itemStats.execute();
            //for (var e : iAbilities) e.ability.onItemStatsLate(e, itemStats);
			
			itemStats.pushIntoContainer(stats);
			abilities.addAll(iAbilities);
		}

        stats.execute();
        abilities.addAll(skillTree.getUnderlyingAbilities());
        for (var a : abilities) {
			if (a.ability instanceof AbilityWithTick tick) {
                tick.tick(a, abilityTick);
			}
            if (!(a.ability instanceof AbilityWithStats is)) {
                continue;
            }
            is.modifyStats(a, stats, AbilityWithStats.Situation.PLAYER);

        }
        stats.scheduleOperation(Stat.BACKPACK_PAGES, StatOperationType.ADD, 2);
        stats.scheduleOperation(Stat.BACKPACK_PAGES, StatOperationType.CAP_MIN, 1);
        stats.operation(new AddStatOperation(Stat.FORGE_SLOTS, 4));
        if (level >= 5) {
            stats.operation(new AddStatOperation(Stat.FORGE_SLOTS, 2));
        }
        stats.operation(new AddStatOperation(Stat.SKILL_TREE_TOKENS, level));
        stats.scheduleOperation(Stat.LUCK, StatOperationType.ADD, 15);
        stats.scheduleOperation(Stat.DAMAGE, StatOperationType.CAP_MIN, 1);
        stats.execute();

        healthSystem.setMaxHealth(stats.stat(Stat.HEALTH));


        var actionStatBar = text();
        actionStatBar.append(text(healthSystem.getHealth() + "\u2665", NamedTextColor.RED));
        if (stats.stat(Stat.MANA) > 0) {
            actionStatBar.append(text(" " + getMana() + "/" + stats.stat(Stat.MANA) + "♠", NamedTextColor.LIGHT_PURPLE));
        }
        if (rewardTick > 0) {
            if (lastXpReward > 0) {
                actionStatBar.append(text(" +" + lastXpReward + " XP", NamedTextColor.AQUA));
            }
            if (lastCoinReward > 0) {
                actionStatBar.append(text(" +" + lastCoinReward + " coins", NamedTextColor.GOLD));
            }
            if (skillTree.getLastSoulReward() > 0) {
                if (skillTree.getLastSoulReward() == 1) {
                    actionStatBar.append(
                            text(" +1 soul", NamedTextColor.AQUA)
                    );
                }
                else {
                    actionStatBar.append(
                            text(" +" + skillTree.getLastSoulReward() + " souls", NamedTextColor.AQUA)
                    );
                }

            }
            rewardTick--;
        }
		
		if (attackTick > 0) attackTick--;

        if (bowTick > 0) {
            bowTick--;
        }

        if (questTick > 0) {
            questTick--;
        }

        if (invulnerabilityTick > 0) {
            invulnerabilityTick--;
        }

        if (areaCheckTick > 0) {
            areaCheckTick--;
        }
        else {
            area = AreaManager.getPlayerArea(this);
            areaCheckTick = 100;
        }

        abilityTick++;

        sendActionBar(actionStatBar);
		player.getInventory().setItem(8, ItemFactory.menuItem);
        player.playerListName(
                text().color(NamedTextColor.GRAY).content("[")
                        .append(text(level, NamedTextColor.WHITE))
                        .append(text("] " + player.getName())).build()
        );
		
		regenTick++;
		if (regenTick >= 40)
		{
			regenTick = 0;

            heal(stats.stat(Stat.VITALITY));
            gainMana(stats.stat(Stat.FOCUS));
		}
		
		refreshHealth();

        playerScoreboard.tick();
		
		// set targets
        targeted.removeIf((e) -> e.dead);
	}
	
	public void openGui(AbstractGUI gui)
	{
		if (this.gui != null)
		{
			this.gui.onClose();
			//player.closeInventory();
			this.gui = null;
		}
		
		GamePlayer that = this;
		new BukkitRunnable()
		{

			@Override
			public void run() {
				that.gui = gui;
				flagIgnoreInvClose = true;
				that.gui.open();
				flagIgnoreInvClose = false;
				that.gui.runTaskTimer(Dungeons.instance, 1, 1);
			}
			
		}.runTaskLater(Dungeons.instance, 1);
	}

	public boolean hasSet(String s) {
		if (sets.isEmpty()) return false;
		Item i = ItemFactory.getItem(s);
        if (i instanceof ItemSet set) {
            return set.requireCount <= sets.getOrDefault(s, 0);
		}
		return false;
	}
	
	public int getDiscoveryLevel(Collection discovery)
	{
		long count = discoveries.getOrDefault(discovery.id, 0L);
		int level = 0;
		
		while (level < discovery.tiers.size() && discovery.tiers.get(level) <= count) level++;
		return level;
	}

    public long getSackSize() {
        if (stats == null) {
            return 0;
        }
        else {
            return stats.stat(Stat.SACK_SPACE);
        }
	}

    public long getSackSpaceUsed() {
        long used = 0;
        for (long i : sack.values()) used += i;

		return used;
	}

    public long getSackSpaceRemaining() {
		return getSackSize() - getSackSpaceUsed();
	}

	
	public boolean hasSackSpace(int am) {
        return getSackSpaceUsed() + am <= getSackSize();
	}

    /**
     * Use this method to check if we can add a certain number of items to
     * this player's ingredient sack.
     * @param amount how much do we want to add to the sack?
     * @return whether there's room for <code>amount</code> of items.
     * @since 1.0.0
     */
    public boolean hasSackSpace(long amount) {
        return getSackSpaceRemaining() >= amount;
    }
	
	
	public void giveItem(ItemStack item)
	{
		giveItem(item, true);
	}
	public void giveItem(ItemStack item, boolean grantDiscoveryProgress)
	{
		if (item == null) return;
		
		Item i = ItemFactory.getItem(item);
		
		if (i != null)
		{
			if (i.discovery != null && grantDiscoveryProgress)
			{
				Collection col = DiscoveryManager.get(i.discovery);
				
				int currentLevel = getDiscoveryLevel(col);
                if (currentLevel == 0 && discoveries.getOrDefault(col.id, 0L) == 0) {
                    sendMessage("<gold><b>New Discovery! " + col.name);
                }
				
				discoveries.put(col.id, discoveries.getOrDefault(col.id, 0L) + item.getAmount() * i.discoveryProgress);
				
				int newLevel = getDiscoveryLevel(col);
				while (newLevel > currentLevel)
				{
                    sendMessage("<yellow> - - - <gold>DISCOVERY LEVEL UP</gold> - - -");
					for (String recipe : col.recipes.getOrDefault(currentLevel, new ArrayList<>()))
					{
						Recipe r = ItemFactory.recipes.get(recipe);
                        var recipeItem = ItemFactory.getItem(r.item);
                        var colour = recipeItem.rarity.textColor.asHexString() + ">";

                        String n = r.customName != null ? r.customName : recipeItem.name;
                        sendMessage("<dark_grey>- <" + colour + n + "</" + colour + " [Recipe]");
					}
                    sendMessage("<dark_grey>- <aqua>+" + col.xpRewards.get(currentLevel) + "XP");
					
					gainXp(col.xpRewards.get(currentLevel));
					currentLevel++;
				}
			}
			
			if (i.type == ItemType.ARROW) quiver.put(i.id, quiver.getOrDefault(i.id, 0) + item.getAmount());
			else if (i.type == ItemType.INGREDIENT && hasSackSpace(item.getAmount()))
			{
                long am = sack.getOrDefault(i.id, 0L) + item.getAmount();
				sack.put(i.id, am);
			}
            else {
                if (player.getInventory().firstEmpty() == -1) {
                    Dungeons.w.dropItemNaturally(player.getLocation(), item);
                }
                else {
                    player.getInventory().addItem(item);
                }
            }
		}
		else player.getInventory().addItem(item);
	}

    /**
     * Platform for granting players large quantities of items, beyond the
     * normal <code>ItemStack</code> scope.
     * @param items a map of the item ids that we want to give to the player
     * @param grantDiscoveryProgress should these items contribute towards discoveries?
     * @implSpec <code>ItemType.INGREDIENT</code>s should preferentially go into the ingredient sack, then into the inventory if
     * there isn't enough room in the sack.<br>
     * <code>ItemType.ARROW</code> should be essentially ignored for space checks as they can always go into the quiver.
     * <br>Other item types should try to go into the inventory if possible. If all else fails, <code>return false</code>.
     * Storage space should be checked <b>before</b> items are actually added.
     * @return <code>true</code> if we managed to add the items to an available storage location, <code>false</code> otherwise.
     * @see ItemStack
     * @see ItemType
     * @since 1.0.0
     */
    public boolean giveItems(
            @Nullable Map<String, Long> items,
            boolean grantDiscoveryProgress) {
        if (items == null || items.isEmpty()) return true;
        boolean space = true;
        long spaceUsed = 0;
		var storage = player.getInventory().getStorageContents();
		var empty = player.getInventory().firstEmpty();
        for (var entry : items.entrySet()) {
            var item = ItemFactory.getItem(entry.getKey());
            long amount = entry.getValue();
            if (item == null || amount == 0 || item.type == ItemType.ARROW) continue;
            if (item.type == ItemType.INGREDIENT) {
                if (hasSackSpace(spaceUsed + amount)) {
                    spaceUsed += amount;
                    continue;
                }
				else {
					var rem = getSackSpaceRemaining() - spaceUsed;
					spaceUsed += rem;
					amount -= rem;
				}
            }
            if (empty == -1 || empty >= storage.length) {
                space = false;
				break;
            }
			else {
				while (empty < storage.length && amount > 0) {
					if (storage[empty] == null) {
						amount -= item.type.maxStackSize();
					}
					empty++;
				}
			}

        }
        if (!space) return false;
		for (var entry : items.entrySet()) {
			var item = ItemFactory.getItem(entry.getKey());
			long amount = entry.getValue();
			if (item == null || amount == 0) continue;
			if (item.type == ItemType.ARROW) {
				quiver.put(item.id, quiver.getOrDefault(item.id, 0) + (int)amount);
			}
			else if (item.type == ItemType.INGREDIENT)
			{
				if (hasSackSpace(amount)) {
					sack.put(item.id, sack.getOrDefault(item.id, 0L) + amount);
					continue;
				}
				else if (getSackSpaceRemaining() > 0) {
					sack.put(item.id, sack.getOrDefault(item.id, 0L) + getSackSpaceRemaining());
					amount -= getSackSpaceRemaining();
				}
			}
			while (amount > 0) {
				var sam = (int)Math.min(item.type.maxStackSize(), amount);
				var stack = ItemFactory.build(entry.getKey(), sam);
                assert stack != null;
                player.getInventory().addItem(stack);
				amount -= sam;
			}
		}
        return true;
    }


    public void sendMessage(String message)
	{
		sendMessage(message, 0);
	}
	public void sendMessage(String message, int tickDelay)
	{
        var minified = MiniMessage.miniMessage().deserialize(message);
        sendMessage(text().append(minified), null, 0, 0, tickDelay);
    }

    public void sendMessage(String message, TextColor colour, int tickDelay) {
        sendMessage(text().content(message).color(colour), null, 0, 0, tickDelay);
    }

    public void sendMessage(String message, NamedTextColor colour, int tickDelay) {
        sendMessage(text().content(message).color(colour), null, 0, 0, tickDelay);
    }
	public void sendMessage(String message, Sound sound)
	{
        sendMessage(text().content(message), sound, 0, 0, 0);
	}
	public void sendMessage(String message, Sound sound, int tickDelay)
	{
        sendMessage(text().content(message), sound, 0, 0, tickDelay);
    }

    public void sendMessage(TextComponent.Builder message, int tickDelay) {
        sendMessage(message, null, 0, 0, tickDelay);
    }

    public void sendMessage(TextComponent.Builder message, Sound sound, double volume, double pitch) {
        sendMessage(message, sound, volume, pitch, 0);
    }

    public void sendMessage(TextComponent.Builder message, Sound sound, int tickDelay) {
        sendMessage(message, sound, 1, 1, tickDelay);
    }

    public void sendMessage(
            TextComponent.Builder message,
            @Nullable Sound sound,
            double volume,
            double pitch,
            int tickDelay) {
        Audience audience = player;
        if (tickDelay == 0) {
            audience.sendMessage(message.build());
            if (sound != null) {
                playSound(sound, volume, pitch);
            }
        }
        else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    audience.sendMessage(message.build());
                    if (sound != null) {
                        playSound(sound, volume, pitch);
                    }
                }
            }.runTaskLater(Dungeons.instance, tickDelay);
        }
    }


    public void sendChunkMessage(List<String> chunk, int tickDelay) {
        for (var ch : chunk) sendMessage(ch, tickDelay);
    }

    /**
     * @param message whatever you want to send to the player client.
     * @deprecated in favour of sendActionBar with a TextComponent.Builder instead.
     */
    @Deprecated
	public void sendActionBar(String message)
	{
        Audience audience = player;
        audience.sendActionBar(text(message));
	}

    public void sendActionBar(TextComponent.Builder message) {
        Audience audience = player;
        audience.sendActionBar(message.build());
    }
	
	public int getMaxTargets()
	{
        return Math.min(8, (int) level + 1);
	}
	
	public long getLevel()
	{
        if (cachedLevel) {
            return level;
        }
        level = LevelUtils.GetLevelFromTotalXP(LevelUtils.GetTotalXP(this));
        xp = LevelUtils.GetRemainderXP(LevelUtils.GetTotalXP(this), level);
        cachedLevel = true;
		return level;
	}
	
	public double getLevelProgress()
	{
        var _ = getLevel();
		return ((double)xp) / LevelUtils.getXpForLevel(level + 1);
	}
	
	public long gainXp(long amount)
	{
        xp += amount;
        boolean sounds = true;
		
		while (xp >= LevelUtils.getXpForLevel(level + 1))
		{
			long lvl = getLevel();
            if (sounds) {
                playSound(Sound.ENTITY_PLAYER_LEVELUP, 1.4, 1.1);
                sounds = false;
            }
            sendMessage("<gold><b>-------------------");
            sendMessage("<aqua><b>Level Up! </b>" + lvl + " <blue>-></blue> " + (lvl + 1));
            if (level == 1) {
                sendMessage("<red><b>Ingredient Sack Unlocked!");
                sendMessage("<red>Ingredients will now automatically");
                sendMessage("<red>go into your sack!");
            }
            sendMessage("<gold><b>-------------------");
			
			xp -= LevelUtils.getXpForLevel(level + 1);
			level++;
		}
		
		return amount;
	}

    public long forgeSlots() {
        return stats.stat(Stat.FORGE_SLOTS);
    }
	
	public void scheduleForgeItem(ForgingItem item)
	{
		if (item.time == 0) giveItem(item.produce(), false);
		else forge.add(item);
	}

	public ItemStack getBackpackItem(int slot) {
        return ItemFactory.buildItemFromString(backpack.getOrDefault(slot, null), this);
	}
	public void setBackpackItem(int slot, ItemStack item) {
        String data = ItemFactory.buildStringFromItem(item);
        backpack.put(slot, data);
	}

	
	public boolean isForgeFull()
	{
        return forgeSlots() <= forge.size();
	}

	public Vector getDirection() {
		return player.getEyeLocation().getDirection().normalize();
	}

    /**
     * @deprecated in favour of mana()
     */
    @Deprecated
	public int getMana()
	{
        return (int) (mana * stats.stat(Stat.MANA));
    }

    public void setMana(long amount)
	{
        double am = ((double) amount) / stats.stat(Stat.MANA);

		am = Math.min(1, am);
		am = Math.max(0, am);

		mana = am;
	}

    public long mana() {
        return Math.round(mana * stats.stat(Stat.MANA));
    }

    public void gainMana(long amount)
	{
        if (amount == 0) {
            return;
        }
        long total = getMana() + amount;

		setMana(total);
	}

    @Deprecated
	public void loseMana(int amount)
	{
		int total = getMana() - amount;

		setMana(total);
	}

    public void loseMana(long amount) {
        long total = mana() - amount;

        setMana(total);
    }

    @Deprecated
	public boolean useMana(int amount)
	{
		if (getMana() < amount) return false;
		
		loseMana(amount);
		return true;
	}

    public boolean useMana(long amount) {
        if (getMana() < amount) {
            return false;
        }

        loseMana(amount);
        return true;
    }


    public void heal(long amount)
	{
        if (amount > 0) {
            healthSystem.heal(amount);

            player.heal(1, RegainReason.REGEN);
        }
	}

	
	public int getQuiverCount()
	{
		int c = 0;
		for (String a : quiver.keySet()) c += quiver.get(a);
		
		return c;
	}
	
	public String useArrow()
	{
		String least = null;
		for (String a : quiver.keySet()) 
		{
			if (quiver.get(a) < 1) quiver.remove(a); 
			else if (least == null) least = a;
			else if (quiver.get(least) > quiver.get(a)) least = a;
		}
		if (least == null) return null;
		else {
			quiver.put(least, quiver.get(least) - 1);
			return least;
		}
		
	}
	
	
	private void refreshHealth()
	{
		player.setSaturation(1);
		player.setFoodLevel(20);
        player.setHealth(Math.max(2, healthSystem.getHealthPercentage() * 20));
	}
	
	public void playSound(Sound sound, double volume, double pitch)
	{
		player.playSound(getLocation(), sound, (float)volume, (float)pitch);
	}
	
	public void playSound(Sound sound, double volume, double pitch, int delay)
	{
		if (delay == 0) playSound(sound, volume, pitch);
		new BukkitRunnable() {

			@Override
			public void run() {
				playSound(sound, volume, pitch);
			}
			
		}.runTaskLater(Dungeons.instance, delay);
		
	}
	
	public void kill()
	{
        sendMessage("<red>You were slain..</red>");
		playSound(Sound.ENTITY_PLAYER_DEATH, 1, 0.9);
		player.teleport(new Location(Dungeons.w, 0.5, 65, 0.5));
        player.setFallDistance(0);
        if (area != null) {
            area.getArea().onPlayerDeath(this);
            teleport(area.getArea().getRespawnPoint(this), false);
        }
        else {
            teleport(PlayerTeleport.WATERWAY_SPAWN, false);
        }
        healthSystem.setHealthPercentage(1);
	}

    public void teleport(PlayerTeleport teleport) {
        teleport(teleport, true);
    }

    public void teleport(PlayerTeleport teleport, boolean playSound) {
        if (playSound) {
            playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.8, 1.1);
        }
        if (area != null) {
            area.getArea().onTeleport(this, teleport);
        }
        player.teleport(teleport.GetLocation());
    }
	
	public ItemStack getMainItem()
	{
		return player.getInventory().getItemInMainHand();
	}
	
	@Override
	public Location getLocation()
	{
		return player.getLocation();
	}

    @Override
    public void teleport(@NotNull Location location) {
        playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.8, 1.1);
        player.teleport(location);
    }


    public void incrementKill(String mobId) {
        kills.put(mobId, kills.getOrDefault(mobId, 0L) + 1);
    }

    public long getKills(String mobId) {
        return kills.getOrDefault(mobId, 0L);
    }


	@Override
	public void remove(){
		// ignore any calls to this method.
		
	}

	public Set<String> getPetLines() {
		var set = new HashSet<String>();
		if (activePet != null) {
			ItemPet pet = (ItemPet) ItemFactory.getItem(activePet);
			set.add(pet.petLine);
		}
		for (var p : pets) {
			ItemPet pet = (ItemPet) ItemFactory.getItem(p);
			set.add(pet.petLine);
		}
		return set;
	}

    public void hideEntity(Entity entity) {
        if (entity == null) {
            return;
        }
        player.hideEntity(Dungeons.instance, entity);
    }

    public void showEntity(Entity entity) {
        if (entity == null) {
            return;
        }
        player.showEntity(Dungeons.instance, entity);
    }

    public void RegisterEventHandler(UUID uuid, GameEventHandler handler) {
        eventHandlers.put(uuid, handler);
    }

    public void DeregisterEventHandler(UUID uuid) {
        eventHandlers.remove(uuid);
    }

    public java.util.Collection<GameEventHandler> GetEventHandlers() {
        return eventHandlers.values();
    }

    public Quests getSelectedQuest() {
        if (selectedQuest == null) {
            return null;
        }
        else if (getQuestSave(selectedQuest).completedQuest) {
            var filt = quests.keySet().stream().filter(q -> !getQuestSave(q).completedQuest);
            var g = filt.findFirst();
            if (g.isPresent()) {
                selectedQuest = g.get();
                return selectedQuest;
            }
            else {
                return null;
            }
        }
        else {
            return selectedQuest;
        }
    }

    public void setSelectedQuest(Quests selectedQuest) {
        this.selectedQuest = selectedQuest;
    }

    public void clearQuests() {
        quests.clear();
    }

    public Quests.QuestSave getQuestSave(Quests quest) {
        Quests.QuestSave save = quests.getOrDefault(quest, null);
        if (save == null) {
            save = quest.createSave(this);
            if (save != null) {
                quests.put(quest, save);
            }
        }
        return save;
    }

    public void loadQuestSave(Quests.QuestSave quest) {
        quests.put(quest.GetQuest(), quest);
    }

    public java.util.Collection<Quests.QuestSave> getQuestSaves() {
        return quests.values();
    }


    public boolean isOnInvulnerableCooldown() {
        return invulnerabilityTick > 0;
    }

    public void setOnInvulnerableCooldown() {
        invulnerabilityTick = stats.getStat(Stat.INVULNERABILITY_TICKS);
    }


    public ItemFactory.FactoryBuildContext getItemContext() {
        return new ItemFactory.FactoryBuildContext(this);
    }

    @Override
    public List<? extends ContextWithAbility<? extends GameEntity>> getAggressiveDamageModifiers() {
        return getAbilitiesWith(Ability.class);
    }

    public <A extends Ability> List<PlayerAbilityContext> getAbilitiesWith(Class<A> clazz) {
        var list = new ArrayList<PlayerAbilityContext>();
        for (var ability : abilities) {
            if (clazz.isInstance(ability.ability)) {
                list.add(ability);
            }
        }
        return list;
    }

    /**
     * This just handles any post-attack events we want the entity to work with.
     * e.g. for projectiles this will destroy the projectile, for players this will
     * trigger attack cooldowns.
     */
    @Override
    public void attack() {
        attackTick = 4;
    }

    @Override
    public DamagePacket getBlankDamagePacket() {
        var packet = new DamagePacket();
        packet.aggressor = this;
        packet.attack = AttackType.MELEE;

        // add damages
        long physical = Math.round(getStat(Stat.DAMAGE) * (1D + ((double) getStat(Stat.STRENGTH) / 100D))
                * (1D + ((double) getStat(Stat.POWER) / 100D))
                * (1D + ((double) getStat(Stat.MIGHT) / 100D)));
        packet.addDamage(DamageType.PHYSICAL, physical);

        // add statuses
        for (var s : stats.statuses.effects.keySet()) {
            packet.statusEffects.put(s, (long) stats.statuses.getStatus(s));
        }

        return packet;
    }

    @Override
    public void damage(@NotNull DamagePacket damagePacket) {
        if (healthSystem.damage(damagePacket)) {
            player.playHurtAnimation(0);
            playSound(Sound.ENTITY_PLAYER_HURT, 0.6, 1);
            setOnInvulnerableCooldown();
            if (healthSystem.isDead()) {
                kill();
            }
        }
    }

    @Override
    public List<? extends ContextWithAbility<? extends GameEntity>> getDefensiveDamageModifiers() {
        return List.of();
    }

    @Override
    public boolean isImmune(StatusEffect effect) {
        return false;
    }

    @Override
    public boolean isAlive() {
        return !healthSystem.isDead();
    }

    /**
     *
     * @param by what is attempting to attack us
     * @return false if the victim is currently invulnerable, true otherwise
     */
    @Override
    public boolean isDamageable(AggressiveEntity by) {
        return !isOnInvulnerableCooldown();
    }

    @Override
    public double getHealthPercentage() {
        return healthSystem.getHealthPercentage();
    }

    @Override
    public long getHealth() {
        return healthSystem.getHealth();
    }

    @Override
    public long getStat(Stat stat) {
        if (stats == null) {
            return 0;
        }
        else {
            return stats.stat(stat);
        }
    }

    @Override
    public LivingEntity getTargetableEntity() {
        return player;
    }

    @Override
    public boolean isTargetable(AggressiveEntity by) {
        if (by instanceof GameEnemy enemy) {
            double dist = by.getLocation().distance(getLocation());
            double yDist = by.getLocation().getY() - getLocation().getY();
            yDist = Math.abs(yDist);

            if (player.getGameMode() == GameMode.CREATIVE) {
                return false;
            }
            else {
                var level = enemy.getEnemyData() == null ? 0 : enemy.getEnemyData().level;
                return dist <= stats.stat(Stat.VISIBILITY) && yDist <= 7 && level > stats.stat(Stat.INTIMIDATION);
            }
        }
        else {
            return false;
        }
    }
}
