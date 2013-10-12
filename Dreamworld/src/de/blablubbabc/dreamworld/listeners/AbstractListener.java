package de.blablubbabc.dreamworld.listeners;

import org.bukkit.event.Listener;

import de.blablubbabc.dreamworld.DreamworldPlugin;

public abstract class AbstractListener implements Listener {
	protected DreamworldPlugin plugin;
	
	public AbstractListener(DreamworldPlugin plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
}
