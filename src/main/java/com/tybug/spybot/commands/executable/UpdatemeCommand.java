package com.tybug.spybot.commands.executable;

import com.tybug.spybot.commands.ExecutableCommand;
import com.tybug.spybot.database.DBFunctions;
import com.tybug.spybot.util.BotUtils;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class UpdatemeCommand extends ExecutableCommand {

	public UpdatemeCommand(MessageReceivedEvent event) {
		super(event);
	}

	@Override
	public void execute() {
		if(args.equals("")) { //no arg given
			System.out.println(DBFunctions.getUpdatePreference(user.getId()));
			if(!DBFunctions.updateSquadExists(user.getId()) || DBFunctions.getUpdatePreference(user.getId()).equals("0")) {
				BotUtils.sendMessage(textChannel, BotUtils.createEmbed("Please specify what types of updates you would like to be notified for!"
						, "See also: !help updateMe"));
				return;
			}
			DBFunctions.setUpdatePreference(user.getId(), 0);
			BotUtils.sendMessage(textChannel, BotUtils.createEmbed("You will no longer recieve a pm with every update"));
			return;
		}

		int arg;
		try{
			arg = Integer.parseInt(args);
		} catch (NumberFormatException e) {
			BotUtils.sendMessage(textChannel, BotUtils.createEmbed("Please give either 1, 2, or 3 in your command!", "See also: !help updateMe"));
			return;
		}
		if(arg > 3 || arg < 1) {
			BotUtils.sendMessage(textChannel, BotUtils.createEmbed("Please give either 1, 2, or 3 in your command!", "See also: !help updateMe"));
			return;
		}
		if(!DBFunctions.updateSquadExists(user.getId())) {
			DBFunctions.addUpdateSquad(user.getId(), arg);
		} else {
			DBFunctions.setUpdatePreference(user.getId(), arg);
		}
		BotUtils.sendMessage(textChannel, BotUtils.createEmbed("You will now be notified on the updates you specified!"));
	}

}
