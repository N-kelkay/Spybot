package com.tybug.spybot.util;

import java.util.ArrayList;
import java.util.List;

import com.tybug.spybot.database.DBFunctions;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * For loading and storing messages sent in a guild.
 * @author Liam DeVoe
 */
public class HistoryLogger {
	private static final int LOAD_PER = 50;

	private List<TextChannel> loadFrom;
	private TextChannel textChannel;

	private List<String> markerMessages;

	public HistoryLogger(TextChannel textChannel, List<TextChannel> loadFrom) {
		this.loadFrom = loadFrom;
		this.textChannel = textChannel;

	}


	public void loadAllMessages() {
		markerMessages = DBFunctions.getMakerMessages(); 
		List<TextChannel> finishedChannels = new ArrayList<TextChannel>();

		Message status = textChannel.sendMessage(getDescriptionMessage(finishedChannels, loadFrom.get(0), 0)).complete();
		
//		statusCheck();

		for(TextChannel c : loadFrom) {
			load(c, finishedChannels, status);
			finishedChannels.add(c);
		}

	}

	/**
	 * Runs a status check on the guild.
	 * <p>
	 * Goes through each text channel in the guild, and if the db doesn't have an entry for that id yet (in the LOGGER) table, it adds it
	 */
//	public void statusCheck() {
//		textChannel.getGuild().getTextChannels().stream().filter(c -> !DBFunctions.channelExists(c.getId())).forEach(c -> DBFunctions.addChannel(c.getId()));
//		//OR
//		for(TextChannel c : textChannel.getGuild().getTextChannels()) {
//			if(!DBFunctions.channelExists(c.getId())) { //TODO write DB functions
//				DBFunctions.addChannel(c.getId());
//			}
//		}
//
//
//	}
	
	
	
	
	
	
	/* TODO
	 * Investigate why it always throws CONSTRAINT_NOTNULL on CONTENT_DISPLAY whenver we store messages (but only some of them???)
	 * Store marker messages automatically after each channel
	 * 
	 */
	/**
	 * Loads all messages from each text channel given to it when constructed.
	 * <p>
	 * Messages are loaded 50 at a time (LOAD_PER) from discord, then stored in the database. If we get a message that we've already stored, we stop loading.
	 * 
	 * Load marker messages (the last known stored message, to check against) and channel sizes (for estimated time remaining)
	 * Create progress bar, send it, start loading messages from the first channel
	 */
	public void load(TextChannel channel, List<TextChannel> finishedChannels, Message status) { 
		MessageHistory history = channel.getHistory();
		List<Message> messages = history.retrievePast(LOAD_PER).complete();
		int count = 0;

		status.editMessage(getDescriptionMessage(finishedChannels, channel, count)).queue();
		 
		while(messages.size() == LOAD_PER) { // keep going until the size of our retrieved messages is less than it should be (end of channel)
			if(count != 0) { //if this isn't our first time, load more messages
				messages = history.retrievePast(LOAD_PER).complete();
			}
			
			
			// store all our messages
			for(Message m : messages) {
				count++;
				if(markerMessages.contains(m.getId())) { //if this is our marker message, we've already stored it so get outta here
					BotUtils.sendMessage(textChannel, "Found a marker message!");
					return;
				}
				DBFunctions.addMessage(m.getId(), m.getAuthor().getId(), m.getChannel().getId() // store message in db
						, m.getContentRaw(), m.getContentDisplay(), m.getCreationTime().toString());
			}
			
			
			//edit status message
			status.editMessage(getDescriptionMessage(finishedChannels, channel, count)).queue();
		}

	}
	
	


	/**
	 * Gets an embed describing the current state of the history logger, and where it is in its progress.
	 * @param finishedChannels Channels the logger has finished logging
	 * @param currentlyLoading The channel the logger is currently loading
	 * @param progressBar The string representation of a progress bar, created by {@link BotUtils.createProgressBar}
	 * @return MessageEmbed The embed containing this information
	 */
	private MessageEmbed getDescriptionMessage(List<TextChannel> finishedChannels, TextChannel currentlyLoading, int messagesLoaded) {
		StringBuilder builder = new StringBuilder();
		finishedChannels.forEach(c -> builder.append("\n" + c.getAsMention()));
		return BotUtils.createEmbed("Loading messages from discord..."
				, "Currently loading messages from " + currentlyLoading.getAsMention() + "\nMessages Loaded: " + messagesLoaded +
				"\n\nFinished loading from:" + builder.toString());
	}



}
