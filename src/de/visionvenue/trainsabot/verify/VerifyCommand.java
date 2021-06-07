package de.visionvenue.trainsabot.verify;

import java.awt.Color;

import org.shanerx.mojang.Mojang;
import org.shanerx.mojang.PlayerProfile;

import de.visionvenue.trainsabot.error.Error;
import de.visionvenue.trainsabot.main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;

public class VerifyCommand extends ListenerAdapter {

	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {

		try {
		
		Role role = e.getJDA().getRoleById(785284945782374460l);

		String[] args = e.getMessage().getContentRaw().split("\\s+");

		if (args[0].equalsIgnoreCase(Main.prefix + "verify")) {
			if (args.length == 2) {

				VerifyCode code = new VerifyCode(args[1]);
				if (code.isValid()) {

					EmbedBuilder creating = new EmbedBuilder();
					creating.setTitle("Accounts werden verknüpft...");
					creating.setColor(Color.YELLOW);
					creating.setFooter("© Trainsa " + Main.year, null);
					e.getChannel().sendMessage(creating.build()).reference(e.getMessage()).mentionRepliedUser(false).queue(message -> {

						Mojang api = new Mojang().connect();
						PlayerProfile player = api.getPlayerProfile(code.getMinecraftUUID().toString());

						EmbedBuilder msg = new EmbedBuilder();
						msg.setTitle("Willkommen **" + player.getUsername() + "**");
						msg.setDescription(
								"Dein Minecraft Account wurde erfolgreich mit deinem Discord Account verknüpft.");
						msg.setColor(0x33cc33);
						msg.setFooter("© Trainsa " + Main.year);
						msg.setThumbnail("https://crafatar.com/avatars/" + player.getUUID());
						e.getChannel().sendMessage(msg.build()).reference(e.getMessage()).queue();

						code.delete(e.getMember().getIdLong());

						e.getGuild().addRoleToMember(e.getMember(), role).queue();
						e.getMember().modifyNickname(player.getUsername());
						message.delete().queue();
					});

				} else {
					EmbedBuilder error = new EmbedBuilder();
					error.setTitle("Ungültiger Code");
					error.setDescription(
							"Bitte überprüfe den Code, oder generiere dir einen neuen indem du !verify eingibst.");
					error.setColor(Color.RED);
					error.setFooter("© Trainsa " + Main.year);
					e.getChannel().sendMessage(error.build()).reference(e.getMessage()).queue();
				}

			} else {
				VerifyCode code = new VerifyCode(e.getMember().getIdLong());

				if (!code.isVerified()) {

					EmbedBuilder msg = new EmbedBuilder();
					msg.setTitle("Minecraft - Discord | Verifikation");
					msg.setDescription(
							"Gebe folgenden Befehl auf dem Minecraft Server ein, um deinen Minecraft Account mit deinem Discord Account zu verknüpfen. Falls du Hilfe brauchst melde dich einfach bei einem Teammitglied");
					msg.addField("", "/verify " + code.getCode(), false);
					msg.setFooter("© Trainsa " + Main.year);
					msg.setColor(Color.YELLOW);

					e.getAuthor().openPrivateChannel().queue(channel -> {

						channel.sendMessage(msg.build()).append(e.getMember().getAsMention()).queue();

					}, (error) -> {
						if (error instanceof ErrorResponseException) {
							ErrorResponseException ex = (ErrorResponseException) error;
							if (!ex.getErrorResponse().equals(ErrorResponse.CANNOT_SEND_TO_USER)) {
								ex.printStackTrace();
								return;
							} else {
								EmbedBuilder errorMsg = new EmbedBuilder();
								errorMsg.setTitle("Private Nachrichten nicht aktiviert!");
								errorMsg.setDescription(
										"Bitte aktiviere die Privaten Nachrichten für diesen Discord Server um dich verifizieren zu können.");
								errorMsg.setColor(Color.RED);
								errorMsg.setFooter("© Trainsa " + Main.year);
								e.getChannel().sendMessage(errorMsg.build()).reference(e.getMessage())
										.queue();
								return;
							}
						}
					});
					
					EmbedBuilder pm = new EmbedBuilder();
					pm.setTitle("Anweisungen zur Verifikation wurden per Private Nachricht gesendet.");
					pm.setDescription("Schaue in deine Privaten Nachrichten, um mit der Verifikation fortzufahren.");
					pm.setColor(Color.YELLOW);
					pm.setFooter("© Trainsa " + Main.year);
					e.getChannel().sendMessage(pm.build()).reference(e.getMessage()).queue();

				} else {
					EmbedBuilder error = new EmbedBuilder();
					error.setTitle("Du bist bereits verifiziert.");
					error.setDescription(
							"Falls du der Meinung bist das dies ein Fehler ist, kontaktiere bitte ein Teammitglied.");
					error.setColor(Color.RED);
					error.setFooter("© Trainsa " + Main.year);
					e.getChannel().sendMessage(error.build()).reference(e.getMessage()).queue();
				}
			}
		}
		
		} catch (Exception ex) {
			String error = ex.toString() + "\n";
			for (int i = 0; i < ex.getStackTrace().length; i++) {
				error = error + ex.getStackTrace()[i].toString() + "\n";
			}
			new Error(error, "Executed Verify Command", e.getChannel(), e.getMember()).sendFull();
			return;
		}

	}

}
