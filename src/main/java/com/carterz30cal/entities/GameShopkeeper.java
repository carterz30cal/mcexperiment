package com.carterz30cal.entities;

import com.carterz30cal.main.Dungeons;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.scheduler.BukkitRunnable;

public class GameShopkeeper extends GameOwnable {

    private Shop shop;

    public GameShopkeeper(String name, Location location, EntityType type) {
        super(name, location, type);
    }

    public static GameShopkeeper spawn(Shop shop) {
        GameShopkeeper n = new GameShopkeeper(shop.shopkeeperName, shop.shopkeeperLocation, shop.shopkeeperType);

        n.shop = shop;
        new BukkitRunnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                n.refresh();
            }

        }.runTaskTimer(Dungeons.instance, 5, 20);

        return n;
    }

    public void refresh() {
        owners.clear();
        for (GamePlayer p : PlayerManager.players.values()) {
            if (shop.requiredQuest != null && !p.completedQuests.contains(shop.requiredQuest)) continue;
            if (p.getLevel() < shop.requiredLevel) continue;

            addOwner(p, false);
        }
        if (owners.size() > 0) {
            refreshVisibility();

            if (main != null && main instanceof Mob) {
                Mob e = (Mob)main;
                //for (EquipmentSlot s : q.questgiverEquipment.keySet()) EntityUtils.setArmourPiece(e, s, q.questgiverEquipment.get(s));
            }
        }
        else {
            main.remove();
            display.remove();
        }


    }
}
