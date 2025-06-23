package com.carterz30cal.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.carterz30cal.items.sets.ItemSet;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.carterz30cal.areas.BossWaterwayHydra;
import com.carterz30cal.dungeoneering.DungeonManager;
import com.carterz30cal.entities.enemies.EnemyTypeFish;
import com.carterz30cal.gui.AbstractGUI;
import com.carterz30cal.items.Collection;
import com.carterz30cal.items.DiscoveryManager;
import com.carterz30cal.items.ForgingItem;
import com.carterz30cal.items.Item;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.items.ItemAttuner;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.items.ItemTypeUse;
import com.carterz30cal.items.Recipe;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.mining.Mineable;
import com.carterz30cal.quests.AbstractQuestType;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatOperationType;
import com.carterz30cal.utils.EntityUtils;
import com.carterz30cal.utils.LevelUtils;
import com.carterz30cal.utils.ScoreboardWrapper;
import com.carterz30cal.utils.StringUtils;

//import de.ancash.actionbar.ActionBarAPI;

public class GamePlayer extends GameEntity
{
	public Player player;
	public StatContainer stats;
	public AbstractGUI gui;

	public Map<String, Integer> sets = new HashMap<>();
	
	public long coins;
	
	public long level;
	public long xp;
	
	public List<ForgingItem> forge = new ArrayList<>();
	
	public List<ItemAbility> abilities;
	
	public List<String> talismans = new ArrayList<>();
	public List<String> completedQuests = new ArrayList<>();
	public Map<String, AbstractQuestType> quests = new HashMap<>();
	
	public String dungeonId;
	
	
	
	public boolean flagIgnoreInvClose;
	
	private double mana;
	
	private int regenTick;
	
	public int attackTick;
	public int bowTick;
	public int questTick;
	
	public long lastXpReward;
	public int lastCoinReward;
	public int rewardTick;
	
	public boolean mining;
	public boolean allowInteract;
	
	public GameEnemy lastDamager;
	
	public Map<String, Integer> discoveries = new HashMap<>();
	public Map<String, Integer> quiver = new HashMap<>();
	public Map<String, Integer> counters = new HashMap<>();
	public Map<String, Integer> sack = new HashMap<>();
	public Map<Integer, String> backpack = new HashMap<>();
	
	public List<GameEnemy> targeted = new ArrayList<>();
	
	private ScoreboardWrapper scoreboard;
	
	
	
	protected void register(UUID uuid)
	{
		player.getPersistentDataContainer().set(GameEnemy.keyEnemy, PersistentDataType.STRING, uuid.toString());
		
		mana = 1;
		
		entities.put(uuid, this);
		
		scoreboard = new ScoreboardWrapper("Dungeons");
		scoreboard.addLine("GOLDCoins: WHITE0");
		scoreboard.addBlankSpace();
		scoreboard.addLine("AQUALevel X");
		player.setScoreboard(scoreboard.getScoreboard());
	}
	
	public void tick()
	{
		for (ForgingItem item : forge)
		{
			//item.time--;
			if (LocalDateTime.now().isAfter(item.finished))
			{
				if (player.getInventory().firstEmpty() == -1) {
					if (!item.haveNotified) {
						sendMessage("REDYour " + ItemFactory.getItemTypeName(item.item) + " REDis done, but you don't have spare room in your inventory.");
						item.haveNotified = true;
					}
				} else {
					sendMessage("GREENYour " + ItemFactory.getItemTypeName(item.item) + " GREENis done!");
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
		else {
			EntityUtils.applyPotionEffect(player, PotionEffectType.MINING_FATIGUE, 5, 0, false);
		}

		player.removePotionEffect(PotionEffectType.DARKNESS);
		
		
		abilities = new ArrayList<>();
		stats = new StatContainer();
		
		stats.scheduleOperation(Stat.HEALTH, StatOperationType.ADD, 100);
		stats.scheduleOperation(Stat.HEALTH, StatOperationType.CAP_MIN, 10);
		stats.scheduleOperation(Stat.VITALITY, StatOperationType.ADD, 5);
		stats.scheduleOperation(Stat.VITALITY, StatOperationType.CAP_MIN, 0);
		stats.scheduleOperation(Stat.VISIBILITY, StatOperationType.ADD, 16);
		stats.scheduleOperation(Stat.VISIBILITY, StatOperationType.CAP_MIN, 1);
		stats.scheduleOperation(Stat.VISIBILITY, StatOperationType.CAP_MAX, 24);
		stats.scheduleOperation(Stat.FOCUS, StatOperationType.CAP_MIN, 0);

		stats.scheduleOperation(Stat.POWER, StatOperationType.CAP_MIN, 0);
		stats.scheduleOperation(Stat.MIGHT, StatOperationType.CAP_MIN, 0);
		stats.scheduleOperation(Stat.STRENGTH, StatOperationType.CAP_MIN, 0);
		
		// level stats!
		
		long levelTens = level / 5;
		
		stats.scheduleOperation(Stat.HEALTH, StatOperationType.ADD, (level - levelTens) * 8);
		stats.scheduleOperation(Stat.DEFENCE, StatOperationType.ADD, levelTens * 2);
		stats.scheduleOperation(Stat.POWER, StatOperationType.ADD, levelTens);
		
		stats.scheduleOperation(Stat.SACK_SPACE, StatOperationType.ADD, Math.max(0, level - 1) * 500);
		stats.scheduleOperation(Stat.SACK_SPACE, StatOperationType.ADD, (level / 8) * 1500);
		
		
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
			if (mainItem.type.use == ItemTypeUse.WIELDABLE || mainItem.type.use == ItemTypeUse.WIELDABLE_CONSUMABLE) items.add(main);
			else ItemFactory.update(main, this);
		}
		if (offItem != null)
		{
			if (offItem.type.use == ItemTypeUse.OFFHAND) items.add(off);
			else ItemFactory.update(off, this);
		}
		
		for (String talisman : talismans) items.add(ItemFactory.build(talisman));


		for (String s : sets.keySet()) {
			if (hasSet(s)) items.add(ItemFactory.build(s));
		}

		for (ItemStack item : items)
		{
			ItemFactory.update(item, this);
			Item i = ItemFactory.getItem(item);
			if (i == null) continue;
			StatContainer itemStats = i.stats.clone();
			for (ItemAttuner attuner : ItemFactory.getAttuners(item)) attuner.stats.pushIntoContainer(itemStats);
			//List<ItemEnchant> enchants = ItemFactory.getItemEnchants(item);
			
			List<ItemAbility> iAbilities = ItemFactory.getItemAbilities(item, this);
			for (ItemAbility e : iAbilities) e.onItemStats(itemStats);
			itemStats.executeOperations();
			for (ItemAbility e : iAbilities) e.onItemStatsLate(itemStats);
			
			itemStats.pushIntoContainer(stats);
			abilities.addAll(iAbilities);
		}
		
		abilities.addAll(quests.values());

		stats.executeOperations();
		for (ItemAbility a : abilities) a.onPlayerStats(stats);
		stats.executeOperations();

		stats.scheduleOperation(Stat.BACKPACK_PAGES, StatOperationType.ADD, 1);
		stats.scheduleOperation(Stat.BACKPACK_PAGES, StatOperationType.CAP_MIN, 1);


		
		String actionBar = "RED" + getHealth() + "\u2665";
		if (stats.getStat(Stat.MANA) > 0) actionBar += " LIGHT_PURPLE" + getMana() + "\u2605";
		//actionBar += mana;
		if (rewardTick > 0) 
		{
			rewardTick--;
			if (lastXpReward > 0) actionBar += "  AQUA+" + lastXpReward + " XP";
			if (lastCoinReward > 0) actionBar += "  GOLD+" + lastCoinReward + " coins";
		} 
		
		if (attackTick > 0) attackTick--;
		if (bowTick > 0) bowTick--;
		if (questTick > 0) questTick--;
		
		sendActionBar(actionBar);
		player.getInventory().setItem(8, ItemFactory.menuItem);
		player.setPlayerListName(StringUtils.colourString("GRAY[WHITE" + level + "GRAY] " + player.getDisplayName()));
		
		regenTick++;
		if (regenTick >= 40)
		{
			regenTick = 0;
			
			gainHealth(stats.getStat(Stat.VITALITY));
			gainMana(1 + stats.getStat(Stat.FOCUS));
		}
		
		refreshHealth();
		
		List<String> score = new ArrayList<>();
		score.add("GOLDCoins: WHITE" + StringUtils.commaify((int) coins));
		score.add("");
		if (BossWaterwayHydra.hasParticipated(this)) {
			score.add("GREENBOLDHydra - Wave " + BossWaterwayHydra.wave);
			score.add("");
		}
		if (quests.size() != 0) {
			List<AbstractQuestType> qs = new ArrayList<>(quests.values());
			qs.sort((a,b) -> Integer.compare(b.getQuestPriority(), a.getQuestPriority()));
			
			AbstractQuestType selected = qs.get(0);
			if (selected != null) {
				score.add("GOLDCurrent Quest:");
				score.addAll(selected.description());
				score.add("");
			}
			
		}
		score.add("AQUALevel " + getLevel() + "BLACK [AQUA+" + Math.round(this.getLevelProgress() * 100) + "%BLACK]");
		
		
		
		
		if (score.size() < scoreboard.size()) {
			scoreboard = new ScoreboardWrapper("Dungeons");
			player.setScoreboard(scoreboard.getScoreboard());
		}
		for (int l = 0; l < score.size(); l++) {
			if (l < scoreboard.size()) scoreboard.setLine(l, score.get(l));
			else scoreboard.addLine(score.get(l));
		}
		
		// set targets
		targeted.removeIf((e) -> e.dead || e.target != this);
		for (GameEnemy nearby : EntityUtils.getNearbyEnemies(getLocation(), stats.getStat(Stat.VISIBILITY)))
		{
			if (nearby instanceof GameSummon) continue;
			if (targeted.size() >= getMaxTargets() && !nearby.type.ignoreTargetLimit) continue;
			
			if (nearby.target == null && isTargetable(nearby))
			{
				targeted.add(nearby);
				nearby.target = this;
			}
		}
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
				// TODO Auto-generated method stub
				that.gui = gui;
				flagIgnoreInvClose = true;
				that.gui.open();
				flagIgnoreInvClose = false;
				that.gui.runTaskTimer(Dungeons.instance, 1, 1);
			}
			
		}.runTaskLater(Dungeons.instance, 1);
	}

	public boolean hasSet(String s) {
		if (sets.size() == 0) return false;
		Item i = ItemFactory.getItem(s);
		if (i != null && i instanceof ItemSet) {
			ItemSet set = (ItemSet) i;
			if (set.requireCount <= sets.getOrDefault(s, 0)) return true;
		}
		return false;
	}
	
	public int getDiscoveryLevel(Collection discovery)
	{
		int count = discoveries.getOrDefault(discovery.id, 0);
		int level = 0;
		
		while (level < discovery.tiers.size() && discovery.tiers.get(level) < count) level++;
		return level;
	}
	
	public int getSackSize() {
		return stats.getStat(Stat.SACK_SPACE);
	}


	/**
	@deprecated Use getSackSpaceUsed() instead
	 */
	@Deprecated
	public int getSackUsed() {
		int used = 0;
		for (int i : sack.values()) used += i;
		
		return used;
	}

	public int getSackSpaceUsed() {
		int used = 0;
		for (int i : sack.values()) used += i;

		return used;
	}
	public int getSackSpaceRemaining() {
		return getSackSize() - getSackSpaceUsed();
	}

	
	public boolean hasSackSpace(int am) {
		return getSackUsed() + am <= getSackSize();
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
				if (currentLevel == 0 && discoveries.getOrDefault(col.id, 0) == 0) sendMessage("GOLDBOLDNew Discovery! " + col.name);
				
				discoveries.put(col.id, discoveries.getOrDefault(col.id, 0) + item.getAmount() * i.discoveryProgress);
				
				int newLevel = getDiscoveryLevel(col);
				while (newLevel > currentLevel)
				{
					sendMessage("YELLOW - - - GOLDDISCOVERY LEVEL UPYELLOW - - -");
					for (String recipe : col.recipes.getOrDefault(currentLevel + 1, new ArrayList<>())) 
					{
						Recipe r = ItemFactory.recipes.get(recipe);
						
						String n = r.customName != null ? r.customName : ItemFactory.getItemTypeName(r.item);
						sendMessage("DARK_GRAY- " + n + " DARK_GRAY[Recipe]");
					}
					sendMessage("DARK_GRAY- AQUA+" + col.xpRewards.get(currentLevel) + "XP");
					
					gainXp(col.xpRewards.get(currentLevel));
					currentLevel++;
				}
			}
			
			if (i.type == ItemType.ARROW) quiver.put(i.id, quiver.getOrDefault(i.id, 0) + item.getAmount());
			else if (i.type == ItemType.INGREDIENT && hasSackSpace(item.getAmount()))
			{
				int am = sack.getOrDefault(i.id, 0) + item.getAmount();
				sack.put(i.id, am);
			}
			else player.getInventory().addItem(item);
		}
		else player.getInventory().addItem(item);
	}
	
	public void sendMessage(String message)
	{
		sendMessage(message, 0);
	}
	public void sendMessage(String message, int tickDelay)
	{
		String edited = StringUtils.colourString(message);
		if (tickDelay == 0) player.sendMessage(edited);
		else
		{
			new BukkitRunnable()
			{

				@Override
				public void run() {
					player.sendMessage(edited);
				}
				
			}.runTaskLater(Dungeons.instance, tickDelay);
		}
	}
	public void sendMessage(String message, Sound sound)
	{
		sendMessage(message, sound, 0);
	}
	public void sendMessage(String message, Sound sound, int tickDelay)
	{
		String edited = StringUtils.colourString(message);
		if (tickDelay == 0) {
			player.sendMessage(edited);
			playSound(sound, 0.6, 0.8);
		}
		else
		{
			new BukkitRunnable()
			{

				@Override
				public void run() {
					player.sendMessage(edited);
					playSound(sound, 0.6, 0.8);
				}
				
			}.runTaskLater(Dungeons.instance, tickDelay);
		}
	}
	
	public void sendTitle(String top, String sub, int in, int stay, int out) {
		player.sendTitle(StringUtils.colourString(top), StringUtils.colourString(sub), in, stay, out);
	}
	
	public void sendActionBar(String message)
	{
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(StringUtils.colourString(message)));
		//ActionBarAPI.sendActionBar(player, StringUtils.colourString(message));
	}

	@Override
	public boolean isTargetable(GameEnemy by)
	{
		if (by instanceof GameSummon) return false;
		double dist = by.getLocation().distance(getLocation());
		double yDist = by.getLocation().getY() - getLocation().getY();
		yDist = Math.abs(yDist);

		if (player.getGameMode() == GameMode.CREATIVE) return false;
		else return dist <= stats.getStat(Stat.VISIBILITY) && yDist <= 5;
	}

	@Override
	public LivingEntity getTargetable() {
		return player;
	}
	
	public int getMaxTargets()
	{
		return Math.min(5, (int)level + 1);
	}
	
	public long getLevel()
	{
		return level;
	}
	
	public double getLevelProgress()
	{
		return ((double)xp) / LevelUtils.getXpForLevel(level + 1);
	}
	
	public long gainXp(long amount)
	{
		long mod = amount;
		
		xp += mod;
		
		while (xp >= LevelUtils.getXpForLevel(level + 1))
		{
			long lvl = getLevel();
			playSound(Sound.ENTITY_PLAYER_LEVELUP, 1.4, 1.1);
			sendMessage("GOLDBOLD-------------------");
			sendMessage("AQUABOLDLevel Up! RESETAQUA" + lvl + " BLUE->AQUA " + (lvl+1));
			if (level == 1) sendMessage("REDBOLDIngredient Sack Unlocked!");
			sendMessage("GOLDBOLD-------------------");
			
			xp -= LevelUtils.getXpForLevel(level + 1);
			level++;
		}
		
		return mod;
	}
	
	public int gainCoins(GameEnemy killed)
	{
		int total = 1 + (killed.type.health / 75);
		total += killed.type.damage / 25;
		total += killed.type.level / 2;

		double extraCoinsMultiplier = (100D + stats.getStat(Stat.BONUS_COINS)) / 100;
		
		coins += Math.round(total * extraCoinsMultiplier * killed.type.coinMultiplier);
		return total;
	}
	
	
	public int getForgeSlots()
	{
		int slots = 4;
		if (level >= 5) slots++;
		
		return slots;
	}
	
	@Override
	public int getHealth() {
		// TODO Auto-generated method stub
		return (int) (stats.getStat(Stat.HEALTH) * health);
	}
	
	public void setHealth(int amount)
	{
		double percent = amount / (double)stats.getStat(Stat.HEALTH);
		health = percent;
		
		health = Math.max(0, health);
		health = Math.min(1, health);
		
		refreshHealth();
	}
	
	public void scheduleForgeItem(ForgingItem item)
	{
		if (item.time == 0) giveItem(item.produce(), false);
		else forge.add(item);
	}

	public ItemStack getBackpackItem(int slot) {
		String i = backpack.getOrDefault(slot, null);
		if (i == null) return null;

		String[] spl = i.split("£");
		ItemStack item = ItemFactory.build(spl[0]);
		if (item == null) return null;
		else {
			if (spl.length == 1) {
			}
			else if (spl.length == 2) {
				item.setAmount(Integer.parseInt(spl[1]));
			}
			else {
				item.setAmount(Integer.parseInt(spl[1]));
				ItemFactory.setItemData(item, spl[2]);
				ItemFactory.update(item, this);
			}
			return item;
		}
	}
	public void setBackpackItem(int slot, ItemStack item) {
		Item itemType = ItemFactory.getItem(item);
		if (itemType == null) backpack.put(slot, null);
		else {
			String data = ItemFactory.getFlatItemData(item);
			if (data == null || data.equals("")) {
				backpack.put(slot, itemType.id + "£" + item.getAmount());
			}
			else {
				backpack.put(slot, itemType.id + "£" + item.getAmount() + "£" + data);
			}
		}
	}

	
	public boolean isForgeFull()
	{
		return getForgeSlots() <= forge.size();
	}
	
	public int getMagicDamage(double powerScaling, double manaScaling) {
		double damage = stats.getStat(Stat.DAMAGE);
		double power = (100D + (stats.getStat(Stat.POWER) * powerScaling)) / 100D;
		double mana = (100D + (stats.getStat(Stat.MANA) * manaScaling)) / 100D;
		
		damage *= power * mana;
		return (int)Math.round(damage);
	}
	
	
	public Vector getDirection() {
		return player.getEyeLocation().getDirection().normalize();
	}
	
	public int getMana()
	{
		return (int) (mana * stats.getStat(Stat.MANA));
	}
	
	public void gainMana(int amount)
	{
		int total = getMana() + amount;
		
		setMana(total);
	}
	
	public void loseMana(int amount)
	{
		int total = getMana() - amount;
		
		setMana(total);
	}
	
	public void setMana(int amount)
	{
		double am = ((double)amount)/stats.getStat(Stat.MANA);
		
		am = Math.min(1, am);
		am = Math.max(0, am);
		
		mana = am;
	}
	
	public boolean useMana(int amount)
	{
		if (getMana() < amount) return false;
		
		loseMana(amount);
		return true;
	}
	
	
	public void gainHealth(int amount)
	{
		int total = getHealth() + amount;
		setHealth(total);
		
		
		EntityRegainHealthEvent e = new EntityRegainHealthEvent(player, 0, RegainReason.CUSTOM);
		Bukkit.getPluginManager().callEvent(e);
		/*
		
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        PacketContainer packet = protocolManager.createPacket(Server.UPDATE_HEALTH);
        
        packet.getFloat()
        .write(0, (float) health)
        .write(1, 5F);
        packet.getIntegers()
        .write(0, 20);
        
        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        */
	}
	
	public boolean inDungeon() {
		return DungeonManager.dungeons.getOrDefault(dungeonId, null) != null;
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
	
	public int getFishingBracket()
	{
		return Math.min(stats.getStat(Stat.FISHING_POWER) / 100, EnemyTypeFish.maxBracket);
	}
	
	
	private void refreshHealth()
	{
		player.setSaturation(1);
		player.setFoodLevel(20);
		player.setHealth(Math.max(2, health * 20));
	}
	
	public void takeHealth(int amount)
	{
		int total = getHealth() - amount;
		
		setHealth(total);
		if (health == 0) kill();
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
				// TODO Auto-generated method stub
				playSound(sound, volume, pitch);
			}
			
		}.runTaskLater(Dungeons.instance, delay);
		
	}
	
	public void kill()
	{
		sendMessage("REDYou were slain..");
		playSound(Sound.ENTITY_PLAYER_DEATH, 1, 0.9);
		player.teleport(new Location(Dungeons.w, 0.5, 65, 0.5));
		
		health = 1;
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
	public void remove(){
		// ignore any calls to this method.
		
	}

	
	public void damage(int damage)
	{
		for (ItemAbility a : abilities) damage = a.onDamaged(this.lastDamager, damage);
		
		
		takeHealth(damage);
		player.damage(1);
	}
	
	@Override
	public void damage(DamageInfo info)
	{
		for (ItemAbility a : abilities) info.damage = a.onDamaged(this.lastDamager, info.damage);
		
		takeHealth(info.damage);
		player.damage(1);
	}
	
}
