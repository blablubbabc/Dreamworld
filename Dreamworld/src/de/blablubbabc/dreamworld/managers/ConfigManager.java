package de.blablubbabc.dreamworld.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.blablubbabc.dreamworld.objects.SoftLocation;

public class ConfigManager {
	
	private Plugin plugin;
	
	// world settings:
	public String dreamWorldName;
	public boolean noAnimalSpawning;
	public boolean noMonsterSpawning;
	
	public int dreamChance;
	
	public int minDurationSeconds;
	public int maxDurationSeconds;
	
	// spawning:
	public boolean spawnRandomly;
	public List<SoftLocation> dreamSpawns;
	
	public boolean applyInitialGamemode;
	public boolean applyInitialHealth;
	public boolean applyInitialHunger;
	public boolean applyInitialPotionEffects;
	
	public GameMode initialGamemode;
	public double initialHealth;
	public int initialHunger;
	public List<PotionEffect> initialPotionEffects;
	
	public boolean clearAndRestorePlayer;
	public int purgeAfterMinutes;
	public int ignoreIfRemainingTimeIsLowerThan;
	
	// fake time
	public boolean fakeTimeEnabled;
	public int fakeTime;
	public int fakeTimeRandomBounds;
	public boolean fakeTimeFixed;
	
	// fake weather
	public boolean fakeRain;
	
	// disabled:
	public boolean hungerDisabled;
	public boolean fallDamageDisabled;
    public boolean entityDamageDisabled;
	public boolean weatherDisabled;
	public boolean itemDroppingDisabled;
	public boolean itemPickupDisabled;
	
	
	public ConfigManager(Plugin plugin) {
		this.plugin = plugin;
		// default config:
		FileConfiguration config = plugin.getConfig();
		config.options().copyDefaults(true);
		plugin.saveDefaultConfig();
		
		// load values:
		
		try {
			
			ConfigurationSection dreamSection = config.getConfigurationSection("dream");
			// world settings:
			dreamWorldName = dreamSection.getString("world name");
			noAnimalSpawning = dreamSection.getBoolean("no animal spawning");
			noMonsterSpawning = dreamSection.getBoolean("no monster spawning");
			
			dreamChance = dreamSection.getInt("chance");
			
			minDurationSeconds = dreamSection.getInt("min duration in seconds");
			maxDurationSeconds = dreamSection.getInt("max duration in seconds");
			
			// spawning:
			spawnRandomly = dreamSection.getBoolean("spawn randomly each time");
			dreamSpawns = SoftLocation.getFromStringList(dreamSection.getStringList("random spawns"));
			
			// some initial values:
				        
			applyInitialGamemode = dreamSection.getBoolean("gamemode.apply");
			initialGamemode = GameMode.getByValue(dreamSection.getInt("gamemode.initial gamemode"));
			
			applyInitialHealth = dreamSection.getBoolean("health.apply");
			initialHealth = dreamSection.getInt("health.initial health");
			
			applyInitialHunger = dreamSection.getBoolean("hunger.apply");
			initialHunger = dreamSection.getInt("hunger.initial hunger");
			
			applyInitialPotionEffects = dreamSection.getBoolean("potion effects.apply");
			initialPotionEffects = new ArrayList<PotionEffect>();
			ConfigurationSection potionsSection = dreamSection.getConfigurationSection("potion effects.initial potion effects");
			if (potionsSection != null) {
				for (String type : potionsSection.getKeys(false)) {
					ConfigurationSection potionSection = potionsSection.getConfigurationSection(type);
					if (potionSection == null) continue;
					initialPotionEffects.add(new PotionEffect(PotionEffectType.getByName(type), potionSection.getInt("duration"), potionSection.getInt("level", 1)));
				}
			}
			
			clearAndRestorePlayer = dreamSection.getBoolean("clear and restore player");
			purgeAfterMinutes = dreamSection.getInt("purge saved dream data after x minutes");
			ignoreIfRemainingTimeIsLowerThan = dreamSection.getInt("ignore if remaining seconds is lower than");
			
			// fake time
			ConfigurationSection fakeTimeSection = dreamSection.getConfigurationSection("fake client time");
			fakeTimeEnabled = fakeTimeSection.getBoolean("enabled") ;
			fakeTime = fakeTimeSection.getInt("time (in ticks)");
			fakeTimeRandomBounds = fakeTimeSection.getInt("random bounds");
			fakeTimeFixed = fakeTimeSection.getBoolean("fixed time");
			
			// fake weather
			fakeRain = dreamSection.getBoolean("fake client weather.raining");
			
			// disabled:
			ConfigurationSection disabledSection = dreamSection.getConfigurationSection("disabled");
			hungerDisabled = disabledSection.getBoolean("hunger");
			fallDamageDisabled = disabledSection.getBoolean("fall damage");
		    entityDamageDisabled = disabledSection.getBoolean("entity damage");
			weatherDisabled = disabledSection.getBoolean("weather");
			itemDroppingDisabled = disabledSection.getBoolean("item dropping");
			itemPickupDisabled = disabledSection.getBoolean("item pickup");
		} catch (Exception e) {
			e.printStackTrace();
			plugin.getLogger().severe("Something went wrong during the reading of the config file! Please verify that all your values are valid! Disabling now.");
			Bukkit.getServer().getPluginManager().disablePlugin(plugin);
		}
	}
	
	public void addSpawnLocation(Location location) {
		dreamSpawns.add(new SoftLocation(location));
		saveSpawns();
	}
	
	public void removeAllSpawnLocations() {
		dreamSpawns.clear();
		saveSpawns();
	}
	
	private void saveSpawns() {
		plugin.getConfig().set("random spawns", SoftLocation.toStringList(dreamSpawns));
		plugin.saveConfig();
	}
}
