package nl.thedutchmc.LibAuthDiscord.authentication;

import java.io.Serializable;
import java.util.UUID;

public class AuthProfile implements Serializable {

	private static final long serialVersionUID = 1L;

	private String discordId;
	private UUID minecraftUuid;
	
	/**
	 * 
	 * @param discordId The Discord ID to be used for the AuthProfile. Cannot be modified afterwards!
	 * @param minecraftUuid The Minecraft UUID to be used for the AuthProfile. Cannot be modified afterwards!
	 */
	public AuthProfile(String discordId, UUID minecraftUuid) {
		this.discordId = discordId;
		this.minecraftUuid = minecraftUuid;
	}
	
	/**
	 * Get the Discord ID stored in this AuthProfile
	 * @return Returns the Discord ID stored in this AuthProfile as a String
	 */
	public String getDiscordId() {
		return this.discordId;
	}
	
	/**
	 * Get the Minecraft UUID stored in this AuthProfile
	 * @return Returns the Minecraft UUID stored in this AuthProfile
	 */
	public UUID getMinecraftUuid() {
		return this.minecraftUuid;
	}
	
	
	
}
