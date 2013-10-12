package de.blablubbabc.dreamworld.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.blablubbabc.dreamworld.DreamworldPlugin;
import de.blablubbabc.dreamworld.messages.Message;
import de.blablubbabc.dreamworld.messages.Messages;

public class CommandManager implements CommandExecutor {

	private DreamworldPlugin plugin;
	
	public CommandManager(DreamworldPlugin plugin) {
		this.plugin = plugin;
		plugin.getCommand("dreamworld").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 1) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("These commands are meant to be executed by players only.");
				return true;
			}
			
			final Player player = (Player) sender;
			if (!(player.hasPermission("dreamworld.admin") || player.isOp())) {
				sender.sendMessage(Messages.getMessage(Message.NO_PERMISSION));
				return true;
			}
			
			if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
				sender.sendMessage(ChatColor.WHITE + "     ~~~ " + ChatColor.AQUA + "Dreamworld Help" + ChatColor.WHITE + " ~~~    ");
				sender.sendMessage(ChatColor.GOLD + "/dw setspawn" + ChatColor.WHITE + " - " + ChatColor.DARK_AQUA + "Sets the spawn location of your current world.");
				sender.sendMessage(ChatColor.GOLD + "/dw dspawn" + ChatColor.WHITE + " - " + ChatColor.DARK_AQUA + "Teleports you to the dream world.");
				sender.sendMessage(ChatColor.GOLD + "/dw spawn" + ChatColor.WHITE + " - " + ChatColor.DARK_AQUA + "Teleports you to the main world's spawn.");
				sender.sendMessage(ChatColor.GOLD + "/dw addspawn" + ChatColor.WHITE + " - " + ChatColor.DARK_AQUA + "Adds your current location to the list of random dream spawns.");
				sender.sendMessage(ChatColor.GOLD + "/dw clearspawns" + ChatColor.WHITE + " - " + ChatColor.DARK_AQUA + "Clears the list of random dream spawns.");
			} else if (args[0].equalsIgnoreCase("setspawn")) {
				Location location = player.getLocation();
				player.getWorld().setSpawnLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ());
				player.sendMessage(ChatColor.GREEN + "The spawn of this world was set to your current position.");
			} else if (args[0].equalsIgnoreCase("dspawn") || args[0].equalsIgnoreCase("spawndream")) {
				player.teleport(plugin.getDreamWorld().getSpawnLocation());
				player.sendMessage(ChatColor.GREEN + "Whoosh! You are now at the dreamworld's spawn.");
			} else if (args[0].equalsIgnoreCase("addspawn")) {
				plugin.getConfigManager().addSpawnLocation(player.getLocation());
				player.sendMessage(ChatColor.GREEN + "Your current position was added to the list of random dream spawns.");
			} else if (args[0].equalsIgnoreCase("clearspawns") || args[0].equalsIgnoreCase("clearspawns")) {
				plugin.getConfigManager().removeAllSpawnLocations();
				player.sendMessage(ChatColor.GREEN + "All random dream spawns were removed!");
			} else if (args[0].equalsIgnoreCase("spawn")) {
				World world = Bukkit.getServer().getWorlds().get(0);
				player.leaveVehicle();
				player.teleport(world.getSpawnLocation());
				player.sendMessage(ChatColor.GREEN + "You were teleported to world '" + ChatColor.WHITE + world.getName() + ChatColor.GREEN + "'.");
			}
			
			return true;
		}
		return false;
	}
}
