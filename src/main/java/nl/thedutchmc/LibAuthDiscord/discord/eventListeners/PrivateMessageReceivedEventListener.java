package nl.thedutchmc.LibAuthDiscord.discord.eventListeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.ChatColor;
import nl.thedutchmc.LibAuthDiscord.authentication.Authentication;

public class PrivateMessageReceivedEventListener extends ListenerAdapter {

	@Override
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
		
		if(event.getAuthor().isBot()) {
			return;
		}
		
		String message = event.getMessage().getContentDisplay();
		
		if(!message.chars().allMatch(Character::isDigit)) {
			event.getChannel().sendMessage("Invalid code! A code is a 6-digit number which does not contain letters!").queue();
			return;
		}
		
		if(!Authentication.pendingVerificationCodeExist(Integer.valueOf(message))) {
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
