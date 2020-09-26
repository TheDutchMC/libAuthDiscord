package nl.thedutchmc.LibAuthDiscord;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import nl.thedutchmc.LibAuthDiscord.authentication.Authentication;
import nl.thedutchmc.LibAuthDiscord.discord.JdaHandler;
import nl.thedutchmc.LibAuthDiscord.minecraftEvents.PlayerLoginEventListener;
import nl.thedutchmc.LibAuthDiscord.whitelist.Whitelist;

public class LibAuthDiscord extends JavaPlugin {
	
	private static JdaHandler JDA;
	private static ConfigurationHandler CONFIG;
	
	@Override
	public void onEnable() {
		
		CONFIG = new ConfigurationHandler(this);
		CONFIG.loadConfig();
		
		JDA = new JdaHandler(this);
		
		JDA.setup(CONFIG.token);
		
		new Authentication(this, CONFIG.authProfileLocation);
		new Whitelist(CONFIG.useWhitelist, CONFIG.permittedRoles, CONFIG.guildId, JDA);
		
		//Register Minecraft events
		Bukkit.getPluginManager().registerEvents(new PlayerLoginEventListener(JDA), this);
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public void logInfo(String log) {
		this.getLogger().info(log);
	}
	
	public void logWarn(String log) {
		this.getLogger().warning(log);
	}
}
