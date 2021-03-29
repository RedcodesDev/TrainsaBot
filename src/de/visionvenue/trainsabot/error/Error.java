package de.visionvenue.trainsabot.error;

import java.awt.Color;

import de.visionvenue.trainsabot.main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class Error {

	
	String error;
	String action;
	TextChannel channel;
	Member memb;
	
	public Error(String error, String action) {
	
		this.error = error;
		this.action = action;
		
	}
	
	public Error(String error, String action, TextChannel channel, Member memb) {
		this.error = error;
		this.action = action;
		this.channel = channel;
		this.memb = memb;
	}
	
	public void send() {
		EmbedBuilder msg = new EmbedBuilder();
		msg.setColor(Color.RED);
		msg.setTitle("New Crash Report");
		msg.addField("Action", this.action, true);
		msg.addField("Error", this.error, false);
		msg.setFooter("© Trainsa " + Main.year);
		Main.jda.getTextChannelById(825318063570157589l).sendMessage(msg.build()).append("<@277048745458401282>").queue();
	}
	
	public void sendFull() {
		EmbedBuilder msg = new EmbedBuilder();
		msg.setColor(Color.RED);
		msg.setTitle("New Crash Report");
		msg.addField("Action", this.action, true);
		msg.addField("Channel", channel.getAsMention(), true);
		msg.addField("Member", memb.getAsMention(), true);
		if(error.length() <= 1000) {
			msg.addField("Error Code", "```java\n" + error + "```", false);
			} else {
				msg.addField("Error Code", "```Longer than 1000 chars! See Console for more.```", false);
				System.out.println(error);
			}
		msg.setFooter("© Trainsa " + Main.year);
		Main.jda.getTextChannelById(825318063570157589l).sendMessage(msg.build()).append("<@277048745458401282>").queue();
	}
	
}
