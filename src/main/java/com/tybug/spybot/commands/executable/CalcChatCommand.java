package com.tybug.spybot.commands.executable;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.OffsetDateTime;

import javax.imageio.ImageIO;

import com.tybug.spybot.commands.ExecutableCommand;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CalcChatCommand extends ExecutableCommand {

	public CalcChatCommand(MessageReceivedEvent event) {
		super(event);
	}

	@Override
	public void execute() {
		String[] args = this.args.split(" ");
		String[] commands = new String[]{"/Library/Frameworks/Python.framework/Versions/3.5/bin/python3", "./calcchat.py", args[0], args[1], args[2]};
		String s = null;
		try {
			s = calcChat(commands);
			System.out.println(s);
			URL url = new URL(s);
			BufferedImage image = ImageIO.read(url);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(image, "png", os);
			InputStream is = new ByteArrayInputStream(os.toByteArray());
			textChannel.sendFile(is, OffsetDateTime.now().toString() + ".png").queue();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private String calcChat(String[] commands) throws IOException {
		Process p = Runtime.getRuntime().exec(commands);
		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
		String s = null;
		while ((s = input.readLine()) != null) {
			System.out.println(s);
		}
		return input.readLine();
	}
}
