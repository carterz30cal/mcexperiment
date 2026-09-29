package com.carterz30cal.entities.interactable;

import com.carterz30cal.entities.Shop;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.gui.ShopGUI;

/**
 * @author carterz30cal
 * @version 3
 * @since 1.0.0
 */
public class GameShopkeeper extends GameEntityInteractable {
    private final Shop shop;

    public GameShopkeeper(Shop shop) {
        super(shop.representationBuilder, shop.shopkeeperLocation);
        title("<gold>" + shop.shopkeeperName);
        subtitle("<gold><b>SHOP</b></gold>");

        this.shop = shop;
        this.requirement = shop.requirement;
    }

    @Override
    public void interact(GamePlayer interactingPlayer) {
        super.interact(interactingPlayer);

        interactingPlayer.openGui(new ShopGUI(interactingPlayer, shop));
    }
}
