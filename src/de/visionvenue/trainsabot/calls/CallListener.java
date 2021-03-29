package de.visionvenue.trainsabot.calls;

import java.awt.Color;
import java.util.EnumSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import de.visionvenue.trainsabot.main.Main;
import de.visionvenue.trainsabot.user.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;

public class CallListener extends ListenerAdapter {

	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {

		String[] args = e.getMessage().getContentRaw().split(";");

		if (e.getMessage().isWebhookMessage()) {
			if (e.getChannel().getIdLong() == 825362965141782539l) {
				if (args[0].trim().equalsIgnoreCase("CALL")) {

					e.getGuild().createVoiceChannel("│🔊Anruf")
							.setParent(e.getGuild().getCategoryById(825662762239000618l))
							.setParent(e.getGuild().getCategoryById(825662762239000618l)).setBitrate(96000)
							.addRolePermissionOverride(e.getGuild().getPublicRole().getIdLong(), 0l,
									Permission.ALL_PERMISSIONS)
							.queue(channel -> {
								for (int i = 1; i < args.length; i++) {
									User user = new User(UUID.fromString(args[i].trim()));
									if (user.isVerified()) {
										channel.putPermissionOverride(e.getGuild().getMemberById(user.getId()))
										.setAllow(EnumSet.of(Permission.CREATE_INSTANT_INVITE,
												Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT,
												Permission.VOICE_SPEAK, Permission.VOICE_STREAM,
												Permission.VOICE_USE_VAD))
										.setDeny(EnumSet.of(Permission.MANAGE_CHANNEL,
												Permission.MANAGE_PERMISSIONS, Permission.MANAGE_WEBHOOKS,
												Permission.VOICE_DEAF_OTHERS, Permission.VOICE_MOVE_OTHERS,
												Permission.VOICE_MUTE_OTHERS, Permission.PRIORITY_SPEAKER))
										.queue();
										try {
											e.getGuild()
													.moveVoiceMember(e.getGuild().getMemberById(user.getId()), channel)
													.queueAfter(500, TimeUnit.MILLISECONDS);
										} catch (IllegalStateException ex) {
											e.getGuild().getMemberById(user.getId()).getUser().openPrivateChannel()
													.queue(pc -> {
														EmbedBuilder error = new EmbedBuilder();
														error.setTitle(
																"Bitte trete dem `│🔊Anruf` Talk bei, um mit deinem Partner zu komunizieren.");
														error.setColor(Color.RED);
														error.setFooter("© Trainsa " + Main.year);
														pc.sendMessage(error.build()).queue();
													}, (error) -> {
														if (error instanceof ErrorResponseException) {
															ErrorResponseException ex1 = (ErrorResponseException) error;
															if (!ex1.getErrorResponse()
																	.equals(ErrorResponse.CANNOT_SEND_TO_USER)) {
																ex1.printStackTrace();
															} else {
																EmbedBuilder errorMsg = new EmbedBuilder();
																errorMsg.setTitle(
																		"Private Nachrichten nicht aktiviert!");
																errorMsg.setDescription(
																		"Bitte aktiviere die Privaten Nachrichten für diesen Discord Server, damit du den Discord Bot nutzen kannst.");
																errorMsg.setColor(Color.RED);
																errorMsg.setFooter("© Trainsa " + Main.year);
																e.getGuild().getTextChannelById(786528100321198081l)
																		.sendMessage(errorMsg.build())
																		.append("<@" + user.getId() + ">").queue();
															}
														}
													});
										}
									} else {
										throw new IllegalAccessError("User is not verified - UUID:" + args[i].trim());
									}
								}
							});
					e.getMessage().delete().queue();
				}
			}
		}

	}

}
