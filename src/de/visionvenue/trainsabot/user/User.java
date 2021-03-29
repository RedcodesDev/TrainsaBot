package de.visionvenue.trainsabot.user;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import de.visionvenue.trainsabot.data.MongoDBHandler;

public class User {

	UUID uuid;
	long userid;
	HashMap<Job, Long> exp;
	Long money;
	Job job;
	boolean verified;
	float playTime;
	Instant firstJoined;
	
	DecimalFormat format = new DecimalFormat("0.00");

	public User(long userid) {
		this.userid = userid;

		MongoCollection<Document> collection = MongoDBHandler.getDatabase().getCollection("users");
		Document doc = collection.find(Filters.eq("discord_id", this.userid)).first();

		if (doc != null) {
			this.verified = true;
			this.uuid = UUID.fromString(doc.getString("_id"));
			this.money = doc.getLong("money");
			this.job = Job.valueOf(doc.getString("currentJob").toUpperCase());
			this.exp = new HashMap<Job, Long>();
			ArrayList<Long> list = (ArrayList<Long>) doc.getList("job_experience", Long.class);
			for (int i = 0; i < list.size(); i++) {
				if (i == 0) {
					this.exp.put(Job.FARMER, list.get(i));
				} else if (i == 1) {
					this.exp.put(Job.BAKER, list.get(i));
				} else if (i == 2) {
					this.exp.put(Job.LUMBERJACK, list.get(i));
				} else if (i == 3) {
					this.exp.put(Job.MINER, list.get(i));
				} else if (i == 4) {
					this.exp.put(Job.FISHER, list.get(i));
				}
			}
			if(doc.containsKey("playTime")) {
				this.playTime = Float.valueOf(format.format(doc.getLong("playTime").floatValue() / 60).replace(",", "."));
			} else {
				this.playTime = 0f;
			}
			
			if(doc.containsKey("firstJoinDate")) {
				this.firstJoined = doc.getDate("firstJoinDate").toInstant();
			} else {
				this.firstJoined = null;
			}

		} else {
			this.verified = false;
		}
	}
	
	public User(UUID uuid) {
		this.uuid = uuid;

		MongoCollection<Document> collection = MongoDBHandler.getDatabase().getCollection("users");
		Document doc = collection.find(Filters.eq("_id", this.uuid.toString())).first();

		if (doc != null) {
			this.verified = true;
			this.userid = doc.getLong("discord_id");
			this.money = doc.getLong("money");
			this.job = Job.valueOf(doc.getString("currentJob").toUpperCase());
			this.exp = new HashMap<Job, Long>();
			ArrayList<Long> list = (ArrayList<Long>) doc.getList("job_experience", Long.class);
			for (int i = 0; i < list.size(); i++) {
				if (i == 0) {
					this.exp.put(Job.FARMER, list.get(i));
				} else if (i == 1) {
					this.exp.put(Job.BAKER, list.get(i));
				} else if (i == 2) {
					this.exp.put(Job.LUMBERJACK, list.get(i));
				} else if (i == 3) {
					this.exp.put(Job.MINER, list.get(i));
				} else if (i == 4) {
					this.exp.put(Job.FISHER, list.get(i));
				}
			}
			if(doc.containsKey("playTime")) {
				this.playTime = doc.getLong("playTime").floatValue() / 60f;
			} else {
				this.playTime = 0f;
			}
			
			if(doc.containsKey("firstJoinDate")) {
				this.firstJoined = doc.getDate("firstJoinDate").toInstant();
			} else {
				this.firstJoined = null;
			}

		} else {
			this.verified = false;
		}
	}


	public boolean isVerified() {
		return this.verified;
	}

	public UUID getUUID() {
		return this.uuid;
	}

	public Long getExp(Job job) {
		return this.exp.get(job);
	}

	public Job getJob() {
		return this.job;
	}

	public Long getMoney() {
		return this.money;
	}
	
	public Long getId() {
		return this.userid;
	}
	 
	public float getPlayTime() {
		return this.playTime;
	}
	
	public Instant getFirstTimeJoined() {
		return this.firstJoined;
	}
	
	public String getFirstTimeJoinedAsString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.GERMANY)
				.withZone(ZoneId.of("Europe/Berlin"));
		String instant = formatter.format(this.firstJoined);
		return instant;
	}

}
