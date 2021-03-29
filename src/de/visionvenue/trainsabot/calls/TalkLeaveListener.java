package de.visionvenue.trainsabot.calls;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TalkLeaveListener extends ListenerAdapter {

	public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
		if(e.getChannelLeft().getParent().getIdLong() == 825662762239000618l) {
			if(e.getChannelLeft().getMembers().size() == 0) {
				e.getChannelLeft().delete().queue();
			}
		}
	}
	
	public void onGuildVoiceMove(GuildVoiceMoveEvent e) {
		if(e.getChannelLeft().getParent().getIdLong() == 825662762239000618l) {
			if(e.getChannelLeft().getMembers().size() == 0) {
				e.getChannelLeft().delete().queue();
			}
		}
	}
	
}
