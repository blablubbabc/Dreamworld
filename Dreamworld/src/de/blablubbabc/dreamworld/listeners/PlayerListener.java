package de.blablubbabc.dreamworld.listeners;

import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
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
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent event) {
		plugin.getDreamManager().onPlayerDeath(event.getEntity());
	}

	// disabled stuff during dreaming:
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (plugin.getDreamManager().isDreaming(player.getName())) {
			if (isAllowedCommand(event.getMessage())) {
				if (!player.hasPermission(plugin.ADMIN_PERMISSION)) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	private boolean isAllowedCommand(String cmd) {
		List<String> allowedCommands = plugin.getConfigManager().allowedCommands;
		if (allowedCommands.contains(cmd)) return true;
		String[] split = cmd.split(" ");
		String cmds = "";
		for (int i = 0; i < split.length; i++) {
			cmds += split[i];
			if (allowedCommands.contains(cmds) || allowedCommands.contains(cmds + " *")) return true;
			cmds += " ";
		}
		return false;
	}
	
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
			if (config.allDamageDisabled || (cause == DamageCause.FALL && config.fallDamageDisabled) || ((cause == DamageCause.ENTITY_ATTACK || cause == DamageCause.PROJECTILE) && config.entityDamageDisabled)) {
				Player player = (Player) event.getEntity();
				if (plugin.getDreamManager().isDreaming(player.getName())) {
					event.setCancelled(true);
					if (cause == DamageCause.VOID) {
						// respawn player:
						plugin.getDreamManager().spawnPlayer(player);
					}
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
