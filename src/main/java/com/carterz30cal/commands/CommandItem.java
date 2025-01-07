package com.carterz30cal.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.carterz30cal.items.ItemFactory;

public class CommandItem implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String label, String[] args) 
	{
		if (!sender.isOp()) sender.sendMessage(ChatColor.RED + "Insufficient permission!");
		else if (sender instanceof Player)
		{
			int amount = 1;
			if (args.length > 1) amount = Integer.parseInt(args[1]);
			
			ItemStack built = ItemFactory.build(args[0], amount);
			
			if (args.length > 2) 
			{
				String enchData = args[2];
				ItemFactory.addItemData(built, "enchants", enchData);
				ItemFactory.update(built, null);
			}
			
			((Player)sender).getInventory().addItem(built);
		}
		else sender.sendMessage(ChatColor.RED + "This command can only be executed by players!");
		return true;
	}

}
