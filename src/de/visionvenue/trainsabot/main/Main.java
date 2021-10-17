package de.visionvenue.trainsabot.main;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.security.auth.login.LoginException;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import de.visionvenue.trainsabot.data.MongoDBHandler;
import de.visionvenue.trainsabot.minecraft.MinecraftServer;
import de.visionvenue.trainsabot.minecraft.buttons.InfoButtonClickListener;
import de.visionvenue.trainsabot.minecraft.commands.InfoCommand;
import de.visionvenue.trainsabot.rules.RuleAcceptEvent;
import de.visionvenue.trainsabot.technicpack.TechnicPack;
import de.visionvenue.trainsabot.technicpack.TechnicPackUpdate;
import de.visionvenue.trainsabot.token.DONOTOPEN;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class Main {

	private Thread loop;

	public static JDA jda;

	public static String Version = "Release 1.2";

	public static boolean Dev = false;
	
	public static String IP = "45.82.121.72:25565";

	public static String year = "2021";

	public static String icon = "https://example.com";

	public static String prefix = "!";

	public static Instant online = Instant.now();

	public static void main(String[] args) {
		try {
			new Main();
		} catch (LoginException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	public Main() throws LoginException, IllegalArgumentException {

		String token = null;
		if (Dev) {
			token = DONOTOPEN.getDevToken();
		} else {
			token = DONOTOPEN.getToken();
		}

		JDABuilder builder = JDABuilder.createDefault(token);

		builder.setActivity(Activity.watching("Bot starting..."));
		builder.setStatus(OnlineStatus.IDLE);

		builder.setEnabledIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS));
		builder.setMemberCachePolicy(MemberCachePolicy.ALL);

		// Register Listener

		builder.addEventListeners(new InfoCommand());
		builder.addEventListeners(new InfoButtonClickListener());
		builder.addEventListeners(new RuleAcceptEvent());

		if (Dev) {
		}

		jda = builder.build();
		System.out.println("The Bot is now Online!");

		year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

		shutdown();
		runLoop();

		MongoDBHandler.connect();

	}

	public void shutdown() {

		new Thread(() -> {

			String line = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

			try {
				while ((line = reader.readLine()) != null) {
					if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("stop")) {
						shutdown = true;
						if (jda != null) {
							MongoDBHandler.disconnect();
							jda.getPresence().setStatus(OnlineStatus.OFFLINE);
							jda.shutdown();
							System.out.println("The Bot is now Offline!");
						}
						if (loop != null) {
							loop.interrupt();
						}
						reader.close();
						System.exit(0);
						break;

					} else if (line.equalsIgnoreCase("rules")) {
						EmbedBuilder msg = new EmbedBuilder();
						msg.setTitle("Regeln für diesen Discord Server");
						msg.addField("§1-1",
								"Alle Mitglieder werden freundlich behandelt! Respekt ist essenziell für ein gelungenes Miteinander!",
								false);
						msg.addField("**§**1-2",
								"Profilbilder » Ethisch und sozial fragwürdige Profilbilder sind untersagt. \r\n"
										+ "(z.B. pornografische und gewaltverherrlichende Bilder/Bilder mit sozialkritischem Hintergrund)",
								false);
						msg.addField("**§**1-3",
								"Beleidigungen und Ähnliches » Wer keinen vernünftigen Umgangston beherrscht muss mit den Konsequenzen leben. \r\n"
										+ "Darunter fallen z.B. Rassismus, Diskriminierung, Drogenverherrlichung, Mobbing etc. ",
								false);
						msg.addField("**§**1-4",
								"Rechte » Das Betteln um Serverrechte ist hier nicht erwünscht. Der Missbrauch von Serverrechten wird mit einem Entzug dieser Berechtigung bestraft. ",
								false);
						msg.addField("**§**1-5",
								"Streitigkeiten » Private Missverständnisse und Streitigkeiten sind auch privat zu klären. Sollte die Situation dennoch eskalieren, zieht euch bitte in einen privaten Channel zurück. ",
								false);
						msg.addField("**§**1-6",
								"Datenschutz » Private Daten, wie Telefonnummern, Adressen, Passwörter usw. dürfen nicht öffentlich ausgetauscht werden. Nutzt dazu einen Privaten Chat, es geht darum das keine dritten User eure Privaten Daten missbrauchen können. \r\n",
								false);
						msg.addField("**§**1-7",
								"Werbung » Das Werben von Servern und Produkten etc. ist verboten. Eine Ausnahme stellt der "
										+ jda.getTextChannelById(795010369050509332l).getAsMention() + " Channel dar!",
								false);
						msg.addField("**§**1-8",
								"Medien » Das Teilen von Pornografischen, Illegalen oder Gewaltverherrlichenden Inhalten ist strengstens verboten.",
								false);
						msg.addField("**§**1-9",
								"Weisungen » Das Server-Team hat volles Weisungsrecht. Wer den Anweisungen des Teams nicht folgt wird verwarnt und im Ernstfall gekickt/gebannt!",
								false);
						msg.setColor(0x33cc33);
						msg.setFooter("© Trainsa " + Main.year);
						jda.getTextChannelById(794538597692473375l).sendMessageEmbeds(msg.build())
								.setActionRow(Button.primary("accept", "✅")).queue();
					} else {
						System.out.println("Use 'exit' or 'stop' to shutdown");
					}
				}

			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}).start();
	}

	public boolean shutdown = false;

	public void runLoop() {
		this.loop = new Thread(() -> {
			long time = System.currentTimeMillis();

			while (!shutdown) {
				if (System.currentTimeMillis() >= time + 1000) {
					time = System.currentTimeMillis();
					onSecond();

				}
			}

		});
		this.loop.setName("Loop");
		this.loop.start();
	}

	int next = 7;
	int minuteCheck = 10;
	boolean commandCheck = true;
	String[] status = new String[] { "%prefix%help", "%members% User", "%version%"

	};

	public void onSecond() {
		if (next <= 0) {

			if (commandCheck) {
				commandCheck = false;

				List<CommandData> cmds = new ArrayList<CommandData>();
				cmds.add(new CommandData("info", "Informationen über den TICS 3.0 Minecraft Server."));

				jda.getGuildById(780041125721407528l).updateCommands().addCommands(cmds).queue();
			}

			Random rand = new Random();
			int i = rand.nextInt(status.length);

			int users = 0;

			for (Guild guild : Main.jda.getGuilds()) {
				users = users + guild.getMemberCount();
			}

			String text = status[i].replace("%members%", String.valueOf(users)).replace("%version%", Version)
					.replace("%prefix%", prefix).replace("%guilds%", String.valueOf(jda.getGuilds().size()));

			if (!jda.getPresence().getActivity().getName().equals(text)) {

				if (!Dev) {
					jda.getPresence().setStatus(OnlineStatus.ONLINE);
					if (text.contains("help")) {
						jda.getPresence().setActivity(Activity.listening(text));
					} else if (text.contains("User") || text.contains("Guilds")) {
						jda.getPresence().setActivity(Activity.watching(text));
					} else {
						jda.getPresence().setActivity(Activity.playing(text));
					}
				} else {
					jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
					jda.getPresence().setActivity(Activity.watching("Development"));
				}
			} else {
				onSecond();
			}

			next = 7;
		} else {
			next--;
		}

		if (minuteCheck <= 0) {

			//Minecraft Server Online Status
			
			MinecraftServer server = new MinecraftServer(Main.IP);
			
			
			MongoCollection<Document> collection = MongoDBHandler.getDatabase().getCollection("data");
			Document doc = collection.find(Filters.eq("_id", "serverstatus")).first();
			
			if(!doc.getBoolean("laststatus").equals(server.isOnline())) {
				if(server.isOnline()) {
					EmbedBuilder msg = new EmbedBuilder();
					msg.setTitle("TICS 3.0 - Minecraft Server Information");
					msg.setDescription("> Der Server ist nun wieder **online**!");
					msg.setColor(0x33cc33);
					msg.setFooter("© Trainsa " + Main.year);
					jda.getTextChannelById(813475362473771069l).sendMessageEmbeds(msg.build()).queue();
					collection.updateOne(Filters.eq("_id", "serverstatus"), Updates.set("laststatus", true));
				} else {
					EmbedBuilder msg = new EmbedBuilder();
					msg.setTitle("TICS 3.0 - Minecraft Server Information");
					msg.setDescription("> Der Server ist **offline**!");
					msg.setColor(Color.RED);
					msg.setFooter("© Trainsa " + Main.year);
					jda.getTextChannelById(813475362473771069l).sendMessageEmbeds(msg.build()).queue();
					collection.updateOne(Filters.eq("_id", "serverstatus"), Updates.set("laststatus", false));
				}
			}
			
			//TechnicModpack Updater
			
			TechnicPack pack = new TechnicPack("tics-30");

			if (!pack.getUpdates().get(0).isPublished()) {
				TechnicPackUpdate update = pack.getUpdates().get(0);
				EmbedBuilder msg = new EmbedBuilder();
				msg.setTitle("TICS 3.0 Modpack Update");
				msg.setDescription(
						"Beschreibung: **" + update.getContent() + "**\nVersion: **" + pack.getVersion() + "**");
				msg.setColor(0x33cc33);
				msg.setFooter("© TICS 3.0 Bot " + Main.year);

				List<ActionRow> rows = new ArrayList<ActionRow>();
				rows.add(ActionRow.of(
						Button.link(pack.getUrl().toString(), "Technic Launcher Download")
								.withEmoji(Emoji.fromMarkdown("🛠")),
						Button.link(pack.getDownloadUrl().toString(), "Direkter Download")
								.withEmoji(Emoji.fromMarkdown("💻"))));
				rows.add(ActionRow.of(
						Button.success("downloads", pack.getDownloads() + " Downloads")
								.withEmoji(Emoji.fromMarkdown("📈")).asDisabled(),
						Button.success("rating", pack.getRating() + " Likes").withEmoji(Emoji.fromMarkdown("⭐"))
								.asDisabled(),
						Button.success("runs", pack.getRuns() + " mal gestartet").withEmoji(Emoji.fromMarkdown("🔁"))
								.asDisabled()));

				jda.getGuildById(780041125721407528l).getTextChannelById(823854376262959105l)
						.sendMessageEmbeds(msg.build()).setActionRows(rows).queue();

				update.publish();
			}

			minuteCheck = 60;

		} else {
			minuteCheck--;
		}

	}

}
