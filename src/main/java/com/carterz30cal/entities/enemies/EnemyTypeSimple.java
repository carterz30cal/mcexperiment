package com.carterz30cal.entities.enemies;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.carterz30cal.items.ItemFactory;
import com.carterz30cal.main.Dungeons;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.EquipmentSlot;

import com.carterz30cal.entities.AbstractEnemyType;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.utils.EntityUtils;
import org.bukkit.inventory.EquipmentSlotGroup;

public class EnemyTypeSimple extends AbstractEnemyType
{
	public static final NamespacedKey KEY_SPEED = new NamespacedKey(Dungeons.instance, "speed");
	public static final NamespacedKey KEY_SCALE = new NamespacedKey(Dungeons.instance, "scale");
	public Map<EquipmentSlot, String> equipment = new HashMap<>();
	public EntityType mainType;

	public boolean hasDirector;

	public EnemyTypeSimple(ConfigurationSection m) 
	{
		super(m);
		
		if (m.contains("equipment"))
		{
			ConfigurationSection e = m.getConfigurationSection("equipment");
            assert e != null;
            for (String eq : e.getKeys(false))
			{
				String item = e.getString(eq);
				if (Objects.equals(item, "null")) continue;
				
				equipment.put(EquipmentSlot.valueOf(eq), item);
			}
		}
		
		mainType = EntityType.valueOf(m.getString("entity-type", "ZOMBIE"));
		hasDirector = m.getBoolean("has-director", false);
	}

	@Override
	public GameEnemy generate(Location location)
	{
		GameEnemy mob = new GameEnemy(location, this);
		
		mob.main = EntityUtils.spawnPart(mainType, location);
		if (hasDirector) {
			mob.director = (Mob) EntityUtils.spawnPart(EntityType.ZOMBIE, location);
			mob.director.setCollidable(false);
			mob.parts.add(mob.director);
			//EntityUtils.setArmourPiece(mob.director, EquipmentSlot.HEAD, ItemFactory.build("LEAF_LITTER"));

			var speedAttribute = mob.director.getAttribute(Attribute.MOVEMENT_SPEED);
			if (speedAttribute != null) {
				speedAttribute.addModifier(new AttributeModifier(KEY_SPEED, speed - 1, Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.ANY));
			}
		}
		if (mob.main instanceof Mob)
		{
			Mob main = (Mob)mob.main;
			for (EquipmentSlot slot : equipment.keySet())
			{
				EntityUtils.setArmourPiece(main, slot, equipment.get(slot));
			}
			main.setRemoveWhenFarAway(false);

			var speedAttribute = main.getAttribute(Attribute.MOVEMENT_SPEED);
			if (speedAttribute != null) {
				speedAttribute.addModifier(new AttributeModifier(KEY_SPEED, speed - 1, Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.ANY));
			}
			var scaleAttribute = main.getAttribute(Attribute.SCALE);
			if (scaleAttribute != null) {
				scaleAttribute.addModifier(
						new AttributeModifier(KEY_SCALE,
								scale - 1,
								Operation.MULTIPLY_SCALAR_1,
								EquipmentSlotGroup.ANY)
				);

			}

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
