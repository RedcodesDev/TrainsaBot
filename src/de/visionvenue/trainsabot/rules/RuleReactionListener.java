package de.visionvenue.trainsabot.rules;

import de.visionvenue.trainsabot.error.Error;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RuleReactionListener extends ListenerAdapter {

	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {

		try {
		
		Role role = e.getJDA().getRoleById(797908572994273281l);
		TextChannel channel = e.getJDA().getTextChannelById(794538597692473375l);

		if (!e.getUser().isBot()) {
			if (e.getChannel().equals(channel)) {
				switch (e.getReactionEmote().getName()) {

				case "✅":
					e.getGuild().addRoleToMember(e.getMember(), role).queue();
					e.getReaction().removeReaction(e.getUser()).queue();
					break;

				default:
					break;

				}
			}
		}
		
		} catch (Exception ex) {
			String error = ex.toString() + "\n";
			for (int i = 0; i < ex.getStackTrace().length; i++) {
				error = error + ex.getStackTrace()[i].toString() + "\n";
			}
			new Error(error, "Rule Reaction", e.getChannel(), e.getMember()).sendFull();
			return;
		}

	}

}
