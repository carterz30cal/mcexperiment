package com.carterz30cal.items;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.utils.RandomUtils;
import com.carterz30cal.utils.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carterz30cal
 * @version 3
 * @since 1.0.0
 */
public class ItemLootTable
{
    private final List<ItemLoot> loot = new ArrayList<>();


	public int getDropCount() {
        return loot.size();
	}

	public ItemLootTable(ConfigurationSection section) {
        for (String drop : section.getKeys(false)) {
            int[] chance = StringUtils.convertStringToFraction(section.getString(drop + ".chance", "1/1"));
            int[] amount = StringUtils.convertStringToFraction(section.getString(drop + ".amount", "1/1"));

            String enchants = section.getString(drop + ".enchants", "");

            addDrop(drop.split("-")[0], amount, chance, enchants);
        }
	}

	public List<ContextualDrop> generateWithContexts(GamePlayer player) {
		List<ContextualDrop> drops = new ArrayList<>();
		for (ItemLoot l : loot)
		{
			if (l.rollDrop(player.stats.getStat(Stat.LUCK)))
			{
				drops.add(new ContextualDrop(l));
			}
		}

		return drops;
	}

	/**
	 * Receive one item from the loot table, using the chances as weights.
	 * @param player the player we're dropping for
	 * @return a drop
	 * @since 1.0.0 [3]
	 * @throws IllegalStateException if somehow we don't have correct weights?
	 */
	public ContextualDrop generateContextOne(GamePlayer player) {
		var total = 0;
		for (var l : loot) total += l.chance[0];
		var choice = RandomUtils.getRandomEx(0, total);
		for (var l : loot) {
			if (choice < l.chance[0]) {
				return new ContextualDrop(l);
			}
			else choice -= l.chance[0];
		}
		throw new IllegalStateException("Somehow the weights didn't add up!");
	}

	
	public void addDrop(String item, int[] amount, int[] chance)
	{
		ItemLoot drop = new ItemLoot();
		drop.item = item;
		drop.amount = amount;
		drop.chance = chance;
		
		figureOutRarity(drop);

		loot.add(drop);
	}

	/**
	 *
	 * @param item the item to drop
	 * @param lowerAmount lower bound of the amount dropped
	 * @param upperAmount upper bound of the amount dropped
	 * @param chance the numerator of the chance
	 * @param outOf the denominator of the chance
	 * @apiNote if using <code>ItemLootTable</code> as a weighted loot table, set <code>outOf = 1</code>.
	 * @since 1.0.0 [3]
	 */
	public void add(String item, int lowerAmount, int upperAmount, int chance, int outOf) {
		var drop = new ItemLoot();
		drop.item = item;
		drop.amount = new int[] {lowerAmount, upperAmount};
		drop.chance = new int[] {chance, outOf};

		figureOutRarity(drop);
		loot.add(drop);
	}


	public void addDrop(String item, int[] amount, int[] chance, String enchants)
	{
		ItemLoot drop = new ItemLoot();
		drop.item = item;
		drop.amount = amount;
		drop.chance = chance;
		drop.enchant = enchants;
		
		figureOutRarity(drop);
		loot.add(drop);
	}

	/**
	 * Figure out the rarity of an <code>ItemLoot</code>.
	 * @param loot the <code>ItemLoot</code>
	 * @since 1.0.0 [3]
	 */
	private void figureOutRarity(ItemLoot loot) {
		double oddsIn = (double) loot.chance[1] / loot.chance[0];
		for (int r = ItemRarity.values().length - 1; r >= 0; r--)
		{
			ItemRarity rarity = ItemRarity.values()[r];
			if (rarity.lootboxOdds == -1) continue;
			if (rarity.lootboxOdds <= oddsIn)
			{
				loot.rarity = rarity;
				break;
			}
		}
		for (int r = ItemRarity.values().length - 1; r >= 0; r--)
		{
			ItemRarity rarity = ItemRarity.values()[r];
			if (rarity.lootOdds == -1) continue;
			if (rarity.lootOdds <= oddsIn) {
				loot.announcementRarity = rarity;
				break;
			}
		}
	}

    public List<ItemLoot> GetLoot() {
        return loot;
    }

	public ItemLootTable() {

	}

	public List<ItemStack> generate(GamePlayer player)
	{
		List<ItemStack> drops = new ArrayList<>();
		for (ItemLoot l : loot)
		{
			if (l.rollDrop(player.stats.getStat(Stat.LUCK)))
			{
				ItemStack it = l.generate();
                if (l.announcementRarity != ItemRarity.COMMON) {
                    var colour = l.announcementRarity.textColor.asHexString() + ">";
                    player.sendMessage("<" + colour + "<b>" + l.announcementRarity.name.toUpperCase() + " DROP! " + ItemFactory.getItem(it).name);
                }


				drops.add(it);
			}
		}

		return drops;
	}

    public static class ContextualDrop {
        private final ItemStack itemStack;
        private final ItemRarity rarity;

		public ItemStack getItemStack() {
			return itemStack.clone();
		}
		public ItemRarity getItemRarity() {
			return rarity;
		}

		protected ContextualDrop(ItemLoot loot) {
			itemStack = loot.generate();
			rarity = loot.rarity;
		}
	}

    public static class ItemLoot
	{
		public String item;
		public int[] amount;
		public int[] chance;
		public ItemRarity rarity;
		public ItemRarity announcementRarity;
		public String enchant;
		
		public boolean rollDrop()
		{
			return rollDrop(0);
		}
		public boolean rollDrop(int luckStat)
		{
			int pick = RandomUtils.getRandom(1, chance[1]);
			return pick <= Math.round(chance[0] * ((100D + luckStat) / 100D));
		}
		
		public ItemStack generate()
		{
			ItemStack gen = ItemFactory.build(item, RandomUtils.getRandom(amount[0], amount[1]));
            if (gen == null) {
                Dungeons.instance.getLogger().severe("ItemLootTable: Could not generate item! " + item);
                return null;
            }
			if (enchant != null) 
			{
				ItemFactory.addItemData(gen, "enchants", enchant);
			}

            ItemFactory.update(gen, null);
			return gen;
		}
	}
}
