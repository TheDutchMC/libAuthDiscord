package nl.thedutchmc.LibAuthDiscord.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class AuthCommandTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		if(args.length == 1) {
			List<String> result = new ArrayList<>();
			result.add("help");
			result.add("unlink");
			
			return result;
		}
		
		if(args.length == 2) {
						
			if(args[0].equalsIgnoreCase("unlink")) {
				
				if(!sender.hasPermission("auth.unlink.others")) {
					
					List<String> result = new ArrayList<>();
					return result;
				} else {
					return null;
				}
			}			
		}

		List<String> result = new ArrayList<>();
		return result;
	}	
}
