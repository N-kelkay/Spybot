package com.tybug.spybot.commands.executable;

import com.tybug.spybot.commands.ExecutableCommand;
import com.tybug.spybot.util.BotUtils;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ServerinfoCommand extends ExecutableCommand {

	public ServerinfoCommand(MessageReceivedEvent event) {
		super(event);
	}

	@Override
	public void execute() {
		BotUtils.sendMessage(textChannel, BotUtils.createServerInfoEmbed(guild));
	}

}
