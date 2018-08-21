package com.tybug.spybot.commands.executable;

import com.tybug.spybot.SpybotUtils;
import com.tybug.spybot.commands.ExecutableCommand;
import com.tybug.spybot.database.DBFunctions;
import com.tybug.spybot.util.BotUtils;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class NameCommand extends ExecutableCommand {

	public NameCommand(MessageReceivedEvent event) {
		super(event);
	}

	@Override
	public void execute() {
		if(clearance > 1) {
			BotUtils.sendMessage(textChannel, BotUtils.createEmbed(SpybotUtils.DENY_COMMAND));
			return;
		}
		String[] parts = args.split(" ");
		String name = "";
		for(int i = 1; i < parts.length; i++) {
			name += parts[i] + " ";
		}
		DBFunctions.setName(parts[0], name);
		SpybotUtils.updateNamePin(jda.getGuildById(SpybotUtils.GUILD_BLAIR));
		SpybotUtils.check(message);
	}

}
