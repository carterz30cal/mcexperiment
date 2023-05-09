package com.carterz30cal.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.utils.LevelUtils;

public class CommandSetLevel implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args)
	{
		if (sender.isOp())
		{
			int level;
			Player target;
			if (args.length == 1 && sender instanceof Player)
			{
				level = Integer.parseInt(args[0]);
				target = (Player)sender;
			}
			else if (args.length == 1) return false;
			else
			{
				level = Integer.parseInt(args[1]);
				target = Bukkit.getPlayer(args[0]);
			}
			
			level = Math.min(level, LevelUtils.LEVEL_MAX);
			
			
			GamePlayer player = PlayerManager.players.get(target.getUniqueId());
			if (level == 0)
			{
				player.discoveries.clear();
			}
			player.level = 0;
			player.xp = 0;
			while (player.level < level)
			{
				player.gainXp(LevelUtils.getXpForLevel(player.level + 1));
			}
			return true;
		}
		else return false;
	}

}
