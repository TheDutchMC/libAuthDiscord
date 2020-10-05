package nl.thedutchmc.LibAuthDiscord.commands.authSubCommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import nl.thedutchmc.LibAuthDiscord.LibAuthDiscord;
import nl.thedutchmc.LibAuthDiscord.authentication.AuthProfile;
import nl.thedutchmc.LibAuthDiscord.authentication.Authentication;

public class UnlinkExecutor {
	
	public static boolean unlink(CommandSender sender, String[] args, boolean other, LibAuthDiscord plugin) {
		
		Player p = null;
		
		//We want to unlink another player 
		if(other) {
			//Validate if the provided playername belongs to a real Player
			p = Bukkit.getPlayer(args[1]);
			
			//If it's null, the target player is not online
			if(p == null) {
				sender.sendMessage(plugin.getPrefix() + ChatColor.GOLD + "This player is not online!");
				return true;
			}
			
		//We want to unlink the sender
		} else {
			p = (Player) sender;
		}
		
		//Check if the player is authenticated
		if(!Authentication.isAuthenticated(p.getUniqueId())) {
			sender.sendMessage(plugin.getPrefix() + ChatColor.GOLD + "This player is not authenticated, so we cannot unlink them!");
			return true;
		}
		
		//Grab the AuthProfile and unlink
		AuthProfile authProfile = Authentication.getAuthProfile(p.getUniqueId());
		boolean unlinkedSuccessfully = Authentication.unlink(authProfile);
		
		//Inform the player if the unlinking was successful or not
		if(unlinkedSuccessfully) {
			
			if(other) {
				sender.sendMessage(plugin.getPrefix() + ChatColor.GOLD + "The player has been unlinked from their Discord account!");
			} else {
				sender.sendMessage(plugin.getPrefix() + ChatColor.GOLD + "You have successfully unlinked your Discord account!"); 
			}
			
			return true;
			
		} else {
			sender.sendMessage(plugin.getPrefix() + ChatColor.GOLD + "An issue occured. Please contact the server administrator!");
			return true;
		}
	}
}
