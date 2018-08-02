package com.tybug.spybot.commands.executable;

import com.tybug.spybot.commands.ExecutableCommand;
import com.tybug.spybot.util.BotUtils;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class EmbedTitleCommand extends ExecutableCommand {

	public EmbedTitleCommand(MessageReceivedEvent event) {
		super(event);
	}

	@Override
	public void execute() {
		BotUtils.makeOrGetBuilder(user).getBuilder().setTitle(args);
		BotUtils.check(message);
	}

}
