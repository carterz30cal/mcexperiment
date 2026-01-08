package com.carterz30cal.commands;

import com.carterz30cal.entities.Shop;
import com.carterz30cal.gui.ShopGUI;
import com.carterz30cal.utils.LevelUtils;
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

public class CommandForce implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args)
	{
		if (!sender.isOp()) sender.sendMessage(ChatColor.RED + "Insufficient permission!");
		else
		{
			GamePlayer p = PlayerManager.players.get(((Player) sender).getUniqueId());
			switch (args[0]) {
				case "calcxp":
					int arg = Integer.parseInt(args[1]);
					p.sendMessage(Long.toString(LevelUtils.getEnemyBaseXpReward(arg)));
					break;
				case "togglerain":
					AreaWaterway.instance.toggleRain();
					break;
				case "startseraphfight":
					BossWaterwaySeraph.attemptStartFight();
					break;
				case "endseraphfight":
					BossWaterwaySeraph.endFight();
					break;
				case "openshop":
					p = PlayerManager.players.get(((Player) sender).getUniqueId());
					p.openGui(new ShopGUI(p, Shop.shops.get(args[1])));
					break;
				case "gentest":
					p = PlayerManager.players.get(((Player) sender).getUniqueId());
					new TestDungeon(p);
					break;
			}
		}
		return true;
	}

}
