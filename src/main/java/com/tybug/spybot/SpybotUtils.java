package com.tybug.spybot;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tybug.spybot.database.DBFunctions;
import com.tybug.spybot.util.BotUtils;
import com.tybug.spybot.util.PrivateEmbedBuilder;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
/**
 * Helper methods specific to ActivityMonitor are found here.
 * All generic helper methods that can or will reasonably be reused are found in {@link DiscordBot.util.BotUtils}
 * <p>
 * However, for convenience and ease of writing in {@link Spybot},
 *  many methods are included here that simply link an identical method in {@link DiscordBot.util.BotUtils}. 
 * @author Liam DeVoe
 */
public class SpybotUtils {
	
	//Inter-project helper finals
	public static final MessageEmbed OPTIONS_MESSAGE_DEFAULT = BotUtils.createEmbed("Options Menu", "**User Specific:**"
			+ "\n1: Timeout"
			+ "\n\n**Server Specific:**"
			+ "\n2: TBA\n\nPlease make your selection by reacting with the number corresponding to"
			+ " the command who's options you would like to edit.");

	//Meta Bot info
	public static final String BOT_PREFIX = "!";

	//Guilds
	public static final String GUILD_BLAIR = "278212358483214337";

	//TextChannels
	public static final String CHANNEL_GENERAL = "278212358483214337";
	public static final String CHANNEL_MEMES = "278309590112927745";
	public static final String CHANNEL_BOOKS = "322549411618095105";
	public static final String CHANNEL_GAMING = "321002143110660097";
	public static final String CHANNEL_BOTS = "278262935522902017";
	public static final String CHANNEL_MUSIC = "387795750408552458";
	public static final String CHANNEL_RULES = "389948548038262784";
	public static final String CHANNEL_CREATIVECORNER = "391991742661918720";
	public static final String CHANNEL_DYNOLOGS = "390299661006667776";
	public static final String CHANNEL_TEST = "427189433796657161";
	public static final String CHANNEL_HOMEWORK = "309834829669859328";
	public static final String CHANNEL_HOMEWORK2 = "278229579976409090";
	public static final String CHANNEL_RESOURCES = "309467813658099733";
	public static final String CHANNEL_CODING = "355126584321114123";

	//VoiceChannels
	public static final String VOICE_HOMEWORK = "278215245737230337";
	public static final String VOICE_OTHER = "368779539742064640";




	//Roles
	public static final String ROLE_MOD = "278572525842071552";
	public static final String ROLE_NERD = "278265107669188608";
	public static final String ROLE_BOT = "389581209472335887";
	public static final String ROLE_SCRUB = "389580914927075338";
	public static final String ROLE_DOING_HOMEWORK = "431908409089064962";
	public static final String ROLE_SNOW = "425818089251143690";
	public static final String ROLE_FRESHIE = "420785461401878529";


	//Emotes
	public static final String EMOTE_CHECK = "388657600419594240";

	//Emojis
	public static final String EMOJI_WARNING = "\u26A0";
	
	//Messages
	public static final String MESSAGE_NAMES = "430213531686666251";



	//IDs
	public static final String AUTHOR = "216008405758771200";
	public static final String SPYBOT = "365609841135321094";


	//Constant messages used throughout
	public static final String DENY_COMMAND = "Insufficient permissions! Please contact Liam if you believe this to be in error.";
	public static final List<String> IGNORED_COLOR_ROLES = Arrays.asList(ROLE_NERD, ROLE_SCRUB, ROLE_BOT, ROLE_FRESHIE);


	/**
	 * Returns the clearance the given member has on the discord (what commands they are allowed to use) <p>
	 * No Roles = 6 <p>
	 * Scrub = 5 <p>
	 * Nerd = 2 <p> 
	 * Author = 0
	 * @param member The member to check the clearance of
	 * @return An int between 0 and 6 inclusive, reflecting their status on discord
	 */
	public static int getClearance(Member member) {
		JDA jda = member.getGuild().getJDA();
		int clearance = 6;
		Role scrub = jda.getRoleById(ROLE_SCRUB), nerd =  jda.getRoleById(ROLE_NERD);

		if(member.getRoles().contains(scrub)) clearance = 5;
		if(member.getRoles().contains(nerd)) clearance = 2;
		if(member.getUser().getId().equals(AUTHOR)) clearance = 0;

		return clearance;
	}


	/**
	 * Updates the pinned nick :: name message in {@link #CHANNEL_GENERAL}, alphabetically sorting each user's nicks
	 * @param guild The guild where the pin message is contained. This will only ever be passed as the {@link #GUILD_BLAIR},
	 * but the method needs it to get the users and their mentions
	 */
	public static void updateNamePin(Guild guild) {
		Map<String, String> names = DBFunctions.getNames();
		Map<String, String> unsorted = new HashMap<>();


		for(String s : names.keySet()) {
			Member m = guild.getMemberById(s);
			if(m == null) {
				continue;
			}
			unsorted.put(m.getEffectiveName().toLowerCase(), s);
		}

		Map<String, String> sorted = new TreeMap<>(unsorted);
		StringBuilder sb = new StringBuilder();
		for(String s : sorted.keySet()) {
			Member m = guild.getMemberById(sorted.get(s));
			sb.append(m.getAsMention() + " - " + names.get(sorted.get(s)) + "\n");
		}
		guild.getTextChannelById(CHANNEL_GENERAL).getMessageById(MESSAGE_NAMES).complete().editMessage(sb.toString()).queue();
	}



	
	
	
	/**
	 * Inserts 24 entries corresponding to the 24 hours, where hours 24 and 1-11 have "AM" after them and hours 12 - 23 have "PM" after them
	 * @param map The map to set up
	 */
	public static void setUpHourMap(Map<String, Integer> map) {
		for(int i = 1; i < 25; i++) {
			if(i > 12) {
				if(i == 24) {
					map.put(12 + " AM", 0);
				} else {
					map.put(i%12 + " PM", 0);
				}
			} else if (i == 12) {
				map.put(12 + " PM", 0);
			}
			else {
				map.put(i + " AM", 0);
			}
		}
	}
	
	
	
		
	//---------------------------------------------------STATISTICS COMMAND----------------------------------------------------------------------
	
	
	/**
	 * Counts how many times any emoji has been used
	 * @param raw A list of raw content strings, from Messages
	 * @return HashMap<String, Integer> A map of each emoji id to the number of times it appears in the list
	 */
	public static HashMap<String, Integer> mapEmojisToFrequency(List<String> raw){
		HashMap<String, Integer> ret = new HashMap<String, Integer>();
		for(String s : raw) {
			Pattern pattern = Pattern.compile("<:(.*?):(.*?)>");
			Matcher matcher = pattern.matcher(s);
			if(matcher.find()) {
				String emojiId = matcher.group(2);
				ret.putIfAbsent(emojiId, 0); //if it's not there already, add it. Will get incremented next so set to zero
				ret.put(emojiId, ret.get(emojiId) + 1); //Increment, guaranteed to be here because of above
			}
		}
		return ret;
	}


		//---------------------------------------------------ACTIVITY GRAPHING----------------------------------------------------------------
		

		/**
		 * Given a UTC timestamp, parses the UTC date and converts to EST
		 * @param timestamp The timestamp to parse
		 * @return The date (in mm/dd/yyy format) in EST
		 */
		public static String parseDate(String timestamp) {

			//2018-01-01T02:39:59.075Z
			timestamp = timestamp.replaceAll("Z", "");
			String[] parts = timestamp.split("[-T:]");
			int yearUTCI = Integer.parseInt(parts[0]); //2018
			int monthUTCI = Integer.parseInt(parts[1]); //3
			int dayUTCI = Integer.parseInt(parts[2]); //1
			int hourUTCI = Integer.parseInt(parts[3]); //2
			int hourESTI = hourUTCI - 4; //-2
			int dayESTI = dayUTCI;
			int monthESTI = monthUTCI;
			int yearESTI = yearUTCI;
			if(hourESTI < 1) { //true
				hourESTI = 12 - Math.abs(hourESTI); //12 - 2 = 10
				dayESTI -= 1; //1 - 1 = 0
			}
			if(dayESTI == 0) {
				monthESTI -= 1; //1 - 1 = 0;
				if(monthESTI == 0) {
					yearESTI -= 1; //2018 - 1 = 2017
					monthESTI = 12; //12
				}
				switch (monthESTI){
				case 1:
					dayESTI = 31;
					break;
				case 2:
					dayESTI = 28;
					break;
				case 3:
					dayESTI = 31;
					break;
				case 4:
					dayESTI = 30;
					break;
				case 5:
					dayESTI = 31;
					break;
				case 6:
					dayESTI = 30;
					break;
				case 7:
					dayESTI = 31;
					break;
				case 8:
					dayESTI = 31;
					break;
				case 9:
					dayESTI = 30;
					break;
				case 10:
					dayESTI = 31;
					break;
				case 11:
					dayESTI = 30;
					break;
				case 12:
					dayESTI = 31;
					break;	
				}
			}

			return monthESTI + "/" + dayESTI + "/" + yearESTI;


		}


		/**
		 * Given a UTC timestamp, parses the UTC hour and converts to EST
		 * @param timestamp The timestamp to parse
		 * @return The hour, in EST time
		 */
		public static String parseHour(String timestamp) { //2018-01-01T04:39:59.075Z
			//		System.out.println("parseHours call. Passed string: " + s);
			timestamp = timestamp.replaceAll("Z", ""); //2018-01-01T04:39:59.075
			String[] parts = timestamp.split("[-T:]"); //2018, 01, 01, 04, 39, 59075
			//		String debug = "";
			//		for(int i = 0; i < parts.length; i++) {
			//			debug += parts[i] + ", ";
			//		}
			//		System.out.println("Parts[]: " + debug);
			int hourUTCI = Integer.parseInt(parts[3]); //4
			//		System.out.println("Parsed hour: " + hourUTCI);
			int hourESTI = hourUTCI - 4; //0
			//		System.out.println("Parsed hour - 4: " + hourESTI);

			if(hourESTI < 1) { //true
				//			System.out.println("ESTI is negative");
				hourESTI = 24 - Math.abs(hourESTI); //24 - 0 = 24
				//			System.out.println("New ESTI: " + hourESTI);
			}
			String hourESTS = "";
			if(hourESTI > 12) {
				//			System.out.println("ESTI is greater than 12");
				if(hourESTI == 24) {
					hourESTS = 12 + " AM";
				} else {
					int temp = hourESTI - 12;
					hourESTS = temp + " PM";
				}
				//			System.out.println("ESTS: " + temp);
			} if(hourESTI < 12) {
				//			System.out.println("ESTI is less than 12");
				hourESTS = hourESTI + " AM";
				//			System.out.println("ESTS: " + hourESTS);
			}
			if(hourESTI == 12) {
				//			System.out.println("ESTI is equal to 12");
				hourESTS = "12 PM";
				//			System.out.println("ESTS: " + hourESTS);
			}
			//		System.out.println("Returning ESTS finally: " + hourESTS + "\n\n");
			return hourESTS;
		}

		/**
		 * Makes a full map (String :: Integer) of Date :: 0, containing every day from 2017-06-02 (Creation of the Blair Discord) to now. 
		 * @return Map<String, Integer> A map of DATE :: 0 as described above
		 * @throws ParseException
		 */
		public static Map<String, Integer> getEmptyMap() throws ParseException {
			Map<String, Integer> ret = new HashMap<String, Integer>();

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = formatter.parse("2017-06-02");
			//		Date startDate = formatter.parse("2016-11-28");
			Date endDate = new Date();
			LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
				int year = date.getYear();
				int month = date.getMonthValue();
				int day = date.getDayOfMonth();
				ret.put(month + "/" + day + "/" + year, 0);
			}

			return ret;
		}


		




		//---------------------------------------------------SNOW CHECKER--------------------------------------------------------------------------------------------
		/**
		 * Replaces all instances of certain strings, specifically those with html tags, with an empty string. 
		 * <p>
		 * (Effectively gets rid of specific html tags, and some extraneous information)
		 * @param s The string to strip of html
		 * @return The input string with specific html tags removed
		 */
		public static String removeHTML(String s) {
			return s.replaceAll("<div class='MSG_EMER'><h3>EMERGENCY MESSAGE: <span class=\"timestamp\">", "")
					.replaceAll(" -- Refresh page to update message</span></h3><p>", "")
					.replaceAll("  ", "");
		}


		/**
		 * Connects to the given webpage and attempts to parse any emergency message the page might contain by looking for html tags.
		 * @param url The url of the website to check
		 * @return String The emergency message contained in the website (plus some formatting), or an empty string if no emergency message was found.
		 */
		public static String getEmergencyMessage(String url) {
			StringBuilder sb = new StringBuilder();
			URL oracle;
			try {
				oracle = new URL(url);

				BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					sb.append(inputLine);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			String unparsed = sb.toString();

			Pattern pattern = Pattern.compile("<div class=\"emergency\">(.*?)</p>");
			Matcher matcher = pattern.matcher(unparsed);
			String parsed = "";
			if(matcher.find()) {
				parsed = matcher.group(1);
				parsed = removeHTML(parsed);
				parsed = "**" + parsed.replaceAll("MM", "M**\n\n```M") + "```";
			}
			return parsed;
		}
		
		
		
		
		/**
		 * Sends a message with the specified content to the specified channel.
		 * <p> 
		 * If the message is over 2k characters long, it keeps on splitting right at 2k chars and sending the message
		 * 
		 * @param channel The TextChannel to send the message to
		 * @param content The raw content the message should contain
		 */
		public static void sendMessage (TextChannel channel, String content) {
			BotUtils.sendMessage(channel, content);
		}
		
		
		
		/**
		 * Sends the specified embed to the specified channel.
		 * @param channel The TextChannel to send the message to
		 * @param content The MessageEmbed the message should contain
		 */
		public static void sendMessage(TextChannel channel, MessageEmbed embed) {
			BotUtils.sendMessage(channel, embed);
		}
		
		
		
		/**
		 * Makes an embed object with a title, and the color Color.Green (kinda brightish)
		 * @param content The title of the embed
		 * @return MessageEmbed The embed object created with the specified title
		 */
		public static MessageEmbed createEmbed(String content) {
			return BotUtils.createEmbed(content, "");
		}
		
		
		/**
		 * Makes an embed object with a title, description, and the color Color.Green (kinda brightish)
		 * @param title The title of the embed
		 * @param content The description of the embed
		 * @return MessageEmbed The embed object created with the specified args
		 */
		public static MessageEmbed createEmbed(String title, String content) {
			return BotUtils.createEmbed(title, content);
		}
		
		
		/**
		 * Sends a private message with the specified content to the specified user
		 * @param user The user to send the message to
		 * @param content The raw content the message should contain
		 */
		public static void sendPrivateMessage(User user, String content) {
			BotUtils.sendPrivateMessage(user, content);
		}
		
		
		/**
		 * Sends a private message with the specified embed to the specified user
		 * @param user The user to send the message to
		 * @param MessageEmbed the embed to send
		 */
		public static void sendPrivateMessage(User user, MessageEmbed embed) {
			BotUtils.sendPrivateMessage(user, embed);
		}
		
		
		/**
		 * Adds the check emoji to the given message
		 * @param message The message to add the check emoji to
		 */
		public static void check(Message message) {
			BotUtils.check(message);
		}
		
		
		
		/**
		 * Makes a MessageEmbed modeled after a help message with the given contents
		 * @param command The name of the command, without the prefix
		 * @param description The description of the command
		 * @param varEx A usage example with variables/placeholders instead of values
		 * @param normalEx A usage example of how you would actually type it
		 * @return MessageEmbed an embed with the given information in the appropriate places, plus constants ("**Description:**) etc
		 */
		public static MessageEmbed createHelpEmbed(String command, String description, String varEx, String normalEx) {
			return BotUtils.createHelpEmbed(command, description, varEx, normalEx);
		}
		

		
		/**
		 * If a PEB with the given user is already contained in the static list, it returns that PEB. Otherwise, it creates a new one with the given user
		 * and returns the newly created PEB.
		 * @param user The user to check against each PEB's guild
		 * @return ServerEmbedBuilder Either the PEB with the guild matching the given user, or the newly created PEB if no matches were found
		 */
		public static PrivateEmbedBuilder makeOrGetBuilder(User user) {
			return BotUtils.makeOrGetBuilder(user);
		}
		
		
		 public static String createProgressBar(int length, int percentage) {
			 return BotUtils.createProgressBar(length, percentage);
		 }

		
		
		
}