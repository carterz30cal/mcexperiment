package com.carterz30cal.areas2;

import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.main.Dungeons;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class AreaManager extends BukkitRunnable {
    private static final AreaManager instance;

    static {
        instance = new AreaManager();
        instance.runTaskTimer(Dungeons.instance, 1, 1);
    }

    public static Areas getPlayerArea(GamePlayer player) {
        if (player == null) {
            return null;
        }
        for (var area : Areas.values()) {
            if (area.getArea().IsInBounds(player)) {
                return area;
            }
        }
        return null;
    }

    public static List<GamePlayer> GetPlayers(Areas area) {
        List<GamePlayer> players = new ArrayList<>();
        for (var player : PlayerManager.getOnlinePlayers()) {
            if (player.area == area) {
                players.add(player);
            }
        }
        return players;
    }

    @Override
    public void run() {
        for (var area : Areas.values()) {
            area.getArea().Tick();
        }
    }
}
