package de.blablubbabc.dreamworld.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.blablubbabc.dreamworld.DreamworldPlugin;
import de.blablubbabc.dreamworld.managers.ConfigManager;

public class PlayerListener extends AbstractListener {

	public PlayerListener(DreamworldPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBedLeave(PlayerBedLeaveEvent event) {
		plugin.getDreamManager().onBedLeave(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		plugin.getDreamManager().continueDreaming(event.getPlayer(), true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onDisconnect(PlayerQuitEvent event) {
		plugin.getDreamManager().onDisconnect(event.getPlayer());
	}

	// disabled stuff during dreaming.
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onHunger(FoodLevelChangeEvent event) {
		if (event.getEntityType() == EntityType.PLAYER) {
			Player player = (Player) event.getEntity();
			if (plugin.getDreamManager().isDreaming(player.getName())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntityType() == EntityType.PLAYER) {
			DamageCause cause = event.getCause();
			ConfigManager config = plugin.getConfigManager();
			if ((cause == DamageCause.FALL && config.fallDamageDisabled) || ((cause == DamageCause.ENTITY_ATTACK || cause == DamageCause.PROJECTILE) && config.entityDamageDisabled)) {
				Player player = (Player) event.getEntity();
				if (plugin.getDreamManager().isDreaming(player.getName())) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onItemDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (plugin.getDreamManager().isDreaming(player.getName())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onItemPickup(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		if (plugin.getDreamManager().isDreaming(player.getName())) {
			event.setCancelled(true);
		}
	}

}
