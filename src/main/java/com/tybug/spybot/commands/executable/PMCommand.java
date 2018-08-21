package com.tybug.spybot.commands.executable;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tybug.spybot.SpybotUtils;
import com.tybug.spybot.commands.ExecutableCommand;
import com.tybug.spybot.commands.WaitingState;
import com.tybug.spybot.database.DBFunctions;
import com.tybug.spybot.util.BotUtils;
import com.tybug.spybot.util.Pending;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class PMCommand extends ExecutableCommand {

	public PMCommand(MessageReceivedEvent event) {
		super(event);
	}

	@Override
	public void execute() {
		String prefix = SpybotUtils.BOT_PREFIX;
		HashMap<String, Pending> pending = Pending.getAll();

		//If we're waiting on a pm from them, specifically of type name
		if(pending.containsKey(user.getId()) && pending.get(user.getId()).getWaitingState().equals(WaitingState.NAME)) {
			DBFunctions.addName(user.getId(), message.getContentDisplay()); //Add their message (presumably their name) to the pin db
			Pending.removePending(user.getId()); //Don't await messages from them anymore
			BotUtils.sendPrivateMessage(user, BotUtils.createEmbed("Thank you!"));
			SpybotUtils.updateNamePin(jda.getGuildById(SpybotUtils.GUILD_BLAIR)); //update immediately, don't wait for another nick change
		}


		if (user.getId().equals(SpybotUtils.AUTHOR)) {
			if(message.getContentDisplay().startsWith(prefix + "pm")) { //so I can spook people and pm them through the bot
				String[] parts = message.getContentRaw().split(" ");
				String id = parts[1];
				BotUtils.sendPrivateMessage(jda.getUserById(id), message.getContentRaw().replaceAll(prefix + "pm " + id, ""));
				//send everything but the id and the command
				BotUtils.check(message);
			}

			//pushes an update to users in trusted list who have also voluntarily done !updateMe
			if(message.getContentDisplay().startsWith("!update")) {
				String[] parts = message.getContentDisplay().replaceFirst	("!update ", "").split("```"); //``` being my delimiter for things
				String fullVersion = parts[0];
				String versionNumber = fullVersion.split(" ")[1];
				//					String versionDate = fullVersion.split(" ")[0];
				List<String> updateContent = new ArrayList<String>();
				try {
					for(int i = 1; i < parts.length; i++) {
						updateContent.add(parts[i]);
					}
				} catch (IndexOutOfBoundsException e) {
					BotUtils.sendPrivateMessage(user, "Make sure to deliminate with ```!");
					return;
				}

				//Make the two embeds, one with a link to the source code, one without
				EmbedBuilder builder = new EmbedBuilder();
				builder.setTitle("New Update!");
				builder.setColor(Color.GREEN);
				for(int i = 0; i < updateContent.size(); i++) { //don't do the last one
					builder.addField(fullVersion, updateContent.get(i),
							false);
				}
				builder.addField("Source Code", "https://drive.google.com/open?id=1lDh9u4mxOe7XYHekDxcsavH66EJix6ck", false);
				
				MessageEmbed updateWithSource = builder.build();
				
				
				builder.clear();
				builder.setTitle("New Update!");
				builder.setColor(Color.GREEN);
				for(int i = 0; i < updateContent.size(); i++) { //don't do the last one
					builder.addField(fullVersion, updateContent.get(i),
							false);
				}
				MessageEmbed updateWithoutSource = builder.build();


				String[] newVersionKeys = versionNumber.split("\\."); //Just splitting on "." would split on every single character
				String[] oldVersionKeys = DBFunctions.getCurrentVersion().split("\\.");
				int updateType = 0; //1 (major), 2 (minor), 3 (bug/polish)

				if(!newVersionKeys[0].equals(oldVersionKeys[0])) {
					updateType = 1;
				} else if(!newVersionKeys[1].equals(oldVersionKeys[1])) {
					updateType = 2;
				} else if(!newVersionKeys[2].equals(oldVersionKeys[2])) {
					updateType = 3;
				} else {
					BotUtils.sendPrivateMessage(user, BotUtils.createEmbed("The version hasn't changed from last time. Aborting"));
					return;
				}

				StringBuilder sb = new StringBuilder("Sent pms to the following people:\n");

				Map<String, String> updateSource = DBFunctions.getUpdateSource();
				for(String s : updateSource.keySet()) {
					int pref = Integer.parseInt(updateSource.get(s)); //3 for all updates
					if(pref >= updateType) { //3 will encompase all of them here
						BotUtils.sendPrivateMessage(jda.getUserById(s), updateWithSource);
						sb.append(jda.getUserById(s).getAsMention() + " (with source)\n");
					}
				}
				
				
				sb.append("\n");
				
				
				Map<String, String> updateNoSource = DBFunctions.getUpdateNoSource();
				for(String s : updateNoSource.keySet()) {
					int pref = Integer.parseInt(updateNoSource.get(s)); //3 for all updates
					if(pref >= updateType) { //3 will encompase all of them here
						BotUtils.sendPrivateMessage(jda.getUserById(s), updateWithoutSource);
						sb.append(jda.getUserById(s).getAsMention() + "\n");
					}
				}
				DBFunctions.setCurrentVersion(versionNumber);
				BotUtils.sendPrivateMessage(jda.getUserById(SpybotUtils.AUTHOR), sb.toString());
			}
		}

		//------- Forwards all dms sent to the bot to me (author) -----------------
		String send = jda.getGuildById(SpybotUtils.GUILD_BLAIR).getMember(user).getAsMention() + ":\n";
		send += message.getContentRaw();
		for(Attachment a : message.getAttachments()) {
			send += a.getUrl() + "\n";
		}
		BotUtils.sendPrivateMessage(jda.getUserById(SpybotUtils.AUTHOR), user.getId().equals(SpybotUtils.AUTHOR) ? "" : send); //only send if it's not the author	
		return;
	}

}
