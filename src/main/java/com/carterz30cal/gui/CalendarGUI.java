package com.carterz30cal.gui;

import com.carterz30cal.areas.events.AbstractEvent;
import com.carterz30cal.areas.events.AbstractEventWithArea;
import com.carterz30cal.areas.events.EventManager;
import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemFactory;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class CalendarGUI extends AbstractGUI {
    public CalendarGUI(GamePlayer owner) {
        super(owner);

        inventory = new GooeyInventory("Calendar", 5);
        inventory.initUsingTemplate(GooeyTemplate.SHOPPY_DARK);
        update();
    }

    public void onTick()
    {
        update();
    }

    private int sort(AbstractEvent a, AbstractEvent b) {
        if (a.active() && b.active()) {
            return Long.compare(a.duration(), b.duration());
        }
        else if (a.active()) {
            return -1;
        }
        else if (b.active()) {
            return 1;
        }
        else {
            return Long.compare(a.happening(), b.happening());
        }
    }
    private boolean keep(AbstractEvent x) {
        if (x instanceof AbstractEventWithArea a) {
            if (a.area() != owner.area) return false;
        }
        return true;
    }

    private void update() {
        inventory.initUsingTemplate(GooeyTemplate.SHOPPY_DARK);
        var events = EventManager.getScheduledEvents().filter(this::keep).sorted(this::sort).toList();
        if (events.isEmpty()) {
            inventory.setSlot(
                    ItemFactory.customItem(
                            "RED_STAINED_GLASS_PANE",
                            "<red>Nothing!",
                            "<red>Your social calendar is looking mighty empty!"
                    ),
                    calc(4, 2)
            );
        } else {
            int i = 0;
            for (var e : events) {
                if (i > 20) break;
                inventory.setSlot(
                        e.calendar(),
                        calc((i % 7) + 1, (i / 7) + 1)
                );
                i++;
            }
        }


        inventory.update();
    }
}
