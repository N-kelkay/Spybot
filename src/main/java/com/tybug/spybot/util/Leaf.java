package com.tybug.spybot.util;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;

public class Leaf<T> {

	private int numReacts;
	private String unicode;
	private MessageEmbed queryMessage = null;
	private MessageEmbed successMessage = null;
	private boolean queried = false;
	
	public Leaf(int numReacts, String unicode, MessageEmbed queryMessage, MessageEmbed successMessage) {
		this.setNumReacts(numReacts);
		this.setUnicode(unicode);
		this.queryMessage = queryMessage;
		this.successMessage = successMessage;
	}

	
	public void edit(Message m, String unicode) {
		if(queried == false) {
			m.editMessage(queryMessage).queue();
			queried = true;
			addReactions(m);
		} else {
			EmbedBuilder builder = new EmbedBuilder(successMessage);
			
			// TODO write the preference somewhere
			m.editMessage(builder.appendDescription(unicode).build()).queue(); //All successMessages leave space for the input to be entered in
			m.clearReactions().queue();
			m.addReaction(OptionEmbed.UNICODE_BACK).queue();
		}
	}
	
	private void addReactions(Message m) {
		refreshMessage(m).clearReactions().complete();
		for(int i = 0; i < numReacts; i++) { //Start at zero to accomodate entering 10 or 2012, as well as  0 = false and 1 = true
			m.addReaction(i + OptionEmbed.UNICODE_NUMBER).complete();
		}
		m.addReaction(OptionEmbed.UNICODE_BACK).queue();
	}
	
	
	private Message refreshMessage(Message m) {
		return m.getTextChannel().getMessageById(m.getId()).complete();
	}
	
	public int getNumReacts() {
		return numReacts;
	}

	public void setNumReacts(int numReacts) {
		this.numReacts = numReacts;
	}

	public String getUnicode() {
		return unicode;
	}

	public void setUnicode(String unicode) {
		this.unicode = unicode;
	}

	public MessageEmbed getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(MessageEmbed successMessage) {
		this.successMessage = successMessage;
	}


	public boolean isQueried() {
		return queried;
	}


	public void setQueried(boolean queried) {
		this.queried = queried;
	}



}