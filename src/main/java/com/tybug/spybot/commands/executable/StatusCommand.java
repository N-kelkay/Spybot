package com.tybug.spybot.commands.executable;

import java.util.ArrayList;
import java.util.HashMap;

import com.tybug.spybot.SpybotUtils;
import com.tybug.spybot.commands.ExecutableCommand;
import com.tybug.spybot.database.DBFunctions;
import com.tybug.spybot.util.BotUtils;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class StatusCommand extends ExecutableCommand {

	ArrayList<String> errors = new ArrayList<String>();
	
	
	public StatusCommand(MessageReceivedEvent event) {
		super(event);
	}

	@Override
	public void execute() {
		if(clearance > 1) {
			BotUtils.sendMessage(textChannel, BotUtils.createEmbed(SpybotUtils.DENY_COMMAND));
			return;
		}

		HashMap<String, String> tasks = new HashMap<String, String>();
		tasks.put("Channel perms", "");
		tasks.put("Color roles", "");

		Message m = textChannel.sendMessage(BotUtils.createEmbed("Status check in progress")).complete();
		for(String s : tasks.keySet()) {
			
			
			switch (s) {
			case "Channel perms":
				tasks.put(s, checkChannelPerms());
				break;
				
			case "Color roles":
				tasks.put(s, checkColorRoles());
				break;
			default:
				return;
			}
			
			m.editMessage(getProgressEmbed(tasks)).queue();
		}
	}



	private String checkChannelPerms() {
		StringBuilder sb = new StringBuilder("I cannot send messages in the following channels: ");
		boolean error = false;
		for(TextChannel c : guild.getTextChannels()) {
			if(!c.canTalk()) {
				sb.append(c.getAsMention() + ", ");
				error = true;
			}
		}
		if(error) {
			errors.add(sb.substring(0, sb.toString().length() - 2));
			return SpybotUtils.EMOJI_WARNING;
		} else {
			return guild.getEmoteById(SpybotUtils.EMOTE_CHECK).getAsMention();
		}
	}
	
	private String checkColorRoles() {
		
		for(Member m : guild.getMembers()) {
			Role r = BotUtils.getColorRole(m);
			if(r == null) continue; //no color role
			System.out.println("Checked " + m.getEffectiveName() + ". Color role name: " + r.getName());

			//Store the color role if it's new, update if it's not. 
			//It still udpates it but the update has no effect if the "new" color role is identical to the old one
			DBFunctions.addOrUpdateColorRole(m.getUser().getId(), r.getId());
			System.out.println("Updated DB entry\n");
		}
		
		return guild.getEmoteById(SpybotUtils.EMOTE_CHECK).getAsMention();
	}
	
	



	private MessageEmbed getProgressEmbed(HashMap<String, String> tasks) {
		StringBuilder sb = new StringBuilder();
		tasks.keySet().stream().forEach(s -> sb.append(s + "...      	  " + tasks.get(s) + "\n"));
		return BotUtils.createEmbed("Status check in progress", sb.toString());
	}

}
