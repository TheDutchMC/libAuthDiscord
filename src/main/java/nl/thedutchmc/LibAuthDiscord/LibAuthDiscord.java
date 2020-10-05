package nl.thedutchmc.LibAuthDiscord;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import nl.thedutchmc.LibAuthDiscord.authentication.Authentication;
import nl.thedutchmc.LibAuthDiscord.commands.AuthCommandExecutor;
import nl.thedutchmc.LibAuthDiscord.commands.AuthCommandTabCompleter;
import nl.thedutchmc.LibAuthDiscord.discord.JdaHandler;
import nl.thedutchmc.LibAuthDiscord.minecraftEvents.PlayerLoginEventListener;
import nl.thedutchmc.LibAuthDiscord.whitelist.Whitelist;

public class LibAuthDiscord extends JavaPlugin {
	
	private static JdaHandler JDA;
	private static ConfigurationHandler CONFIG;
	
	@Override
	public void onEnable() {
		
		//Initialize the configuration, and read it
		CONFIG = new ConfigurationHandler(this);
		CONFIG.loadConfig();
		
		//Initialize JDA and connect with Discord
		JDA = new JdaHandler(this);
		JDA.setup(CONFIG.token);
		
		//Initalize the Authentication system
		new Authentication(this, CONFIG.authProfileLocation);
		
		//Initalize the Whitelist system
		new Whitelist(CONFIG.useWhitelist, CONFIG.permittedRoles, CONFIG.guildId, JDA);
		
		//Register Minecraft events
		Bukkit.getPluginManager().registerEvents(new PlayerLoginEventListener(JDA), this);
		
		//Minecraft commands
		this.getCommand("auth").setExecutor(new AuthCommandExecutor(this));
		this.getCommand("auth").setTabCompleter(new AuthCommandTabCompleter());
	}
	
	@Override
	public void onDisable() {
		
	}
	
	/**
	 * Log something via the libAuthDiscord logger with level INFO
	 * @param log The String to log
	 */
	public void logInfo(String log) {
		this.getLogger().info(log);
	}
	
	/**
	 * Log something via the libAuthDiscord logger with level WARN
	 * @param log The String to log
	 */
	public void logWarn(String log) {
		this.getLogger().warning(log);
	}
	
	/**
	 * Get the prefix used for sending messages to the Player in-game
	 * @return the prefix 
	 */
	public String getPrefix() {
		return ChatColor.GRAY + "[" + ChatColor.AQUA + "AUTH" + ChatColor.GRAY + "] " + ChatColor.RESET; 
	}
}
