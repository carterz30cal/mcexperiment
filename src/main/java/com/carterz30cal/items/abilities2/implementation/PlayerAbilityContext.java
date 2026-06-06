package com.carterz30cal.items.abilities2.implementation;

import com.carterz30cal.entities.player.GamePlayer;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class PlayerAbilityContext implements ContextWithAbility<GamePlayer> {
    public GamePlayer owner;
    public int level;
    public Ability ability;

    public PlayerAbilityContext(GameAbility ability) {
        this.ability = ability;
        this.owner = null;
        this.level = 1;
    }

    public String name() {
        if (ability instanceof AbilityWithName name) {
            return name.name(this);
        }
        else {
            return "";
        }
    }

    public List<TextComponent.Builder> componentDescription() {
        if (ability instanceof AbilityWithDescription description) {
            var components = description.componentDescription(this);
            if (components == null || components.isEmpty()) {
                var messages = description.miniMessageDescription(this);
                var built = new ArrayList<TextComponent.Builder>();
                for (var message : messages) {
                    built.add(text().append(MiniMessage.miniMessage().deserialize(message).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)));
                }
                return built;
            }
            else {
                return components;
            }
        }
        else {
            return List.of();
        }
    }

    @Nullable
    public TextColor colour() {
        if (ability instanceof AbilityWithName name) {
            return name.colour(this);
        }
        else {
            return null;
        }
    }

    @Override
    public long getLevel() {
        return level;
    }

    @Override
    public Ability getAbility() {
        return ability;
    }

    @Override
    public GamePlayer getOwner() {
        return owner;
    }
}