package com.carterz30cal.commands;

import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandKit implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] arg3)
	{
		if (sender instanceof Player)
		{
			GamePlayer player = PlayerManager.players.get(((Player) sender).getUniqueId());
			
			String weaponEnchants = "ENCHANT_SHARPNESS-5,ENCHANT_SHOCKED-5,ENCHANT_SHARKING-1,ENCHANT_BLADE-10,ENCHANT_FIREASPECT-4,ENCHANT_CORRUPTMIGHT-8,ENCHANT_SNOWSTORM-10";
			String weaponAttuners = "water_orb,water_orb,water_orb,water_orb,water_orb,water_orb,water_orb,water_orb";
			String armourEnchants = "ENCHANT_TITANIC-5,ENCHANT_ELECTRICHEALTH-5,ENCHANT_PEARLED-10,ENCHANT_HEALTHY-10";
			
			String weap;
			String arm;
			String wan;
			String shi;
			String boww;
			switch (arg3[0])
			{
			case "testing":
				weap = "hammerhead_sword";
				arm = "shark";
				wan = "wand_of_vitality_2";
				shi = "hammerhead_shield";
				boww = "tooth_bow";
				break;
			case "regrowth":
				weap = "hammerhead_sword";
				arm = "regrowth";
				wan = "wand_of_vitality_2";
				shi = "regrowth_shield";
				boww = "tooth_bow";
				break;
			case "op":
				weap = "great_white_shark_scythe";
				arm = "greatwhiteshark";
				wan = "wand_of_vitality_3";
				shi = "shark_shield";
				boww = "horizon_bow";
				break;
			case "hydra":
				weap = "hydra_slayer";
				arm = "hydra";
				wan = "wand_of_vitality_3";
				shi = "shark_shield";
				boww = "horizon_bow";
				break;
			case "flame":
				weap = "super_flaming_sword";
				arm = "greatwhiteshark";
				wan = "wand_of_vitality_3";
				shi = "shark_shield";
				boww = "horizon_bow";
				break;
			case "verg_t1":
				weap = "frost_sword";
				arm = "icer";
				wan = "blizzard_sword_2";
				shi = "dry_ice";
				boww = "blizzard_sword_6";
				break;
			default:
				weap = "flimsy_sword";
				arm = "rotten_fish";
				wan = "wand_of_vitality";
				shi = "flimsy_shield";
				boww = "flimsy_bow";
				break;
			}
			
			
			
			ItemStack scythe = ItemFactory.build(weap);
			ItemFactory.setItemEnchAttunersShortcut(scythe, 
					weaponEnchants,
					weaponAttuners);
			
			ItemStack bow = ItemFactory.build(boww);
			ItemFactory.setItemEnchAttunersShortcut(bow, weaponEnchants, weaponAttuners);
			
			ItemStack wand = ItemFactory.build(wan);
			ItemFactory.setItemEnchAttunersShortcut(wand, 
					weaponEnchants,
					"water_gem,water_gem,water_gem,water_gem,water_gem,water_gem,water_gem,water_gem");

			ItemStack helmet = ItemFactory.build(arm + "_helmet");
			ItemFactory.setItemEnchAttunersShortcut(helmet, 
					armourEnchants + ",ENCHANT_STEALTH-2",
					null);
			
			ItemStack chestplate = ItemFactory.build(arm + "_chestplate");
			ItemFactory.setItemEnchAttunersShortcut(chestplate, 
					armourEnchants,
					null);
			
			ItemStack leggings = ItemFactory.build(arm + "_leggings");
			ItemFactory.setItemEnchAttunersShortcut(leggings, 
					armourEnchants,
					null);
			
			ItemStack boots = ItemFactory.build(arm + "_boots");
			ItemFactory.setItemEnchAttunersShortcut(boots, 
					armourEnchants,
					null);
			
			ItemStack shield = ItemFactory.build(shi);
			
			player.giveItem(scythe);
			player.giveItem(wand);
			player.giveItem(bow);
			player.giveItem(helmet);
			player.giveItem(chestplate);
			player.giveItem(leggings);
			player.giveItem(boots);
			player.giveItem(shield);
		}
		return false;
	}

}
