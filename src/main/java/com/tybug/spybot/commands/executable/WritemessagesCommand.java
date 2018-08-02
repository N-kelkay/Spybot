package com.tybug.spybot.commands.executable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tybug.spybot.SpybotUtils;
import com.tybug.spybot.commands.ExecutableCommand;
import com.tybug.spybot.database.DBFunctions;
import com.tybug.spybot.util.BotUtils;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class WritemessagesCommand extends ExecutableCommand {

	public WritemessagesCommand(MessageReceivedEvent event) {
		super(event);
	}

	@Override
	public void execute() {

		String modifier;
		try {
			modifier = args.replaceAll("-", "");
		} catch (NullPointerException e) {
			BotUtils.sendMessage(textChannel, "Please add a modifier!");
			return;
		}

		if(clearance > 1) {
			BotUtils.sendMessage(textChannel, "Temporarily allowed only for clearance 1 or lower, as the feature is in beta.");
			return;
		}
		if(message.getMentionedUsers().size() < 1 && !modifier.equals("g") && !modifier.equals("gh") && !modifier.equals("hg")) {
			BotUtils.sendMessage(textChannel, "You must mention at least one user in your command!");
			return;
		}


		List<String> timestamps = new ArrayList<String>();
		Map<String, Integer> messagesPerDate = new HashMap<String, Integer>();

		switch(modifier) {

		case "hg": 
		case "gh": { //hourly
			timestamps = DBFunctions.getAllTimestamps();
			Map<String, Integer> messagesPerHour = new HashMap<String, Integer>();
			SpybotUtils.setUpHourMap(messagesPerHour);

			for(String s : timestamps) {
				String formatted = SpybotUtils.parseHour(s);
				if(messagesPerHour.containsKey(formatted)) {
					messagesPerHour.put(formatted, messagesPerHour.get(formatted) + 1);
				} else {
					messagesPerHour.put(formatted, 1);
				}
			}

			for(String s : messagesPerHour.keySet()) {
				BotUtils.appendLog("messages", s + "," + messagesPerHour.get(s), true);
			}
			break;
		}
		//TODO add parseAndAppend function or similar to reduce duplication
		case "g": { //global
			timestamps = DBFunctions.getAllTimestamps();
			try {
				messagesPerDate = SpybotUtils.getEmptyMap();
			} catch (ParseException e) {
				e.printStackTrace();
			}


			for(String s : timestamps) {
				String formatted = SpybotUtils.parseDate(s);
				if(messagesPerDate.containsKey(formatted)) {
					messagesPerDate.put(formatted, messagesPerDate.get(formatted) + 1);
				} else {
					messagesPerDate.put(formatted, 1);
				}
			}
			for(String s : messagesPerDate.keySet()) {
				System.out.println(s + "," + messagesPerDate.get(s));
				BotUtils.appendLog("messages", s + "," + messagesPerDate.get(s), true);
			}
			break;
		}
		case "w": { //week
			for(User u : message.getMentionedUsers()) {
				timestamps = DBFunctions.getTimestamps(u.getId());
				//month/day/year (padded) :: Number of messages
				try {
					messagesPerDate = SpybotUtils.getEmptyMap();
				} catch (ParseException e) {
					e.printStackTrace();
				}


				for(String s : timestamps) {
					String formatted = SpybotUtils.parseDate(s);
					if(messagesPerDate.containsKey(formatted)) {
						messagesPerDate.put(formatted, messagesPerDate.get(formatted) + 1);
					} else {
						messagesPerDate.put(formatted, 1);
					}
				}
				for(String s : messagesPerDate.keySet()) {
					System.out.println(s + "," + messagesPerDate.get(s));
					BotUtils.appendLog("messages", s + "," + messagesPerDate.get(s), true);
				}
			}
			break;
		}
		default:
			for(User u : message.getMentionedUsers()) {
				timestamps = DBFunctions.getTimestamps(u.getId());
				//month/day/year (padded) :: Number of messages
				try {
					messagesPerDate = SpybotUtils.getEmptyMap();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				for(String s : timestamps) {
					String formatted = SpybotUtils.parseDate(s);
					if(messagesPerDate.containsKey(formatted)) {
						messagesPerDate.put(formatted, messagesPerDate.get(formatted) + 1);
					} else {
						messagesPerDate.put(formatted, 1);
					}
				}
				for(String s : messagesPerDate.keySet()) {
					System.out.println(s + "," + messagesPerDate.get(s));
					BotUtils.appendLog("messages", s + "," + messagesPerDate.get(s), true);
				}
			}
			break;
		}

		BotUtils.check(message);

	}

}
