#!/bin/bash
git pull &>> log.txt
javac -cp "dependencies/*:src/main/java" src/main/java/com/tybug/spybot/Spybot.java -d sources/ # Compile to class files
nohup java -cp "./sources:./dependencies/*" com.tybug.spybot.Spybot &>> log.txt # Run the main class, redirect stdout and stderr to log.txt