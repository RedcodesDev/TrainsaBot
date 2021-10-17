package de.visionvenue.trainsabot.minecraft.buttons;

import de.visionvenue.trainsabot.main.Main;
import de.visionvenue.trainsabot.minecraft.MinecraftPlayer;
import de.visionvenue.trainsabot.minecraft.MinecraftServer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class InfoButtonClickListener extends ListenerAdapter {

	public void onButtonClick(ButtonClickEvent e) {

		e.deferReply().setEphemeral(true).queue();
		
		MinecraftServer server = new MinecraftServer(Main.IP);

		EmbedBuilder msg = new EmbedBuilder();

		switch (e.getButton().getId()) {

		case "players":

			msg.setTitle("**TICS 3.0** - Minecraft Server Spielerliste (**" + server.getPlayerCount() + "**/**"
					+ server.getMaxPlayerCount() + "**)");
			String plrs = "";
			for (MinecraftPlayer plr : server.getPlayers()) {
				plrs += "`" + plr.getUsername() + "`\n";
			}
			msg.setDescription(plrs);
			msg.setColor(0x33cc33);
			msg.setFooter("© Trainsa " + Main.year);
			e.getHook().editOriginalEmbeds(msg.build()).queue();
			break;

		case "mods":

			msg.setTitle("**TICS 3.0** - Minecraft Server Modliste (**" + server.getMods().size() + "**)");
			String mods = "";
			for (String mod : server.getMods()) {
				if (mods.length() <= 2000) {
					mods += "`" + mod + "`\n";
				} else {
					mods += "...";
					break;
				}
			}

			msg.setDescription(mods);
			msg.setColor(0x33cc33);
			msg.setFooter("© Trainsa " + Main.year);
			e.getHook().editOriginalEmbeds(msg.build()).queue();
			break;

		default:
			break;

		}
	}

}
