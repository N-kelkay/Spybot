package com.tybug.spybot.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * A representation of the Timeout command, for keeping track of which channels messages were sent in since the timeout was initiated and other
 * information to tell the user when their timeout finishes
 * @author Liam DeVoe
 */
public class Timeout {
	private static ArrayList<Timeout> allTimeouts = new ArrayList<Timeout>();
	private Map<String, Boolean> messagesSent = new HashMap<String, Boolean>();
	private String userId;

	
	public Timeout(Member member) {
		for(TextChannel c : member.getGuild().getTextChannels()) {
			messagesSent.put(c.getId(), false);
		}
		userId = member.getUser().getId();
		allTimeouts.add(this);
	}

	
	
	public Map<String, Boolean> getMessagesSent() {
		return messagesSent;
	}
	

	public String getUserId() {
		return userId;
	}


	/**
	 * Sets whether a message was sent in this channel or not
	 * @param id The id of the textChannel to set
	 * @param b True if a message was sent here, false to reset it to default
	 */
	public void setMessagesSent(String id, boolean b) {
		messagesSent.put(id, b);
	}
	
	/**
	 * Removes the given Timeout from the list of all Timeouts
	 * @param toRemove The timeout to remove
	 */
	public static void removeTimout(Timeout toRemove) {
		allTimeouts.remove(toRemove);
	}



	public static ArrayList<Timeout> getAllTimeouts() {
		return allTimeouts;
	}


}
