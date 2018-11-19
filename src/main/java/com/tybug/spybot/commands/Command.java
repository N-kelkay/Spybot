package com.tybug.spybot.commands;


import java.util.EnumSet;

import com.tybug.spybot.SpybotUtils;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildController;
/**
 * Contains information about a string - if it is a valid command, the text of the command (not containing the command or bot prefix itself) and the
 * type of command as a CommandType
 * @see CommandType
 * @author Liam DeVoe
 */
public class Command {
	private boolean isValid;
	
	//for easy referencing in copy pasted commands, plus lots of this.get looks ugly
	protected JDA jda;
	protected Guild guild;
	protected GuildController gc;
	protected TextChannel textChannel;
	protected User user;
	protected Member member;
	protected CommandType command;
	protected Message message;
	protected String args;
	protected int clearance;
	

	
	public Command(MessageReceivedEvent event) { // Yuck..cache references everywhere... TODO restructure the fuck out of this..maybe time for a complete rewrite of entire bot
		Message m = event.getMessage();
		this.jda = event.getJDA();
		this.guild = event.getGuild();
		this.textChannel = event.getTextChannel();
		this.message = m;
		this.user = event.getAuthor();
		this.member = event.getMember();
		this.command = getCommandType(m.getContentRaw());
		
		
		
		if(event.isFromType(ChannelType.TEXT)) {
			this.clearance = SpybotUtils.getClearance(member);
			this.gc = this.guild.getController();
		}
		this.isValid = (this.command.equals(CommandType.INVALID) ? false : true); //Invalid if the CommandType is invalid, otherwise not
		this.args = m.getContentDisplay().replaceFirst("(?i)" + SpybotUtils.BOT_PREFIX + this.command.toString() + "\\s*", ""); //Match for either a trailing space or not
		//case insensitive, removes the command name and the leading space
		
		String[] parts = this.args.split(" ");
		if(parts.length < 2) {
			return;
		}
		if(parts[parts.length - 2].equals("AS") && this.clearance == 0) { //author only
			this.user = jda.getUserById(parts[parts.length - 1]);
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < parts.length - 2; i++) {
				sb.append(parts[i]);
			}
			this.args = sb.toString();
		}
	}
	public CommandType getCommand() {
		return command;
	}
	
	private static CommandType getCommandType(String command) {
		if(command.length() == 0) {
			return CommandType.INVALID;
		}
		if(!command.substring(0, 1).equals(SpybotUtils.BOT_PREFIX)) { //if the first character isn't the bot prefix, return invalid
			return CommandType.INVALID;
		}
		command = command.toUpperCase().replaceFirst(SpybotUtils.BOT_PREFIX, ""); //remove the prefix
		String[] parts = command.split(" ");
		String one = parts[0]; //only check first word
		String two = "INVALID";
		if(parts.length > 1) { //Always make it invalid unless the command is double worded
			two = command.split(" ")[0] + " " + command.split(" ")[1]; //check both words
		}
		
		EnumSet<CommandType> set = EnumSet.allOf(CommandType.class);
		for(CommandType c : set) {
			if(one.equals(c.toString()) || two.equals(c.toString())) {
				return c;
			}
		}
		
		return CommandType.INVALID;
	}	
	
	public boolean isValid() {
		return isValid;
	}
	public String getText() {
		return args;
	}
	public Message getMessage() {
		return message;
	}
	public JDA getJda() {
		return jda;
	}

}