package nl.thedutchmc.LibAuthDiscord.whitelist;

import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.RestAction;
import nl.thedutchmc.LibAuthDiscord.discord.JdaHandler;

public class Whitelist {

	private static boolean isEnabled;
	private static List<String> permittedRoles;
	
	private static String guildId;
	private static JdaHandler jdaHandler;
	
	public Whitelist(boolean isEnabled, List<String> permittedRoles, String guildId, JdaHandler jdaHandler) {
		Whitelist.isEnabled = isEnabled;
		Whitelist.permittedRoles = permittedRoles;
		Whitelist.guildId = guildId;
		Whitelist.jdaHandler = jdaHandler;
	}
	
	/**
	 * Check if a user is permitted to join the server
	 * @param discordId The Discord ID of the user to check
	 * @return Returns true if the provided discord ID may join the server. Returns false if they may not. Also returns true if the whitelist feature is disabled
	 */
	public static boolean isEntryPermitted(String discordId) {
		if(!isEnabled) return true;
		
		//Get the guild by the ID provided in the configuration
		Guild g = jdaHandler.getJda().getGuildById(guildId);
		
		//Create and execute a RestAction to get the Member from the Discord API
		RestAction<Member> getMember = g.retrieveMemberById(discordId);
		Member m = getMember.complete();
	
		//Iterate over the Member's role, and check if any of the roles they have matches any of the permitted roles, return true if this is the case
		for(Role discordUserRole : m.getRoles()) {			
			if(permittedRoles.contains(discordUserRole.getId())) return true;
		}

		//No role matched, so return false.
		return false;
	}
}
