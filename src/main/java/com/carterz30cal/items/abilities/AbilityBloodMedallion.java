package com.carterz30cal.items.abilities;

import com.carterz30cal.entities.player.GamePlayer;
import com.carterz30cal.items.ItemAbility;
import com.carterz30cal.main.Dungeons;
import com.carterz30cal.utils.EntityUtils;
import com.carterz30cal.utils.MathsUtils;
import com.carterz30cal.utils.ParticleUtils;
import com.carterz30cal.utils.RandomUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class AbilityBloodMedallion extends ItemAbility {
	public final Particle.DustOptions DUST_CIRCLE = new Particle.DustOptions(Color.RED, 1.1F);
	public AbilityBloodMedallion(GamePlayer owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "REDTemple Teleport";
	}
	
	public List<String> description()
	{
		List<String> l = new ArrayList<>();
		l.add("GRAYCosts LIGHT_PURPLE5 ManaGRAY to teleport to the REDTempleGRAY.");
		l.add("DARK_GRAYRight click to use.");
		return l;
	}
	
	public void onRightClick()
	{
		if (owner.useMana(5)) {
			EntityUtils.applyPotionEffect(owner.player, PotionEffectType.LEVITATION, 80, 1, false);
			new BukkitRunnable() {
				int tick = 0;
				Location base = owner.getLocation();
				@Override
				public void run() {
					// TODO Auto-generated method stub
					tick++;
					if (tick == 81) {
						cancel();
						owner.player.teleport(new Location(Dungeons.w, -277.5, 65, 45.5));
					}
					else {
						
						if (tick % 10 == 8) {
							Location p = owner.player.getLocation().add(0, 1, 0);
							Vector dir = new Vector(RandomUtils.getDouble(-2, 2), RandomUtils.getDouble(-0.5, 0.5), RandomUtils.getDouble(-2, 2));
							Location p1 = p.clone().add(dir);
							Location p2 = p.clone().subtract(dir);
							
							ParticleUtils.spawnLine(p1, p2, DUST_CIRCLE, 30);
							owner.damage(24);
						}
						
						Location o1 = base.clone();
						Location o2 = base.clone();
						
						o1.add(MathsUtils.getCircleX(tick * 15) * 2, tick / 15D, MathsUtils.getCircleZ(tick * 15) * 2);
						o2.add(MathsUtils.getCircleX((tick + 180) * 15) * 1.8, (81 / 15D) - tick / 15D, MathsUtils.getCircleZ((tick + 180) * 15) * 1.8);
						
						ParticleUtils.spawn(o1, DUST_CIRCLE, 0);
						ParticleUtils.spawn(o2, DUST_CIRCLE, 0);
					}
				}
				
			}.runTaskTimer(Dungeons.instance, 1, 1);
			
		}
	}

}
