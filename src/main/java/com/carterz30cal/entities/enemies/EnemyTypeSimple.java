package com.carterz30cal.entities.enemies;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.EquipmentSlot;

import com.carterz30cal.entities.AbstractEnemyType;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.utils.EntityUtils;

public class EnemyTypeSimple extends AbstractEnemyType
{
	public Map<EquipmentSlot, String> equipment = new HashMap<>();
	public EntityType mainType;
	public EnemyTypeSimple(ConfigurationSection m) 
	{
		super(m);
		
		if (m.contains("equipment"))
		{
			ConfigurationSection e = m.getConfigurationSection("equipment");
			for (String eq : e.getKeys(false))
			{
				String item = e.getString(eq);
				if (item == "null") continue;
				
				equipment.put(EquipmentSlot.valueOf(eq), item);
			}
		}
		
		mainType = EntityType.valueOf(m.getString("entity-type", "ZOMBIE"));
	}

	@Override
	public GameEnemy generate(Location location)
	{
		GameEnemy mob = new GameEnemy(location, this);
		
		mob.main = EntityUtils.spawnPart(mainType, location);
		if (mob.main instanceof Mob)
		{
			Mob main = (Mob)mob.main;
			for (EquipmentSlot slot : equipment.keySet())
			{
				EntityUtils.setArmourPiece(main, slot, equipment.get(slot));
			}
			main.setRemoveWhenFarAway(false);
			
			main.getAttribute(Attribute.MOVEMENT_SPEED).addModifier(new AttributeModifier("MOD_SPEED", speed - 1, Operation.MULTIPLY_SCALAR_1));
		}
		
		if (mob.main instanceof Slime)
		{
			Slime myHomeSlime = (Slime)mob.main;
			myHomeSlime.setSize(size);
		}
		
		
		mob.doTick();
		mob.register();
		return mob;
	}

}
