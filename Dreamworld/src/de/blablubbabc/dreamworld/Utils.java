package de.blablubbabc.dreamworld;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

class Utils {
	// LOCATIONS TO / FROM STRING

	static String LocationToString(Location loc) {
		return loc.getWorld().getName() + ";" + loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch();
	}
	
	static Location StringToLocation(String string) {
		if (string == null) return null;
		String[] split = string.split(";");
		if (split.length != 6) return null;
		World world = Bukkit.getWorld(split[0]);
		if (world == null) return null;
		Double x = parseDouble(split[1]);
		if (x == null) return null;
		Double y = parseDouble(split[2]);
		if (y == null) return null;
		Double z = parseDouble(split[3]);
		if (z == null) return null;
		Float yaw = parseFloat(split[4]);
		if (yaw == null) return null;
		Float pitch = parseFloat(split[5]);
		if (pitch == null) return null;
		return new Location(world, x, y, z, yaw, pitch);
	}
	
	static List<Location> StringsToLocations(List<String> strings) {
		List<Location> locs = new ArrayList<Location>();
		for (String s : strings) {
			Location loc = StringToLocation(s);
			if (locs != null) locs.add(loc);
		}
		return locs;
	}
	
	static List<String> LocationsToStrings(List<Location> locs) {
		List<String> strings = new ArrayList<String>();
		for (Location loc : locs) {
			if (loc != null) strings.add(LocationToString(loc));
		}
		return strings;
	}
	
	private static Double parseDouble(String s) {
		try {
			return Double.parseDouble(s);
		} catch (Exception e) {
			return null;
		}
	}
	
	private static Float parseFloat(String s) {
		try {
			return Float.parseFloat(s);
		} catch (Exception e) {
			return null;
		}
	}
}
