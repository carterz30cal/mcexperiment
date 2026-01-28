package com.carterz30cal.areas2.bosses;

import com.carterz30cal.areas2.spawners.AbstractEnemySpawner;
import com.carterz30cal.entities.GamePlayer;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class AbstractAreaBoss extends AbstractEnemySpawner {
    protected boolean active = false;

    public void OnRightClick(GamePlayer player, Location clicked) {

    }

    public void OnLeftClick(GamePlayer player, Location clicked) {

    }

    public void OnPlayerDeath(GamePlayer player) {

    }

    public boolean IsActive() {
        return active;
    }

    public boolean IsPlayerInvolved(GamePlayer player) {
        return false;
    }

    public String GetName(GamePlayer player) {
        return "Boss";
    }

    public List<String> GetScoreboard(GamePlayer player) {
        List<String> list = new ArrayList<String>();
        list.add(GetName(player));
        return list;
    }
}
