package de.blablubbabc.dreamworld.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import de.blablubbabc.dreamworld.DreamworldPlugin;
import de.blablubbabc.dreamworld.managers.ConfigManager;

public class WorldListener extends AbstractListener {

	public WorldListener(DreamworldPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onRainStart(WeatherChangeEvent event) {
		ConfigManager config = plugin.getConfigManager();
		if (event.getWorld().getName().equals(config.dreamWorldName)) {
			if (event.toWeatherState() && config.weatherDisabled) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onThinderStart(ThunderChangeEvent event) {
		ConfigManager config = plugin.getConfigManager();
		if (event.getWorld().getName().equals(config.dreamWorldName)) {
			if (event.toThunderState() && config.weatherDisabled) {
				event.setCancelled(true);
			}
		}
	}
}
