package com.tybug.spybot.commands.executable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.tybug.spybot.SpybotUtils;
import com.tybug.spybot.commands.ExecutableCommand;
import com.tybug.spybot.commands.Timeout;
import com.tybug.spybot.util.BotUtils;

import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class TimeoutCommand extends ExecutableCommand {

	
	public TimeoutCommand(MessageReceivedEvent event) {
		super(event);
	}
	
	@Override
	public void execute() {
		double hours = 0;
		try {
			hours = Double.parseDouble(this.getText());
		} catch(IndexOutOfBoundsException e) {
			BotUtils.sendMessage(textChannel, BotUtils.createEmbed("Please specify an amount of time in hours!",
					"See also: " + SpybotUtils.BOT_PREFIX + "help timeout"));
			return;
		} catch(NumberFormatException e) {
			BotUtils.sendMessage(textChannel, BotUtils.createEmbed("Please provide a valid number in hours",
					"See also: " + SpybotUtils.BOT_PREFIX + "help timeout"));
			return;
		}
		if(hours > 100) {
			BotUtils.sendMessage(textChannel, BotUtils.createEmbed("Timeouts longer than 100 hours are not allowed. Please contact <@" + SpybotUtils.AUTHOR + "> if"
					+ " you would like a longer timeout.",
					"See also: " + SpybotUtils.BOT_PREFIX + "help timeout"));
			return;
		}
		final Role hw = jda.getRoleById(SpybotUtils.ROLE_DOING_HOMEWORK);
		BotUtils.sendMessage(textChannel, BotUtils.createEmbed("Successfully imposed timeout for " + hours + " hours", 
				"You will not be able to see any channels during this"
						+ " time. The duration will not be lowered, so choose your timeout with caution."));
		gc.addSingleRoleToMember(member, hw).queue();
		if(user.getId().equals(SpybotUtils.AUTHOR)) {
			gc.removeSingleRoleFromMember(member, jda.getRoleById(SpybotUtils.ROLE_MOD)).queue();
			//I also need mod removed from me or admin perms still lets me see channels
		}
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		final Timeout timeout = new Timeout(member);
		scheduler.schedule(() -> {
			gc.removeSingleRoleFromMember(member, hw).queue();
			if(user.getId().equals(SpybotUtils.AUTHOR)) {
				gc.addSingleRoleToMember(member, jda.getRoleById(SpybotUtils.ROLE_MOD)).queue();
			}

			StringBuilder sb = new StringBuilder();
			for(String id : timeout.getMessagesSent().keySet()) {
				sb.append(timeout.getMessagesSent().get(id) ? "<#" + id + ">\n" : ""); //if a message was sent there, add it to the list
			}
			BotUtils.sendPrivateMessage(member.getUser(), BotUtils.createEmbed("Your self imposed timeout is up!"
					, "**These channels have had activity while you were away:**\n\n" + sb.toString())); //notify the user
			Timeout.removeTimout(timeout); //remove the timeout so it doesn't keep getting triggered by onMessageRecieved
		}, (long) hours, TimeUnit.HOURS);

	}
	


	
}
