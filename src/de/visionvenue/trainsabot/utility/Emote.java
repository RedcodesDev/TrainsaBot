package de.visionvenue.trainsabot.utility;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import de.visionvenue.trainsabot.data.MongoDBHandler;
import de.visionvenue.trainsabot.main.Main;

public class Emote {

	String key;
	long id;
	String mention;
	net.dv8tion.jda.api.entities.Emote emote;

	public Emote(String key) {
		this.key = key;

		MongoCollection<Document> collection = MongoDBHandler.getDatabase().getCollection("emotes");
		Document doc = collection.find(Filters.eq("_id", this.key)).first();

		if (doc != null) {
			this.id = doc.getLong("emoteid");
			this.emote = Main.jda.getGuildById(780041125721407528l).getEmoteById(this.id);
			this.mention = this.emote.getAsMention();
		} else {
			throw new NullPointerException("Emote \"" + this.key + "\" was not found!");
		}

	}

	public String getKey() {
		return this.key;
	}
	
	public String getMention() {
		return this.mention;
	}
	
	public net.dv8tion.jda.api.entities.Emote getEmote() {
		return this.emote;
	}

}
