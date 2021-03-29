package de.visionvenue.trainsabot.user;

import java.awt.Color;

import org.shanerx.mojang.Mojang;
import org.shanerx.mojang.PlayerProfile;

import de.visionvenue.trainsabot.error.Error;
import de.visionvenue.trainsabot.main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class UserCommand extends ListenerAdapter {

	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {

		try {

			String[] args = e.getMessage().getContentRaw().split("\\s+");

			if (args[0].equalsIgnoreCase(Main.prefix + "user")) {
				if (args.length == 2) {
					boolean integer = true;
					String mentionId;

					if (args[1].contains("<@!") || args[1].contains("<@")) {
						if (args[1].contains(">")) {
							mentionId = args[1].replace("<@!", "").replace(">", "").replace("<@", "");
						} else {
							mentionId = args[1];
						}
					} else {
						mentionId = args[1];
					}

					try {
						e.getGuild().getMemberById(mentionId);
					} catch (NumberFormatException ex) {
						integer = false;
					}
					if (integer && e.getGuild().getMemberById(mentionId) != null) {
						e.getGuild().retrieveMemberById(mentionId).queue(member -> {

							User user = new User(member.getIdLong());

							if (user.isVerified()) {

								EmbedBuilder creating = new EmbedBuilder();
								creating.setTitle("Wird geladen...");
								creating.setColor(Color.YELLOW);
								creating.setFooter("© Trainsa " + Main.year, null);
								e.getChannel().sendMessage(creating.build()).reference(e.getMessage())
										.mentionRepliedUser(false).queue(message -> {

											Mojang api = new Mojang().connect();
											PlayerProfile player = api.getPlayerProfile(user.getUUID().toString());

											EmbedBuilder msg = new EmbedBuilder();
											msg.setTitle(player.getUsername());
											msg.setDescription(member.getAsMention());
											msg.addField("Geld", user.getMoney() + "$", true);

											String xp = "";

											for (Job job : Job.values()) {
												if (!job.equals(Job.NONE)) {
													xp = xp + "\n> " + job.getName() + " - " + user.getExp(job)
															+ " EXP";
												}
											}

											msg.addField("Beruf", user.getJob().getName(), true);
											msg.addField("Erfahrung", xp, false);
											if (user.getPlayTime() > 0.0f) {
												msg.addField("Spielzeit", String.valueOf(user.getPlayTime()).replace(".", ",") + " Minuten", true);
											}
											if (user.getFirstTimeJoined() != null) {
												msg.addField("Erster Beitritt", user.getFirstTimeJoinedAsString(), true);
											}
											msg.setColor(0x33cc33);
											msg.setFooter("© Trainsa " + Main.year);
											msg.setThumbnail("https://crafatar.com/avatars/" + player.getUUID() + ".png");
											e.getChannel().sendMessage(msg.build()).reference(e.getMessage()).queue();

											message.delete().queue();
										});

							} else {
								EmbedBuilder error = new EmbedBuilder();
								error.setTitle("Accounts noch nicht verknüpft.");
								error.setDescription(
										"Bevor du diesen Befehl ausführen kannst, must du deinen Discord Account mit deinem Minecraft Account verknüpfen. Gebe dazu !verify ein.");
								error.setColor(Color.RED);
								error.setFooter("© Trainsa " + Main.year);
								e.getChannel().sendMessage(error.build()).reference(e.getMessage()).queue();
							}

						});

					} else {
						EmbedBuilder error = new EmbedBuilder();
						error.setTitle("User wurde nicht gefunden.");
						error.setColor(Color.RED);
						error.setFooter("© Trainsa " + Main.year);
						e.getChannel().sendMessage(error.build()).reference(e.getMessage()).queue();
					}
				} else {

					Member member = e.getMember();

					User user = new User(member.getIdLong());

					if (user.isVerified()) {

						EmbedBuilder creating = new EmbedBuilder();
						creating.setTitle("Wird geladen...");
						creating.setColor(Color.YELLOW);
						creating.setFooter("© Trainsa " + Main.year, null);
						e.getChannel().sendMessage(creating.build()).reference(e.getMessage()).mentionRepliedUser(false)
								.queue(message -> {

									Mojang api = new Mojang().connect();
									PlayerProfile player = api.getPlayerProfile(user.getUUID().toString());

									EmbedBuilder msg = new EmbedBuilder();
									msg.setTitle(player.getUsername());
									msg.setDescription(member.getAsMention());
									msg.addField("Geld", user.getMoney() + "$", true);

									String xp = "";

									for (Job job : Job.values()) {
										if (!job.equals(Job.NONE)) {
											xp = xp + "\n> " + job.getName() + " - " + user.getExp(job) + " EXP";
										}
									}
									msg.addField("Beruf", user.getJob().getName(), true);
									msg.addField("Erfahrung", xp, false);
									if (user.getPlayTime() > 0.0f) {
										msg.addField("Spielzeit", String.valueOf(user.getPlayTime()).replace(".", ",") + " Minuten", true);
									}
									if (user.getFirstTimeJoined() != null) {
										msg.addField("Erster Beitritt", user.getFirstTimeJoinedAsString(), true);
									}
									msg.setColor(0x33cc33);
									msg.setFooter("© Trainsa " + Main.year);
									msg.setThumbnail("https://crafatar.com/avatars/" + player.getUUID() + ".png");
									e.getChannel().sendMessage(msg.build()).reference(e.getMessage()).queue();

									message.delete().queue();
								});

					} else {
						EmbedBuilder error = new EmbedBuilder();
						error.setTitle("Accounts noch nicht verknüpft.");
						error.setDescription(
								"Bevor du diesen Befehl ausführen kannst, must du deinen Discord Account mit deinem Minecraft Account verknüpfen. Gebe dazu !verify ein.");
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
			new Error(error, "Executed User Command", e.getChannel(), e.getMember()).sendFull();
			return;
		}

	}

}
