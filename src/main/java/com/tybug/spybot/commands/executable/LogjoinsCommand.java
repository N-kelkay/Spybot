package com.tybug.spybot.commands.executable;

import com.tybug.spybot.SpybotUtils;
import com.tybug.spybot.commands.ExecutableCommand;
import com.tybug.spybot.database.DBFunctions;
import com.tybug.spybot.util.BotUtils;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class LogjoinsCommand extends ExecutableCommand {

	public LogjoinsCommand(MessageReceivedEvent event) {
		super(event);
	}

	@Override
	public void execute() {
		if(clearance > 1) {
			BotUtils.sendMessage(textChannel, BotUtils.createEmbed(SpybotUtils.DENY_COMMAND));
			return;
		}
		guild.getMembers().forEach(m -> DBFunctions.addJoin(m.getUser().getId(), m.getJoinDate().toString()));
		BotUtils.check(message);
	}

}
