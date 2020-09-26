package nl.thedutchmc.LibAuthDiscord.authentication;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import nl.thedutchmc.LibAuthDiscord.LibAuthDiscord;

public class Authentication {
	
	private LibAuthDiscord plugin;
	private static ProfileStorage STORAGE;
	
	private static List<AuthProfile> authProfiles;
	
	private static HashMap<Integer, UUID> pendingVerifications = new HashMap<>();
	private static HashMap<Integer, String> pendingVerificationsByUsername = new HashMap<>();

	
	public Authentication(LibAuthDiscord plugin, String authProfilePath) {
		this.plugin = plugin;
		
		STORAGE = new ProfileStorage(this.plugin, authProfilePath);
		authProfiles = STORAGE.read();
	}
	
	public static boolean isAuthenticated(UUID minecraftUuid) { 
		
		for(AuthProfile profile : authProfiles) {
			if(profile.getMinecraftUuid().equals(minecraftUuid)) return true;
		}
		
		return false;
	}
	
	public static boolean isAuthenticated(String discordId) {
		for(AuthProfile profile : authProfiles) {
			if(profile.getDiscordId().equals(discordId)) return true;
		}
		
		return false;
	}
	
	public static boolean authenticate(String discordId, UUID minecraftUuid) {
		if(isAuthenticated(minecraftUuid) || isAuthenticated(discordId)) return false;
		
		AuthProfile authProfile = new AuthProfile(discordId, minecraftUuid);
		authProfiles.add(authProfile);
		
		STORAGE.store(authProfile);
		
		return true;
	}
	
	/**
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
	
	public static int createPendingAuthentication(UUID minecraftUuid, String username) {		
		
		if(pendingVerifications.containsValue(minecraftUuid)) return -1;
		
		int n = generateCode();
		
		pendingVerifications.put(n, minecraftUuid);
		pendingVerificationsByUsername.put(n, username);
		
		return n;
	}
	
	public static void removePendingAuthentication(int code) {
		pendingVerifications.remove(code);
	}
	
	public static boolean pendingVerificationCodeExist(int code) {
		return pendingVerifications.containsKey(code);
	}
	
	public static UUID getMinecraftUuid(int code) {
		return pendingVerifications.get(code);
	}
	
	public static String getMinecraftUsername(int code) {
		return pendingVerificationsByUsername.get(code);
	}
	
	private static int generateCode() {
		final Random rnd = new Random();
		final int n = 100000 + rnd.nextInt(900000);
		
		return n;
	}
}
