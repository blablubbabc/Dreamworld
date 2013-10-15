package de.blablubbabc.dreamworld.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import de.blablubbabc.dreamworld.DreamworldPlugin;

public class PlayerDataStore implements ConfigurationSerializable {
	private String playerName;

	// DATA
	// Location
	private SoftLocation location;
	// Inventory
	private ItemStack[] invContent;
	private ItemStack[] invArmor;
	// PotionEffects
	private List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
	// Flying
	private boolean allowFlight;
	private boolean isFlying;
	// Status
	private float exhaustion;
	private float saturation;
	private int foodlevel;
	private double health;
	private double healthScale;
	private boolean healthScaled;
	private GameMode gamemode;
	// level / exp
	private int level;
	private float exp;
	// player time
	private boolean playerTimeRelative;
	private long playerTime;
	

	public PlayerDataStore(Player player) {
		this.playerName = player.getName();
		storePlayer();
	}
	
	@SuppressWarnings("unchecked")
	public PlayerDataStore(String playerName, Map<String, Object> map) {
		this.playerName = playerName;
		
		this.location = SoftLocation.getFromString((String) map.get("location"));
		this.invContent = ((List<ItemStack>) map.get("inv content")).toArray(new ItemStack[0]);
		this.invArmor = ((List<ItemStack>) map.get("inv armor")).toArray(new ItemStack[0]);
		this.potionEffects = (List<PotionEffect>) map.get("potion effects");
		this.allowFlight = (Boolean) map.get("allow flight");
		this.isFlying = (Boolean) map.get("is flying");
		this.gamemode = GameMode.getByValue((Integer) map.get("gamemode"));
		this.exhaustion = (Float) map.get("exhaustion");
		this.saturation = (Float) map.get("saturation");
		this.foodlevel = (Integer) map.get("foodlevel");
		this.health = (Double) map.get("health");
		this.healthScale = (Double) map.get("health scale");
		this.healthScaled = (Boolean) map.get("is health scaled");
		this.level = (Integer) map.get("level");
		this.exp = (Float) map.get("exp");
		this.playerTimeRelative = (Boolean) map.get("is playertime relative");
		this.playerTime = (Long) map.get("playertime");
	}

	public void storePlayer() {
		Player player = Bukkit.getPlayerExact(playerName);
		// STORE DATA
		// Location
		location = new SoftLocation(player.getLocation());
		// Inventory
		player.closeInventory();
		PlayerInventory inv = player.getInventory();
		invContent = inv.getContents();
		invArmor = inv.getArmorContents();
		// PotionEffects
		potionEffects.addAll(player.getActivePotionEffects());
		// Flying
		allowFlight = player.getAllowFlight();
		isFlying = player.isFlying();
		// Status
		gamemode = player.getGameMode();
		exhaustion = player.getExhaustion();
		saturation = player.getSaturation();
		foodlevel = player.getFoodLevel();
		// health
		health = player.getHealth();
		healthScale = player.getHealthScale();
		healthScaled = player.isHealthScaled();
		// vehicle
		// Level / exp
		level = player.getLevel();
		exp = player.getExp();
		// player time and weather
		playerTimeRelative = player.isPlayerTimeRelative();
		playerTime = playerTimeRelative ? player.getPlayerTimeOffset() : player.getPlayerTime();
		
		// CLEAR
		if (DreamworldPlugin.getInstance().getConfigManager().clearAndRestorePlayer) {
			clearPlayer();
		}
	}

	public void clearPlayer() {
		Player player = Bukkit.getPlayerExact(playerName);
		// CLEAR PLAYER
		// Inventory
		player.closeInventory();
		player.getInventory().clear(-1, -1);
		// PotionEffects
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		// Flying
		player.setAllowFlight(false);
		player.setFlying(false);
		
		player.setGameMode(GameMode.ADVENTURE);
		player.setFoodLevel(20);
		player.setSaturation(20.0F);
		player.setExhaustion(0.0F);
		player.setHealth(20);
		player.setFireTicks(0);
		
		player.leaveVehicle();
		// Level / exp
		player.setLevel(0);
		player.setExp(0F);
	}

	public boolean isValidLocation() {
		Location teleportLocation = location.getBukkitLocation();
		return teleportLocation != null;
	}
	
	public void restorePlayer() {
		Player player = Bukkit.getPlayerExact(playerName);
		
		if (DreamworldPlugin.getInstance().getConfigManager().clearAndRestorePlayer) {
			clearPlayer();
			
			// RESTORE PLAYER
			// Inventory
			if (invContent != null) {
				player.getInventory().setContents(invContent);
			}
			if (invArmor != null) {
				player.getInventory().setArmorContents(invArmor);
			}
			// PotionEffects
			for (PotionEffect effect : potionEffects) {
				player.addPotionEffect(effect);
			}
			// Flying
			player.setAllowFlight(allowFlight);
			player.setFlying(isFlying);
			// Status
			player.setGameMode(gamemode);
			player.setExhaustion(exhaustion);
			player.setSaturation(saturation);
			player.setFoodLevel(foodlevel);
			// health
			player.setHealth(health);
			player.setHealthScale(healthScale);
			player.setHealthScaled(healthScaled);
			// Level / exp
			player.setLevel(level);
			player.setExp(exp);
		}
		
		// location
		Location teleportLocation = location.getBukkitLocation();
		if (teleportLocation != null) {
			player.teleport(teleportLocation);
		} else {
			DreamworldPlugin.getInstance().getLogger().severe("Restore location for player '" + playerName + "' is no longer available. Was the world '" + location.getWorldName() + "' unloaded or renamed?");
		}
		
		// player time
		player.setPlayerTime(playerTimeRelative ? teleportLocation.getWorld().getTime() + playerTime: playerTime, playerTimeRelative);
		
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("location", location.toString());
		map.put("inv content", Arrays.asList(invContent));
		map.put("inv armor", Arrays.asList(invArmor));
		map.put("potion effects", potionEffects);
		map.put("allow flight", allowFlight);
		map.put("is flying", isFlying);
		map.put("gamemode", gamemode.getValue());
		map.put("exhaustion", exhaustion);
		map.put("saturation", saturation);
		map.put("foodlevel", foodlevel);
		map.put("health", health);
		map.put("health scale", healthScale);
		map.put("is health scaled", healthScaled);
		map.put("level", level);
		map.put("exp", exp);
		map.put("is playertime relative", playerTimeRelative);
		map.put("playertime", playerTime);
		
		return map;
	}
}
