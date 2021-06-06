package de.visionvenue.trainsabot.rules;

import java.awt.Color;

import de.visionvenue.trainsabot.main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RuleAcceptEvent extends ListenerAdapter {

	public void onButtonClick(ButtonClickEvent e) {

		if (e.getComponentId().equals("accept")) {
			Role role = e.getJDA().getRoleById(797908572994273281l);
			if (!e.getMember().getRoles().contains(role)) {
				e.getGuild().addRoleToMember(e.getMember(), role).queue();
				EmbedBuilder msg = new EmbedBuilder();
				msg.setTitle("Du hast die Regeln erfolgreich akzeptiert!");
				msg.setColor(0x33cc33);
				msg.setFooter("© Trainsa " + Main.year);
				e.replyEmbeds(msg.build()).setEphemeral(true).queue();
			} else {
				EmbedBuilder msg = new EmbedBuilder();
				msg.setTitle("Du hast die Regeln bereits akzeptiert!");
				msg.setColor(Color.YELLOW);
				msg.setFooter("© Trainsa " + Main.year);
				e.replyEmbeds(msg.build()).setEphemeral(true).queue();
			}
		}

	}

}
