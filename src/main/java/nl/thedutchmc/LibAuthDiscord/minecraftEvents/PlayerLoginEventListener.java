package nl.thedutchmc.LibAuthDiscord.minecraftEvents;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import net.md_5.bungee.api.ChatColor;
import nl.thedutchmc.LibAuthDiscord.authentication.Authentication;
import nl.thedutchmc.LibAuthDiscord.discord.JdaHandler;
import nl.thedutchmc.LibAuthDiscord.whitelist.Whitelist;

public class PlayerLoginEventListener implements Listener {

	private JdaHandler jdaHandler;
	
	public PlayerLoginEventListener(JdaHandler jdaHandler) {
		this.jdaHandler = jdaHandler;
	}
	
	@EventHandler
	public void onPlayerLoginEventListener(PlayerLoginEvent event) {
		
		if(event.getPlayer().isOp()) {
			return;
		}

		if(!Authentication.isAuthenticated(event.getPlayer().getUniqueId())) {
			
			int code = Authentication.createPendingAuthentication(event.getPlayer().getUniqueId(), event.getPlayer().getName());
			
			event.disallow(Result.KICK_OTHER, 
					ChatColor.GOLD + "You have not linked your Discord yet!\n"
					+ "Please send a private message to " + ChatColor.RED + jdaHandler.getBotNameAsTag() + ChatColor.GOLD + " with the code: " + ChatColor.RED + code + ChatColor.GOLD + "!");
			
			return;
		}
		
		String discordId = Authentication.getAuthProfile(event.getPlayer().getUniqueId()).getDiscordId();
		boolean isPermitted = Whitelist.isEntryPermitted(discordId);
		
		
		if(!isPermitted) {
			event.disallow(Result.KICK_WHITELIST,
					ChatColor.GOLD + "You do not have the correct role to be allowed on this server! Please contact an Administrator of your server!");
		}
	}
}
