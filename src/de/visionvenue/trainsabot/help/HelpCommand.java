package de.visionvenue.trainsabot.help;

import de.visionvenue.trainsabot.main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import de.visionvenue.trainsabot.error.Error;

public class HelpCommand extends ListenerAdapter {

	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {

		try {

			String[] args = e.getMessage().getContentRaw().split("\\s+");

			if (args[0].equalsIgnoreCase(Main.prefix + "help")) {
				EmbedBuilder msg = new EmbedBuilder();
				msg.setTitle(e.getJDA().getSelfUser().getName() + " Hilfe");
				msg.addField(Main.prefix + "help", "Zeigt dir diese Liste an.", true);
				msg.addField(Main.prefix + "verify",
						"Erstellt einen Verifizierungscode, um deinen Minecraft Account mit Discord zu verbinden.",
						true);
				msg.addField(Main.prefix + "verify `code`",
						"Löst einen Verifizierungscode ein, um deinen Minecraft Account mit Discord zu verbinden.",
						true);
				msg.addField(Main.prefix + "user `@user`", "Zeigt dir Informationen über einen User an.", true);
				msg.setColor(0x33cc33);
				msg.setFooter("© Trainsa " + Main.year);
				e.getChannel().sendMessage(msg.build()).reference(e.getMessage()).queue();
			}

		} catch (Exception ex) {
			String error = ex.toString() + "\n";
			for (int i = 0; i < ex.getStackTrace().length; i++) {
				error = error + ex.getStackTrace()[i].toString() + "\n";
			}
			new Error(error, "Executed Help Command", e.getChannel(), e.getMember()).sendFull();
			return;
		}
	}

}
