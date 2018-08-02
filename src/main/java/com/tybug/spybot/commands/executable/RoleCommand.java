package com.tybug.spybot.commands.executable;

import java.awt.Color;

import com.tybug.spybot.SpybotUtils;
import com.tybug.spybot.commands.ExecutableCommand;
import com.tybug.spybot.database.DBFunctions;
import com.tybug.spybot.util.BotUtils;

import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class RoleCommand extends ExecutableCommand {

	public RoleCommand(MessageReceivedEvent event) {
		super(event);
	}

	@Override
	public void execute() {
		Color c = null;
		try {
			c = BotUtils.hex2Rgb(args);
		} catch (IndexOutOfBoundsException | NumberFormatException e) { //IOOB or bad input exception
			BotUtils.sendMessage(textChannel, BotUtils.createEmbed("Please give an appropriate, 6 digit hex code for the color!",
					"See also: " + SpybotUtils.BOT_PREFIX + "help role"));
			return;
		}
		String roleId = DBFunctions.getColorRole(user.getId());
		if(roleId != null) { //currently has a color role
			guild.getRoleById(roleId).delete().queue();
			DBFunctions.removeColorRole(user.getId()); // Delete from discord and remove from db
		}
		Role r = gc.createRole().setColor(c).setName(user.getName()).complete();
		int pos = guild.getRoleById(SpybotUtils.ROLE_MOD).getPosition() - 1; //The spot right below mod
		gc.modifyRolePositions().selectPosition(r).moveTo(pos).queue(); //so it displays their color instead of Magnet Nerd
		gc.addSingleRoleToMember(member, r).queue();
		DBFunctions.addColorRole(user.getId(), r.getId()); //Add to the db so it will get removed when they make a new one
		BotUtils.check(message);

	}

}
