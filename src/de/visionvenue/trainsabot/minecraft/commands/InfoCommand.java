package de.visionvenue.trainsabot.minecraft.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.List;

import de.visionvenue.trainsabot.main.Main;
import de.visionvenue.trainsabot.minecraft.MinecraftServer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;

public class InfoCommand extends ListenerAdapter {

	public void onSlashCommand(SlashCommandEvent e) {

		e.deferReply().queue();

		MinecraftServer server = new MinecraftServer(Main.IP);

		if (server.isOnline()) {

			if (server.getIcon64() != null) {

				String base64 = server.getIcon64();
				String base64ImageString = base64.replace("data:image/png;base64,", "");
				Decoder decoder = Base64.getDecoder();
				byte[] imageBytes = decoder.decode(base64ImageString);

				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream("icon.png");
				} catch (FileNotFoundException e2) {
					e2.printStackTrace();
				}
				try {
					try {
						fos.write(imageBytes);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} finally {
					try {
						fos.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

			}
		}

		EmbedBuilder msg = new EmbedBuilder();
		msg.setTitle("**TICS 3.0** - Minecraft Server Information");
		msg.setDescription("Online: **" + server.isOnline() + "**\nMinecraft Version: **" + server.getMinecraftVersion()
				+ "**\nIP: **" + server.getIpRaw() + "**\nAktive Spieler: **" + server.getPlayerCount() + "**/**"
				+ server.getMaxPlayerCount() + "**\nMOTD: **" + server.getMotd() + "**");
		msg.setColor(0x33cc33);
		msg.setFooter("© Trainsa " + Main.year);
		msg.setThumbnail("attachment://icon.png");

		List<Button> row1 = new ArrayList<Button>();
		if (server.isOnline()) {
			row1.add(Button.secondary("serverinfo_players", "Playerlist (" + server.getPlayerCount() + ")")
					.withEmoji(Emoji.fromMarkdown("💺")));
			row1.add(Button.secondary("serverinfo_mods", "Modlist (" + server.getMods().size() + ")")
					.withEmoji(Emoji.fromMarkdown("🌐")));
		} else {
			row1.add(Button.secondary("serverinfo_players", "Playerlist").asDisabled().withEmoji(Emoji.fromMarkdown("💺")));
			row1.add(Button.secondary("serverinfo_mods", "Modlist").asDisabled().withEmoji(Emoji.fromMarkdown("🌐")));
		}

		e.getHook().editOriginalEmbeds(msg.build()).addFile(new File("icon.png"), "icon.png").setActionRow(row1)
				.queue();

	}

}
