package com.tybug.spybot.commands.executable;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

import com.tybug.spybot.commands.ExecutableCommand;
import com.tybug.spybot.database.DBFunctions;
import com.tybug.spybot.util.BotUtils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ChangelogCommand extends ExecutableCommand {

	public ChangelogCommand(MessageReceivedEvent event) {
		super(event);
	}

	@Override
	public void execute() {
		List<String> changelog = BotUtils.getChangeLog();
		StringBuilder sb = new StringBuilder();
		for(String s : changelog) {
			sb.append(s);
		}
		
		String contents = sb.toString();
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.addBlankField(false);
		builder.setTitle("Changelog – Currently running " + DBFunctions.getCurrentVersion());
		changelog = changelog.stream().limit(3).collect(Collectors.toList()); //only take the most recent 3 segments (roadmap + 2 updates)
		for(String s : changelog) {
			int index = changelog.indexOf(s);
			if(index == 0) {
				builder.addField("Roadmap",  s, false);
				builder.addBlankField(false);
				continue;
			}
			builder.addField(s.split("\n")[0], s.replace(s.split("\n")[0], ""), false);
			if(index != changelog.size() - 1) {
				builder.addBlankField(false); //Add an extra field for every one but the last one, looks weird for that
			}
		}
		builder.setColor(Color.GREEN);
		BotUtils.sendMessage(textChannel, builder.build());
		
		Message message = new MessageBuilder().append("Full Changelog:").build();
		textChannel.sendFile(contents.getBytes(), "Changelog.txt", message).queue();
	}
	
}
