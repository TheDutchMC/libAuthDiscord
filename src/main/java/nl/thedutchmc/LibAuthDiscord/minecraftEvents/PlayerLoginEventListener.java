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
		
		//Check if the player is OP, if yes, return. We do this because OPs override any requirement we set
		if(event.getPlayer().isOp()) {
			return;
		}
		
		//Check if the player is whitelisted, if yes, return. We do this because server administrators might want to let players in who e.g don't have Discord
		if(event.getPlayer().isWhitelisted()) {
			return;
		}

		//Check if the user is authenticated. If not, inform them they need to authenticate themselves and give them instructions on how to do so
		if(!Authentication.isAuthenticated(event.getPlayer().getUniqueId())) {
			
			//Generate a pendingAuthentication for the player, or if one already exists, get the code
			int code = Authentication.createPendingAuthentication(event.getPlayer().getUniqueId(), event.getPlayer().getName());
			
			//Prevent them from joining and tell them how they can authenticate themselves
			event.disallow(Result.KICK_OTHER, 
					ChatColor.GOLD + "You have not linked your Discord yet!\n"
					+ "Please send a private message to " + ChatColor.RED + jdaHandler.getBotNameAsTag() + ChatColor.GOLD + " with the code: " + ChatColor.RED + code + ChatColor.GOLD + "!");
			
			return;
		}
		
		//Get the DiscordID of the player who is trying to join, by getting their AuthProfile and getting the ID from that
		String discordId = Authentication.getAuthProfile(event.getPlayer().getUniqueId()).getDiscordId();
		
		//Get if the user is permitted to join the server or not
		boolean isPermitted = Whitelist.isEntryPermitted(discordId);
		
		//Check if the user is permitted to join the server
		//If they are not, kick them and tell them that they do not have the required role to be on the server
		if(!isPermitted) {
			event.disallow(Result.KICK_WHITELIST,
					ChatColor.GOLD + "You do not have the correct role to be allowed on this server! Please contact an Administrator of your server!");
		}
	}
}
