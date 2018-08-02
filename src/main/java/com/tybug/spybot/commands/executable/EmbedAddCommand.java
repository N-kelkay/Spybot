package com.tybug.spybot.commands.executable;

import com.tybug.spybot.commands.ExecutableCommand;
import com.tybug.spybot.util.BotUtils;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class EmbedAddCommand extends ExecutableCommand {

	public EmbedAddCommand(MessageReceivedEvent event) {
		super(event);
	}

	@Override
	public void execute() {
		String[] parts = args.split("\\|");
		try {
			BotUtils.makeOrGetBuilder(user).getBuilder().addField(parts[0], parts[1], true);
		} catch (IndexOutOfBoundsException e) {
			BotUtils.sendMessage(textChannel, BotUtils.createEmbed("Please specify both a title and a description, delimited by | (pipe)!"));
			return;
		}
		BotUtils.check(message);
		return;
	
	}

}
