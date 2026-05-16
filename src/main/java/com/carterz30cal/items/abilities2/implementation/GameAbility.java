package com.carterz30cal.items.abilities2.implementation;

import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.GameEntity;
import com.carterz30cal.entities.damage.StatusEffect;
import com.carterz30cal.entities.enemies.implementation.GameEnemy;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemReq;
import com.carterz30cal.items.ItemType;
import com.carterz30cal.items.abilities2.Abilities;
import com.carterz30cal.stats.Stat;
import com.carterz30cal.stats.StatContainer;
import com.carterz30cal.stats.StatDisplayType;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.kyori.adventure.text.Component.text;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class GameAbility {

    public Abilities source;

    public abstract String name(AbilityContext context);

    @Deprecated
    public List<String> description(AbilityContext context) {
        return new ArrayList<>();
    }

    public TextColor colour(AbilityContext context) {
        return TextColor.color(NamedTextColor.LIGHT_PURPLE);
    }

    /**
     * Generate a list of component builders that provide descriptions on items.
     * Typically uncoloured, possibly decorated.
     * If this returns an empty list, then that means we want to use the miniMessageDescription method instead
     *
     * @param context required ability context for parametric descriptions.
     * @return a list of component builders
     * @since 1.0.0
     */
    public List<TextComponent.Builder> componentDescription(@NotNull AbilityContext context) {
        return new ArrayList<>();
    }

    /**
     * Generate a list of component builders that provide descriptions on items.
     * Typically used over componentDescription for simplicity.
     *
     * @param context required ability context for parametric descriptions.
     * @return a list of component builders
     * @since 1.0.0
     */
    public List<String> miniMessageDescription(@NotNull AbilityContext context) {
        return new ArrayList<>();
    }

    public void onKill(AbilityContext context, GameEnemy killed)
    {

    }

    public void onAttack(AbilityContext context, DamageInfo info, GameEntity attacked)
    {

    }

    public void onPreAttack(AbilityContext context, DamageInfo info)
    {

    }

    public int onStatusBuildup(AbilityContext context, StatusEffect effect, int current) {
        return current;
    }

    public void onStatusProc(AbilityContext context, StatusEffect effect, GameEnemy enemy) {

    }


    public int onDamaged(AbilityContext context, GameEnemy damager, int damage) {
        return damage;
    }

    public void onLeftClick(AbilityContext context)
    {

    }

    public void onRightClick(AbilityContext context)
    {

    }

    public void onItemStats(AbilityContext context, StatContainer item)
    {

    }
    public void onItemStatsLate(AbilityContext context, StatContainer item)
    {

    }

    public void onPlayerStats(AbilityContext context, StatContainer item) {

    }

    /**
     * Method for determining what item types this ability can work with.
     * Currently only used for enchantments.
     * @since 1.0.0
     * @return A set of all ItemTypes this enchant is usable on
     */
    public Set<ItemType> getApplicableTypes() {
        return new HashSet<>();
    }

    public int getEnchantPower(AbilityContext context) {
        return 0;
    }

    public int getMaximumLevel() {
        return 1;
    }

    public List<ItemReq> getCatalystRequirements(AbilityContext context, int desiredLevel) {
        return new ArrayList<>();
    }

    protected String formattedDisplay(Stat stat, long val) {
        String prefix = val >= 0 ? "+" : "";
        String suffix = stat.display == StatDisplayType.PERCENTAGE ? "%" : "";

        return "<" + stat.textColour.asHexString() + ">" + prefix + val + suffix + stat.getIcon() + "</" + stat.textColour.asHexString() + ">";
    }

    public static class AbilityContext {
        public GamePlayer owner;
        public int level;
        public GameAbility ability;

        public AbilityContext(GameAbility ability) {
            this.ability = ability;
            this.owner = null;
            this.level = 1;
        }

        public String name() {
            return ability.name(this);
        }
        public List<String> description() {
            return ability.description(this);
        }


        public List<TextComponent.Builder> componentDescription() {
            var components = ability.componentDescription(this);
            if (components == null || components.isEmpty()) {
                var messages = ability.miniMessageDescription(this);
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

        public TextColor colour() {
            return ability.colour(this);
        }
        public int getEnchantPower() {
            return ability.getEnchantPower(this);
        }
    }
}
