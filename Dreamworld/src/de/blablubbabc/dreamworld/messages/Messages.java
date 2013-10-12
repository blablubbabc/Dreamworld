package de.blablubbabc.dreamworld.messages;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.blablubbabc.dreamworld.DreamworldPlugin;

public class Messages {
	private static String[] messages;

	// loads messages from the messages.yml configuration file into memory
	public static void loadMessages(String messagesFilePath) {
		Message[] messageIDs = Message.values();
		messages = new String[Message.values().length];

		HashMap<String, CustomizableMessage> defaults = new HashMap<String, CustomizableMessage>();
		
		// initialize default messages
		addDefault(defaults, Message.DREAM_ENTER, "&8Woosh! &7It seems that you are dreaming!", null);
		addDefault(defaults, Message.DREAM_LEAVE, "&8Woosh! &7Good morning. Did you sleep well?", null);
		addDefault(defaults, Message.NO_PERMISSION, "&cYou don't have the permission for that.", null);
		
		// load the message file
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File(messagesFilePath));

		// for each message ID
		for (int i = 0; i < messageIDs.length; i++) {
			// get default for this message
			Message messageID = messageIDs[i];
			CustomizableMessage messageData = defaults.get(messageID.name());

			// if default is missing, log an error and use some fake data for
			// now so that the plugin can run
			if (messageData == null) {
				DreamworldPlugin.getInstance().getLogger().severe("Missing message for " + messageID.name() + ".  Please contact the developer.");
				messageData = new CustomizableMessage(messageID, "Missing message!  ID: " + messageID.name() + ".  Please contact a server admin.", null);
			}

			// read the message from the file, use default if necessary
			messages[messageID.ordinal()] = config.getString("Messages." + messageID.name() + ".Text", messageData.text);
			config.set("Messages." + messageID.name() + ".Text", messages[messageID.ordinal()]);
			// translate colors
			messages[messageID.ordinal()] = ChatColor.translateAlternateColorCodes('&', messages[messageID.ordinal()]);

			if (messageData.notes != null) {
				messageData.notes = config.getString("Messages." + messageID.name() + ".Notes", messageData.notes);
				config.set("Messages." + messageID.name() + ".Notes", messageData.notes);
			}
		}

		// save any changes
		try {
			config.save(messagesFilePath);
		} catch (IOException exception) {
			DreamworldPlugin.getInstance().getLogger().severe("Unable to write to the configuration file at \"" + messagesFilePath + "\"");
		}

		defaults.clear();
		System.gc();
	}

	// helper for above, adds a default message and notes to go with a message
	private static void addDefault(HashMap<String, CustomizableMessage> defaults, Message id, String text, String notes) {
		CustomizableMessage message = new CustomizableMessage(id, text, notes);
		defaults.put(id.name(), message);
	}

	// gets a message from memory
	public static String getMessage(Message messageID, String... args) {
		String message = messages[messageID.ordinal()];

		for (int i = 0; i < args.length; i++) {
			String param = args[i];
			message = message.replace("{" + i + "}", param);
		}

		return message;

	}
}
