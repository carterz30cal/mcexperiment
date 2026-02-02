package com.carterz30cal.entities.interactable;

import com.carterz30cal.entities.Shop;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.gui.ShopGUI;
import org.bukkit.entity.EntityType;

public class GameShopkeeper extends GameEntityInteractable {
    private final Shop shop;

    public GameShopkeeper(Shop shop) {
        super(shop.shopkeeperType, shop.shopkeeperLocation, shop.shopkeeperName, "GOLDBOLDShop");

        this.shop = shop;
        if (shop.shopkeeperType == EntityType.MANNEQUIN) {
            this.skullProfileId = shop.skullProfileId;
        }
    }

    @Override
    protected boolean HasMetRequirements(GamePlayer interactingPlayer) {
        return (interactingPlayer.getLevel() >= shop.requiredLevel);
    }

    @Override
    public void Interact(GamePlayer interactingPlayer) {
        super.Interact(interactingPlayer);

        interactingPlayer.openGui(new ShopGUI(interactingPlayer, shop));
    }
}
