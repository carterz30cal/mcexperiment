package com.carterz30cal.entities;

import com.carterz30cal.utils.StringUtils;

public enum DamageType
{
	PHYSICAL("GRAYPhysical", 100),
	PROJECTILE("GRAYProjectile", 90),
	MAGICAL("AQUAMagical", 100),
	HOLY("YELLOWHoly", 130),
	BLEED("REDBleed", 60),
	FIRE("GOLDFire", 10),
	FROST("BLUEFrost", 0),
	WITHER("BLACKWither", 0);
	
	public String name;
	public final int knockbackModifier;
	private DamageType(String name, int kb)
	{
		this.name = StringUtils.colourString(name);
		this.knockbackModifier = kb;
	}
}
