package com.carterz30cal.entities.interactable;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.PlayerManager;
import com.carterz30cal.entities.enemies.core.GameEnemy;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.EntityUtils;
import com.carterz30cal.utils.StringUtils;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mannequin;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.UUID;

public class GameOwnable extends GameEntity {
    private final EntityType entityType;
    @SuppressWarnings("unused")
    private final BukkitRunnable ticker;
    private final GameOwnable self;
    protected LivingEntity entityMain;
    protected ArmorStand entityTitle;
    protected ArmorStand entitySubtitle;
    protected String title;
    protected String subtitle;
    protected Location location;
    protected String skullProfileId;

    public GameOwnable(EntityType entityType, Location location, String title, String subtitle) {
        this.location = location;
        this.title = title;
        this.subtitle = subtitle;
        this.entityType = entityType;
        this.uuid = UUID.randomUUID();
        this.self = this;
        this.ticker = new Ticker();
        this.skullProfileId = null;

        register(this.uuid);
    }

    public GameOwnable(String skullProfileId, Location location, String title, String subtitle) {
        this.entityType = EntityType.MANNEQUIN;
        this.skullProfileId = skullProfileId;
        this.location = location;
        this.title = title;
        this.subtitle = subtitle;
        this.uuid = UUID.randomUUID();
        this.self = this;
        this.ticker = new Ticker();

        register(this.uuid);
    }

    @Override
	public void remove() {
        if (entityMain != null) {
            entityMain.remove();
        }
        if (entitySubtitle != null) {
            entitySubtitle.remove();
        }
        if (entityTitle != null) {
            entityTitle.remove();
        }
	}

    @Override
	public Location getLocation() {
		// TODO Auto-generated method stub
        return location;
	}

    @SuppressWarnings("unused")
    protected boolean getVisibility(GamePlayer player) {
        return true;
    }

    protected void tick() {
        ensureValidity();
        PlayerManager.players.forEach((uuid1, gamePlayer) -> {
            if (getVisibility(gamePlayer)) {
                gamePlayer.showEntity(entityMain);
                gamePlayer.showEntity(entityTitle);
                gamePlayer.showEntity(entitySubtitle);
            }
            else {
                gamePlayer.hideEntity(entityMain);
                gamePlayer.hideEntity(entityTitle);
                gamePlayer.hideEntity(entitySubtitle);
            }
        });
    }

    private void ensureValidity() {
        if (!getLocation().getChunk().isLoaded()) {
            return;
        }
        if (!getValidityToSpawn()) {
            if (entityMain != null) {
                entityMain.remove();
            }
            if (entityTitle != null) {
                entityTitle.remove();
            }
            if (entitySubtitle != null) {
                entitySubtitle.remove();
            }
        }
        else {
            if (entityMain == null || !entityMain.isValid()) {
                if (entityMain != null) {
                    entityMain.remove();
                }
                entityMain = (LivingEntity) EntityUtils.spawnPart(entityType, location);
                if (entityMain != null) {
                    entityMain.getPersistentDataContainer().set(GameEnemy.keyEnemy, PersistentDataType.STRING, uuid.toString());
                    entityMain.setGravity(false);
                    entityMain.setCollidable(false);
                    Objects.requireNonNull(entityMain.getAttribute(Attribute.MOVEMENT_SPEED)).setBaseValue(0);

                    if (entityMain instanceof Mannequin man) {
                        man.setImmovable(true);
                        var prof = ItemFactory.getSkullProfile(skullProfileId);
                        if (prof != null) {
                            man.setProfile(ResolvableProfile.resolvableProfile(prof));
                        }
                    }
                }
            }
            else {
                if (entityTitle == null || !entityTitle.isValid()) {
                    if (entityTitle != null) {
                        entityTitle.remove();
                    }
                    entityTitle = EntityUtils.spawnHologram(entityMain.getEyeLocation().add(0, 0.5, 0), -1);
                    if (entityTitle != null) {
                        entityTitle.setCustomName(StringUtils.colourString(title));
                    }
                }
                if (entitySubtitle == null || !entitySubtitle.isValid()) {
                    if (entitySubtitle != null) {
                        entitySubtitle.remove();
                    }
                    entitySubtitle = EntityUtils.spawnHologram(entityMain.getEyeLocation().add(0, 0.25, 0), -1);
                    if (entitySubtitle != null) {
                        entitySubtitle.setCustomName(StringUtils.colourString(subtitle));
                    }
                }
            }
        }
    }

    private boolean getValidityToSpawn() {
        boolean valid = false;
        for (var player : PlayerManager.getOnlinePlayers()) {
            if (player.getLocation().distance(location) <= 48) {
                valid = true;
                break;
            }
        }
        return valid;
    }

    private class Ticker extends BukkitRunnable {
        private Ticker() {
            runTaskTimer(Dungeons.instance, 0, 1);
        }

        @Override
        public void run() {
            self.tick();
        }
    }
}
