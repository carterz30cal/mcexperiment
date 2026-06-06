package com.carterz30cal.entities.player;

import com.carterz30cal.areas.quests.Quests;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout;
import net.megavex.scoreboardlibrary.api.sidebar.component.LineDrawable;
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent;
import net.megavex.scoreboardlibrary.api.sidebar.component.animation.CollectionSidebarAnimation;
import net.megavex.scoreboardlibrary.api.sidebar.component.animation.SidebarAnimation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class PlayerScoreboard {
    private final Sidebar sidebar;
    private final ComponentSidebarLayout sidebarLayout;
    private final GamePlayer owner;

    private final SidebarAnimation<Component> titleAnimation;

    public PlayerScoreboard(GamePlayer owner) {
        sidebar = Dungeons.scoreboardLibrary.createSidebar();
        sidebar.addPlayer(owner.player);
        this.owner = owner;

        titleAnimation = createGradient("mcExperiment", NamedTextColor.LIGHT_PURPLE, NamedTextColor.DARK_PURPLE);
        var title = SidebarComponent.animatedLine(titleAnimation);

        var lines = SidebarComponent.builder()
                .addDynamicLine(() -> {
                    var subAreaName = owner.area != null ? owner.area.getArea().GetSubAreaName(owner) : "Void";
                    return text(subAreaName, NamedTextColor.DARK_GRAY);
                })
                .addBlankLine()
                .addDynamicLine(() -> text("Coins: ", NamedTextColor.GOLD).append(text(StringUtils.addCommas(owner.coins))))
                .addDynamicLine(() -> text("Sack: ", NamedTextColor.GOLD).append(text(owner.getSackSpaceUsed())).append(text("/")).append(text(owner.getSackSize())))
                .addComponent(new AreaInfoComponent())
                .addComponent(new QuestInfoComponent())
                .addBlankLine()
                .addDynamicLine(() -> MiniMessage.miniMessage().deserialize("<aqua>Level " + owner.getLevel()
                        + " <dark_grey>[</dark_grey>+" + Math.round(owner.getLevelProgress() * 100) + "%<dark_grey>]"));

        sidebarLayout = new ComponentSidebarLayout(title, lines.build());
    }

    public void tick() {
        titleAnimation.nextFrame();

        sidebarLayout.apply(sidebar);

    }

    @NotNull
    private SidebarAnimation<Component> createGradient(String text,
                                                       @NotNull NamedTextColor colour1,
                                                       @NotNull NamedTextColor colour2) {
        float step = 1f / 24f;

        List<Component> frames = new ArrayList<>((int) (2f / step));

        float phase = -1f;
        while (phase < 1) {
            frames.add(MiniMessage.miniMessage().deserialize("<gradient:" + colour1.asHexString() + ":" + colour2.asHexString() + ":" + phase + ">" + text));
            phase += step;
        }

        return new CollectionSidebarAnimation<>(frames);
    }

    private class AreaInfoComponent implements SidebarComponent {

        @Override
        public void draw(@NotNull LineDrawable drawable) {
            if (owner.area == null || owner.area.getArea().GetScoreboard(owner).isEmpty()) {
                return;
            }
            drawable.drawLine(text());
            for (var sc : owner.area.getArea().GetScoreboard(owner)) {
                drawable.drawLine(MiniMessage.miniMessage().deserialize(sc));
            }
        }
    }

    private class QuestInfoComponent implements SidebarComponent {

        @Override
        public void draw(@NotNull LineDrawable drawable) {
            var chosenQuest = owner.GetSelectedQuest();
            if (chosenQuest != null) {
                Quests.QuestSave save = owner.GetQuestSave(chosenQuest);
                if (save.sectionSave.HasTalkedTo()) {
                    drawable.drawLine(text());
                    drawable.drawLine(text("Quest: ", NamedTextColor.GOLD).append(text(chosenQuest.getName(), NamedTextColor.WHITE)));
                    for (var sc : save.sectionSave.GetDescription()) {
                        drawable.drawLine(MiniMessage.miniMessage().deserialize(sc));
                    }
                }
            }
        }
    }
}
