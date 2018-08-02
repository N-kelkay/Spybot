package com.tybug.spybot.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * An executable command. Each command will have their own implementation of how it should be executed
 * @author Liam DeVoe
 *
 */
public abstract class ExecutableCommand extends Command {

	public ExecutableCommand(MessageReceivedEvent event) {
		super(event);
	}

	//Will always act upon information contained in the message JDA event passed in the cosntructor
	public abstract void execute();
}
