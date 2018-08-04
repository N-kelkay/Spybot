package com.tybug.spybot.util;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.tybug.spybot.SpybotUtils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

/**
 * Generic bot helper methods, mainly focused around interacting with messages or embeds.
 * @author Liam DeVoe
 */
public class BotUtils {
	
	
	
	
	/**
	 * Sorts a map based on their value in descending order.
	 *
	 * @param map The map to be sorted
	 * @param <K> The keys
	 * @param <V> The entries
	 * @return The Map object, sorted by the value in descending order.
	 * @author Botkiller
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDesc(Map<K, V> map) {
		return map.entrySet()                 	                                  // Get the entries
				.stream()                                                       // Convert into stream
				.sorted(Map.Entry.comparingByValue(Collections.reverseOrder())) // Sort them by their value in reverse order (highest first)
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue,
						(e1, e2) -> e1,
						LinkedHashMap::new
						));                                                             // Convert to map again, keeping the order.
	}

	/**
	 * Reads from changelog.txt, delimited by ```
	 * @return ArrayList A list of strings, the first one being the roadmap and each subsequent entry being a distinct update
	 */
	public static ArrayList<String> getChangeLog() {
		Scanner scanner = null;
		String roadmap = "";
		ArrayList<String> changelogs = new ArrayList<String>();
		try {
			scanner = new Scanner(new FileReader("changelog.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
		while(scanner.hasNext()) {

			//Get the roadmap
			while(scanner.hasNext()) {
				String next = scanner.nextLine();
				if(next.equals("```")) {
					break;
				}
				sb.append(next + "\n");
			}
			roadmap = sb.toString();
			sb = new StringBuilder();


			//Load the rest of the changelog in chunks, date delimited
			while(scanner.hasNext()) {
				String next = scanner.nextLine();
				if(next.equals("```")) { //my delimiter
					changelogs.add(sb.toString());
					sb = new StringBuilder();
					continue;
				}
				sb.append(next + "\n");
			}
		}
		scanner.close();
		changelogs.add(0, roadmap);
		return changelogs;
	}


	/**
	 * Returns a percentage representation of the fraction num/denom
	 * @param num The numerator
	 * @param denom The denominator
	 * @return The percentage of num/denom, rounded down
	 */
	public static String getPercentage(int num, int denom) {
		int percent = (num * 100) / denom;
		return percent + "%";
	}

	/**
	 * Adds the specified content to the end of the specified log
	 * @param file The name of the file to write to (Without the extension, and assuming it's in the parent directory)
	 * @param log The content to append
	 * @param append If true, content will be written to the end of the file, or if false the beginning of the file
	 */
	public static void appendLog(String file, String log, boolean append) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file + ".txt", append));
			bw.newLine();
			bw.write(log);
			bw.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {                     
			if (bw != null) try {
				bw.close();
			} catch (IOException ioe2) {}
		}
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
		int length = content.length();
		if(length != 0) {
			if(length > 2000) {
				for (int i = 0; i < length; i += 2000) {
					sendMessage(channel, content.substring(i, i + 2000));
				}
			} else {
				channel.sendMessage(content).queue();
			}
		}
	}



	/**
	 * Sends the specified embed to the specified channel.
	 * @param channel The TextChannel to send the message to
	 * @param content The MessageEmbed the message should contain
	 */
	public static void sendMessage(TextChannel channel, MessageEmbed embed) {
		try {
			channel.sendMessage(embed).queue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	/**
	 * Converts a hex value to rbg value and returns it as a color object
	 * <p>
	 * NOTE: Not try/catched or input checked!
	 * @param colorStr The hex value, with or without the pound
	 * @return Color A color object representing the parsed red, blue, and green values
	 */
	public static Color hex2Rgb(String colorStr) {
		colorStr = colorStr.replaceAll("#", "");
		return new Color(
				Integer.valueOf(colorStr.substring(0, 2), 16),
				Integer.valueOf(colorStr.substring(2, 4), 16),
				Integer.valueOf(colorStr.substring(4, 6), 16));
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
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(title);
		builder.setDescription(content);
		builder.setColor(Color.GREEN);
		return builder.build();
	}



	/**
	 * Sends a private message with the specified content to the specified user
	 * @param user The user to send the message to
	 * @param content The raw content the message should contain
	 */
	public static void sendPrivateMessage(User user, String content) {
		if(content.equals("")) {
			return; //don't send anything if it's empty
		}
		user.openPrivateChannel().complete().sendMessage(content).queue();	
	}



	/**
	 * Sends a private message with the specified embed to the specified user
	 * @param user The user to send the message to
	 * @param MessageEmbed the embed to send
	 */
	public static void sendPrivateMessage(User user, MessageEmbed embed) {
		user.openPrivateChannel().complete().sendMessage(embed).queue();	
	}



	/**
	 * Adds the check emoji to the given message
	 * @param message The message to add the check emoji to
	 */
	public static void check(Message message) {
		message.addReaction(SpybotUtils.EMOTE_CHECK).queue(); //add the checkmark
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
		return createEmbed("Man page for " + SpybotUtils.BOT_PREFIX + command,
				"**Description:**\n" + description + "\n\n**Usage:**\n" + varEx + "\n" + normalEx);
	}



	/**
	 * Gives a server's text channels and roles, and their corresponding ids.
	 * @param guild The guild to get information of
	 * @return MessageEmbed An embed with this information contained in it
	 */
	public static MessageEmbed createServerInfoEmbed(Guild guild) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Server Info for " + guild.getName());
		builder.setColor(Color.green);
		StringBuilder sb = new StringBuilder();
		for(TextChannel c : guild.getTextChannels()) {
			sb.append(c.getName().substring(0, 1).toUpperCase() + c.getName().substring(1) + " - " + c.getId() + "\n");
		}
		builder.addField("Text Channels", sb.toString(), false);

		sb = new StringBuilder();
		for(Role r : guild.getRoles()) {
			sb.append(r.getName() + " - " + r.getId() + "\n");
		}
		builder.addField("Roles", sb.toString(), false);
		return builder.build();

	}
	
	/**
	 * Finds the role with the highest position that has a non-default color attached to it.
	 * 
	 * @param member The member to check the roles of
	 * @return The color role pertaining to this member, or null if there is none
	 */
	public static Role getColorRole(Member member) {
		//Since JDA returns them in order, we can just pick the first one that has a color, that's not
		for(Role r : member.getRoles()) {
			if(r.getColor() == null || SpybotUtils.IGNORED_COLOR_ROLES.contains(r.getId())) {
				continue;
			}
			
			return r;
		}
		
		return null;
	}



	/**
	 * If a PEB with the given user is already contained in the static list, it returns that PEB. Otherwise, it creates a new one with the given user
	 * and returns the newly created PEB.
	 * @param user The user to check against each PEB's guild
	 * @return ServerEmbedBuilder Either the PEB with the guild matching the given user, or the newly created PEB if no matches were found
	 */
	public static PrivateEmbedBuilder makeOrGetBuilder(User user) {
		for(PrivateEmbedBuilder seb : PrivateEmbedBuilder.getAllPrivateEmbedBuilders()) {
			if(seb.getUser().equals(user)) {
				return seb;
			}
		}
		return new PrivateEmbedBuilder(user);
	}

	public static String createProgressBar(int length, int num, int denom) {
		return createProgressBar(length, (denom == 0) ? (0) : (num * 100) / (denom));
	}

	public static String createProgressBar(int length, int percentage) {

		int barsToFill = (length * percentage) / (100);
		StringBuilder sb = new StringBuilder("[");
		for(int i = 0; i < barsToFill; i++) {
			sb.append("#");
		}
		for(int i = barsToFill; i < length; i++) {
			sb.append(" ");
		}
		sb.append("] " + percentage + "%");
		return sb.toString();
	}





}
