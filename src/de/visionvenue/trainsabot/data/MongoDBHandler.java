package de.visionvenue.trainsabot.data;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import de.visionvenue.trainsabot.main.Main;
import de.visionvenue.trainsabot.token.DONOTOPEN;

public class MongoDBHandler {

	static MongoDatabase db;
	static MongoClient client;

	public static void connect() {

			ConnectionString connString;
			if(Main.Dev) {
				connString = new ConnectionString(
						DONOTOPEN.getDevMongoConnectionURL());
				MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(connString)
						.retryWrites(true).build();
				client = MongoClients.create(settings);
				MongoDatabase database = client.getDatabase("Development");
				System.out.println("Connected to database");
				db = database;
			} else {
				connString = new ConnectionString(
						DONOTOPEN.getMongoConnectionURL());
				MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(connString)
						.retryWrites(true).build();
				client = MongoClients.create(settings);
				MongoDatabase database = client.getDatabase("Database");
				System.out.println("Connected to database");
				db = database;
			}
	}
	
	public static void disconnect() {
		client.close();
	}

	public static MongoDatabase getDatabase() {
		return db;
	}
}

