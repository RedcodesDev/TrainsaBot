package de.visionvenue.trainsabot.yt;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import de.visionvenue.trainsabot.data.MongoDBHandler;
import de.visionvenue.trainsabot.main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

public class Youtube {

	String channelid;
	String title;
	String description;
	String shortDescription;
	String videoId;
	URL url;
	URL thumbnail;
	boolean newVideo;

	public Youtube(String channelid) {
		this.channelid = channelid;

		try {

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(
					new URL("https://www.youtube.com/feeds/videos.xml?channel_id=" + this.channelid).openStream());

			NodeList list = doc.getElementsByTagName("entry");

			Node node = list.item(0);

			if (node.getNodeType() == Node.ELEMENT_NODE) {

				Element element = (Element) node;

				this.videoId = element.getElementsByTagName("yt:videoId").item(0).getTextContent();
				this.title = element.getElementsByTagName("title").item(0).getTextContent();
				this.url = new URL("https://www.youtube.com/watch?v=" + this.videoId);
				this.thumbnail = new URL("https://i3.ytimg.com/vi/" + this.videoId + "/hqdefault.jpg");

				NodeList children = element.getChildNodes();

				for (int i = 0; i < children.getLength(); i++) {

					Node child = children.item(i);

					if (child.getNodeName().equalsIgnoreCase("media:group")) {

						if (child.getNodeType() == Node.ELEMENT_NODE) {

							Element childElement = (Element) child;

							this.description = childElement.getElementsByTagName("media:description").item(0)
									.getTextContent();

						}
					}
				}

			}

		} catch (IOException | SAXException | ParserConfigurationException ex) {
			ex.printStackTrace();
		}
		
		MongoCollection<org.bson.Document> collection = MongoDBHandler.getDatabase().getCollection("videos");
		org.bson.Document doc = collection.find(Filters.eq("lastVideoId", this.videoId)).first();
		
		if(doc == null) {
			this.newVideo = true;
		} else {
			this.newVideo = false;
		}

	}

	public String getTitle() {
		return this.title;
	}

	public String getDescription() {
		return this.description;
	}

	public URL getURL() {
		return this.url;
	}

	public String getVideoId() {
		return this.videoId;
	}

	public String getChannelId() {
		return this.channelid;
	}
	
	public URL getThumbnailURL() {
		return this.thumbnail;
	}

	public String getShortDescription() {
		return this.description.substring(0, 100) + "...";
	}

	public boolean isNew() {
		return newVideo;
	}
	
	public void publish(TextChannel channel) {
		this.newVideo = false;
		MongoCollection<org.bson.Document> collection = MongoDBHandler.getDatabase().getCollection("videos");
		collection.updateOne(Filters.eq("_id", this.channelid), Updates.set("lastVideoId", this.videoId));
		
		EmbedBuilder msg = new EmbedBuilder();
		msg.setTitle("Ein neues Video wurde veröffentlicht!", this.url.toString());
		msg.setDescription(this.url.toString());
		msg.addField(this.getTitle(), this.getShortDescription(), true);
		msg.setColor(0x33cc33);
		msg.setFooter("© Trainsa " + Main.year);
		msg.setImage(this.thumbnail.toString());
		channel.sendMessage(msg.build()).queue();
		
		
	}
	
}
