package de.blablubbabc.dreamworld;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

class PlayerDataStore {
	private Player player;

	// DATA
	// Location
	private Location location;
	// Inventory
	private ItemStack[] invContent;
	private ItemStack[] invArmor;
	// PotionEffects
	private ArrayList<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
	// Flying
	private boolean allowFlight;
	private boolean isFlying;
	// Status
	private float exhaustion;
	private float saturation;
	private int foodlevel;
	private double health;
	private int fireTicks;
	private int remainingAir;
	private int ticksLived;
	private int noDamageTicks;
	private float fallDistance;
	private GameMode gamemode;
	private double lastDamage;
	private EntityDamageEvent lastDamageCause;
	// Level / exp
	private int level;
	private float exp;

	PlayerDataStore(Player player) {
		this.player = player;
		storePlayer();
	}

	void storePlayer() {
		// STORE DATA
		// Location
		location = player.getLocation();
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
		exhaustion = player.getExhaustion();
		saturation = player.getSaturation();
		foodlevel = player.getFoodLevel();
		health = player.getHealth();
		fireTicks = player.getFireTicks();
		remainingAir = player.getRemainingAir();
		ticksLived = player.getTicksLived();
		if(ticksLived < 1) ticksLived = 1;
		noDamageTicks = player.getNoDamageTicks();
		fallDistance = player.getFallDistance();
		gamemode = player.getGameMode();
		lastDamage = player.getLastDamage();
		lastDamageCause = player.getLastDamageCause();
		// vehicle
		// Level / exp
		level = player.getLevel();
		exp = player.getExp();

		// CLEAR
		clearPlayer();
	}

	void clearPlayer() {
		// CLEAR PLAYER
		// Names
		// displayname
		// location
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
		// Status
		// exhaustion
		// saturation
		player.setFoodLevel(20);
		player.setHealth(20);
		player.setFireTicks(0);
		// remainingAir
		// ticksLived
		// noDamageTicks
		// fallDistance
		player.setGameMode(GameMode.ADVENTURE);
		// lastDamage
		// lastDamageCause
		player.leaveVehicle();
		// Level / exp
		player.setLevel(0);
		player.setExp(0F);
	}

	void restorePlayer() {
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
		player.setExhaustion(exhaustion);
		player.setSaturation(saturation);
		player.setFoodLevel(foodlevel);
		player.setHealth(health);
		player.setFireTicks(fireTicks);
		player.setRemainingAir(remainingAir);
		player.setTicksLived(ticksLived);
		player.setNoDamageTicks(noDamageTicks);
		player.setFallDistance(fallDistance);
		player.setGameMode(gamemode);
		player.setLastDamage(lastDamage);
		player.setLastDamageCause(lastDamageCause);
		// Level / exp
		player.setLevel(level);
		player.setExp(exp);
		// location
		player.teleport(location);
	}
}
