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

public class EquationCommand extends ExecutableCommand{

	public EquationCommand(MessageReceivedEvent event) {
		super(event);
	}

	@Override
	public void execute() {
		try {

			String[] commands = new String[]{"./maxima.sh", "print(tex1(" + args + "))$"};


			Process p = Runtime.getRuntime().exec(commands);

			BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String s = null;
			StringBuilder sb = new StringBuilder();
			while ((s = error.readLine()) != null) {
				sb.append(s);
			}
			if(sb.toString().length() > 0) {
				textChannel.sendMessage("```fix\nFATAL ERROR:\n" + sb.toString() + "\n```").queue();
			}


			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String output = input.readLine();

			commands = new String[]{"python3", "./latex.py", output};
			p = Runtime.getRuntime().exec(commands);
			input = new BufferedReader(new InputStreamReader(p.getInputStream()));

			s = input.readLine();

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


	//	while ((s = error.readLine()) != null) {
	//		System.err.println(s);
	//	}


}
