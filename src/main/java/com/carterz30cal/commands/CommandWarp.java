package com.carterz30cal.commands;

import com.carterz30cal.areas2.PlayerTeleport;
import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.entities.player.GamePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandWarp implements CommandExecutor {

    public CommandWarp() {

    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (commandSender instanceof Player) {
            if (args.length == 0) {
                return false;
            }

            PlayerTeleport teleport = PlayerTeleport.GetTeleport(args[0]);
            if (teleport == null) {
                return false;
            }
            else {
                GamePlayer player = PlayerManager.players.get(((Player) commandSender).getUniqueId());
                player.Teleport(teleport);
                return true;
            }
        }
        else {
            return false;
        }
    }

    public static class WarpTabCompleter implements TabCompleter {

        @Override
        public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
            if (commandSender instanceof Player) {
                if (args.length == 1) {
                    GamePlayer player = PlayerManager.players.get(((Player) commandSender).getUniqueId());
                    List<String> autos = new ArrayList<>();
                    for (var pt : PlayerTeleport.values()) {
                        if (pt.HasRequirements(player)) {
                            autos.add(pt.GetCommandShorthand());
                        }
                    }
                    autos.removeIf(a -> !a.contains(args[0]));
                    return autos;
                }
                else {
                    return null;
                }
            }
            else {
                return null;
            }
        }
    }
}
