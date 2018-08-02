package com.tybug.spybot.commands.executable;

import java.util.List;
import java.util.Map;

import com.tybug.spybot.SpybotUtils;
import com.tybug.spybot.commands.ExecutableCommand;
import com.tybug.spybot.database.DBFunctions;
import com.tybug.spybot.util.BotUtils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class StatsCommand extends ExecutableCommand {

	public StatsCommand(MessageReceivedEvent event) {
		super(event);
	}

	@Override
	public void execute() {

		User u = null;
		TextChannel tc = null;
		try { //try a user
			u = jda.getUserById(args);

		} catch (Exception e) {
			try { //try a channel
				tc = guild.getTextChannelById(args);
			} catch(Exception e2) {
				//ignore
			}
		}

		EmbedBuilder builder = new EmbedBuilder();


		// Embed
		// Statistics for ___
		// Total messages: __
		// Percent of server messages: __%
		// Most active in: #channel
		// Most used emoji: <:emoji:>
		// Average message length (all): __
		// Average message length (minus bots and coding): __ 

		if(message.getMentionedUsers().size() > 0 || u != null) {
			if(u == null) {
				u = message.getMentionedUsers().get(0);
			}
			String userId = u.getId();
			Map<String, Integer> activity = DBFunctions.getActivityByUserPerChannel(userId);
			activity = BotUtils.sortByValueDesc(activity);
			String key = (String) activity.keySet().toArray()[0]; //the channel with the most messages sent


			List<String> contentRaw = DBFunctions.getContentRawOfMessagesSentByUser(userId);


			//ID :: Count
			Map<String, Integer> emojis = SpybotUtils.mapEmojisToFrequency(contentRaw); 


			//Finding char count of all messages
			List<String> contentDisplay = DBFunctions.getContentDisplayOfMessagesSentByUser(userId);
			int charCountAll = contentDisplay.stream().mapToInt(String::length).sum();


			emojis = BotUtils.sortByValueDesc(emojis);
			String emojiKey = null;
			try {
				emojiKey = (String) emojis.keySet().toArray()[0];
			} catch (ArrayIndexOutOfBoundsException e) {
				//ignore
			}

			//so that it checks and says "no emoji used yet"
			Emote mostUsed = (emojiKey != null ? guild.getEmoteById(emojiKey) : null);
			int numMessages = DBFunctions.getNumMessagesSentByUser(userId);
			Message toDelete = textChannel.sendMessage("Constructing your user specific statistics! This may take a moment.").complete();
			builder.setTitle("Statistics for " + guild.getMember(u).getEffectiveName());
			builder.setFooter("Requested by " + user.getName(), user.getAvatarUrl());
			builder.addField("General Statistics",
					"Total messages: " + numMessages + "\n" + 
							"Percent of server: " + BotUtils.getPercentage(numMessages, DBFunctions.getTotalMessages()) + "\n" + 
							"Most active in: <#" + key + "> (" + activity.get(key) + ")\n" +  
							"Most used emoji: " + (emojiKey != null ? (mostUsed != null ? mostUsed.getAsMention() : "Emoji no longer on server!") +
									" (" + emojis.get(emojiKey) + " times)": "No emoji used yet!") + "\n" + 
									"Average message length (all): " + charCountAll / contentDisplay.size() + "\n", false);
			textChannel.sendMessage(builder.build()).queue();
			toDelete.delete().queue();
			return;
		}

		// Embed
		// Statistics for __
		// 	Total messages: __
		// 	Percent of server messages: __%
		// 	Most used emoji: <:emoji:>
		// 	Average message length (all): __
		// Activity breakdown:
		//	**#1**: __ **(num, __%)**
		//	etc, up to 10
		if(message.getMentionedChannels().size() > 0 || tc != null) {
			if(tc == null) {
				tc = message.getMentionedChannels().get(0);
			}


			String id = tc.getId();
			@SuppressWarnings("unused") //WIP
			List<String> contentDisplay = DBFunctions.getContentDisplayOfMessagesSentByChannel(id);

			builder.setTitle("Statistics for " + tc.getName())
			.addField("General statistics", 
					"Total messages: " + DBFunctions.getNumMessagesSentByChannel(id) + "\n" + 
							"Percent of server messages: " + BotUtils.getPercentage(DBFunctions.getNumMessagesSentByChannel(id), DBFunctions.getTotalMessages()) + "\n" + 
							"Most used emoji: ", false)
			.setFooter("Requested by " + user.getName(), user.getAvatarUrl());
		}

		// Embed
		// #channel
		// Total messages: __
		// Percent of server messages: __%
		// Most active: @user (__% of channel messages)
		// 
		Message toDelete = textChannel.sendMessage("Constructing your server-wide statistics! This may take a moment.").complete();


		builder.setTitle("Statistics for " + guild.getName())
		.setFooter("Requested by " + user.getName(), user.getAvatarUrl());
		
		for(TextChannel c : guild.getTextChannels()) {
			int numMessagesInChannel = DBFunctions.getNumMessagesSentByChannel(c.getId());
			Map<String, Integer> activity = DBFunctions.getActivityInChannel(c.getId());
			activity = BotUtils.sortByValueDesc(activity);
			String key = (String) activity.keySet().toArray()[0];
			builder.addField(c.getName().substring(0, 1).toUpperCase() + c.getName().substring(1), 
					"Total messages: " + numMessagesInChannel + "\n" +
							"Percent of server: " + BotUtils.getPercentage(numMessagesInChannel, DBFunctions.getTotalMessages()) + "\n" +
							"Most active: <@" + key + "> (" + activity.get(key) + " messages)\n" +
							""
							, false);
		}

		textChannel.sendMessage(builder.build()).queue();
		builder.clear();


		builder.setTitle("The top 5 people by messages sent");
		builder.setFooter("Requested by " + user.getName(), user.getAvatarUrl());
		Map<String, Integer> activity = DBFunctions.getTotalActivity();
		activity = BotUtils.sortByValueDesc(activity);
		for(int i = 0; i < 5; i++) {
			String key = (String) activity.keySet().toArray()[i];
			int pos = i + 1;
			builder.addField("**#" + pos + "**", "<@" + key + ">: " + activity.get(key), false);
		}

		textChannel.sendMessage(builder.build()).queue();
		toDelete.delete().queue();
		return;
	
	}

}
