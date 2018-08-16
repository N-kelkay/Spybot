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

import com.tybug.spybot.commands.CommandType;
import com.tybug.spybot.commands.ExecutableCommand;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class EquationCommand extends ExecutableCommand{

	private CommandType type;

	public EquationCommand(MessageReceivedEvent event, CommandType type) {
		super(event);
		this.type = type;
	}

	@Override
	public void execute() {
		try {
			if(type.equals(CommandType.EQ)) {
				String result = maxima(this.args);
				if(result == null) {
					return;
				}
				latex(result);
			}
			
			else if(type.equals(CommandType.EVAL)) {
				latex(args);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	private String maxima(String args) throws IOException {
		String[] commands = new String[]{"./maxima.sh", "print(tex1(" + args + "))$"};


		Process p = Runtime.getRuntime().exec(commands);
		


		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String s = null;
		StringBuilder sb = new StringBuilder();
		while ((s = input.readLine()) != null) { // errors output to stdout, perhaps becoause I'm using bash instead of executing from cmd line, but either way
			sb.append(s + "\n");
		}
		
		s = sb.toString();
		if(s.contains("error") || s.contains("syntax")) {
			textChannel.sendMessage("```fix\nFATAL ERROR:\n" + s + "\n```").queue();
			return null;
		}
		
		return s;
	}

	private void latex(String input) throws IOException {
		String[] commands = new String[]{"python3", "./latex.py", input};
		Process p = Runtime.getRuntime().exec(commands);
		BufferedReader inputStream = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
		String s = inputStream.readLine();

		URL url = new URL(s);
		BufferedImage image = ImageIO.read(url);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(image, "png", os);
		InputStream is = new ByteArrayInputStream(os.toByteArray());
		textChannel.sendFile(is, OffsetDateTime.now().toString() + ".png").queue();
	}


}
