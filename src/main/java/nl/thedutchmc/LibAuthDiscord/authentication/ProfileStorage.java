package nl.thedutchmc.LibAuthDiscord.authentication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import nl.thedutchmc.LibAuthDiscord.LibAuthDiscord;

public class ProfileStorage {

	private LibAuthDiscord plugin;
	private String basePath;
	
	public ProfileStorage(LibAuthDiscord plugin, String basePath) {
		this.plugin = plugin;
		this.basePath = basePath;
	}
	
	public void store(AuthProfile authProfile) {
		
		System.out.println(plugin.getDataFolder());
		System.out.println(authProfile);
		System.out.println(authProfile.getMinecraftUuid());
		
		File outFile = new File(this.basePath + File.separator + authProfile.getMinecraftUuid().toString() + ".auth");
		
		if(!outFile.exists()) {
			try {
				outFile.createNewFile();
			} catch (IOException e) {
				plugin.logWarn("There was an error creating the AuthProfile (" + authProfile.getMinecraftUuid().toString() + ".auth" + "). A IOException was thrown!");
			}
		}
		
		try {
			FileOutputStream fos = new FileOutputStream(outFile.getAbsolutePath());
			ObjectOutputStream out = new ObjectOutputStream(fos);
			
			out.writeObject(authProfile);
			
			out.close();
			fos.close();
			
		} catch (FileNotFoundException e) {
			plugin.logWarn("There was an error saving the AuthProfile (" + authProfile.getMinecraftUuid().toString() + ".auth" + "). A FileNotFoundException was thrown!");
		} catch (IOException e) {
			plugin.logWarn("There was an error saving the AuthProfile (" + authProfile.getMinecraftUuid().toString() + ".auth" + "). A IOException was thrown!");
		}
	}
	
	@Nullable
	public List<AuthProfile> read() {
		
		List<AuthProfile> result = new ArrayList<>();
		
		for(String path : discoverAuthProfiles()) {
				
			try {
				
				AuthProfile authProfile = null;

				FileInputStream fis = new FileInputStream(path);
				ObjectInputStream in = new ObjectInputStream(fis);
				
				authProfile = (AuthProfile) in.readObject();
				
				in.close();
				fis.close();
				
				result.add(authProfile);
			} catch (IOException | ClassNotFoundException e) {
				plugin.logWarn("An Exception was thrown whilst trying to read an AuthProfile!");
				return null;
			}
		}
		
		return result;
	}
	
	@Nullable
	private List<String> discoverAuthProfiles() {
		File storageFolder = new File(this.basePath);
		
		if(!storageFolder.exists()) {
			try {
				Files.createDirectories(Paths.get(storageFolder.getAbsolutePath()));
			} catch (IOException | SecurityException e) {
				plugin.logWarn("Failed to create AuthProfiles directory! Please check your file permissions!");
				
				return null;
			}
		}
		
		try {
			Stream<Path> walk = Files.walk(Paths.get(storageFolder.getAbsolutePath()));
			
			List<String> result = walk.map(x -> x.toString()).filter(f -> f.endsWith(".auth")).collect(Collectors.toList());
			
			walk.close();
			
			return result;
		} catch(IOException e) {
			plugin.logWarn("A IOException was thrown whilst discovering AuthProfiles!");
		
			e.printStackTrace();
			
			return null;
		}
	}
}
