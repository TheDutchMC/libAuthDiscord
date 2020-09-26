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
	
	public static boolean isEntryPermitted(String discordId) {
		if(!isEnabled) return true;
		
		Guild g = jdaHandler.getJda().getGuildById(guildId);
		
		RestAction<Member> getMember = g.retrieveMemberById(discordId);
		Member m = getMember.complete();
	
		for(Role discordUserRole : m.getRoles()) {			
			if(permittedRoles.contains(discordUserRole.getId())) return true;
		}

		return false;
	}
}
