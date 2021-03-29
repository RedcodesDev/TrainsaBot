package de.visionvenue.trainsabot.verify;

import java.time.LocalDateTime;
import java.util.UUID;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import de.visionvenue.trainsabot.data.MongoDBHandler;

public class VerifyCode {

	String code;
	UUID uuid;
	long userid;
	boolean valid;

	public VerifyCode(String code) {
		this.code = code;

		MongoCollection<Document> collection = MongoDBHandler.getDatabase().getCollection("verify");
		Document doc = collection.find(Filters.eq("_id", this.code)).first();

		if (doc != null) {
			this.valid = true;

			this.uuid = UUID.fromString(doc.getString("mc_uuid"));
			if(doc.getLong("dc_id") != null) {
			this.userid = doc.getLong("dc_id");
			} else {
				this.userid = 0l;
			}

		} else {
			this.valid = false;
		}
	}

	public VerifyCode(long userid) {

		this.userid = userid;

		if(isVerified()) {
			return;
		}
		
		MongoCollection<Document> collection = MongoDBHandler.getDatabase().getCollection("verify");
		Document doc = collection.find(Filters.eq("dc_id", this.userid)).first();

		if (doc != null) {
			this.code = doc.getString("_id");
		} else {
			this.uuid = null;
			this.code = Integer.toHexString(LocalDateTime.now().getHour() + LocalDateTime.now().getMinute()
					+ LocalDateTime.now().getDayOfYear() + LocalDateTime.now().getNano());
			doc = new Document("_id", this.code).append("mc_uuid", this.uuid).append("dc_id", this.userid);
			collection.insertOne(doc);
		}

	}

	public String getCode() {
		return this.code;
	}

	public boolean isValid() {
		return this.valid;
	}

	public long getDiscordUserId() {
		return this.userid;
	}
	
	public void setMinecraftUUID(UUID uuid) {
		this.uuid = uuid;
		
		MongoCollection<Document> collection = MongoDBHandler.getDatabase().getCollection("verify");
		collection.updateOne(Filters.eq("_id", this.code), Updates.set("mc_uuid", this.uuid.toString()));
	}

	public UUID getMinecraftUUID() {
		return this.uuid;
	}
	
	public boolean isVerified() {
		MongoCollection<Document> collection = MongoDBHandler.getDatabase().getCollection("users");
		Document doc = collection.find(Filters.eq("discord_id", this.userid)).first();
		
		if(doc != null) {
			return true;
		}
		return false;
	}

	private void verify() {
		MongoCollection<Document> collection = MongoDBHandler.getDatabase().getCollection("users");
		collection.updateOne(Filters.eq("_id", this.uuid.toString()), Updates.set("discord_id", this.userid));
	}

	public void delete(long userid) {
		this.userid = userid;
		verify();
		MongoCollection<Document> collection = MongoDBHandler.getDatabase().getCollection("verify");
		collection.deleteMany(Filters.eq("mc_uuid", this.uuid.toString()));
		collection.deleteMany(Filters.eq("dc_id", this.userid));
	}

}
