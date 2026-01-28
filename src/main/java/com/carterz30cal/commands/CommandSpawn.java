package com.carterz30cal.commands;

import com.carterz30cal.entities.EnemyManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSpawn implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args)
	{
		if (!sender.isOp()) sender.sendMessage(ChatColor.RED + "Insufficient permission!");
		else if (sender instanceof Player)
		{
            int amount = 1;
            if (args.length > 1) {
                amount = Integer.parseInt(args[1]);
            }

            while (amount > 0) {
                EnemyManager.spawn(args[0], ((Player) sender).getLocation());
                amount--;
            }
		}
		else sender.sendMessage(ChatColor.RED + "This command can only be executed by players!");
		return true;
	}

}
