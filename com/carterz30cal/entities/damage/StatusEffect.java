package com.carterz30cal.entities.damage;

import com.carterz30cal.entities.DamageType;

public enum StatusEffect 
{
	BLEED("Bleed", "BLD", "\u00D7", "RED", 250, 1.1, new DamagingStatus(90, 0.035, DamageType.BLEED)),
	BURN("Burn", "BRN", "\u00D7", "GOLD", 100, 1.6, new DotStatus(10, 0.005, 4, 7, DamageType.FIRE)),
	POISON("Poison", "PSN", "\u2620", "GREEN", 1000, 1.2, new DotStatus(5, 0, 20, 2, DamageType.WITHER)),
	DEATH("Instant Death", "DTH", "\u2620", "WHITE", 10000, 1.04, new DeathStatus()),
	;
	
	private StatusEffect(String name, String shortName, String symbol, String colour, int defaultResistance, double resistanceMultiplier, AbstractStatus effect) {
		this.name = name;
		this.shortName = shortName;
		this.symbol = symbol;
		this.colour = colour;
		this.defaultResistance = defaultResistance;
		this.resistanceMultiplier = resistanceMultiplier;
		this.effect = effect;
	}
	
	public String name;
	public String shortName;
	public String symbol;
	public String colour;
	public int defaultResistance; // -1 for immune
	public double resistanceMultiplier = 1.2;
	public AbstractStatus effect;
	
	public String getLoreName() {
		return colour + name + symbol;
	}
	
	public String getEnemyDisplayName() {
		return colour + shortName;
	}
}
