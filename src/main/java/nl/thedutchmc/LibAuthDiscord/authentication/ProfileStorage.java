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
	
	/**
	 * Write an AuthProfile to disk
	 * @param authProfile The AuthProfile to write to disk
	 */
	public void store(AuthProfile authProfile) {
		
		//The file which will store the AuthProfile
		File outFile = new File(this.basePath + File.separator + authProfile.getMinecraftUuid().toString() + ".auth");
		
		//Check if the AuthProfile file exists. If it does not create it.
		if(!outFile.exists()) {
			try {
				
				//Create the file
				outFile.createNewFile();
				
			} catch (IOException e) {
				plugin.logWarn("There was an error creating the AuthProfile (" + authProfile.getMinecraftUuid().toString() + ".auth" + "). A IOException was thrown!");
			}
		}
		
		try {
			
			//Created the required Streams so we can write the file to disk
			FileOutputStream fos = new FileOutputStream(outFile.getAbsolutePath());
			ObjectOutputStream out = new ObjectOutputStream(fos);
			
			//Write the object into the file
			out.writeObject(authProfile);
			
			//Lastly, close the Streams
			out.close();
			fos.close();
			
		} catch (FileNotFoundException e) {
			plugin.logWarn("There was an error saving the AuthProfile (" + authProfile.getMinecraftUuid().toString() + ".auth" + "). A FileNotFoundException was thrown!");
		} catch (IOException e) {
			plugin.logWarn("There was an error saving the AuthProfile (" + authProfile.getMinecraftUuid().toString() + ".auth" + "). A IOException was thrown!");
		}
	}
	
	/**
	 * Read <strong>all</strong> AuthProfiles from disk<br>
	 * Reads from the path set in the configuration
	 * @return Returns a List of AuthProfiles which were read from the disk. Returns null if an IOException or ClassNotFoundException occured!
	 */
	@Nullable
	public List<AuthProfile> read() {
		
		//Create an empty List to which we add the AuthProfiles
		List<AuthProfile> result = new ArrayList<>();
		
		//Iterate over the discovered AuthProfiles
		for(String path : discoverAuthProfiles()) {
				
			try {
				
				AuthProfile authProfile = null;

				//Open the Streams we need to read the file
				FileInputStream fis = new FileInputStream(path);
				ObjectInputStream in = new ObjectInputStream(fis);

				//Read the file, and set the AuthProfile equal to what we read
				authProfile = (AuthProfile) in.readObject();
				
				//Close the Streams we opened earlier
				in.close();
				fis.close();
				
				//Lastly, add the AuthProfile to the results List
				result.add(authProfile);
				
			} catch (IOException | ClassNotFoundException e) {
				plugin.logWarn("An Exception was thrown whilst trying to read an AuthProfile!");
				return null;
			}
		}
		
		//We're done reading everything, so return the result List
		return result;
	}
	
	/**
	 * Delete an AuthProfile from disk
	 * @param authProfile The AuthProfile to delete
	 * @return true if the delete was successful. False if the file did not exist, or an IOException occured
	 */
	public boolean delete(AuthProfile authProfile) {
		File outFile = new File(this.basePath + File.separator + authProfile.getMinecraftUuid().toString() + ".auth");

		if(!outFile.exists()) return false;
		
		try {
			Files.delete(outFile.toPath());
		} catch (IOException e) {			
			plugin.logWarn("An issue occured whilst trying to delete the AuthProfile " + outFile.getAbsolutePath());
			return false;
		}
		
		return true;
	}
	
	//Returns null if an IOException or SecurityException was thrown
	@Nullable
	private List<String> discoverAuthProfiles() {
		
		//The Folder in which all our AuthProfiles are stored. This path is set in the configuration.
		File storageFolder = new File(this.basePath);
		
		//Check if the storage folder exists, if not, create it.
		if(!storageFolder.exists()) {
			try {
				
				//Create the directory, and parent directories if those do not exist.
				Files.createDirectories(Paths.get(storageFolder.getAbsolutePath()));
			} catch (IOException | SecurityException e) {
				plugin.logWarn("Failed to create AuthProfiles directory! Please check your file permissions!");
				
				return null;
			}
		}
		
		try {
			//Walk the provided Path
			Stream<Path> walk = Files.walk(Paths.get(storageFolder.getAbsolutePath()));
			
			//Filter on the file extension, must be .auth, and put the found paths into the result list.
			List<String> result = walk.map(x -> x.toString()).filter(f -> f.endsWith(".auth")).collect(Collectors.toList());
			
			//Close the walk
			walk.close();
			
			//Return the result since we are done
			return result;
		} catch(IOException e) {
			plugin.logWarn("A IOException was thrown whilst discovering AuthProfiles!");
		
			e.printStackTrace();
			
			return null;
		}
	}
}
