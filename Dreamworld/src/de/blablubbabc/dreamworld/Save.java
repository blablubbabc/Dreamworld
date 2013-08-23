package de.blablubbabc.dreamworld;

import org.bukkit.Location;
import org.bukkit.entity.Player;

class Save {
	private final Location location;
	private final long time;
	private final int remaining;
	private final boolean nightmare;
	
	Save(Player player, boolean nightmare, int remaining) {
		location = player.getLocation();
		time = System.currentTimeMillis();
		this.nightmare = nightmare;
		this.remaining = remaining;
	}
	
	Location getLocation() {
		return location;
	}
	
	long getTime() {
		return time;
	}
	
	boolean isNightmare() {
		return nightmare;
	}
	
	int getRemaining() {
		return remaining;
	}
}
