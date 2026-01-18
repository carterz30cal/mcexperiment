package com.carterz30cal.areas2;

import com.carterz30cal.areas2.spawners.AbstractEnemySpawner;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.GamePlayer;
import com.carterz30cal.utils.Box;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGameArea {
    public Areas parent;
    protected Box boundingBox;
    protected String areaName;
    protected SpawnerContext context;
    protected List<AbstractEnemySpawner> registeredSpawners = new ArrayList<>();


    public void Tick() {
        for (var spawner : registeredSpawners) {
            spawner.tick();
        }
    }

    public void OnKill(GameEnemy enemy) {

    }

    public void RegisterSpawner(AbstractEnemySpawner spawner) {
        registeredSpawners.add(spawner);
        spawner.RegisterParent(this);
    }

    public String GetSubAreaName(GamePlayer player) {
        return areaName;
    }

    public boolean IsInBounds(GameEntity entity) {
        return boundingBox.IsWithin(entity.getLocation());
    }

    public boolean IsInBounds(GamePlayer player) {
        return IsInBounds((GameEntity) player);
    }

    public SpawnerContext GetContext() {
        return context;
    }

    public class SpawnerContext {
        public String spawningMode = "NORMAL";
    }
}
