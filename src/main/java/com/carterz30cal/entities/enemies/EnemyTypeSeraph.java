package com.carterz30cal.entities.enemies;

import com.carterz30cal.areas2.bosses.waterway.AreaBossWaterwaySeraph;
import com.carterz30cal.entities.DamageInfo;
import com.carterz30cal.entities.GameEnemy;
import com.carterz30cal.entities.player.GamePlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Set;

public class EnemyTypeSeraph extends EnemyTypeDamageCapped
{
    public int bonusHealthPerPlayer;
	public EnemyTypeSeraph(ConfigurationSection m) {
		super(m);

        bonusHealthPerPlayer = m.getInt("bonus-health-per-player", 5000);
    }

    @Override
    public int getMaxHealth() {
        return health + (AreaBossWaterwaySeraph.GetParticipatingCount() * bonusHealthPerPlayer);
    }

    @SuppressWarnings("unchecked")
	public void onDamaged(GameEnemy enemy, DamageInfo info)
	{
		enemy.data.putIfAbsent("damagers", new HashSet<GamePlayer>());
		((HashSet<GamePlayer>)enemy.data.get("damagers")).add(info.attacker);
		
		super.onDamaged(enemy, info);
	}
	
	@SuppressWarnings("unchecked")
	public void onKilled(GameEnemy enemy)
	{
		Set<GamePlayer> damagers = ((HashSet<GamePlayer>)enemy.data.getOrDefault("damagers", new HashSet<GamePlayer>()));
		damagers.remove(enemy.lastDamager);
		
		for (GamePlayer damager : damagers)
		{
			enemy.dropItems(damager);
		}
		
		super.onKilled(enemy);
	}

}
