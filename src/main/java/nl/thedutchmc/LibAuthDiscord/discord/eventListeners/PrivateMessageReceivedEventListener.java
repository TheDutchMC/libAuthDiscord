package nl.thedutchmc.LibAuthDiscord.discord.eventListeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.ChatColor;
import nl.thedutchmc.LibAuthDiscord.LibAuthDiscord;
import nl.thedutchmc.LibAuthDiscord.authentication.AuthProfile;
import nl.thedutchmc.LibAuthDiscord.authentication.Authentication;

public class PrivateMessageReceivedEventListener extends ListenerAdapter {

	private LibAuthDiscord plugin;
	
	public PrivateMessageReceivedEventListener(LibAuthDiscord plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
		
		if(event.getAuthor().isBot()) {
			return;
		}
		
		String message = event.getMessage().getContentDisplay();
				
		if(message.equalsIgnoreCase("unlink")) {
			
			if(!Authentication.isAuthenticated(event.getAuthor().getId())) {
				event.getChannel().sendMessage("You are not authenticated, so we cannot unlink!").queue();
				return;
			}
			
			AuthProfile profile = Authentication.getAuthProfile(event.getAuthor().getId());
			
			//Authentication.unlink does stuff on the main Bukkit Thread, so we use a BukkitRunnable to run it on the main Thread
			new BukkitRunnable() {
				
				@Override
				public void run() {
					boolean unlinkedSuccessfully = Authentication.unlink(profile);
					
					if(unlinkedSuccessfully) {
						event.getChannel().sendMessage("Unlinking successful!").queue();
					} else {
						event.getChannel().sendMessage("There was an error unlinking. Please contact your server's administrator!").queue();
					}
				}
			}.runTask(plugin);

			return;
		}
		
		if(!message.chars().allMatch(Character::isDigit)) {
			event.getChannel().sendMessage("Invalid code! A code is a 6-digit number which does not contain letters!").queue();
			return;
		}
		
		if(!Authentication.pendingAuthenticationCodeExist(Integer.valueOf(message))) {
			event.getChannel().sendMessage("This code does not exist!").queue();
			return;
		}
					
		boolean authSuccessful = Authentication.authenticate(event.getAuthor().getId(), Authentication.getMinecraftUuid(Integer.valueOf(message)));
		
		Authentication.removePendingAuthentication(Integer.valueOf(message));
		
		if(authSuccessful) {
			event.getChannel().sendMessage("You have been authenticated with the Minecraft player: ``" + Authentication.getMinecraftUsername(Integer.valueOf(message)) + "``!").queue();
			
		} else {
			event.getChannel().sendMessage("The Authentication was not successful! Your Discord account, or Minecraft account is already authenticated!").queue();
		}
		
		Player verifiedPlayer = Bukkit.getPlayer(Authentication.getMinecraftUuid(Integer.valueOf(message)));
		if(verifiedPlayer != null && verifiedPlayer.isOnline()) {
			verifiedPlayer.sendMessage(ChatColor.GRAY + "[" + ChatColor.AQUA + "DiscordAuth" + ChatColor.GRAY + "]" + ChatColor.GOLD + " You have been authenticated with the Discord user " + ChatColor.RED + event.getAuthor().getAsTag());
		}
	}
}
