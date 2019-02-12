package com.tybug.spybot.commands;

import java.util.ArrayList;
import java.util.List;

import com.tybug.spybot.SpybotUtils;
import com.tybug.spybot.util.BotUtils;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * For monitoring {@link http://www.montgomeryschoolsmd.org/emergency/} and notifying people if school is delayed or closed.
 * @author Liam DeVoe
 */
public class SnowChecker {
	private JDA jda;
	private List<String> pastAnnouncements;

	public SnowChecker(JDA jda) {
		this.jda = jda;
		pastAnnouncements = new ArrayList<String>();
	}

	/**
	 * Checks the {@link http://www.montgomeryschoolsmd.org/emergency/} website every 30 seconds and pings @snow if the content under the "EMERGENCY HEADER"
	 * was changed to a non-zero content (presumably, if they put a message up about closure or delay)
	 * @param jda The jda instance to use (in this case, just the bot jda)
	 */
	public void start() {


		new Thread(() -> {
			String website = "https://www.montgomeryschoolsmd.org";
			TextChannel general = jda.getTextChannelById(SpybotUtils.CHANNEL_GENERAL);
			TextChannel dynolog = jda.getTextChannelById(SpybotUtils.CHANNEL_MODTALK);

			while(true) {
				String parsed = SpybotUtils.getEmergencyMessage(website);
				System.out.println(parsed);
				if(!pastAnnouncements.contains(parsed)) {
					pastAnnouncements.add(parsed);
				}
				String compare = SpybotUtils.getEmergencyMessage(website);
				while(parsed.equals(compare)) {
					compare = SpybotUtils.getEmergencyMessage(website);

					try {
						Thread.sleep(300000); //5 minutes
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}

				}

				BotUtils.sendMessage(dynolog, "Previous:\n```" + parsed + "```\n\nCurrent:\n```" + compare + "```");
				BotUtils.sendMessage(general, "<@&512037353657991181> bitch this better be school being delayed or imma kill liam: ```" + compare + "```");
				parsed = SpybotUtils.getEmergencyMessage(website);
				if(!parsed.equals("")) {
					if(pastAnnouncements.contains(parsed)) {
						BotUtils.sendMessage(dynolog, "<@216008405758771200> I would have posted this: " + parsed
								+ "but it was a past announcment!");
						break;
					}
					BotUtils.sendMessage(general, jda.getRoleById(SpybotUtils.ROLE_SNOW).getAsMention() + "```\n" + parsed + "\n```");
				}
				if(!pastAnnouncements.contains(parsed)) {
					pastAnnouncements.add(parsed);
				}
			}
		}).start();
	}
}
