package com.tybug.spybot.commands.executable;

import com.tybug.spybot.commands.ExecutableCommand;
import com.tybug.spybot.util.BotUtils;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class EmbedDescriptionCommand extends ExecutableCommand {

	public EmbedDescriptionCommand(MessageReceivedEvent event) {
		super(event);
	}

	@Override
	public void execute() {
		BotUtils.makeOrGetBuilder(user).getBuilder().setDescription(args);
		BotUtils.check(message);
	}

}
