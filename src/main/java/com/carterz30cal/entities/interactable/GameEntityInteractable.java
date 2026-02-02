package com.carterz30cal.entities.interactable;

import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.player.GamePlayer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameEntityInteractable extends GameOwnable {
    private static final Map<UUID, GameEntityInteractable> entities = new HashMap<>();

    public GameEntityInteractable(EntityType entityType, Location location, String title, String subtitle) {
        super(entityType, location, title, subtitle);
        entities.put(GetUUID(), this);
    }

    public GameEntityInteractable(String skullProfileId, Location location, String title, String subtitle) {
        super(skullProfileId, location, title, subtitle);
        entities.put(GetUUID(), this);
    }

    public static GameEntityInteractable GetEntity(Entity entity) {
        UUID uuid = UUID.fromString(entity.getPersistentDataContainer().getOrDefault(GameEnemy.keyEnemy, PersistentDataType.STRING, UUID.randomUUID().toString()));
        return entities.getOrDefault(uuid, null);
    }

    public void Interact(GamePlayer interactingPlayer) {

    }

    protected boolean HasMetRequirements(GamePlayer interactingPlayer) {
        return true;
    }

    @Override
    protected boolean getVisibility(GamePlayer player) {
        return HasMetRequirements(player);
    }
}
