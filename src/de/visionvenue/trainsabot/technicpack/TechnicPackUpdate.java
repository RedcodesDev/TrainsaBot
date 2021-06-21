package de.visionvenue.trainsabot.technicpack;

import java.io.IOException;
import java.net.URL;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import de.visionvenue.trainsabot.data.MongoDBHandler;

public class TechnicPackUpdate {

	TechnicPack pack;
	String user;
	int id;
	String content;
	URL avaterUrl;
	URL url;
	boolean isPublished;

	public TechnicPackUpdate(TechnicPack pack, String user, int id, String content, String avatarUrl, String url) {
		this.pack = pack;
		this.user = user;
		this.id = id;
		this.content = content;
		try {
			this.avaterUrl = new URL(avatarUrl);
			this.url = new URL(url);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		MongoCollection<Document> collection = MongoDBHandler.getDatabase().getCollection("data");
		Document doc = collection.find(Filters.eq("_id", "technicpack")).first();
		if(this.id > doc.getInteger("lastupdate")) {
			this.isPublished = false;
		} else {
			this.isPublished = true;
		}
		
	}
	
	public boolean isPublished() {
		return this.isPublished;
	}
	
	public TechnicPack getPack() {
		return this.pack;
	}
	
	public String getUser() {
		return this.user;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getContent() {
		return this.content;
	}
	
	public URL getAvatarUrl() {
		return this.avaterUrl;
	}
	
	public URL getUrl() {
		return this.url;
	}
	
	public void publish() {
		MongoCollection<Document> collection = MongoDBHandler.getDatabase().getCollection("data");
		Document doc = collection.find(Filters.eq("_id", "technicpack")).first();
		collection.updateOne(doc, Updates.set("lastupdate", this.id));
	}

}
