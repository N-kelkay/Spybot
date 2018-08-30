package com.tybug.spybot.commands.executable;

import com.tybug.spybot.commands.ExecutableCommand;
import com.tybug.spybot.util.BotUtils;

import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class EmbedSendCommand extends ExecutableCommand {

	public EmbedSendCommand(MessageReceivedEvent event) {
		super(event);
	}

	@Override
	public void execute() {
		MessageEmbed toSend = BotUtils.makeOrGetBuilder(user).getBuilder().build();
		if(toSend == null) {
			BotUtils.sendMessage(textChannel, BotUtils.createEmbed("Please provide at least a title!", "See also: !help embed title"));
			return;
		}
		
		if(message.getMentionedChannels().size() < 1) {
			BotUtils.sendMessage(textChannel, BotUtils.createEmbed("Please provide a text channel in your command!"));
			return;
		}
		message.getMentionedChannels().forEach(c -> BotUtils.sendMessage(c, toSend));
		BotUtils.check(message);

	}

}
