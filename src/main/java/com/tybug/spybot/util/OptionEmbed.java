package com.tybug.spybot.util;

import java.util.HashMap;

import com.tybug.spybot.commands.WaitingState;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class OptionEmbed {
	
	public static final MessageEmbed MAINSCREEN = BotUtils.createEmbed("Options Menu", "**User Specific:**\n"
            + "Timeout (1), RemindMe (2)\n\n"
            + "**Server Wide:**\n"
            + "TBA");
   
    public static final MessageEmbed TIMEOUT = BotUtils.createEmbed("User Timeout Options", "Update time left (1)");
    public static final MessageEmbed TIMEOUT1_QUERY = BotUtils.createEmbed("Should I update the time you have left on your timeout?");
    public static final MessageEmbed TIMEOUT1_SUCCESS = BotUtils.createEmbed("Update time left", "Set preference to ");

    public static final MessageEmbed REMINDME = BotUtils.createEmbed("User RemindMe Options", "Ping when sent (1)");
    public static final MessageEmbed REMINDME1_QUERY = BotUtils.createEmbed("Should I ping you when sending a remind message?");
    public static final MessageEmbed REMINDME1_SUCCESS = BotUtils.createEmbed("Ping when sent", "Set preference to ");

   
    public static final String UNICODE_NUMBER = "\u20E3";
    public static final String UNICODE_BACK = "\u2B05";
    

	private Node<MessageEmbed> node;
	private static HashMap<User, OptionEmbed> allOptionCommands = new HashMap<User, OptionEmbed>();
	private Message message;
	private TextChannel textChannel;
	
	
	public OptionEmbed(MessageReceivedEvent event) {
		this(MAINSCREEN, event);
	}


	private OptionEmbed(MessageEmbed embed, MessageReceivedEvent event) {
		this.node = new Node<MessageEmbed>(embed);
		this.textChannel = event.getTextChannel();
		Node<MessageEmbed> parent = this.node;
		Node<MessageEmbed> node = parent.addChild(TIMEOUT, "1" + UNICODE_NUMBER);
			node.addLeaf(2, "1" + UNICODE_NUMBER, TIMEOUT1_QUERY, TIMEOUT1_SUCCESS);
			
		node = parent.addChild(REMINDME, "2" + UNICODE_NUMBER);
			node.addLeaf(2, "1" + UNICODE_NUMBER, REMINDME1_QUERY, REMINDME1_SUCCESS);
			
		allOptionCommands.put(event.getAuthor(), this);

	}
	
	public void start() {
		this.send();

	}


	/**
	 * Sends the original root option screen which will be constantly edited later and creates the OPTION_REACT pending
	 * @param textChannel
	 */
	public void send() {
		Message m = textChannel.sendMessage(node.getData()).complete();
		this.message = m;
		Pending.addPending(m.getId(), WaitingState.OPTION_REACT);

		for(int i = 1; i < node.getChildren().size() + 1; i++) {
			message.addReaction(i + UNICODE_NUMBER).complete();
		}
	}

	public void edit() {
		edit(node.getData());
	}
	
	
	public void edit(MessageEmbed m) {
		message.editMessage(m).queue();
		refreshMessage().clearReactions().complete();
		addReactions();
	}


	public void addReactions() {
		for(int i = 1; i < node.getChildren().size() + node.getLeaves().size() + 1; i++) { //both nodes and leaves
			message.addReaction(i + UNICODE_NUMBER).complete();
		}
		if(node.getParent() != null) { //if it's not the root node
			message.addReaction(UNICODE_BACK).queue();
		}
	}


	public void surface() {
		for(Leaf<MessageEmbed> leaf : node.getLeaves()) {
			if(leaf.isQueried()) { //guaranteed to only have one queried leaf per node
				this.edit(); //Don't go back two screens, just one
				leaf.setQueried(false); //reset all their queried tags so if we go back it won't go straight to confirmation screen
				return;
			}
		}
		node = node.getParent();
		this.edit();
	}



	public void delve(String unicode) {
		System.out.println("\n" + unicode);
		for(Node<MessageEmbed> child : node.getChildren()) {
			if(child.getUnicode().equals(unicode)) {
				node = child;
				this.edit();
				return;
			}
		}

		for(Leaf<MessageEmbed> leaf : node.getLeaves()) {
			System.out.println(leaf.getUnicode());
			if(leaf.isQueried()) { //start checking at 0 instead
				
				int toAdd = Integer.parseInt(unicode.substring(0, 1)) + 1;
				System.out.println("ToAdd: " + toAdd + "\nParseInt: " + Integer.parseInt(unicode.substring(0, 1)));
				System.out.println(toAdd + unicode.substring(1));
				
				if(leaf.getUnicode().equals(toAdd + unicode.substring(1))) {
					leaf.edit(message, unicode);
				}
				
			} 
			else if(leaf.getUnicode().equals(unicode)) {
				leaf.edit(message, unicode);
			}
		}
		
	}


	public static HashMap<User, OptionEmbed> getAllOptionCommands() {
		return allOptionCommands;
	}


	public static void setAllOptionCommands(HashMap<User, OptionEmbed> allOptionCommands) {
		OptionEmbed.allOptionCommands = allOptionCommands;
	}


	private Message refreshMessage() {
		return message.getTextChannel().getMessageById(message.getId()).complete();
	}


}