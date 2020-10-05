package nl.thedutchmc.LibAuthDiscord.authentication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import nl.thedutchmc.LibAuthDiscord.LibAuthDiscord;

public class Authentication {
	
	private LibAuthDiscord plugin;
	private static ProfileStorage STORAGE;
	private static List<AuthProfile> authProfiles;
	private static HashMap<Integer, UUID> pendingAuthentications = new HashMap<>();
	private static HashMap<Integer, String> pendingAuthenticationsByUsername = new HashMap<>();

	
	public Authentication(LibAuthDiscord plugin, String authProfilePath) {
		this.plugin = plugin;
		
		STORAGE = new ProfileStorage(this.plugin, authProfilePath);
		authProfiles = STORAGE.read();
	}
	
	/**
	 * Check if a UUID is authenticated
	 * @param minecraftUuid The UUID of the user that wants to be checked
	 * @return Returns true if authenticated, false if not
	 */
	public static boolean isAuthenticated(UUID minecraftUuid) { 
		
		for(AuthProfile profile : authProfiles) {
			if(profile.getMinecraftUuid().equals(minecraftUuid)) return true;
		}
		
		return false;
	}
	
	/**
	 * Check if a Discord ID is authenticated
	 * @param discordId The Discord ID of the user that wants to be checked
	 * @return Returns true if authenticated, false if not
	 */
	public static boolean isAuthenticated(String discordId) {
		for(AuthProfile profile : authProfiles) {
			if(profile.getDiscordId().equals(discordId)) return true;
		}
		
		return false;
	}
	
	/**
	 * <strong>This method should not be used by other plugins. libDiscordAuth handles authentication.</strong><br>
	 * <br>
	 * Authenticate a user and create an AuthProfile for the user.
	 * 
	 * @param discordId The Discord ID of the user who is to be authenticated
	 * @param minecraftUuid The Minecraft UUID of the user who is to be authenticated
	 * @return Returns true if the user was successfully authenticated. Returns false if either of the provided arguments are already authenticated
	 */
	public static boolean authenticate(String discordId, UUID minecraftUuid) {
		if(isAuthenticated(minecraftUuid) || isAuthenticated(discordId)) return false;
		
		AuthProfile authProfile = new AuthProfile(discordId, minecraftUuid);
		authProfiles.add(authProfile);
		
		STORAGE.store(authProfile);
		
		return true;
	}
	
	/**
	 * Get the AuthProfile of the provided discord ID.
	 * @param discordId The Discord ID to use when looking for the AuthProfile
	 * @return the AuthProfile associated with the provided ID. Will return null if no AuthProfile was found!
	 */
	@Nullable
	public static AuthProfile getAuthProfile(String discordId) {
		for(AuthProfile authProfile : authProfiles) {
			if(authProfile.getDiscordId().equals(discordId)) return authProfile;
		}
		
		return null;
	}
	
	/** 
	 * Get the AuthProfile of the provided Minecraft UUID
	 * @param minecraftUuid The minecraft UUID to use when looking for the AuthProfile
	 * @return the AuthProfile associated with the provided UUID. Will return null if no AuthProfile was found!
	 */
	@Nullable
	public static AuthProfile getAuthProfile(UUID minecraftUuid) {
		for(AuthProfile authProfile : authProfiles) {
			if(authProfile.getMinecraftUuid().equals(minecraftUuid)) return authProfile;
		}
		
		return null;
	}
	
	/**
	 * <strong> This method should not be used by other plugins. libAuthDiscord handles authentication</strong><br>
	 * <br>
	 * Create a pending authentication for the user
	 * 
	 * @param minecraftUuid The Minecraft UUID of the user to create a pending authentication for
	 * @param username The Minecraft in-game-name of the user to create a pending authentication for
	 * @return Returns the code to be send to the authentication bot
	 */
	public static int createPendingAuthentication(UUID minecraftUuid, String username) {		
		
		//Check if the user already has a pending authentication
		if(pendingAuthentications.containsValue(minecraftUuid)) {
			
			//Iterate over the pending authentications
			for(Map.Entry<Integer, UUID> entry : pendingAuthentications.entrySet()) {	
				
				//Check if the current value is equal to the provided UUID. If it is return the key associated with this value
				if(entry.getValue().equals(minecraftUuid)) return entry.getKey();
			}
		}
		
		//The user does not have a pending authentication yet. Create one
		int n = generateCode();
		
		pendingAuthentications.put(n, minecraftUuid);
		pendingAuthenticationsByUsername.put(n, username);
		
		return n;
	}
	
	/**
	 * <strong>This method should not be used by other plugins. libAuthDiscord handles authentication.</strong><br>
	 * <br>
	 * Remove a pending authentication<br>
	 * You should check first if the pending authentication you want to remove exists!
	 * 
	 * @param code The code of the pending verification to be removed
	 */
	public static void removePendingAuthentication(int code) {
		pendingAuthentications.remove(code);
	}
	
	/**
	 * <strong>This method should not be used by other plugins. libAuthDiscord handles authentication.</strong><br>
	 * <br>
	 * Check if a pending authentication exists
	 * 
	 * @param code The code of the pending authentication to be checked.
	 * @return
	 */
	public static boolean pendingAuthenticationCodeExist(int code) {
		return pendingAuthentications.containsKey(code);
	}
	
	/**
	 * Get the Minecraft UUID associated with the provided pending authentication code.
	 * You should check if the code exists before calling this method!
	 * 
	 * @param code The code to look up.
	 * @return Returns the UUID of the associated pending authentication code.
	 */
	public static UUID getMinecraftUuid(int code) {
		return pendingAuthentications.get(code);
	}
	
	/**
	 * Get the Minecraft in-game-name associated with the provided pending authentication code.
	 * You should check if the code exists before calling this method!
	 * 
	 * @param code The code to look up.
	 * @return Returns the Minecraft in-game-name of the associated pending authentication code.
	 */
	public static String getMinecraftUsername(int code) {
		return pendingAuthenticationsByUsername.get(code);
	}
	
	/**
	 * Unlink an account
	 * @param authProfile The AuthProfile to unlink
	 * @return true if the unlinking was successful, false if not.
	 */
	public static boolean unlink(AuthProfile authProfile) {
			
		//Remove the AuthProfile from the list
		authProfiles.remove(authProfile);
		
		//Check if the user is online, if they are kick them
		final OfflinePlayer op = Bukkit.getOfflinePlayer(authProfile.getMinecraftUuid());
		if(op.isOnline()) {
			final Player p = Bukkit.getPlayer(authProfile.getMinecraftUuid());
			p.kickPlayer(ChatColor.GOLD + "You have unlinked your Discord account. Please relink to regain access to this server!");
		}
		
		boolean deleteSuccessful = STORAGE.delete(authProfile);
		
		return deleteSuccessful;
	}
	
	private static int generateCode() {
		final Random rnd = new Random();
		final int n = 100000 + rnd.nextInt(900000);
		
		return n;
	}
}
