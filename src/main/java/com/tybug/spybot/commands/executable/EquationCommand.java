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



		String[] commands = new String[]{"./maxima.sh", "print(tex1(" + args + "))$"};
		executeProcess(commands, true);

		commands = new String[]{"/Library/Frameworks/Python.framework/Versions/3.5/bin/python3.5", "./latex.py", args};
		executeProcess(commands, true);


	}

	private void executeProcess(String[] commands, boolean debug) {

		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = null;
			proc = rt.exec(commands);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String s = null;
			while ((s = stdInput.readLine()) != null) {
				if(debug) System.out.println(s);
				URL url = new URL(s);
				BufferedImage image = ImageIO.read(url);
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				ImageIO.write(image, "png", os);
				InputStream is = new ByteArrayInputStream(os.toByteArray());
				textChannel.sendFile(is, OffsetDateTime.now().toString() + ".png").queue();
			}

			BufferedReader error = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
			s = null;
			while ((s = error.readLine()) != null) {
				if(debug) System.err.println(s);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
