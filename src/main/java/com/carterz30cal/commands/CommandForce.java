package com.carterz30cal.commands;

import com.carterz30cal.areas.areas.GameAreaWaterway;
import com.carterz30cal.areas.bosses.waterway.AreaBossWaterwaySeraph;
import com.carterz30cal.areas.events.EventManager;
import com.carterz30cal.areas.events.waterway.WaterwayRainEvent;
import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.entities.Shop;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.gui.ShopGUI;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.LevelUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandForce implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args)
	{
		if (!sender.isOp()) sender.sendMessage(ChatColor.RED + "Insufficient permission!");
		else
		{
			GamePlayer p = PlayerManager.players.get(((Player) sender).getUniqueId());
			switch (args[0]) {
				case "rain":
					GameAreaWaterway.downpour.start();
					break;
				case "calcxp":
					int arg = Integer.parseInt(args[1]);
					p.sendMessage(Long.toString(LevelUtils.getEnemyBaseXpReward(arg)));
					break;
                case "clearquests":
                    p.clearQuests();
                    p.setSelectedQuest(null);
                    break;
                case "seraph":
                    AreaBossWaterwaySeraph.instance.register(p);
                    break;
                case "checkanticheat":
                    var block = Dungeons.instance.getServer().createBlockData(Material.AIR);
                    p.player.sendBlockChange(p.player.getLocation().subtract(0, 1, 0), block);
                    break;
				case "openshop":
					p.openGui(new ShopGUI(p, Shop.shops.get(args[1])));
					break;
			}
		}
		return true;
	}

}
