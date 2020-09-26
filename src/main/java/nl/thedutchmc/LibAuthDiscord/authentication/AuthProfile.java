package nl.thedutchmc.LibAuthDiscord.authentication;

import java.io.Serializable;
import java.util.UUID;

public class AuthProfile implements Serializable {

	private static final long serialVersionUID = 1L;

	private String discordId;
	private UUID minecraftUuid;
	
	public AuthProfile(String discordId, UUID minecraftUuid) {
		this.discordId = discordId;
		this.minecraftUuid = minecraftUuid;
	}
	
	public String getDiscordId() {
		return this.discordId;
	}
	
	public UUID getMinecraftUuid() {
		return this.minecraftUuid;
	}
	
	
	
}
