package de.visionvenue.trainsabot.minecraft;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MinecraftServer {

	String ip;
	String ipRaw;
	int port;
	String motd;
	List<MinecraftPlayer> players;
	int playerCount;
	int maxPlayerCount;
	String minecraftVersion;
	boolean online;
	List<String> mods;
	int responseCode;
	String icon64;

	public MinecraftServer(String ip) {
		this.ipRaw = ip;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet("https://api.mcsrvstat.us/2/" + this.ipRaw);
			CloseableHttpResponse response1 = httpclient.execute(httpGet);
			this.responseCode = response1.getStatusLine().getStatusCode();
			if (this.responseCode == 200) {
				HttpEntity entity1 = response1.getEntity();

				JsonElement element = JsonParser.parseReader(new InputStreamReader((InputStream) entity1.getContent()));

				JsonObject e = element.getAsJsonObject();

				this.ip = e.get("ip").getAsString();
				this.port = e.get("port").getAsInt();
				this.online = e.get("online").getAsBoolean();
				if (this.online) {
					this.motd = e.get("motd").getAsJsonObject().get("clean").getAsJsonArray().get(0).getAsString();
					this.playerCount = e.get("players").getAsJsonObject().get("online").getAsInt();
					this.maxPlayerCount = e.get("players").getAsJsonObject().get("max").getAsInt();

					if (e.get("icon") != null) {
						this.icon64 = e.get("icon").getAsString();
					}

					this.minecraftVersion = e.get("version").getAsString();

					this.mods = new ArrayList<String>();
					for (String str : e.get("mods").getAsJsonObject().get("raw").getAsJsonObject().keySet()) {
						this.mods.add(
								e.get("mods").getAsJsonObject().get("raw").getAsJsonObject().get(str).getAsString());
					}

					this.players = new ArrayList<MinecraftPlayer>();

					for (String str : e.get("players").getAsJsonObject().get("uuid").getAsJsonObject().keySet()) {
						this.players.add(new MinecraftPlayer(UUID.fromString(e.get("players").getAsJsonObject()
								.get("uuid").getAsJsonObject().get(str).getAsString())));
					}

				}
				EntityUtils.consume(entity1);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public String getIp() {
		return this.ip;
	}

	public String getIpRaw() {
		return this.ipRaw;
	}

	public int getPort() {
		return this.port;
	}

	public String getMotd() {
		return this.motd;
	}

	public List<MinecraftPlayer> getPlayers() {
		return this.players;
	}

	public int getPlayerCount() {
		return this.playerCount;
	}

	public int getMaxPlayerCount() {
		return this.maxPlayerCount;
	}

	public String getMinecraftVersion() {
		return this.minecraftVersion;
	}

	public boolean isOnline() {
		return this.online;
	}

	public List<String> getMods() {
		return this.mods;
	}

	public int getResponseCode() {
		return this.responseCode;
	}

	public String getIcon64() {
		return this.icon64;
	}

}
