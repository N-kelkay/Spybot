package com.tybug.spybot.commands.executable;

import java.awt.Color;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.tybug.spybot.SpybotUtils;
import com.tybug.spybot.commands.ExecutableCommand;
import com.tybug.spybot.util.BotUtils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class RemindmeCommand extends ExecutableCommand {

	public RemindmeCommand(MessageReceivedEvent event) {
		super(event);
	}

	@Override
	public void execute() {

		double hours = 0;
		try {
			hours = Double.parseDouble(args.split(" ")[0]);
		} catch(IndexOutOfBoundsException e) {
			BotUtils.sendMessage(textChannel, BotUtils.createEmbed("Please specify an amount of time in hours!",
					"See also: " + SpybotUtils.BOT_PREFIX + "help remindme"));
			return;
		} catch(NumberFormatException e) {
			BotUtils.sendMessage(textChannel, BotUtils.createEmbed("Please provide a valid number in hours",
					"See also: " + SpybotUtils.BOT_PREFIX + "help remindme"));
			return;
		}
		if(hours > 100 && clearance > 1) {
			BotUtils.sendMessage(textChannel, BotUtils.createEmbed("Remindme is built for short term reminders. Anything longer than 100 hours is not allowed. "
					+ "Please contact <@" + SpybotUtils.AUTHOR + "> if you would like a longer remind time"
					, "See also: " + SpybotUtils.BOT_PREFIX + "help remindme"));
			return;
		}

		final TextChannel c = message.getMentionedChannels().get(0);
		String contentRaw = message.getContentRaw();
		//Makes mentions actually work when the remindme is sent
		for(TextChannel c2 : message.getMentionedChannels()) {
			contentRaw = contentRaw.replaceAll("#" + c2.getName(), c2.getAsMention());
		}
		for(User u : message.getMentionedUsers()) {
			contentRaw = contentRaw.replaceAll("@" + u.getName(), u.getAsMention());
		}

		for(Member m : message.getMentionedMembers()) {
			contentRaw = contentRaw.replaceAll("@" + m.getEffectiveName(), m.getAsMention());
		}
		for(Role r : message.getMentionedRoles()) {
			contentRaw = contentRaw.replaceAll("@" + r.getName(), r.getAsMention());
		}
		String parts[] = contentRaw.split(" ");

		StringBuilder sb = new StringBuilder();
		for(int i = 3; i < parts.length; i++) {
			sb.append(parts[i] + " ");
		}

		long seconds = (long) (hours * 60.0 * 60.0);
		final double hours2 = hours;
		BotUtils.sendMessage(textChannel, BotUtils.createEmbed("Successfully scheduled a reminder in " + seconds + " seconds"));

		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.schedule(() -> {
			c.sendMessage(sb.toString()).complete().editMessage(new EmbedBuilder().setTitle("Automatic reminder")
					.setDescription("Requested " + hours2 + " hours ago by " + 
							user.getAsMention()).setColor(Color.GREEN).build()).queue();
		}, seconds, TimeUnit.SECONDS);
	
	}

}
