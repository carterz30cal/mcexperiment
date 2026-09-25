package com.carterz30cal.utils;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * @author carterz30cal
 * @version 1
 * @since 1.0.0
 */
public class PastingBox extends Box {
    private final World from;
    private final World to;
    protected int cx;
    protected int cy;
    protected int cz;
    protected boolean done = false;
    private int pastes = 0;

    /**
     *
     * @param original
     * @param from
     * @param to
     * @since 1.0.0 [1]
     */
    public PastingBox(Box original, World from, World to) {
        super(original);
        this.from = from;
        this.to = to;

        cx = x1;
        cy = y1;
        cz = z1;
    }

    /**
     * Paste the next block in the box to the designated location
     *
     * @return <code>false</code> if we're done, <code>true</code> otherwise.
     * @since 1.0.0 [1]
     */
    public boolean next() {
        return next(DefaultBlockMutator.instance);
    }


    /**
     * Paste the next block in the box to the designated location
     *
     * @return <code>false</code> if we're done, <code>true</code> otherwise.
     * @since 1.0.0 [1]
     */
    public boolean next(BlockMutator mutator) {
        var state = mutator.mutate(from.getBlockState(cx, cy, cz).copy(new Location(to, cx, cy, cz)));
        state.update(true, false);

        cx++;
        pastes++;
        if (cx > x2) {
            cx = x1;
            cz++;
            if (cz > z2) {
                cz = z1;
                cy++;
                if (cy > y2) {
                    done = true;
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Is this paste done?
     *
     * @return done or not
     * @since 1.0.0 [1]
     */
    public boolean finished() {
        return done;
    }

    /**
     * Progress done on this paste
     *
     * @return the percentage of this paste complete
     * @since 1.0.0 [1]
     */
    public double progress() {
        return (double) pastes / volume();
    }


}
