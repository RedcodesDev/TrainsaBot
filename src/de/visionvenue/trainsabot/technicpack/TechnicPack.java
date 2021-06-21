package de.visionvenue.trainsabot.technicpack;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TechnicPack {

	int id;
	String name;
	String displayName;
	URL url;
	String minecraftVersion;
	int rating;
	int downloads;
	int runs;
	String description;
	String version;
	URL downloadUrl;
	int responseCode;
	List<TechnicPackUpdate> updates;

	public TechnicPack(String name) {
		this.name = name;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet("https://api.technicpack.net/modpack/" + this.name + "?build=%3Cbuild");
			CloseableHttpResponse response1 = httpclient.execute(httpGet);
			this.responseCode = response1.getStatusLine().getStatusCode();
			if (this.responseCode == 200) {
				HttpEntity entity1 = response1.getEntity();

				JsonElement element = JsonParser.parseReader(new InputStreamReader((InputStream) entity1.getContent()));

				JsonObject e = element.getAsJsonObject();
				this.id = e.get("id").getAsInt();
				this.displayName = e.get("displayName").getAsString();
				this.url = new URL(e.get("platformUrl").getAsString());
				this.minecraftVersion = e.get("minecraft").getAsString();
				this.rating = e.get("ratings").getAsInt();
				this.downloads = e.get("downloads").getAsInt();
				this.runs = e.get("runs").getAsInt();
				this.description = e.get("description").getAsString();
				this.version = e.get("version").getAsString();
				this.downloadUrl = new URL(e.get("url").getAsString());

				this.updates = new ArrayList<TechnicPackUpdate>();

				for (JsonElement elemt : e.get("feed").getAsJsonArray()) {
					JsonObject obj = elemt.getAsJsonObject();

					this.updates.add(new TechnicPackUpdate(this, obj.get("user").getAsString(),
							Integer.valueOf(
									obj.get("url").getAsString().replace("https://www.technicpack.net/status/", "")),
							obj.get("content").getAsString(), obj.get("avatar").getAsString(),
							obj.get("url").getAsString()));

				}

				EntityUtils.consume(entity1);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public URL getDownloadUrl() {
		return this.downloadUrl;
	}

	public String getMinecraftVersion() {
		return this.minecraftVersion;
	}

	public int getRating() {
		return this.rating;
	}

	public int getDownloads() {
		return this.downloads;
	}

	public int getRuns() {
		return this.runs;
	}

	public String getDescription() {
		return this.description;
	}

	public String getVersion() {
		return this.version;
	}

	public URL getUrl() {
		return this.url;
	}

	public List<TechnicPackUpdate> getUpdates() {
		return this.updates;
	}

}
