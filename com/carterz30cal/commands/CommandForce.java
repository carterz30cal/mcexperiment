package com.carterz30cal.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.carterz30cal.areas.AreaWaterway;
import com.carterz30cal.areas.BossWaterwaySeraph;
import com.carterz30cal.dungeoneering.AbstractDungeon;
import com.carterz30cal.dungeoneering.TestDungeon;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.quests.Quest;

public class CommandForce implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args)
	{
		if (!sender.isOp()) sender.sendMessage(ChatColor.RED + "Insufficient permission!");
		else
		{
			
			switch (args[0])
			{
			case "togglerain":
				AreaWaterway.instance.toggleRain();
				break;
			case "startseraphfight":
				BossWaterwaySeraph.attemptStartFight();
				break;
			case "endseraphfight":
				BossWaterwaySeraph.endFight();
				break;
			case "clearmyquests":
				GamePlayer p = PlayerManager.players.get(((Player)sender).getUniqueId());
				p.quests.clear();
				p.completedQuests.clear();
				p.completedQuests.add("player_joined");
				for (Quest q : Quest.quests.values()) q.instance.refresh();
				break;
			case "gentest":
				p = PlayerManager.players.get(((Player)sender).getUniqueId());
				new TestDungeon(p);
				break;
			}
		}
		return true;
	}

}
