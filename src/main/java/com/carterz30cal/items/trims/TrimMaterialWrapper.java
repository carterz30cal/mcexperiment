package com.carterz30cal.items.trims;

import org.bukkit.inventory.meta.trim.TrimMaterial;

public enum TrimMaterialWrapper {
    AMETHYST(TrimMaterial.AMETHYST),
    COPPER(TrimMaterial.COPPER),
    DIAMOND(TrimMaterial.DIAMOND),
    EMERALD(TrimMaterial.EMERALD),
    GOLD(TrimMaterial.GOLD),
    IRON(TrimMaterial.IRON),
    LAPIS(TrimMaterial.LAPIS),
    NETHERITE(TrimMaterial.NETHERITE),
    QUARTZ(TrimMaterial.QUARTZ),
    REDSTONE(TrimMaterial.REDSTONE),
    RESIN(TrimMaterial.RESIN),
    NULL(null);
    private final TrimMaterial trimMaterial;

    TrimMaterialWrapper(TrimMaterial trimMaterial) {
        this.trimMaterial = trimMaterial;
    }

    public TrimMaterial GetTrimMaterial() {
        return trimMaterial;
    }

}
