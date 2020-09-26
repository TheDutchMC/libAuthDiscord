package nl.thedutchmc.LibAuthDiscord;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigurationHandler {

	private LibAuthDiscord plugin;
	
	String token, guildId, authProfileLocation;
	boolean useWhitelist, useAlternateLocation;
	List<String> permittedRoles;
	
	public ConfigurationHandler(LibAuthDiscord plugin) {
		this.plugin = plugin;
	}
	
	private File file;
	private FileConfiguration config;
	
	private FileConfiguration getConfig() {
		return config;
	}
	
	public void loadConfig() {
		file = new File(plugin.getDataFolder(), "config.yml");
		
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			plugin.saveResource("config.yml", false);
		}
		
		config = new YamlConfiguration();
		
		try {
			config.load(file);
			readConfig();
		} catch (InvalidConfigurationException e) {
			plugin.logWarn("Invalid config.yml!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readConfig() {
		token = this.getConfig().getString("token");
		
		useWhitelist = this.getConfig().getBoolean("useWhitelist");
		guildId = this.getConfig().getString("guildId");
		permittedRoles = this.getConfig().getStringList("permittedRoles");
		
		useAlternateLocation = this.getConfig().getBoolean("useAlternateLocation");
		
		authProfileLocation = (useAlternateLocation) ? this.getConfig().getString("alternateLocation") : plugin.getDataFolder() + File.separator + "AuthProfiles";
	}
}
