package com.carterz30cal.items.trims;

import org.bukkit.inventory.meta.trim.TrimPattern;

public enum TrimPatternWrapper {
    BOLT(TrimPattern.BOLT),
    COAST(TrimPattern.COAST),
    DUNE(TrimPattern.DUNE),
    EYE(TrimPattern.EYE),
    FLOW(TrimPattern.FLOW),
    HOST(TrimPattern.HOST),
    RAISER(TrimPattern.RAISER),
    RIB(TrimPattern.RIB),
    SENTRY(TrimPattern.SENTRY),
    SHAPER(TrimPattern.SHAPER),
    SILENCE(TrimPattern.SILENCE),
    SNOUT(TrimPattern.SNOUT),
    SPIRE(TrimPattern.SPIRE),
    TIDE(TrimPattern.TIDE),
    VEX(TrimPattern.VEX),
    WARD(TrimPattern.WARD),
    WAYFINDER(TrimPattern.WAYFINDER),
    WILD(TrimPattern.WILD),
    NULL(null);
    private final TrimPattern trimPattern;

    TrimPatternWrapper(TrimPattern trimPattern) {
        this.trimPattern = trimPattern;
    }

    public TrimPattern GetTrimPattern() {
        return trimPattern;
    }
}
