package com.carterz30cal.items.abilities.necropolis.runes;

import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.health.damage.AttackType;
import com.carterz30cal.entities.health.damage.DamagePacket;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.abilities.implementation.AggressiveAbility;
import com.carterz30cal.items.abilities.implementation.ContextWithAbility;
import com.carterz30cal.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class FlowRune extends GameRuneAbility implements AggressiveAbility {

    public FlowRune() {
        super("Flow");
    }

    @Override
    public void damage(ContextWithAbility<? extends GameEntity> context, DamagePacket packet) {
        if (packet.attack != AttackType.ENVIRONMENT && context.getOwner() instanceof GamePlayer player) {
            player.gainMana(1);
        }
    }

    @Override
    public List<String> miniMessageDescription(@NotNull ContextWithAbility<? extends GameEntity> context) {
        var description = "<grey>When you strike an enemy, regain <light_purple>1♠ Mana</light_purple>.";
        return StringUtils.wrapText(description, 30);
    }
}
