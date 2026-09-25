package com.carterz30cal.entities.interactable;

import com.carterz30cal.areas.quests.requirements.LevelRequirement;
import com.carterz30cal.entities.Shop;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.gui.ShopGUI;
import com.carterz30cal.main.Dungeons;

/**
 * @author carterz30cal
 * @version 2
 * @since 1.0.0
 */
public class GameShopkeeper extends GameEntityInteractable {
    private final Shop shop;

    public GameShopkeeper(Shop shop) {
        super(shop.representationBuilder, shop.shopkeeperLocation);
        title(shop.shopkeeperName);
        subtitle("<gold><b>Shop</b></gold>");

        this.shop = shop;
        this.requirement = new LevelRequirement(shop.requiredLevel);
    }

    @Override
    public void interact(GamePlayer interactingPlayer) {
        super.interact(interactingPlayer);

        interactingPlayer.openGui(new ShopGUI(interactingPlayer, shop));
    }
}
