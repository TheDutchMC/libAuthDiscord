package nl.thedutchmc.LibAuthDiscord.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import nl.thedutchmc.LibAuthDiscord.LibAuthDiscord;
import nl.thedutchmc.LibAuthDiscord.commands.authSubCommands.UnlinkExecutor; 

public class AuthCommandExecutor implements CommandExecutor {

	private LibAuthDiscord plugin;
	
	public AuthCommandExecutor(LibAuthDiscord plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(args.length < 1) {
			sender.sendMessage(plugin.getPrefix() + ChatColor.GOLD + "Not enough arguments. Use " + ChatColor.RED + "/auth help" + ChatColor.GOLD + " for help.");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("help")) {
			sender.sendMessage(ChatColor.AQUA + "LibAuthDiscord Help Page");
			sender.sendMessage("- " + ChatColor.GOLD + "/auth help " + ChatColor.RESET + "Shows the help menu");
			sender.sendMessage("- " + ChatColor.GOLD + "/auth unlink [Playername] " + ChatColor.RESET + "Unlink your, or someone else's Discord account.");
		}
		
		if(args[0].equalsIgnoreCase("unlink")) {
			
			//Check if the sender has the permission to unlink
			if(!sender.hasPermission("auth.unlink.self")) {
				sender.sendMessage(plugin.getPrefix() + ChatColor.GOLD + "You do not have permission to use this command!");
				return true;
			}
			
			//Argument length is less than 2, so they want to unlink themselves
			if(args.length < 2) {
				return UnlinkExecutor.unlink(sender, args, false, plugin);
			}
			
			//Check if the sender has the permission to unlink others
			if(!sender.hasPermission("auth.unlink.others")) {
				sender.sendMessage(plugin.getPrefix() + ChatColor.GOLD + "You do not have permission to use this command!");
				return true;
			}
			
			return UnlinkExecutor.unlink(sender, args, true, plugin);
		}
		
		
		return false;
	}
	
}
