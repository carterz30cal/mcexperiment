package com.carterz30cal.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.carterz30cal.entities.EnemyManager;
import com.carterz30cal.entities.GameOwnable;

public class CommandSpawn implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args)
	{
		if (!sender.isOp()) sender.sendMessage(ChatColor.RED + "Insufficient permission!");
		else if (sender instanceof Player)
		{
			if (args[0].equals("test_ownable")) {
				new GameOwnable("hahaha", ((Player)sender).getLocation(), EntityType.ZOMBIE);
			}
			else {
				int amount = 1;
				if (args.length > 1) amount = Integer.parseInt(args[1]);
				
				while (amount > 0) 
				{
					EnemyManager.spawn(args[0], ((Player)sender).getLocation());
					amount--;
				}
			}
		}
		else sender.sendMessage(ChatColor.RED + "This command can only be executed by players!");
		return true;
	}

}
