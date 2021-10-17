package de.visionvenue.trainsabot.minecraft;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MinecraftPlayer {

	UUID uuid;
	String username;
	URL headSkin;

	public MinecraftPlayer(UUID uuid) {
		this.uuid = uuid;

		try {

			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(
					"https://sessionserver.mojang.com/session/minecraft/profile/" + this.uuid.toString());
			CloseableHttpResponse response = httpclient.execute(httpGet);
			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode == 200) {
				HttpEntity entity1 = response.getEntity();

				JsonElement element = JsonParser.parseReader(new InputStreamReader((InputStream) entity1.getContent()));

				JsonObject e = element.getAsJsonObject();

				this.username = e.get("name").getAsString();
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}

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
