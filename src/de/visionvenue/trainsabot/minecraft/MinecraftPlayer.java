package de.visionvenue.trainsabot.minecraft;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.shanerx.mojang.Mojang;
import org.shanerx.mojang.PlayerProfile;

public class MinecraftPlayer {

	UUID uuid;
	String username;
	URL headSkin;
	
	
	public MinecraftPlayer(UUID uuid) {
		this.uuid = uuid;
		
		Mojang api = new Mojang().connect();
		PlayerProfile player = api.getPlayerProfile(this.uuid.toString());
		
		this.username = player.getUsername();
		
		try {
			this.headSkin = new URL("http://cravatar.eu/avatar/" + this.uuid + ".png");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public UUID getUUID() {
		return this.uuid;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public URL getHeadSkin() {
		return this.headSkin;
	}
	
}
