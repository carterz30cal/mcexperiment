package com.carterz30cal.mining;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class OreType {
    public String name;
    public int hardness;
    public int powerRequired;
    public Material blockType;
    public String item;
    public int lowerBound;
    public int upperBound;

    // number out of a thousand chance to convert to the 'rare' ore
    public int conversionChance;
    public Material convertsInto;

    public Material minesInto;
    public int regenTime;

    public OreType(ConfigurationSection config) {
        name = config.getString("name");
        hardness = config.getInt("hardness", 1) * 20;
        powerRequired = config.getInt("power-required", 1);
        blockType = Material.valueOf(config.getString("block-type"));
        item = config.getString("item");
        lowerBound = config.getInt("lower-bound", 1);
        upperBound = config.getInt("upper-bound", 1);
        conversionChance = config.getInt("conversion-chance", 0);
        convertsInto = Material.valueOf(config.getString("converts-into", "AIR"));

        minesInto = Material.valueOf(config.getString("mines-into"));
        regenTime = config.getInt("regen-time", 40);

    }
}
