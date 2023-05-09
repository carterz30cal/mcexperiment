package com.carterz30cal.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.items.ItemFactory;

public class CommandMax implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		if (sender instanceof Player)
		{
			GamePlayer player = PlayerManager.players.get(((Player) sender).getUniqueId());
			
			String weaponEnchants = "ENCHANT_SHARPNESS-5,ENCHANT_SHOCKED-5,ENCHANT_SHARKING-1,"
					+ "ENCHANT_BLADE-10,ENCHANT_FIREASPECT-4,ENCHANT_CORRUPTMIGHT-8,"
					+ "ENCHANT_SNOWSTORM-10";
			String weaponAttuners = "water_orb,water_orb,water_orb,water_orb,water_orb,water_orb,water_orb,water_orb";
			
			ItemStack item = player.player.getInventory().getItemInMainHand();
			ItemFactory.setItemEnchAttunersShortcut(item, 
					weaponEnchants,
					weaponAttuners);
			
			
			return true;
		}
		else {
			return false;
		}
	}

}
