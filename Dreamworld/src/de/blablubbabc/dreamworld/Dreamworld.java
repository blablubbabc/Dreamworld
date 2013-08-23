package de.blablubbabc.dreamworld;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Dreamworld extends JavaPlugin implements Listener {
	public final Random random = new Random();
	public Logger logger;
	
	private String dreamWorldName = "dreamworld";
	private int dreamChance;
	private int dreamDurationMin;
	private int dreamDurationMax;
	//private List<Location> dreamSpawns;
	
	private int purgeAfter;
	
	private String nightmareWorldName = "dreamworld";
	private int nightmareChance;
	private int nightDurationMin;
	private int nightDurationMax;
	
	private Map<Player, PlayerDataStore> players = new HashMap<Player, PlayerDataStore>();
	private Map<String, Integer> dream = new HashMap<String, Integer>();
	private Map<String, Integer> nightmare = new HashMap<String, Integer>();
	private Map<String, Save> saved = new HashMap<String, Save>();
	
	@Override
	public void onEnable(){
		logger = getLogger();
		
		Configuration config = getConfig();
		// dream:
		dreamWorldName = config.getString("dream.worldname", "dreamworld");
		boolean noMobsDream = config.getBoolean("dream.no mobs", false);
		dreamChance = config.getInt("dream.chance", 70);
		dreamDurationMin = config.getInt("dream.min duration in seconds", 15);
		dreamDurationMax = config.getInt("dream.max duration in seconds", 45);
		if(dreamDurationMin < 1) dreamDurationMin = 1;
		if(dreamDurationMax < dreamDurationMin) dreamDurationMax = dreamDurationMin;
		//dreamSpawns = Utils.StringsToLocations(config.getStringList("dream.spawns"));
		purgeAfter = config.getInt("dream.purge saved players after seconds", 86400);
		// nightmare:
		nightmareWorldName = config.getString("dream.nightmare.worldname", "dreamworld");
		boolean noMobsNight = config.getBoolean("dream.nightmare.no mobs", false);
		nightmareChance = config.getInt("dream.nightmare.chance (that a dream results in a nightmare)", 20);
		nightDurationMin = config.getInt("dream.nightmare.min duration in seconds", 5);
		nightDurationMax = config.getInt("dream.nightmare.max duration in seconds", 45);
		if(nightDurationMin < 1) nightDurationMin = 1;
		if(nightDurationMax < nightDurationMin) nightDurationMax = nightDurationMin;
		
		
		// write config back to file:
		config.set("dream.worldname", dreamWorldName);
		config.set("dream.no mobs", noMobsDream);
		config.set("dream.chance", dreamChance);
		config.set("dream.min duration in seconds", dreamDurationMin);
		config.set("dream.max duration in seconds", dreamDurationMax);
		//config.set("dream.spawns");
		config.set("dream.purge saved players after seconds", purgeAfter);
		config.set("dream.nightmare.worldname", nightmareWorldName);
		config.set("dream.nightmare.no mobs", noMobsNight);
		config.set("dream.nightmare.chance (that a dream results in a nightmare)", nightmareChance);
		config.set("dream.nightmare.min duration in seconds", nightDurationMin);
		config.set("dream.nightmare.max duration in seconds", nightDurationMax);
		
		
		// save
		saveConfig();
		
		World dreamWorld = getServer().getWorld(dreamWorldName);
		if (dreamWorld == null) {
			logger.info("World '"+dreamWorldName+"' seems to not be loaded/created. Doing this now..");
			dreamWorld = getDreamWorld();
		}
		if(noMobsDream) dreamWorld.setSpawnFlags(false, false);
		
		World nightmareWorld = getServer().getWorld(nightmareWorldName);
		if (nightmareWorld == null) {
			logger.info("World '"+nightmareWorldName+"' seems to not be loaded/created. Doing this now..");
			nightmareWorld = getNightWorld();
		}
		if(noMobsNight) nightmareWorld.setSpawnFlags(false, false);
		
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getScheduler().runTaskTimer(this, new Runnable() {
			
			@Override
			public void run() {
				//purge saved:
				long time = System.currentTimeMillis();
				Iterator<String> iter = saved.keySet().iterator();
				while(iter.hasNext()) {
					String name = iter.next();
					Save save = saved.get(name);
					if((time-save.getTime()) > purgeAfter) {
						if(save.isNightmare()) {
							nightmare.remove(name);
						} else {
							dream.remove(name);
						}
						saved.remove(name);
					}
				}	
				
				for(String name : dream.keySet()) {
					Player player = getServer().getPlayerExact(name);
					if(player != null && player.isOnline()) {
						int timeLeft = dream.get(name);
						if(timeLeft <= 1) {
							leaveDream(player);
						} else {
							dream.put(name, timeLeft-1);
						}	
					}
				}
				for(String name : nightmare.keySet()) {
					Player player = getServer().getPlayerExact(name);
					if(player != null && player.isOnline()) {
						int timeLeft = nightmare.get(name);
						if(timeLeft <= 1) {
							leaveNight(player);
						} else {
							nightmare.put(name, timeLeft-1);
						}	
					}
				}
			}
		}, 10L, 20L);
		
		logger.info(this.getName()+ " enabled.");
	}
	
	@Override
	public void onDisable() {
		Iterator<Player> iter = players.keySet().iterator();
		while(iter.hasNext()) {
			Player p = iter.next();
			if(isNightmare(p.getName())) leaveNight(p);
			else leaveDream(p);
		}
		
		logger.info(this.getName()+ " disabled.");
	}
	
	@EventHandler
	public void onSleep(PlayerBedLeaveEvent event) {
		final Player player = event.getPlayer();
		if(player.hasPermission("dreamworld.dream")) {
			if(!dream.containsKey(player.getName()) && !nightmare.containsKey(player.getName())) {
				getServer().getScheduler().runTaskLater(this, new Runnable() {
					
					@Override
					public void run() {
						int c = random.nextInt(100);
						if(c < dreamChance) {
							if(c < nightmareChance) {
								//nightmare
								joinNightFresh(player);
							} else {
								//dream
								joinDreamFresh(player);
							}
						}
					}
				}, 1L);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if(players.containsKey(player)) {
				event.setDamage(0);
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onKick(PlayerKickEvent event) {
		onPlayerDisconnect(event.getPlayer());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onQuit(PlayerQuitEvent event) {
		onPlayerDisconnect(event.getPlayer());
	}
	
	private void onPlayerDisconnect(Player player) {
		if(players.containsKey(player)) {
			String name = player.getName();
			boolean bNightmare = isNightmare(name);
			saved.put(name, new Save(player, bNightmare, (bNightmare ? nightmare.get(name):dream.get(name))));
			if(bNightmare) leaveNight(player);
			else leaveDream(player);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Save save = saved.get(player.getName());
		if(save != null) {
			restorePlayer(player, save);
		}
	}
	
	private void restorePlayer(Player player, Save save) {
		String name = player.getName();
		saved.remove(name);
		if(save.isNightmare()) {
			joinNight(player, save.getRemaining(), save.getLocation());
		} else {
			joinDream(player, save.getRemaining(), save.getLocation());
		}
	}
	
	private boolean isNightmare(String name) {
		return nightmare.containsKey(name);
	}
	
	private void joinNightFresh(Player player) {
		//TODO
	}
	
	private void joinNight(Player player, int duration, Location loc) {
		//TODO
	}
	
	private void leaveNight(Player player) {
		//TODO
	}
	
	private void joinDreamFresh(Player player) {
		if(player.isOnline()) joinDream(player, dreamDurationMin + random.nextInt(dreamDurationMax-dreamDurationMin), getDreamWorld().getSpawnLocation());
	}
	
	private void joinDream(Player player, int duration, Location loc) {
		dream.put(player.getName(), duration);
		players.put(player, new PlayerDataStore(player));
		player.teleport(loc);
		//EFECTS
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 20, false));
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 10, false));
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, false));
		player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Integer.MAX_VALUE, 1, false));
		player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 100, false));
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 100, false));
	}
	
	private void leaveDream(Player player) {
		players.get(player).restorePlayer();
		players.remove(player);
		dream.remove(player.getName());
	}
	
	private World getDreamWorld() {
		return getLoadWorld(dreamWorldName);
	}
	
	private World getNightWorld() {
		return getLoadWorld(nightmareWorldName);
	}
	
	private World getLoadWorld(String worldname) {
		return getServer().createWorld(new WorldCreator(worldname));
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(label.equalsIgnoreCase("dw") && args.length == 1) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("These commands are meant to be executed by players only.");
				return true;
			}
			final Player player = (Player) sender;
			if(!(player.hasPermission("dreamworld.admin") || player.isOp())) {
				sender.sendMessage(ChatColor.RED+"No permission.");
				return true;
			}
			if(args[0].equalsIgnoreCase("help")) {
				
				sender.sendMessage(ChatColor.WHITE+"*~*~"+ChatColor.AQUA+"Dreamworld Help"+ChatColor.WHITE+"~*~*");
				sender.sendMessage(ChatColor.GOLD+"/dw setspawn"+ChatColor.WHITE+" - "+ChatColor.DARK_AQUA+"Sets the spawn location of the actual world.");
				sender.sendMessage(ChatColor.GOLD+"/dw dspawn"+ChatColor.WHITE+" - "+ChatColor.DARK_AQUA+"Teleport to the dream world.");
				sender.sendMessage(ChatColor.GOLD+"/dw nspawn"+ChatColor.WHITE+" - "+ChatColor.DARK_AQUA+"Teleport to the nightmare world.");
				
			} else if(args[0].equalsIgnoreCase("setspawn")) {
				player.getWorld().setSpawnLocation(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
			} else if(args[0].equalsIgnoreCase("spawndream") || args[0].equalsIgnoreCase("dspawn")) {
				player.teleport(getDreamWorld().getSpawnLocation());
			} else if(args[0].equalsIgnoreCase("spawnnightmare") || args[0].equalsIgnoreCase("spawnnight") || args[0].equalsIgnoreCase("nspawn")) {
				player.teleport(getNightWorld().getSpawnLocation());
			}
			return true;
		}
		return false;
	}
	
	
}
