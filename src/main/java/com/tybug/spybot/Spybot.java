package com.tybug.spybot;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.security.auth.login.LoginException;

import com.tybug.spybot.commands.Command;
import com.tybug.spybot.commands.CommandType;
import com.tybug.spybot.commands.ExecutableCommand;
import com.tybug.spybot.commands.SnowChecker;
import com.tybug.spybot.commands.Timeout;
import com.tybug.spybot.commands.WaitingState;
import com.tybug.spybot.commands.executable.ChangelogCommand;
import com.tybug.spybot.commands.executable.EmbedAddCommand;
import com.tybug.spybot.commands.executable.EmbedDescriptionCommand;
import com.tybug.spybot.commands.executable.EmbedResetCommand;
import com.tybug.spybot.commands.executable.EmbedSendCommand;
import com.tybug.spybot.commands.executable.EmbedTitleCommand;
import com.tybug.spybot.commands.executable.EmojiCommand;
import com.tybug.spybot.commands.executable.EquationCommand;
import com.tybug.spybot.commands.executable.LoghistoryCommand;
import com.tybug.spybot.commands.executable.LogjoinsCommand;
import com.tybug.spybot.commands.executable.PMCommand;
import com.tybug.spybot.commands.executable.RemindmeCommand;
import com.tybug.spybot.commands.executable.RoleCommand;
import com.tybug.spybot.commands.executable.ServerinfoCommand;
import com.tybug.spybot.commands.executable.StatsCommand;
import com.tybug.spybot.commands.executable.StatusCommand;
import com.tybug.spybot.commands.executable.TimeoutCommand;
import com.tybug.spybot.commands.executable.UpdatemeCommand;
import com.tybug.spybot.commands.executable.WritemessagesCommand;
import com.tybug.spybot.database.DBFunctions;
import com.tybug.spybot.util.OptionEmbed;
import com.tybug.spybot.util.Pending;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.role.RoleDeleteEvent;
import net.dv8tion.jda.core.events.user.UserTypingEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
/**
 * The main hub of the discord bot. Where events are handled, the bot instance itself is created, and most importantly commands are
 * interpreted and acted upon.
 * @author Liam DeVoe
 */
public class Spybot extends ListenerAdapter
{
	public static boolean pause = false;

	public static void main(String[] args) {
		
		BufferedReader br;
		String token = null;
		
		try {
			br = new BufferedReader(new FileReader("token.txt"));
			token = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		try
		{
			JDA jda = new JDABuilder(AccountType.BOT) //boot up the bot instance
					.setToken(token) //pass the token (effectively the password)
					.addEventListener(new Spybot()) //for messageRecieved etc
					.setGame(Game.playing("Created by Liam")) //set "Playing..." display message
					.buildBlocking();  //build the whole thing, blocking guarantees it will be completely loaded vs async which does not
			SnowChecker snow = new SnowChecker(jda); //start up the snow checker at the beginning
			snow.start();
		}
		catch (LoginException | InterruptedException e) {
			e.printStackTrace();
		}
		

	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		//If it's neither a pm nor a guild message, ignore. Also ignore bot messages since bad recursion stuff
		if(!(event.isFromType(ChannelType.PRIVATE) || event.isFromType(ChannelType.TEXT)) || event.getAuthor().isBot()) return;
		
		if(event.getMessage().getAuthor().getId().equals(SpybotUtils.AUTHOR) && event.getMessage().getContentDisplay().equals("!unpause")) {
			pause = false;
			return;
		}
		if(pause) return; //hell naw biatch
		if(event.isFromType(ChannelType.PRIVATE)) {
			ExecutableCommand executable = new PMCommand(event);
			executable.execute();
		}

		
		Member member = event.getMember();
		TextChannel textChannel = event.getTextChannel();
		
		if(member == null) return; //Happens sometimes, not really sure why. Webhooks maybe?


		//So the user gets told messages were sent in that channel when their timeout is up
		Timeout.getAllTimeouts().forEach(t -> t.setMessagesSent(textChannel.getId(), true));


		Command command = new Command(event);
		if(!command.isValid()) return; //No need to go through every case in the switch if the message isn't a command


		//Surround every switch with {} so they each have a different scope and variable names can be reused
		switch(command.getCommand()) {
		//help first
		case HELP: {
			switch(command.getText().toUpperCase()) {
			case "HELP":
				SpybotUtils.sendMessage(textChannel, SpybotUtils.createHelpEmbed("help"
						, "Gives information as well as usage of the given command.",
						"!help [command]",
						"!help remindme"));
				return;

			case "ROLE":
				SpybotUtils.sendMessage(textChannel, SpybotUtils.createHelpEmbed("role",
						"Assigns you a role with the color specified.\n" +
								"If you use the command while you still have a bot-created color role, it will be deleted"
								+ " and overriden by your newly chosen role and color.", 
								"!role [#color]", 
						"!role #de13fa"));
				return;
			case "TIMEOUT":
				SpybotUtils.sendMessage(textChannel, SpybotUtils.createHelpEmbed("timeout"
						,"Gives you a role that prevents you from reading any messages in this server for the given amount of time."
								+ " Feel free to use decimal hours.",
								"!timeout [hours]", "!timeout .75"));
				return;

			case "REMINDME":
				SpybotUtils.sendMessage(textChannel, SpybotUtils.createHelpEmbed("remindme"
						, "Sends a message in the given amount of hours to the given channel. Feel free to use decimal hours.",
						"!remindme [hours] [#channel] [message]",
						"!remindme 48.71 <#" + SpybotUtils.CHANNEL_GENERAL + "> This is a message from the future"));
				return;

			case "OPTIONS":
				SpybotUtils.sendMessage(textChannel, SpybotUtils.createHelpEmbed("options"
						, "Configurable settings for a variety of commands and features, both user specific and server wide.",
						"!options",
						"!options"));
				return;
			case "UPDATEME":
				SpybotUtils.sendMessage(textChannel, SpybotUtils.createHelpEmbed("updateme"
						, "Adds you to the list of people who recieve a pm when the bot is updated.\n Giving it an argument of 1 will make it only dm you"
								+ " for major updates, 2 for major and minor, and 3 for major, minor, and bug/polish (major.minor.bug, ex 2.5.4)"
								+ "\nTo take yourself off this list, simply run !updateMe with no argument."
								+ "\n If you have special permission from <@" + SpybotUtils.AUTHOR + ">, you will also recieve the source code with every update.",
								"!updateMe [arg]",
						"!updateMe 2"));
				return;

			case "CHANGELOG":
				SpybotUtils.sendMessage(textChannel, SpybotUtils.createHelpEmbed("changelog",
						"Sends the current changelog to the channel, with the roadmap at the top.",
						"!changelog",
						""));
				return;

			case "SERVERINFO":
				SpybotUtils.sendMessage(textChannel, SpybotUtils.createHelpEmbed("serverinfo"
						, "Gives server-wide statistics, such as messages sent per channel and top 5 users by messages sent.",
						"!serverinfo",
						""));
				return;

			case "STATS":
				SpybotUtils.sendMessage(textChannel, SpybotUtils.createHelpEmbed("stats"
						, "Gives statistics for a particular user, or yourself if no user is specified",
						"!serverinfo\n!serverinfo [@user]\n!serverinfo [id]",
						"!serverinfo <@" + SpybotUtils.AUTHOR + ">\n!serverinfo " + SpybotUtils.AUTHOR));
				return;

			case "EMBED_ADD":
				SpybotUtils.sendMessage(textChannel, SpybotUtils.createHelpEmbed("embed add"
						, "Adds a field to the embed with the given title and body, delimited by | (pipe)",
						"!embed add [title] | [body]",
						"!embed add Hello there | How are you today?"));
				return;

			case "EMBED_TITLE":
				SpybotUtils.sendMessage(textChannel, SpybotUtils.createHelpEmbed("embed title"
						, "Sets the title/name of the embed",
						"!embed title [title]",
						"!embed title Read me!"));
				return;

			case "EMBED_DESCRIPTION":
				SpybotUtils.sendMessage(textChannel, SpybotUtils.createHelpEmbed("embed description"
						, "Sets the description of the embed (The text of the default/very first field)",
						"!embed description [description]",
						"!embed title This is an embed"));
				return;

			case "EMBED_SEND":
				SpybotUtils.sendMessage(textChannel, SpybotUtils.createHelpEmbed("embed send"
						, "Sends the embed as it currently is to the given channel",
						"!embed send [#channel]",
						"!embed send <#" + SpybotUtils.CHANNEL_GENERAL + ">"));
				return;

			case "EMBED_RESET":
				SpybotUtils.sendMessage(textChannel, SpybotUtils.createHelpEmbed("embed reset"
						, "Resets the embed back to default",
						"!embed reset",
						""));
				return;

				//Bad input or just !help (aliased to !commands)
			case "commands":
			default: 
				String description = "help, role, timeout, remindme, updateme, changelog, serverinfo, stats,"
						+ " embed_add, embed_title, embed_description, embed_send, embed_reset"
						+ "\n\nNote: Clearance 0 (admin) commands not listed";
				EmbedBuilder eb = new EmbedBuilder();
				SpybotUtils.sendMessage(textChannel, eb
						.setTitle("List of commands")
						.setDescription(description)
						.setFooter("Use !help [command] for more information", event.getJDA().getSelfUser().getAvatarUrl())
						.setColor(Color.green)
						.build());
			}
			return;
		}

		//homework commands
		case EQ: {
			ExecutableCommand executable = new EquationCommand(event, CommandType.EQ);
			executable.execute();
			return;
		}
		
		case EVAL: {
			ExecutableCommand executable = new EquationCommand(event, CommandType.EVAL);
			executable.execute();
			return;
		}
		//modifies your user (adds role/whatever)
		case ROLE: { 
			ExecutableCommand executable = new RoleCommand(event);
			executable.execute();
			return;
		}
		case TIMEOUT: { 
			ExecutableCommand executable = new TimeoutCommand(event);
			executable.execute();
			return;
		}
		case REMINDME: {
			ExecutableCommand executable = new RemindmeCommand(event);
			executable.execute();
			return;
		}
		
		case OPTIONS: {
			OptionEmbed optionCommand = new OptionEmbed(event);
			optionCommand.start();
			return;
		}
		
		//modifies the server
		
		case EMOJI:{
			ExecutableCommand executable = new EmojiCommand(event);
			executable.execute();
			return;
		}

		//info
		case CHANGELOG: {
			ExecutableCommand executable = new ChangelogCommand(event);
			executable.execute();
			return;
		}
		case SERVERINFO: {
			ExecutableCommand executable = new ServerinfoCommand(event);
			executable.execute();
			return;
		}
		
		case STATS: {
			ExecutableCommand executable = new StatsCommand(event);
			executable.execute();
			return;
		}
		//embeds
		case EMBED_ADD: {
			ExecutableCommand executable = new EmbedAddCommand(event);
			executable.execute();
			return;
		}
		case EMBED_TITLE: {
			ExecutableCommand executable = new EmbedTitleCommand(event);
			executable.execute();
			return;
		}
		case EMBED_DESCRIPTION: {
			ExecutableCommand executable = new EmbedDescriptionCommand(event);
			executable.execute();
			return;
		}
		case EMBED_CLEAR:
		case EMBED_RESET: {
			ExecutableCommand executable = new EmbedResetCommand(event);
			executable.execute();
			return;
		}
		case EMBED_SEND: {
			ExecutableCommand executable = new EmbedSendCommand(event);
			executable.execute();
			return;
		}

		case UPDATEME: {
			ExecutableCommand executable = new UpdatemeCommand(event);
			executable.execute();
			return;
		}

		//author only
		case LOGHISTORY: {
			ExecutableCommand executable = new LoghistoryCommand(event);
			executable.execute();
			return;
		}
		case LOGJOINS: {
			ExecutableCommand executable = new LogjoinsCommand(event);
			executable.execute();
			return;
		}
		case WRITEMESSAGES: {
			ExecutableCommand executable = new WritemessagesCommand(event);
			executable.execute();
			return;
		}
		case PAUSE: {
			if(SpybotUtils.getClearance(event.getMember()) > 1) {
				SpybotUtils.sendMessage(textChannel, SpybotUtils.createEmbed(SpybotUtils.DENY_COMMAND));
				return;
			}
			pause = true;
			SpybotUtils.check(event.getMessage());
			return;	
		}
		
		case STATUS: {
			ExecutableCommand executable = new StatusCommand(event);
			executable.execute();
			return;
		}
		default: //just ignore it, we won't ever get here anyway because isValid
			break;
		}

	}







	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		JDA jda = event.getJDA();
		Member member = event.getMember();
		TextChannel general = jda.getTextChannelById(SpybotUtils.CHANNEL_GENERAL);
		SpybotUtils.sendMessage(general, SpybotUtils.createEmbed("Welcome to the Blair Discord, " + member.getAsMention() + "!", "An alphabetized list of"
				+ " real life names can be found pinned to"
				+ " this channel (click the 'pin' with the red dot at the top of the screen) if you want to know who anyone is.\n\n"
				+ "So that the pin can be kept automatically updated, please dm <@" + SpybotUtils.SPYBOT + "> (me) your real life name."));
		SpybotUtils.sendPrivateMessage(member.getUser(), SpybotUtils.createEmbed("Send your name here as a single message with no extraneous information and no linebreaks.", 
				 "If there are multiple people in the magnet"
				+ " with your name, add the first letter of your last name. For instance, \"Ryan H.\""));
		event.getGuild().getController().addSingleRoleToMember(member, jda.getRoleById(SpybotUtils.ROLE_NERD)).queue();
		Pending.addPending(member.getUser().getId(), WaitingState.NAME);
	}



	@Override
	public void onGuildMemberNickChange(GuildMemberNickChangeEvent event) { //re-sort the pinned message on every nick change
		SpybotUtils.updateNamePin(event.getGuild());
	}

	@Override
	public void onRoleDelete(RoleDeleteEvent event) { //make sure if a role gets deleted we don't keep it in the db, avoids NPEs
		DBFunctions.removeColorRoleFromRoleId(event.getRole().getId());
	}
	
	@Override
	public void onUserTyping(UserTypingEvent event) { //If they have the scrub role, remove it and give them the nerd role
		JDA jda = event.getJDA();
		Guild blair = jda.getGuildById(SpybotUtils.GUILD_BLAIR);
		User user = event.getUser();
		Member member = blair.getMember(user);
		Role scrub = blair.getRoleById(SpybotUtils.ROLE_SCRUB);
		if(member != null) {
			if(member.getRoles().contains(scrub)) {
				blair.getController().removeSingleRoleFromMember(member, scrub).queue();
				blair.getController().addSingleRoleToMember(member, jda.getRoleById(SpybotUtils.ROLE_NERD)).queue();
			}
		}
	}
	
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		if(event.getUser().isBot()) {
			return;
		}
		String unicode = event.getReactionEmote().getName();
		HashMap<String, Pending> pending = Pending.getAll();
		if(!pending.keySet().contains(event.getMessageId())) {
			return;
		}
		
		OptionEmbed oc = OptionEmbed.getAllOptionCommands().get(event.getUser());
		
		if(unicode.equals(OptionEmbed.UNICODE_BACK)) {
			if(oc == null) {
				SpybotUtils.sendMessage(event.getTextChannel(), "OC is null! wtf liam");
				return;
			}
			oc.surface();
		} else {
			oc.delve(unicode);
		}
	}

}