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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.tybug.spybot.commands.CommandType;
import com.tybug.spybot.commands.ExecutableCommand;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class EquationCommand extends ExecutableCommand{

	private CommandType type;
	private final String[] blacklist = {"with", "file", "save", "string", "open", "batch", "load", "path", "write", "read", "tex"};
	public EquationCommand(MessageReceivedEvent event, CommandType type) {
		super(event);
		this.type = type;
	}

	@Override
	public void execute() {
		try {
			if(type.equals(CommandType.EVAL)) {
				args = args.replace("\\", "");
				for(String s : blacklist) {
					if(!args.contains(s)) {
						continue;
					}
					
					// File i/o stuff, prevent rce
					textChannel.sendMessage("Nice try.").queue();
					return;
				}
				
				Pattern pattern = Pattern.compile("(\\d+)([a-zA-Z])");
				Matcher matcher = pattern.matcher(args);
				
				int count = 0; // Matcher#start is still dealing with the orignal string, but we keep adding * to it
				while(matcher.find()) {
					int offset = matcher.group(1).length();
//					System.out.println("Start: " + matcher.start());
					String before = args.substring(0, matcher.start() + offset + count);
//					System.out.println("Before: " + before);
					String after = args.substring(matcher.start() + offset + count);
//					System.out.println("After: " + after);
					args = before + "*" + after;
//					System.out.println("Final: " + args);
					count++;
				}
				
				pattern = Pattern.compile("([xyz]{2,})");
				matcher = pattern.matcher(args);
				
				count = 0;
//				System.out.println("Matching letters");
				while(matcher.find()) {
//					System.out.println("Start: " + matcher.start());
					String before = args.substring(0, matcher.start() + 1 + count);
//					System.out.println("Before: " + before);
					
					
					
					String after = args.substring(matcher.start() + 1 + count);
//					System.out.println("After: " + after);

					// subtract one first to account for the already multiplied number * variable previously, double because we're adding two every time
					for(int i = 0; i < ((matcher.group(1).length() - 1) * 2) - 3; i+=2) {
						after = after.substring(0, i + 1) + "*" + after.substring(i + 1);
//						System.out.println("After again: " + after);
						count++;
					}
					args = before + "*" + after;
//					System.out.println("Final: " + args);
					count++;
				}
				
				String result = maxima(args);
				System.out.println("Maxima result: " + result);
				if(result == null) {
					return;
				}
				latex(result);
			}
			
			else if(type.equals(CommandType.LATEX)) {
				latex(args);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	private String maxima(String equation) throws IOException {
		String[] commands = new String[]{"./maxima.sh", "print(tex1(" + equation + "))$"};

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
		System.out.println("Latex result: " + s);
		URL url = new URL(s);
		BufferedImage image = ImageIO.read(url);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(image, "png", os);
		InputStream is = new ByteArrayInputStream(os.toByteArray());
		textChannel.sendFile(is, OffsetDateTime.now().toString() + ".png").queue();
	}


}
