package com.tybug.spybot.commands.executable;

import java.io.IOException;

import com.tybug.spybot.SpybotUtils;
import com.tybug.spybot.commands.ExecutableCommand;
import com.tybug.spybot.util.BotUtils;

import net.dv8tion.jda.core.entities.Icon;
import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class EmojiCommand extends ExecutableCommand {

	public EmojiCommand(MessageReceivedEvent event) {
		super(event);
	}

	@Override
	public void execute() {
		Attachment a = message.getAttachments().get(0);
		Icon icon = null;
		try {
			icon = a.getAsIcon();
		} catch (IOException e) {
			BotUtils.sendMessage(textChannel, "Error downloading file! Please contact <@" + SpybotUtils.AUTHOR + ">.");
			return;
		}
		String name = a.getFileName().split("\\.")[0]; //get rid of the file extension
		gc.createEmote(name, icon).complete();
		BotUtils.check(message);
	}

}
