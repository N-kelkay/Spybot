package com.tybug.spybot.commands.executable;

import java.util.List;

import com.tybug.spybot.SpybotUtils;
import com.tybug.spybot.commands.ExecutableCommand;
import com.tybug.spybot.util.BotUtils;
import com.tybug.spybot.util.HistoryLogger;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class LoghistoryCommand extends ExecutableCommand {

	public LoghistoryCommand(MessageReceivedEvent event) {
		super(event);
	}

	@Override
	public void execute() {
		if(clearance > 1) {
			BotUtils.sendMessage(textChannel, BotUtils.createEmbed(SpybotUtils.DENY_COMMAND));
			return;
		}

		//All the channels, or the ones they mentioned if they did mention any
		List<TextChannel> loadFrom = (message.getMentionedChannels().size() > 0) ? (message.getMentionedChannels()) : (guild.getTextChannels());
		HistoryLogger logger = new HistoryLogger(textChannel, loadFrom);
		logger.loadAllMessages();
	}

}
